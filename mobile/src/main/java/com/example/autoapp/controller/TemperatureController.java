package com.example.autoapp.controller;


import com.example.autoapp.customclass.TemperatureBarOverlay;

/**
 * A controller that handles temperature updates for the driver and passenger.
 */
public class TemperatureController {
    private final TemperatureBarOverlay mDriverTempBar;
    private final TemperatureBarOverlay mPassengerTempBar;
//    private final HvacController mHvacController;
    public TemperatureController(TemperatureBarOverlay passengerTemperatureBar,
                                 TemperatureBarOverlay driverTemperatureBar) {
        mDriverTempBar = driverTemperatureBar;
        mPassengerTempBar = passengerTemperatureBar;
//        mHvacController = controller;
//        mHvacController.registerCallback(mCallback);
        mDriverTempBar.setTemperatureChangeListener(mDriverTempClickListener);
        mPassengerTempBar.setTemperatureChangeListener(mPassengerTempClickListener);
        mDriverTempBar.setTemperature(50);
        mPassengerTempBar.setTemperature(50);
    }
//    private final HvacController.Callback mCallback = new HvacController.Callback() {
//        @Override
//        public void onPassengerTemperatureChange(float temp) {
//            mPassengerTempBar.setTemperature((int) temp);
//        }
//        @Override
//        public void onDriverTemperatureChange(float temp) {
//            mDriverTempBar.setTemperature((int) temp);
//        }
//    };
    private final TemperatureBarOverlay.TemperatureAdjustClickListener mPassengerTempClickListener =
            new TemperatureBarOverlay.TemperatureAdjustClickListener() {
                @Override
                public void onTemperatureChanged(int temperature) {
//                    mHvacController.setPassengerTemperature(temperature);
                }
            };
    private final TemperatureBarOverlay.TemperatureAdjustClickListener mDriverTempClickListener =
            new TemperatureBarOverlay.TemperatureAdjustClickListener() {
                @Override
                public void onTemperatureChanged(int temperature) {
//                    mHvacController.setDriverTemperature(temperature);
                }
            };
}