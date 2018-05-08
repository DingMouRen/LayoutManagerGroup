package com.dingmouren.example.layoutmanagergroup;

import android.app.Application;
import android.content.Context;


/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */

public class MyApplication extends Application{
    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }
}
