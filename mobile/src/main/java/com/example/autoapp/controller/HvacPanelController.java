package com.example.autoapp.controller;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.autoapp.R;
import com.example.autoapp.customclass.FanDirectionButtons;
import com.example.autoapp.customclass.FanSpeedBar;
import com.example.autoapp.customclass.HvacPanelRow;
import com.example.autoapp.customclass.SeatWarmerButton;
import com.example.autoapp.customclass.TemperatureBarOverlay;
import com.example.autoapp.customclass.ToggleButton;

import java.util.ArrayList;
import java.util.List;
/**
 * A state machine to control transition from various HVAC UI layouts.
 */
public class HvacPanelController {
    private static final int PANEL_ANIMATION_TIME_MS = 200;
    private static final int PANEL_COLLAPSE_ANIMATION_TIME_MS = 500;
    private static final int PANEL_ANIMATION_DELAY_MS = 100;
    private static final int PANEL_ANIMATION_LONG_DELAY_MS = 3 * PANEL_ANIMATION_DELAY_MS;
    private static final float DISABLED_BUTTON_ALPHA = 0.20f;
    private static final float ENABLED_BUTTON_ALPHA = 1.0f;
    private static final int STATE_COLLAPSED = 0;
    private static final int STATE_COLLAPSED_DIMMED = 1;
    private static final int STATE_FULL_EXPANDED = 2;
    @IntDef({STATE_COLLAPSED,
            STATE_COLLAPSED_DIMMED,
            STATE_FULL_EXPANDED})
    private @interface HvacPanelState {}
    private @HvacPanelState int mCurrentState;
    private int mPanelCollapsedHeight;
    private int mPanelFullExpandedHeight;
    private View mPanel;
    private View mContainer;
    private TemperatureBarOverlay mDriverTemperature;
    private TemperatureBarOverlay mPassengerTemperature;
    private ViewGroup mDriverTemperatureTouchOverlay;
    private ViewGroup mPassengerTemperatureTouchOverlay;
    private SeatWarmerButton mDriverSeatWarmer;
    private SeatWarmerButton mPassengerSeatWarmer;
    private ToggleButton mAcButton;
    private ToggleButton mRecycleAirButton;
    private ToggleButton mFrontDefrosterButton;
    private ToggleButton mRearDefrosterButton;
    private Drawable mAutoOnDrawable;
    private Drawable mAutoOffDrawable;
    private ImageView mAutoButton;
    private HvacPanelRow mPanelTopRow;
    private HvacPanelRow mPanelBottomRow;
    private FanSpeedBar mFanSpeedBar;
    private FanDirectionButtons mFanDirectionButtons;
    private float mTopPanelMaxAlpha = 1.0f;
    private WindowManager mWindowManager;
    private HvacPanelStateTransition mTransition;
    private View mHvacFanControlBackground;
    private FanSpeedBarController mFanSpeedBarController;
    private FanDirectionButtonsController mFanDirectionButtonsController;
    private TemperatureController mTemperatureController;
    private SeatWarmerController mSeatWarmerController;
    private boolean mInAnimation;
    private boolean mHvacIsOn;
    // TODO: read from shared pref
    private boolean mAutoMode;
    public HvacPanelController(Context context, View container,
                               /*WindowManager windowManager,*/ TemperatureBarOverlay driverTemperature,
                               TemperatureBarOverlay passengerTemperature, ViewGroup driverTemperatureBarTouchOverlay,
                               ViewGroup passengerTemperatureBarTouchOverlay) {
        mCurrentState = STATE_COLLAPSED;
//        mWindowManager = windowManager;
        Resources res = context.getResources();
        mPanelCollapsedHeight = res.getDimensionPixelSize(R.dimen.car_hvac_panel_collapsed_height);
        mPanelFullExpandedHeight
                = res.getDimensionPixelSize(R.dimen.car_hvac_panel_full_expanded_height);
        mAutoOffDrawable = res.getDrawable(R.drawable.ic_auto_off);
        mAutoOnDrawable = res.getDrawable(R.drawable.ic_auto_on);
        mDriverTemperature = driverTemperature;
        mDriverTemperature.setBarOnClickListener(mPanelClickListener);
        mPassengerTemperature = passengerTemperature;
        mPassengerTemperature.setBarOnClickListener(mPanelClickListener);
        mDriverTemperature.setCloseButtonOnClickListener(mOpenCloseToggleListener);
        mPassengerTemperature.setCloseButtonOnClickListener(mOpenCloseToggleListener);
        // Initially the hvac panel is collapsed, disable touches on the overlay container.
//        disableTouchOnOverlay(mDriverTemperature);
//        disableTouchOnOverlay(mPassengerTemperature);
        mDriverTemperatureTouchOverlay = driverTemperatureBarTouchOverlay;
        mPassengerTemperatureTouchOverlay = passengerTemperatureBarTouchOverlay;
        mDriverTemperatureTouchOverlay.setOnClickListener(mOpenCloseToggleListener);
        mPassengerTemperatureTouchOverlay.setOnClickListener(mOpenCloseToggleListener);
        mContainer = container;
//        mContainer.setVisibility(View.INVISIBLE);
//        mContainer.setOnClickListener(mOpenCloseToggleListener);
        mPanel = mContainer.findViewById(R.id.hvac_center_panel);
        mHvacFanControlBackground = mPanel.findViewById(R.id.fan_control_bg);
        mPanel.setOnClickListener(mPanelClickListener);
        // Set up top row buttons
        mPanelTopRow = mContainer.findViewById(R.id.top_row);
        mAcButton = mPanelTopRow.findViewById(R.id.ac_button);
        mAcButton.setToggleIcons(res.getDrawable(R.drawable.ic_ac_on),
                res.getDrawable(R.drawable.ic_ac_off));
        mRecycleAirButton = mPanelTopRow.findViewById(R.id.recycle_air_button);
        mRecycleAirButton.setToggleIcons(res.getDrawable(R.drawable.ic_recycle_air_on),
                res.getDrawable(R.drawable.ic_recycle_air_off));
        // Setup bottom row buttons
        mPanelBottomRow = mContainer.findViewById(R.id.bottom_row);
        mAutoButton = mContainer.findViewById(R.id.auto_button);
        mAutoButton.setOnClickListener(mAutoButtonClickListener);
        mFrontDefrosterButton = mPanelBottomRow.findViewById(R.id.front_defroster);
        mRearDefrosterButton = mPanelBottomRow.findViewById(R.id.rear_defroster);
        mFrontDefrosterButton.setToggleIcons(res.getDrawable(R.drawable.ic_front_defroster_on),
                res.getDrawable(R.drawable.ic_front_defroster_off));
        mRearDefrosterButton.setToggleIcons(res.getDrawable(R.drawable.ic_rear_defroster_on),
                res.getDrawable(R.drawable.ic_rear_defroster_off));
        mFanSpeedBar = mContainer.findViewById(R.id.fan_speed_bar);
        mFanDirectionButtons = mContainer.findViewById(R.id.fan_direction_buttons);
        mDriverSeatWarmer = mContainer.findViewById(R.id.left_seat_heater);
        mPassengerSeatWarmer = mContainer.findViewById(R.id.right_seat_heater);
    }
    public void updateHvacController() {
        //TODO: handle disconnected HvacController.
//        mHvacController = controller;
        mFanSpeedBarController = new FanSpeedBarController(mFanSpeedBar);
        mFanDirectionButtonsController
                = new FanDirectionButtonsController(mFanDirectionButtons);
        mTemperatureController = new TemperatureController(mPassengerTemperature,
                mDriverTemperature);
        mSeatWarmerController = new SeatWarmerController(mPassengerSeatWarmer,
                mDriverSeatWarmer);
        // Toggle buttons do not need additional logic to map between hardware
        // and UI settings. Simply use a ToggleListener to handle clicks.
        mAcButton.setIsOn(false);
        mAcButton.setToggleListener(new ToggleButton.ToggleListener() {
            @Override
            public void onToggled(boolean isOn) {
//                mHvacController.setAcState(isOn);
            }
        });
        mFrontDefrosterButton.setIsOn(false);
        mFrontDefrosterButton.setToggleListener(new ToggleButton.ToggleListener() {
            @Override
            public void onToggled(boolean isOn) {
//                mHvacController.setFrontDefrosterState(isOn);
            }
        });
        mRearDefrosterButton.setIsOn(false);
        mRearDefrosterButton.setToggleListener(new ToggleButton.ToggleListener() {
            @Override
            public void onToggled(boolean isOn) {
//                mHvacController.setRearDefrosterState(isOn);
            }
        });
        mRecycleAirButton.setIsOn(false);
        mRecycleAirButton.setToggleListener(new ToggleButton.ToggleListener() {
            @Override
            public void onToggled(boolean isOn) {
//                mHvacController.setAirCirculation(isOn);
            }
        });
        setAutoMode(false);
//        mHvacController.registerCallback(mToggleButtonCallbacks);
//        mToggleButtonCallbacks.onHvacPowerChange(mHvacController.getHvacPowerState());
    }
    /*private HvacController.Callback mToggleButtonCallbacks
            = new HvacController.Callback() {
        @Override
        public void onAirCirculationChange(boolean isOn) {
            mRecycleAirButton.setIsOn(isOn);
        }
        @Override
        public void onFrontDefrosterChange(boolean isOn) {
            mFrontDefrosterButton.setIsOn(isOn);
        }
        @Override
        public void onRearDefrosterChange(boolean isOn) {
            mRearDefrosterButton.setIsOn(isOn);
        }
        @Override
        public void onAcStateChange(boolean isOn) {
            mAcButton.setIsOn(isOn);
        }
        @Override
        public void onAutoModeChange(boolean isOn) {
            mAutoMode = isOn;
            setAutoMode(mAutoMode);
        }
        @Override
        public void onHvacPowerChange(boolean isOn) {
            // When the HVAC Power is turned off, collapse the panel and fade the temperature
            // bars. Also disable expanding the panel until power is back on.
            mHvacIsOn = isOn;
            if (!mHvacIsOn && mCurrentState == STATE_FULL_EXPANDED) {
                transitionState(STATE_FULL_EXPANDED, STATE_COLLAPSED);
            }
            mDriverTemperature.setIsOn(mHvacIsOn);
            mPassengerTemperature.setIsOn(mHvacIsOn);
        }
    };*/
    /**
     * Take the listeners and animators from a {@link AnimatorSet} and merge them to the
     * input {@link Animator} and {@link android.animation.Animator.AnimatorListener} lists.
     */
    private void combineAnimationSet(List<Animator> animatorList,
                                     List<Animator.AnimatorListener> listenerList, AnimatorSet set) {
        ArrayList<Animator> list = set.getChildAnimations();
        if (list != null) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                animatorList.add(list.get(i));
            }
        }
        ArrayList<Animator.AnimatorListener> listeners = set.getListeners();
        if (listeners != null) {
            int size = listeners.size();
            for (int i = 0; i < size; i++) {
                listenerList.add(listeners.get(i));
            }
        }
    }
    /**
     * Play necessary animations between {@link HvacPanelState} transitions
     */
    /*private void transitionState(@HvacPanelState int startState, @HvacPanelState int endState) {
        if (startState == endState || mInAnimation) {
            return;
        }
        List<Animator> animationList = new ArrayList<>();
        List<Animator.AnimatorListener> listenerList = new ArrayList<>();
        ValueAnimator heightAnimator = getPanelHeightAnimator(startState, endState);
        mTransition = new HvacPanelStateTransition(startState, endState);
        ValueAnimator fanBgAlphaAnimator;
        switch (endState) {
            case STATE_COLLAPSED:
                // Transition to collapsed state:
                // 1. Collapse the temperature bars.
                // 2. Collapse the top and bottom panel, staggered with a different delay.
                // 3. Decrease height of the hvac center panel, but maintain container height.
                // 4. Fade the background of the fan controls seperately to create staggered effect.
                animationList.add(heightAnimator);
                heightAnimator.setDuration(PANEL_COLLAPSE_ANIMATION_TIME_MS);
                fanBgAlphaAnimator
                        = ObjectAnimator.ofFloat(mHvacFanControlBackground, View.ALPHA, 1, 0)
                        .setDuration(PANEL_COLLAPSE_ANIMATION_TIME_MS);
                fanBgAlphaAnimator.setStartDelay(PANEL_ANIMATION_DELAY_MS);
                animationList.add(fanBgAlphaAnimator);
                ValueAnimator panelAlphaAnimator
                        = ObjectAnimator.ofFloat(mContainer, View.ALPHA, 1, 0);
                panelAlphaAnimator.setDuration(200);
                panelAlphaAnimator.setStartDelay(300);
                animationList.add(panelAlphaAnimator);
                combineAnimationSet(animationList, listenerList,
                        mDriverTemperature.getCollapseAnimations());
                combineAnimationSet(animationList, listenerList,
                        mPassengerTemperature.getCollapseAnimations());
                combineAnimationSet(animationList, listenerList,
                        mPanelTopRow.getCollapseAnimation(PANEL_ANIMATION_DELAY_MS,
                                mTopPanelMaxAlpha));
                combineAnimationSet(animationList, listenerList,
                        mPanelBottomRow.getCollapseAnimation(PANEL_ANIMATION_DELAY_MS,
                                mTopPanelMaxAlpha));
                break;
            case STATE_COLLAPSED_DIMMED:
                // Hide the temperature numbers, open arrows and auto state button.
                // TODO: determine if this section is still needed.
                break;
            case STATE_FULL_EXPANDED:
                // Transition to expaneded state:
                // 1. Expand the temperature bars.
                // 2. Expand the top and bottom panel, staggered with a different delay.
                // 3. Increase height of the hvac center panel, but maintain container height.
                // 4. Fade in fan control background in a staggered manner.
                fanBgAlphaAnimator
                        = ObjectAnimator.ofFloat(mHvacFanControlBackground, View.ALPHA, 0, 1)
                        .setDuration(PANEL_ANIMATION_TIME_MS);
                fanBgAlphaAnimator.setStartDelay(PANEL_ANIMATION_DELAY_MS);
                animationList.add(fanBgAlphaAnimator);
                animationList.add(heightAnimator);
                combineAnimationSet(animationList, listenerList,
                        mDriverTemperature.getExpandAnimatons());
                combineAnimationSet(animationList, listenerList,
                        mPassengerTemperature.getExpandAnimatons());
                // During expansion, the bottom panel animation should be delayed
                combineAnimationSet(animationList, listenerList,
                        mPanelTopRow.getExpandAnimation(PANEL_ANIMATION_DELAY_MS,
                                mTopPanelMaxAlpha));
                combineAnimationSet(animationList, listenerList,
                        mPanelBottomRow.getExpandAnimation(PANEL_ANIMATION_LONG_DELAY_MS, 1f));
                break;
            default:
        }
        // If there are animations for the state change, play them all together and ensure
        // the animation listeners are attached.
        if (animationList.size() > 0) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animationList);
            for (Animator.AnimatorListener listener : listenerList) {
                animatorSet.addListener(listener);
            }
            animatorSet.addListener(mAnimatorListener);
            animatorSet.start();
        }
    }*/
    private AnimatorSet.AnimatorListener mAnimatorListener = new AnimatorSet.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            mTransition.onTransitionStart();
        }
        @Override
        public void onAnimationEnd(Animator animation) {
            mTransition.onTransitionComplete();
        }
        @Override
        public void onAnimationCancel(Animator animation) {}
        @Override
        public void onAnimationRepeat(Animator animation) {}
    };
    private ValueAnimator getPanelHeightAnimator(@HvacPanelState int startState,
                                                 @HvacPanelState int endState) {
        int startHeight = getStateHeight(startState);
        int endHeight = getStateHeight(endState);
        if (startHeight == endHeight) {
            return null;
        }
        ValueAnimator heightAnimator = new ValueAnimator().ofInt(startHeight, endHeight)
                .setDuration(PANEL_ANIMATION_TIME_MS);
        heightAnimator.addUpdateListener(mHeightUpdateListener);
        return heightAnimator;
    }
    private int getStateHeight(@HvacPanelState int state) {
        switch (state) {
            case STATE_COLLAPSED:
            case STATE_COLLAPSED_DIMMED:
                return mPanelCollapsedHeight;
            case STATE_FULL_EXPANDED:
                return mPanelFullExpandedHeight;
            default:
                throw new IllegalArgumentException("No height mapped to HVAC State: " + state);
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setAutoMode(boolean isOn) {
        if (isOn) {
            mPanelTopRow.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            mAutoMode = true;
            mPanelTopRow.disablePanel(true);
            mTopPanelMaxAlpha = DISABLED_BUTTON_ALPHA;
            mAutoButton.setImageDrawable(mAutoOnDrawable);
        } else {
            mPanelTopRow.disablePanel(false);
            mTopPanelMaxAlpha = ENABLED_BUTTON_ALPHA;
            mAutoButton.setImageDrawable(mAutoOffDrawable);
        }
        mHvacFanControlBackground.setAlpha(mTopPanelMaxAlpha);
        mPanelTopRow.setAlpha(mTopPanelMaxAlpha);
    }
    private View.OnClickListener mPanelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mHvacIsOn) {
                return;
            }
            switch (mCurrentState) {
                case STATE_COLLAPSED:
//                    transitionState(STATE_COLLAPSED, STATE_FULL_EXPANDED);
                    break;
                case STATE_COLLAPSED_DIMMED:
//                    transitionState(mCurrentState, STATE_COLLAPSED);
                    break;
                default:
            }
        }
    };
    private View.OnClickListener mAutoButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mAutoMode = !mAutoMode;
