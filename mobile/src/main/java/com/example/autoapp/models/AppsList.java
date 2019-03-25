package com.example.autoapp.models;

import java.util.ArrayList;

public class AppsList {

    private ArrayList<Apps> appsArrayList = new ArrayList<>();

    /***
     *
     * @return the array list of Apps objects
     */
    public ArrayList<Apps> getAppsArrayList() {
        return appsArrayList;
    }

    /***
     *
     * @param appsArrayList the array list of App objects to be set
     */
    public void setAppsArrayList(ArrayList<Apps> appsArrayList) {
        this.appsArrayList = appsArrayList;
    }

    /***
     *
     * @param app the object of Apps to be added to the list
     */
    public void setApp(Apps app) {
        appsArrayList.add(app);
    }

    /***
     *
     * @param position the index of app to be obtained in the list
     * @return the object of App in the index of position in the list
     */
    public Apps getApp(int position) {
        return appsArrayList.get(position);
    }

    /***
     *
     * @return the total number of apps in the list
     */
    public int getAppsCount() {
        return appsArrayList.size();
    }
}
