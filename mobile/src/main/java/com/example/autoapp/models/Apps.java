package com.example.autoapp.models;

public class Apps {
    private int id;
    private String appName;

    public Apps(int id, String appName) {
        this.id = id;
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
