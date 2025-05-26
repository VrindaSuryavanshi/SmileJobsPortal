package com.example.smilejobportal.Model;

public class SettingModel {
    private String title;
    private String icon;

    public SettingModel() {} // Default constructor

    public SettingModel(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getIcon() {
        return icon;
    }
}
