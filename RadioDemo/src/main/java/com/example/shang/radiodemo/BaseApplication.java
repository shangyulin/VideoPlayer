package com.example.shang.radiodemo;

import android.app.Application;

import io.vov.vitamio.Vitamio;

/**
 * Created by Shang on 2017/10/12.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Vitamio.initialize(this);
    }
}
