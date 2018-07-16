package com.d180523.frpv;

import android.app.Application;

import com.d180523.frpv.utils.SPUtils;

import java.util.HashMap;
import java.util.Map;


public class AppContext extends Application {

    private static AppContext instance;

    public static Map<String, String> session_keys = new HashMap<String, String>();

    public SPUtils spc;

    @Override
    public void onCreate() {

        super.onCreate();
        instance = this;
        spc = SPUtils.getInstance(this);
    }

    public static AppContext getInstance() {
        return instance;
    }
}
