package com.example.smilejobportal.Model;

public class SettingModel {
    private String title;
    private String icon;
    private String key;
    public SettingModel() {} // Default constructor

    public SettingModel(String title, String icon,String key) {
        this.title = title;
        this.icon = icon;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public String getIcon() {
        return icon;
    }
    public String getKey() {
        return key;
    }
    public void setkey(String key) {
        this.key = key;
    }
}
