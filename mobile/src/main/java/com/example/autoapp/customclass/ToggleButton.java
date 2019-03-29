package com.example.autoapp.customclass;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * A toggle button that has two states, each with a different drawable icon.
 */
public class ToggleButton extends android.support.v7.widget.AppCompatImageButton {
    /**
     * A listener that is notified when the button is toggled.
     */
    public interface ToggleListener {
        void onToggled(boolean isOn);
    }

    private boolean mIsOn;
    private Drawable mDrawableOff;
    private Drawable mDrawableOn;
    private ToggleListener mListener;

    public ToggleButton(Context context) {
        super(context);
    }

    public ToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsOn) {
                    setImageDrawable(mDrawableOff);
                    if (mListener != null) {
                        mListener.onToggled(false);
                    }
                    mIsOn = false;
                } else {
                    setImageDrawable(mDrawableOn);
                    if (mListener != null) {
                        mListener.onToggled(true);
                    }
                    mIsOn = true;
                }
            }
        });
    }

    public void setToggleListener(ToggleListener listener) {
        mListener = listener;
    }

    public void setToggleIcons(Drawable on, Drawable off) {
        mDrawableOff = off;
        mDrawableOn = on;
        setImageDrawable(mIsOn ? mDrawableOn : mDrawableOff);
    }

    public void setIsOn(boolean on) {
        mIsOn = on;
        if (mIsOn) {
            setImageDrawable(mDrawableOn);
        } else {
            setImageDrawable(mDrawableOff);
        }
    }
}