//            mHvacController.setAutoMode(mAutoMode);
            setAutoMode(mAutoMode);
        }
    };
    private View.OnClickListener mOpenCloseToggleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mHvacIsOn) {
                return;
            }
            if (mCurrentState == STATE_FULL_EXPANDED) {
//                transitionState(mCurrentState, STATE_COLLAPSED);
            } else if (mCurrentState == STATE_COLLAPSED) {
//                transitionState(mCurrentState, STATE_FULL_EXPANDED);
            }
        }
    };
    private ValueAnimator.AnimatorUpdateListener mHeightUpdateListener
            = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int height = (Integer) animation.getAnimatedValue();
            int currentHeight = mPanel.getLayoutParams().height;
            mPanel.getLayoutParams().height = height;
            mPanel.setTop(mPanel.getTop() + height - currentHeight);
            mPanel.requestLayout();
        }
    };
    /**
     * Handles the necessary setup/clean up before and after a state transition.
     */
    private class HvacPanelStateTransition {
        private @HvacPanelState int mEndState;
        private @HvacPanelState int mStartState;
        public HvacPanelStateTransition(@HvacPanelState int startState,
                                        @HvacPanelState int endState) {
            mStartState = startState;
            mEndState = endState;
        }
        public void onTransitionStart() {
            mInAnimation = true;
            if (mEndState == STATE_FULL_EXPANDED) {
                mContainer.setAlpha(1);
                mContainer.setVisibility(View.VISIBLE);
            } else if (mStartState == STATE_FULL_EXPANDED) {
                // Finished transitioning out of the fully expanded panel. Make the HVAC container
                // and temperature bars not touchable so clicks on the screen bar are not intercepted.
//                disableTouchOnOverlay(mContainer);
//                disableTouchOnOverlay(mDriverTemperature);
//                disableTouchOnOverlay(mPassengerTemperature);
            }
        }
        public void onTransitionComplete() {
            if (mStartState == STATE_FULL_EXPANDED) {
                mContainer.setVisibility(View.INVISIBLE);
//                enableTouchOnOverlay(mDriverTemperatureTouchOverlay);
//                enableTouchOnOverlay(mPassengerTemperatureTouchOverlay);
            } else if (mEndState == STATE_FULL_EXPANDED) {
                // Finished transitioning into the fully expanded HVAC panel, make the
                // container and temperature bars touchable since it covers the screen.
//                enableTouchOnOverlay(mContainer);
//                enableTouchOnOverlay(mDriverTemperature);
//                enableTouchOnOverlay(mPassengerTemperature);
//                disableTouchOnOverlay(mDriverTemperatureTouchOverlay);
//                disableTouchOnOverlay(mPassengerTemperatureTouchOverlay);
            }
            // set new states
            mCurrentState = mEndState;
            mInAnimation = false;
        }
    }
    /*private void disableTouchOnOverlay(View view) {
        WindowManager.LayoutParams params
                = (WindowManager.LayoutParams) view.getLayoutParams();
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                & ~WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        mWindowManager.updateViewLayout(view, params);
    }
    private void enableTouchOnOverlay(View view) {
        WindowManager.LayoutParams params
                = (WindowManager.LayoutParams) view.getLayoutParams();
        params.flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        & ~WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        mWindowManager.updateViewLayout(view, params);
    }*/
}