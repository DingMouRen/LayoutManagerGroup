package com.dingmouren.example.layoutmanagergroup;

import android.app.Application;
import android.content.Context;

/**
 * Created by dingmouren on 2018/4/30.
 */

public class MyApplication extends Application{
    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }
}
