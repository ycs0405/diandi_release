package com.cargps.android.model.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.cargps.android.MyApplication;
import com.cargps.android.MyContacts;
import com.cargps.android.R;
import com.cargps.android.interfaces.DialogCallback;
import com.cargps.android.interfaces.ICallBack;
import com.cargps.android.model.view.RoundImageView;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.LoginResponse;
import com.cargps.android.net.responseBean.StringResponse;
import com.cargps.android.utils.DialogUtils;
import com.cargps.android.utils.ImageLoadOptions;
import com.cargps.android.utils.ImageUtils;
import com.cargps.android.utils.SdUtil;
import com.fu.baseframe.FrameApplication;
import com.fu.baseframe.net.CallServer;
import com.fu.baseframe.net.CustomDataRequest;
import com.fu.baseframe.net.HttpListener;
import com.fu.baseframe.utils.SystemOpt;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yolanda.nohttp.FileBinary;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 个人信息
 *
 * @author fu
 */
@EActivity(R.layout.activity_persion_info_layout)
public class PersionInfoActivity extends BaseActivity {

    //	private static final int CAMERA_PERMISSION_REQUEST_CODE = 0X211;
    private static final int RESULT_CAMARE_IMAGE = 0x001;
    private static final int RESULT_PHOTO_IMAGE = 0x002;
    private static final String PATH_DOCUMENT = "document";
    @ViewById
    RoundImageView headImg;
    @ViewById
    public EditText nikeNameEt, mobileNoEt;
    @ViewById
    public TextView authFlagTv;

    TextView rightTv;

    @Override
    @AfterViews
    public void initViews() {
        super.initViews();

        setTitleText("个人信息");

    }

    @Click
    public void exitBtn() {
        app.loginOut(new ICallBack() {

            @Override
            public void callback() {
                finish();
            }
        });
    }

    @Click
    public void headLayout() {
        DialogUtils.getInstance().showSelectPicture(this, new DialogCallback() {
            @Override
            public void camareClick() {
//				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用android自带的照相机
//				File file = SdUtil.getFile("head.png", true);
//				Uri uri = Uri.fromFile(file);
//				intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0); 
//				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//				startActivityForResult(intent, RESULT_CAMARE_IMAGE);


                Intent in = new Intent();
                /* 开启Pictures画面Type设定为image */
                in.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
                in.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
                startActivityForResult(in, RESULT_PHOTO_IMAGE);
            }

            @Override
            public void picClick() {

            }
        });
    }

    @Click
    public void changeNikeImg() {
        this.rightTv.setVisibility(View.VISIBLE);
        this.rightTv.setText("保存");
        nikeNameEt.setEnabled(true);
        nikeNameEt.requestFocus();
        mobileNoEt.setEnabled(true);
    }

    @Click
    public void phoneImg() {
        this.rightTv.setVisibility(View.VISIBLE);
        this.rightTv.setText("保存");
        nikeNameEt.setEnabled(true);
        mobileNoEt.requestFocus();
        mobileNoEt.setEnabled(true);
    }

