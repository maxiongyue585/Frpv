package com.d180523.frpv.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.d180523.frpv.activity.IniDetailsActivity;
import com.d180523.frpv.utils.ToastUtil;

import static com.d180523.frpv.common.Const.FILE.INI_FILENAME;


public class FrpcService extends Service {

    private static final String TAG = FrpcService.class.getSimpleName();

    private FrpcThread sshd;

    private String iniFileName;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            return super.onStartCommand(intent, Service.START_FLAG_REDELIVERY, startId);
        }
        iniFileName = intent.getStringExtra(INI_FILENAME);


        Log.d(TAG, "onStartCommand: ");
        if (sshd == null) {

            sshd = new FrpcThread(this, iniFileName, new PrintCallback() {
                @Override
                public void print(String line) {
                    senMsg(line);
                }
            });
            sshd.start();
        } else {
            ToastUtil.showToast(this, "frpc已启动", 0);
            Log.d(TAG, "onStartCommand: frpc already started");
        }

        return super.onStartCommand(intent, Service.START_FLAG_REDELIVERY, startId);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (sshd != null && sshd.isAlive()) {

            try {
                sshd.terminate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onDestroy: 关闭线程");
        }
    }

    public interface PrintCallback {
        void print(String line);
    }

    /**
     * 通过广播发送启动信息
     *
     * @param line
     */
    private void senMsg(String line) {
        Intent intent = new Intent();
        intent.setAction(IniDetailsActivity.SERVICE_ACION);
        intent.putExtra(IniDetailsActivity.BR_LOG_INI, line);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
