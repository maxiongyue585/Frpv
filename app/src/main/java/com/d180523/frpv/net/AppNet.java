package com.d180523.frpv.net;

import android.util.Log;

import com.d180523.frpv.api.ApiServer;
import com.d180523.frpv.net.bean.FrpcProfile;
import com.d180523.frpv.net.bean.LoginResponse;

import okhttp3.ResponseBody;
import retrofit2.Response;


public class AppNet {

    public static final String TAG = AppNet.class.getSimpleName();

    public static LoginResponse login(String username, String password) {

        ApiServer us = NetUtils.retrofit.create(ApiServer.class);

        try {
            Response<LoginResponse> r = us.login(username, password).execute();

            LoginResponse lr = r.body();

            if (lr.getMsg().isSuccess()) {

                lr = r.body();
                Log.d(TAG, "login: " + lr.toString());
            } else {

                Log.d(TAG, "login: fail");
            }

            return lr;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
