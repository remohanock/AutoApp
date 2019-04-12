package com.example.autoapp.models;

import android.graphics.drawable.Drawable;

public class InstalledApps {
    private String InstalledAppName;
    private String appPackageName;
    private String appSourceDir;
    private Drawable drawable;

    public InstalledApps(String InstalledAppName, String appPackageName, String appSourceDir, Drawable drawable) {
        this.InstalledAppName = InstalledAppName;
        this.appPackageName = appPackageName;
        this.appSourceDir = appSourceDir;
        this.drawable = drawable;
    }

    public String getInstalledAppName() {
        return InstalledAppName;
    }

    public void setInstalledAppName(String installedAppName) {
        this.InstalledAppName = installedAppName;
    }

    public String getAppSourceDir() {
        return appSourceDir;
    }

    public void setAppSourceDir(String appSourceDir) {
        this.appSourceDir = appSourceDir;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
