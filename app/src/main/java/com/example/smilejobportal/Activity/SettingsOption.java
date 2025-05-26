package com.example.smilejobportal.Activity;

public class SettingsOption {
    private String title;
    private String icon; // Store icon name or Firebase Storage URL
    private String key;

    public SettingsOption() {
        // Default constructor required for Firebase
    }

    public SettingsOption(String title, String icon, String key) {
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
}

