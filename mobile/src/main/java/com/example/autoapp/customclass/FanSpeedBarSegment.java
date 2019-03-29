package com.example.autoapp.customclass;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

import com.example.autoapp.R;

/**
 * Represents a single bar in the fan speed bar.
 */
public class FanSpeedBarSegment extends android.support.v7.widget.AppCompatImageView {
    private boolean mTurnedOn;
    private final int mDotExpandedSize;
    private final int mDotSize;
    private final ValueAnimator mDotWidthExpandAnimator;
    private final ValueAnimator.AnimatorUpdateListener mExpandListener
            = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int size = (int) animation.getAnimatedValue();
            GradientDrawable drawable = (GradientDrawable) getDrawable();
            drawable.setCornerRadius(size / 2);
            drawable.setSize(size, size);
        }
    };
    public FanSpeedBarSegment(Context context) {
        super(context);
    }
    public FanSpeedBarSegment(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public FanSpeedBarSegment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    {
        setScaleType(ScaleType.CENTER);
        Resources res = getResources();
        mDotExpandedSize = res.getDimensionPixelSize(R.dimen.hvac_fan_speed_dot_expanded_size);
        mDotSize = res.getDimensionPixelSize(R.dimen.hvac_fan_speed_dot_size);
        mDotWidthExpandAnimator = ValueAnimator.ofInt(mDotSize, mDotExpandedSize);
        mDotWidthExpandAnimator.addUpdateListener(mExpandListener);
        GradientDrawable dot = new GradientDrawable();
        dot.setColor(res.getColor(R.color.fan_speed_custom_color));
        dot.setSize(mDotSize, mDotSize);
        dot.setCornerRadius(mDotSize / 2);
        setImageDrawable(dot);
    }
    public void playTurnOnAnimation(int duration, int delayMs) {
        mDotWidthExpandAnimator.setStartDelay(delayMs);
        mDotWidthExpandAnimator.setDuration(duration);
        mDotWidthExpandAnimator.start();
        mTurnedOn = true;
    }
    public void playTurnOffAnimation(int duration, int delayMs) {
        mDotWidthExpandAnimator.setStartDelay(delayMs);
        mDotWidthExpandAnimator.setDuration(duration);
        mDotWidthExpandAnimator.reverse();
        mTurnedOn = false;
    }
    public void setTurnedOn(boolean isOn) {
        mTurnedOn = isOn;
        GradientDrawable drawable = (GradientDrawable) getDrawable();
        if (mTurnedOn) {
            drawable.setCornerRadius(0);
            // Setting size -1, makes the drawable grow to the size of the image view.
            drawable.setSize(-1, -1);
        } else {
            drawable.setCornerRadius(mDotSize / 2);
            drawable.setSize(mDotSize, mDotSize);
        }
        setImageDrawable(drawable);
    }
    public boolean isTurnedOn() {
        return mTurnedOn;
    }
}
