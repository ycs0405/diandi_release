package com.fu.qr_codescan.decoding;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cargps.android.R;
import com.cargps.android.model.fragment.ScanEleCarFragment;
import com.fu.qr_codescan.camera.CameraManager;
import com.fu.qr_codescan.view.ViewfinderResultPointCallback;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Vector;

public class CaptureFragmentHandler extends Handler {

    private static final String TAG = "";

    private final ScanEleCarFragment fragment;
    private final DecodeThread decodeThread;
    private State state;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    public CaptureFragmentHandler(ScanEleCarFragment fragment, Vector<BarcodeFormat> decodeFormats,
                                  String characterSet) {
        this.fragment = fragment;
        decodeThread = new DecodeThread(fragment, decodeFormats, characterSet,
                new ViewfinderResultPointCallback(fragment.getViewfinderView()));
        decodeThread.start();
        state = State.SUCCESS;
        // Start ourselves capturing previews and decoding.
        CameraManager.get().startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == R.id.auto_focus) {
            if (state == State.PREVIEW) {
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            }
        } else if (message.what == R.id.restart_preview) {
            Log.d(TAG, "Got restart preview message");
            restartPreviewAndDecode();
        } else if (message.what == R.id.decode_succeeded) {
            Log.d(TAG, "Got decode succeeded message");
            state = State.SUCCESS;
            Bundle bundle = message.getData();
            /***********************************************************************/
            Bitmap barcode = bundle == null ? null :
                    (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);//���ñ����߳�
            fragment.handleDecode((Result) message.obj, barcode);//���ؽ��?        /***********************************************************************/
        } else if (message.what == R.id.decode_failed) {
            // We're decoding as fast as possible, so when one decode fails, start another.
            state = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
        } else if (message.what == R.id.return_scan_result) {
            Log.d(TAG, "Got return scan result message");
//			fragment.getActivity().setResult(Activity.RESULT_OK, (Intent) message.obj);
//			activity.finish();
        } else if (message.what == R.id.launch_product_query) {
            Log.d(TAG, "Got product query message");
            String url = (String) message.obj;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            fragment.getActivity().startActivity(intent);
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        CameraManager.get().stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            decodeThread.join();
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    public void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
            CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            fragment.drawViewfinder();
        }
    }

}
