package com.example.shang.radiodemo;

/**
 * Created by Shang on 2017/10/13.
 */
public class Video {
    private String iconUrl;
    private String url;
    private String name;

    public Video(String iconUrl, String url, String name) {
        this.iconUrl = iconUrl;
        this.url = url;
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
