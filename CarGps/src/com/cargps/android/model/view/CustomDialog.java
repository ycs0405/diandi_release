package com.cargps.android.model.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;

import com.cargps.android.R;
import com.cargps.android.utils.KeyBoardUtils;

public class CustomDialog extends Dialog {

    public CustomDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
    }

    protected CustomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
    }

    public CustomDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public static class Builder {
        private Context context;
        private Bitmap image;
        private int theme = R.style.pairDialog;
        private int layout = R.layout.pair_dialog;
        private EditText input;
        private ImageView button;


        public Builder(Context context) {
            this.context = context;
        }

        public Bitmap getImage() {
            return image;
        }

        public void setImage(Bitmap image) {
            this.image = image;
        }

        public void setLayout(int layoutId, int theme) {
            this.theme = theme;
            this.layout = layoutId;
        }

        public int getLayoutId() {
            return layout;
        }

        public int getThemeId() {
            return theme;
        }

        @SuppressLint("Override")
        public CustomDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomDialog dialog = new CustomDialog(context, theme);
            View layout = inflater.inflate(getLayoutId(), null);
            dialog.addContentView(layout, new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                    , android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.setContentView(layout);

            input = (EditText) layout.findViewById(R.id.et_input);

            button = (ImageView) layout.findViewById(R.id.bt_click);
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (mClickListener != null) {
                        KeyBoardUtils.closeKeybord(input, context);
                        mClickListener.onClick(dialog, 0);
                    }
                }
            });

            return dialog;
        }

    }


    private static DialogInterface.OnClickListener mClickListener;

    public void setClickListener(DialogInterface.OnClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public String getEdittext() {
        return ((EditText) findViewById(R.id.et_input)).getText().toString();
    }
}
