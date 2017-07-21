package com.yezhu.lifebox.net;

/**
 * Created by Aven on 2017/7/13.
 */

public class ApkInfo {

    private Integer id;
    private String apk_path;
    private String apk_pic;
    private String apk_info;
    private String apk_name;

    public ApkInfo(){

    }

    public ApkInfo(String apk_path,String apk_pic,String apk_info,String apk_name){
        this.apk_path = apk_path;
        this.apk_pic = apk_pic;
        this.apk_name = apk_name;
        this.apk_info = apk_info;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApk_path() {
        return apk_path;
    }

    public void setApk_path(String apk_path) {
        this.apk_path = apk_path;
    }

    public String getApk_pic() {
        return apk_pic;
    }

    public void setApk_pic(String apk_pic) {
        this.apk_pic = apk_pic;
    }

    public String getApk_info() {
        return apk_info;
    }

    public void setApk_info(String apk_info) {
        this.apk_info = apk_info;
    }

    public String getApk_name() {
        return apk_name;
    }

    public void setApk_name(String apk_name) {
        this.apk_name = apk_name;
    }

}