    @Click
    public void authLayout() {
        com.cargps.android.model.activity.AuthCardIdActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).start();
    }

    @Override
    public void rightTv(TextView rightTv) {
        this.rightTv = rightTv;
        this.rightTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                app.HideKeyboard(nikeNameEt);
                setUserInfoData();
            }
        });
    }

    public void showView() {
        if (app.rootUserInfo != null) {
            if (app.rootUserInfo.idNo != null && !app.rootUserInfo.idNo.isEmpty()) {
                authFlagTv.setText("已认证");
            }

            if (app.userInfo != null) {
                ImageLoader.getInstance().displayImage("http://" + app.userInfo.userImgurl, headImg, ImageLoadOptions.getOptions());
            }
            mobileNoEt.setText(app.rootUserInfo.mobileNo);
            nikeNameEt.setText(app.rootUserInfo.fullName);


        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        showView();
    }

    public void setUserInfoData() {
        if (app.rootUserInfo == null) return;
        String userName = nikeNameEt.getText().toString();
        String mobile = mobileNoEt.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            showToast("用户名不能为空!");
            return;
        }

        if (TextUtils.isEmpty(mobile)) {
            showToast("手机号不能为空!");
            return;
        }
        String urlStr = MyContacts.MAIN_URL + "v1.0/userInfo/updateInfo";
        Map<String, String> param = new HashMap<String, String>();
        param.put("userName", userName);
        param.put("mobileNo", mobile);
        HttpRequest<LoginResponse> httpRequest = new HttpRequest<LoginResponse>(this, urlStr, new HttpResponseListener<LoginResponse>() {

            @Override
            public void onResult(LoginResponse result) {
                if (result != null && result.data != null) {
                    if (result.statusCode == 200) {
                        app.setLocalUserinfo(result.data);
                        nikeNameEt.setEnabled(true);
                        mobileNoEt.setEnabled(true);
                        rightTv.setVisibility(View.GONE);

                        showView();

                        app.getUserInfoData();
                    }
                }
            }

            @Override
            public void onFail(int code) {

            }
        }, LoginResponse.class, param, "POST", true);

        httpRequest.addHead("mobileNo", app.rootUserInfo.mobileNo);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", app.rootUserInfo.accessToken);

        HttpExecute.getInstance().addRequest(httpRequest);
    }


    @SuppressWarnings("unused")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_CAMARE_IMAGE:
                    Bitmap bitmap1 = ImageUtils.getimage(SdUtil.getFile("head.png", true).getAbsolutePath());
                    ImageUtils.SavePicInLocal(bitmap1, "Copy.png");
                    upFileImg();
                    break;
                case RESULT_PHOTO_IMAGE:
                    if (data != null) {
                        Uri uri = data.getData();
                        ContentResolver cr = getContentResolver();
                        String degree = "";
                        if (isDownloadsDocument(uri)) {
                            String wholeID = getDocumentId(data.getData());
                            String id = wholeID.split(":")[1];
                            String[] column = {MediaStore.Images.Media.DATA,
                                    MediaStore.Images.Media.ORIENTATION};
                            String sel = MediaStore.Images.Media._ID + "=?";
                            Cursor cursor = getContentResolver().query(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel,
                                    new String[]{id}, null);
                            int columnIndex1 = cursor.getColumnIndex(column[1]);
                            if (cursor.moveToFirst()) {
                                degree = cursor.getString(columnIndex1);
                            }
                        }

                        try {
                            Bitmap bitmap = ImageUtils.compressImageFromFile(cr, uri);
                            ImageUtils.SavePicInLocal(ImageUtils.comp(bitmap), "Copy.png");
                            upFileImg();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static boolean isDownloadsDocument(Uri uri) {
        if (uri.getAuthority() != null) {
            Log.e("coument", uri.getAuthority());
        }
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public static String getDocumentId(Uri documentUri) {
        final List<String> paths = documentUri.getPathSegments();
        if (paths.size() < 2) {
            throw new IllegalArgumentException("Not a document: " + documentUri);
        }
        if (!PATH_DOCUMENT.equals(paths.get(0))) {
            throw new IllegalArgumentException("Not a document: " + documentUri);
        }
        return paths.get(1);
    }

    private void upFileImg() {
        String urlStr = MyContacts.MAIN_URL + "v1.0/userInfo/uploadPicture";
        Request<StringResponse> request = new CustomDataRequest<StringResponse>(urlStr, RequestMethod.POST, StringResponse.class);
        request.setHeader("context-Type", "multipart/form-data");
        request.setHeader("mobileNo", MyApplication.getInstance().rootUserInfo.mobileNo);
        request.setHeader("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        request.setHeader("accesstoken", MyApplication.getInstance().rootUserInfo.accessToken);
        request.add("file", new FileBinary(SdUtil.getFile("Copy.png", false)));
        CallServer.getRequestInstance().add(this, urlStr.hashCode(), request, new HttpListener<StringResponse>() {

            @Override
            public void onSucceed(int what, Response<StringResponse> response) {
                if (response.isSucceed() && response.get() != null) {
                    ImageLoader.getInstance().displayImage("http://" + response.get().data, headImg, ImageLoadOptions.getOptions());
                    app.getUserInfoData();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode,
                                 long networkMillis) {
                // TODO Auto-generated method stub

            }
        }, FrameApplication.mainActivity, true);
    }

    @Override
    public boolean isLogin() {
        // TODO Auto-generated method stub
        return true;
    }
}
