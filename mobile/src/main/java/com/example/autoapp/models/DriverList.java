package com.example.autoapp.models;

import java.util.ArrayList;

public class DriverList {

    private ArrayList<Driver> driverArrayList = new ArrayList<>();

    /***
     *
     * @return the list of Driver objects
     */
    public ArrayList<Driver> getDriverArrayList() {
        return driverArrayList;
    }

    /***
     *
     * @param driverArrayList the list of Driver objects to be set in the list
     */
    public void setDriverArrayList(ArrayList<Driver> driverArrayList) {
        this.driverArrayList = driverArrayList;
    }

    /***
     *
     * @param driver the object of driver to be added to the list
     */
    public void setDriver(Driver driver) {
        driverArrayList.add(driver);
    }

    /***
     *
     * @param position the index of Driver object to be obtained in the list
     * @return the object of Driver to be obtained
     */
    public Driver getDriver(int position) {
        return driverArrayList.get(position);
    }

    /***
     *
     * @return the total number of drivers in the list
     */
    public int getDriversCount() {
        return driverArrayList.size();
    }
}

