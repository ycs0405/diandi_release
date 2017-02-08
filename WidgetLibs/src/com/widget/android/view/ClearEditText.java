package com.widget.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

@SuppressLint("ClickableViewAccessibility")
public class ClearEditText extends EditText {

    private Drawable clearDrawable;

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        Drawable[] drawables = getCompoundDrawables();
        clearDrawable = drawables[2];

        setOnFocusChangeListener(focusChangeListener);
        addTextChangedListener(new TextChange());
    }

    OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                boolean isVisible = getText().toString().length() >= 1;
                setClearImgVisible(isVisible);
            } else {
                setClearImgVisible(false);

            }
        }
    };

    public class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            boolean isVisible = getText().toString().length() >= 1;
            setClearImgVisible(isVisible);

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_UP:
                boolean isClean = (event.getX() > (getWidth() - getTotalPaddingRight())) && (event.getX() < (getWidth() - getPaddingRight()));
                if (isClean) {
                    setText("");
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void setClearImgVisible(boolean isVisible) {
        Drawable d = null;
        if (isVisible) {
            d = clearDrawable;
        } else {
            d = null;
        }
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], d, getCompoundDrawables()[3]);
    }
}
