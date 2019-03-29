package com.example.autoapp.controller;

import android.util.SparseIntArray;

import com.example.autoapp.customclass.FanDirectionButtons;


public class FanDirectionButtonsController {
    private final static int FAN_DIRECTION_COUNT = 4;
    private final FanDirectionButtons mFanDirectionButtons;
    private SparseIntArray mFanDirectionMap = new SparseIntArray(FAN_DIRECTION_COUNT);
    public FanDirectionButtonsController(FanDirectionButtons speedBar) {
        mFanDirectionButtons = speedBar;
        initialize();
    }
    private void initialize() {
        // Note Car specific values are being used here, as not all cars have the floor
        // and defroster fan direction.
       /* mFanDirectionMap.put(FanDirectionButtons.FAN_DIRECTION_FACE,
                VehicleHvacFanDirection.VEHICLE_HVAC_FAN_DIRECTION_FACE);
        mFanDirectionMap.put(FanDirectionButtons.FAN_DIRECTION_FACE_FLOOR,
                VehicleHvacFanDirection.VEHICLE_HVAC_FAN_DIRECTION_FACE_AND_FLOOR);
        mFanDirectionMap.put(FanDirectionButtons.FAN_DIRECTION_FLOOR,
                VehicleHvacFanDirection.VEHICLE_HVAC_FAN_DIRECTION_FLOOR);
        mFanDirectionMap.put(FanDirectionButtons.FAN_DIRECTION_FLOOR_DEFROSTER,
                VehicleHvacFanDirection.VEHICLE_HVAC_FAN_DIRECTION_DEFROST_AND_FLOOR);*/
        mFanDirectionButtons.setFanDirectionClickListener(mListener);
    }
    private final FanDirectionButtons.FanDirectionClickListener mListener
            = new FanDirectionButtons.FanDirectionClickListener() {
        @Override
        public void onFanDirectionClicked(@FanDirectionButtons.FanDirection int direction) {
//            mHvacController.setFanDirection(mFanDirectionMap.get(direction));
        }
    };
}