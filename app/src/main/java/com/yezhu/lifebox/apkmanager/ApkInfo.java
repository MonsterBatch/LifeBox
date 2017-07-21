package com.yezhu.lifebox.apkmanager;

import android.graphics.drawable.Drawable;

/**
 * Created by yuyifei on 17-7-6.
 */

class ApkInfo {
    private Drawable mIcon;
    private String mPackageName;
    private CharSequence mAppName;
    private String mApkPath;


    public ApkInfo(CharSequence appName, String packageName, Drawable icon, String apkPath) {
        this.mAppName = appName;
        this.mPackageName = packageName;
        this.mIcon = icon;
        this.mApkPath = apkPath;
    }

    public Drawable getmIcon() {
        return mIcon;
    }

    public String getmPackageName() {
        return mPackageName;
    }

    public CharSequence getmAppName() {
        return mAppName;
    }

    public String getApkPath() {
        return mApkPath;
    }
}
