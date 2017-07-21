package com.yezhu.lifebox;

import android.graphics.drawable.Drawable;

/**
 * Created by yuyifei on 17-7-6.
 */

public class AppInfo {

    private Drawable mIcon;
    private String mPackageName;
    private CharSequence mAppName;


    public AppInfo(CharSequence appName, String packageName, Drawable icon) {
        this.mAppName = appName;
        this.mPackageName = packageName;
        this.mIcon = icon;
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

}
