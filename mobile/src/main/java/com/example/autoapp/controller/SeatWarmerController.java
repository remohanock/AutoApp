package com.example.autoapp.controller;


import com.example.autoapp.customclass.SeatWarmerButton;

/**
 * A controller to handle changes in the heated seat levels.
 */
public class SeatWarmerController {
    private final SeatWarmerButton mPassengerSeatButton;
    private final SeatWarmerButton mDriverSeatButton;
    public SeatWarmerController(SeatWarmerButton passengerSeatButton,
                                SeatWarmerButton driverSeatButton) {
        mDriverSeatButton = driverSeatButton;
        mPassengerSeatButton = passengerSeatButton;
//        mHvacController = hvacController;
//        mHvacController.registerCallback(mCallback);
        mPassengerSeatButton.setSeatWarmerClickListener(mPassengerSeatListener);
        mDriverSeatButton.setSeatWarmerClickListener(mDriverSeatListener);
    }
    /*private final HvacController.Callback mCallback = new HvacController.Callback() {
        @Override
        public void onPassengerSeatWarmerChange(int level) {
            // If the value of the heating is less than HEAT_OFF, it means the seats are
            // being cooled, show heated seat button as off.
            if (level < SeatWarmerButton.HEAT_OFF) {
                mPassengerSeatButton.setHeatLevel(SeatWarmerButton.HEAT_OFF);
            } else {
                mPassengerSeatButton.setHeatLevel(level);
            }
        }
        @Override
        public void onDriverSeatWarmerChange(int level) {
            // If the value of the heating is less than HEAT_OFF, it means the seats are
            // being cooled, show heated seat button as off.
            if (level < SeatWarmerButton.HEAT_OFF) {
                mDriverSeatButton.setHeatLevel(SeatWarmerButton.HEAT_OFF);
            } else {
                mDriverSeatButton.setHeatLevel(level);
            }
        }
    };*/
    private final SeatWarmerButton.SeatWarmerButtonClickListener mPassengerSeatListener
            = new SeatWarmerButton.SeatWarmerButtonClickListener() {
        @Override
        public void onSeatWarmerButtonClicked(@SeatWarmerButton.HeatingLevel int level) {
//            mHvacController.setPassengerSeatWarmerLevel(level);
        }
    };
    private final SeatWarmerButton.SeatWarmerButtonClickListener mDriverSeatListener
            = new SeatWarmerButton.SeatWarmerButtonClickListener() {
        @Override
        public void onSeatWarmerButtonClicked(@SeatWarmerButton.HeatingLevel int level) {
//            mHvacController.setDriverSeatWarmerLevel(level);
        }
    };
}