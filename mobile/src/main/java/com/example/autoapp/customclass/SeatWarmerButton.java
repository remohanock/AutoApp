package com.example.autoapp.customclass;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;

import com.example.autoapp.R;

/**
 * A seat warmer button that has 3 heating levels.
 */
public class SeatWarmerButton extends android.support.v7.widget.AppCompatImageView {
    public static final int HEAT_OFF = 0;
    public static final int HEAT_LEVEL_ONE = 1;
    public static final int HEAT_LEVEL_TWO = 2;
    public static final int HEAT_LEVEL_THREE = 3;
    public interface SeatWarmerButtonClickListener{
        void onSeatWarmerButtonClicked(@HeatingLevel int level);
    }
    @IntDef({HEAT_OFF, HEAT_LEVEL_ONE, HEAT_LEVEL_TWO, HEAT_LEVEL_THREE})
    public @interface HeatingLevel {}
    private @HeatingLevel int mCurrentHeatSetting;
    private final Drawable mStateDrawables[] = new Drawable[4];
    private SeatWarmerButtonClickListener mListener;
    public SeatWarmerButton(Context context) {
        super(context);
        init();
    }
    public SeatWarmerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public SeatWarmerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        Resources res = getResources();
        mStateDrawables[0] = res.getDrawable(R.drawable.ic_seat_heat_off);
        mStateDrawables[1] = res.getDrawable(R.drawable.ic_seat_heat_level_1);
        mStateDrawables[2] = res.getDrawable(R.drawable.ic_seat_heat_level_2);
        mStateDrawables[3] = res.getDrawable(R.drawable.ic_seat_heat_level_3);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentHeatSetting = getNextHeatSetting(mCurrentHeatSetting);
                Drawable d = getDrawableForState(mCurrentHeatSetting);
                setImageDrawable(d);
                if (mListener != null) {
                    mListener.onSeatWarmerButtonClicked(mCurrentHeatSetting);
                }
            }
        });
    }
    public void setSeatWarmerClickListener(SeatWarmerButtonClickListener listener) {
        mListener = listener;
    }
    public void setHeatLevel(@HeatingLevel int level) {
        mCurrentHeatSetting = level;
        setImageDrawable(getDrawableForState(mCurrentHeatSetting));
    }
    private Drawable getDrawableForState(@HeatingLevel int level) {
        switch (level) {
            case HEAT_LEVEL_ONE:
                return mStateDrawables[1];
            case HEAT_LEVEL_TWO:
                return mStateDrawables[2];
            case HEAT_LEVEL_THREE:
                return mStateDrawables[3];
            case HEAT_OFF:
            default:
                return mStateDrawables[0];
        }
    }
    private @HeatingLevel int getNextHeatSetting(@HeatingLevel int level) {
        switch (level) {
            case HEAT_OFF:
                return HEAT_LEVEL_ONE;
            case HEAT_LEVEL_ONE:
                return HEAT_LEVEL_TWO;
            case HEAT_LEVEL_TWO:
                return HEAT_LEVEL_THREE;
            case HEAT_LEVEL_THREE:
                return HEAT_OFF;
            default:
                return HEAT_OFF;
        }
    }
}