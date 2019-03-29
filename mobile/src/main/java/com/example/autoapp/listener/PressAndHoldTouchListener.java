package com.example.autoapp.listener;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
/**
 * A wrapper for a click listener that repeats the click when a press and hold action takes place.
 */
public class PressAndHoldTouchListener implements OnTouchListener {
    private static final int REPEAT_ACTION_DELAY_MS = 100;
    private static final int FIRST_ACTION_DELAY_MS = 300;
    private Handler mHandler = new Handler();
    private final OnClickListener clickListener;
    private View mView;
    public PressAndHoldTouchListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHandler.removeCallbacks(mRepeatedAction);
                mHandler.postDelayed(mRepeatedAction, FIRST_ACTION_DELAY_MS);
                mView = view;
                mView.setPressed(true);
                clickListener.onClick(view);
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(mRepeatedAction);
                mView.setPressed(false);
                mView = null;
                return true;
            default:
                return false;
        }
    }
    private Runnable mRepeatedAction = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, REPEAT_ACTION_DELAY_MS);
            clickListener.onClick(mView);  //
        }
    };
}