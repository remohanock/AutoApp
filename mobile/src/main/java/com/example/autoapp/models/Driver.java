package com.example.autoapp.models;

public class Driver {

    private String driverName;
    private String driverPhoto;

    public Driver(String driverName, String driverPhoto) {
        this.driverName = driverName;
        this.driverPhoto = driverPhoto;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhoto() {
        return driverPhoto;
    }

    public void setDriverPhoto(String driverPhoto) {
        this.driverPhoto = driverPhoto;
    }
}
