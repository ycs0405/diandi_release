package com.cargps.android.model.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.cargps.android.MyApplication;
import com.cargps.android.MyContacts;
import com.cargps.android.R;
import com.cargps.android.interfaces.ILogin;
import com.cargps.android.model.activity.CarCantrolActivity;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.UnlockInfoResponse;
import com.fu.baseframe.utils.SystemOpt;
import com.fu.qr_codescan.camera.CameraManager;
import com.fu.qr_codescan.decoding.CaptureFragmentHandler;
import com.fu.qr_codescan.decoding.InactivityTimer;
import com.fu.qr_codescan.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

@EFragment(R.layout.activity_capture)
public class ScanEleCarFragment extends BaseFragment implements Callback, ILogin {

    private CaptureFragmentHandler handler;

    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    TextView titleTv;

    @ViewById
    SurfaceView surfaceView;
    @ViewById
    ViewfinderView viewfinderView;
    /**
     * Called when the activity is first created.
     */
    @ViewById
    ImageView inputImg, openledImg;
    boolean isLed = false;

    boolean isScan = false;

    boolean isResume;

    @AfterViews
    public void initViews() {
        CameraManager.init(app);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(MyApplication.mainActivity);


        openledImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isLed) {
                    CameraManager.get().openLed();
                } else {
                    CameraManager.get().closeLed();
                }
                isLed = !isLed;
            }
        });

        inputImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                com.cargps.android.model.activity.InputImeiActivity_.intent(getActivity()).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).start();
            }
        });


    }


    @SuppressWarnings("deprecation")
    @Override
    public void onResume() {
        super.onResume();
        isResume = true;

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) app.getSystemService(Context.AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }


    @Override
    public void onPause() {
        super.onPause();
        isResume = false;
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        isResume = false;
        inactivityTimer.shutdown();
        super.onDestroy();
    }


    /**
     * 处理扫描结果
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        if (resultString.equals("")) {
            Toast.makeText(MyApplication.mainActivity, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            unlocakData(resultString);
        }

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            int pix[] = getDisplayScreenResolution(MyApplication.mainActivity);
            float w = pix[0];
            int h = pix[1];
            float scale = ((float) CameraManager.get().cameraResolution.x) / ((float) CameraManager.get().cameraResolution.y);

            float nw = w * scale;

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) nw, h);
            surfaceView.setLayoutParams(layoutParams);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureFragmentHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            MyApplication.mainActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) app.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    public int[] getDisplayScreenResolution(Context context) {
        int[] screenSizeArray = new int[2];

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        android.view.Display display = wm.getDefaultDisplay();
        display.getMetrics(dm);

        screenSizeArray[0] = dm.widthPixels;
        screenSizeArray[1] = dm.heightPixels;
        int ver = Build.VERSION.SDK_INT;
        if (ver < 13) {
            screenSizeArray[1] = dm.heightPixels;
        } else if (ver == 13) {
            try {
                Method mt = display.getClass().getMethod("getRealHeight");
                screenSizeArray[1] = (Integer) mt.invoke(display);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (ver > 13) {
            try {
                Method mt = display.getClass().getMethod("getRawHeight");
                screenSizeArray[1] = (Integer) mt.invoke(display);

            } catch (Exception e) {
                screenSizeArray[1] = dm.heightPixels;
            }
        }

        return screenSizeArray;
    }

    public void unlocakData(final String code) {

        if (MyApplication.getInstance().rootUserInfo == null) return;

        AMapLocation location = AllEleCarFrament.getInstance().getAmapLocationCurr();
        if (location == null) {
            Toast.makeText(getActivity(), "正在定位。。。。", Toast.LENGTH_SHORT).show();
            return;
        }
        String urlStr = MyContacts.MAIN_URL + "v1.0/ebike/scanBike";

        Map<String, String> param = new HashMap<String, String>();
        param.put("imeiIdOrDeviceId", code);
        param.put("longitude", location.getLongitude() + "");
        param.put("latitude", location.getLatitude() + "");
        HttpRequest<UnlockInfoResponse> httpRequest = new HttpRequest<UnlockInfoResponse>(getActivity(), urlStr, new HttpResponseListener<UnlockInfoResponse>() {

            @Override
            public void onResult(UnlockInfoResponse result) {

                if (result != null && result.statusCode == 200) {
                    MyApplication.INPUT_IMEI = code;
                    Intent intent = new Intent(MyApplication.mainActivity, CarCantrolActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (result != null && result.statusCode == 403) {
                    Toast.makeText(getActivity(), "租车需要押金，请支付押金才能租车。", Toast.LENGTH_SHORT).show();
                    com.cargps.android.model.activity.PayActivity_.intent(getActivity()).code(code).yajin(true).money(result.data).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
                } else {
                    Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                    if (isResume) {
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                handler.restartPreviewAndDecode();
                            }
                        }, 10 * 1000);
                    }
                }
            }

            @Override
            public void onFail(int code) {
                if (isResume) {
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            handler.restartPreviewAndDecode();
                        }
                    }, 10 * 1000);
                }
            }
        }, UnlockInfoResponse.class, param, "POST", true);

        httpRequest.addHead("mobileNo", app.rootUserInfo.mobileNo);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", app.rootUserInfo.accessToken);
        HttpExecute.getInstance().addRequest(httpRequest);
    }


    @Override
    public boolean isLogin() {
        return true;
    }

}