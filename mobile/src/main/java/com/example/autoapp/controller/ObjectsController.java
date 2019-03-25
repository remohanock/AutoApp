package com.example.autoapp.controller;

import com.example.autoapp.models.Apps;
import com.example.autoapp.models.AppsList;
import com.example.autoapp.models.Driver;
import com.example.autoapp.models.DriverList;

public class ObjectsController {

    private DriverList driverList = new DriverList();
    private AppsList appsList = new AppsList();


    /***
     *
     * @param position the index of the driver to be obtained
     * @return the driver object for the position in the list
     */
    public Driver getDriver(int position) {
        return driverList.getDriver(position);
    }

    /***
     *
     * @param driver The object of the driver to set
     */
    public void setDriver(Driver driver) {
        driverList.setDriver(driver);
    }

    /***
     *
     * @return the list drivers
     */
    public DriverList getDriverList() {
        return driverList;
    }

    /***
     *
     * @return the total number of drivers in the list
     */
    public int getDriversCount() {
        return driverList.getDriversCount();
    }


    /***
     *
     * @param position the index of the app in the list
     * @return the object of App at the position in the list
     */
    public Apps getApps(int position) {
        return appsList.getAppsArrayList().get(position);
    }

    /***
     *
     * @param apps the object of Apps to be set in the list
     */
    public void setApps(Apps apps) {
        appsList.setApp(apps);
    }

    /***
     *
     * @return the list of object of Apps
     */
    public AppsList getAppsList() {
        return appsList;
    }

    /***
     *
     * @return the total number of apps in the list
     */
    public int getAppsCount() {
        return appsList.getAppsCount();
    }


}
