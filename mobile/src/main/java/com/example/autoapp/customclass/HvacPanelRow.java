package com.example.autoapp.customclass;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.example.autoapp.R;

/**
 * A row in the center HVAC panel.
 */
public class HvacPanelRow extends LinearLayout {
    private static final int EXPAND_ANIMATION_TIME_MS = 300;
    private static final int EXPAND_ANIMATION_ALPHA_TIME_MS = 100;
    private final int mAnimationHeightShift;
    private boolean mDisableClickOnChildren;
    public HvacPanelRow(Context context) {
        super(context);
    }
    public HvacPanelRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public HvacPanelRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    {
        Resources res = getResources();
        mAnimationHeightShift
                = res.getDimensionPixelSize(R.dimen.hvac_panel_row_animation_height_shift);
    }
    public AnimatorSet getExpandAnimation(long delayMs, float endAlpha) {
        return getTransitionAnimation(delayMs, endAlpha, true /* expanding */);
    }
    public AnimatorSet getCollapseAnimation(long delayMs, float startAlpha) {
        return getTransitionAnimation(delayMs, startAlpha, false /* expanding */);
    }
    /**
     * Disable/enable touch events on all child views of this panel
     */
    public void disablePanel(boolean disable) {
        mDisableClickOnChildren = disable;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDisableClickOnChildren;
    }
    private AnimatorSet getTransitionAnimation(long delayMs, float maxAlpha, boolean expanding) {
        ValueAnimator alphaAnimator;
        ValueAnimator translationYAnimator;
        if (expanding) {
            alphaAnimator = ValueAnimator.ofFloat(0, maxAlpha);
            translationYAnimator = ValueAnimator.ofFloat(mAnimationHeightShift, 0);
        } else {
            alphaAnimator = ValueAnimator.ofFloat(maxAlpha, 0);
            translationYAnimator = ValueAnimator.ofFloat(0, mAnimationHeightShift);
        }
        alphaAnimator.setStartDelay(delayMs);
        alphaAnimator.setDuration(EXPAND_ANIMATION_ALPHA_TIME_MS);
        alphaAnimator.addUpdateListener(mAlphaUpdateListener);
        translationYAnimator.addUpdateListener(mTranslationYUpdateListener);
        translationYAnimator.setDuration(EXPAND_ANIMATION_TIME_MS);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnimator, translationYAnimator);
        return set;
    }
    private final ValueAnimator.AnimatorUpdateListener mTranslationYUpdateListener
            = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float) animation.getAnimatedValue();
            setTranslationY(value);
        }
    };
    private final ValueAnimator.AnimatorUpdateListener mAlphaUpdateListener
            = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float alpha = (float) animation.getAnimatedValue();
            setAlpha(alpha);
        }
    };
}