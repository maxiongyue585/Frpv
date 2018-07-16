package com.d180523.frpv.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.d180523.frpv.R;
import com.d180523.frpv.service.FrpcService;
import com.d180523.frpv.utils.AppUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import static com.d180523.frpv.common.Const.FILE.INI_FILENAME;
import static com.d180523.frpv.common.Const.FILE.INI_FILENAME_PARMS;

@EActivity(R.layout.activity_ini_details)
public class IniDetailsActivity extends AppCompatActivity {

    private static final String TAG = "IniDetailsActivity";

    @ViewById
    TextView tv_start_log;

    @ViewById
    Button btn_start_or_stop;

    private String iniFileName;

    public static final String BR_LOG_INI = "br_log_msg";

    public static final String SERVICE_ACION = "com.d180523.frpv.service.FrpcService";

    private StringBuilder mLogStr = new StringBuilder();

    private MyReceiver myReceiver;

    private Intent mFrpcServiceIntent;

    @AfterViews
    void init() {
        iniFileName = this.getIntent().getStringExtra(INI_FILENAME_PARMS);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Enable自定义的View
            actionBar.setCustomView(R.layout.layout_ini_file_title);
            TextView tv_title = actionBar.getCustomView().findViewById(R.id.tv_title);
            tv_title.setText(iniFileName);
            TextView tv_log = actionBar.getCustomView().findViewById(R.id.tv_content);
            tv_log.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(IniDetailsActivity.this, IniContentActivity_.class).putExtra(INI_FILENAME_PARMS, iniFileName));
                }
            });

            ImageButton ib_back = actionBar.getCustomView().findViewById(R.id.ib_back);
            ib_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        if (AppUtils.isServiceRunning(this, "com.d180523.frpv.service.FrpcService")) {
            tv_start_log.setText("该服务已启动");
            btn_start_or_stop.setText(R.string.btn_stop_service);
        } else {
            btn_start_or_stop.setText(R.string.btn_start_service);
        }

        if (myReceiver == null) {
            myReceiver = new MyReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(SERVICE_ACION);
            LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, filter);
        }

        if (mFrpcServiceIntent == null) {
            mFrpcServiceIntent = new Intent(this, FrpcService.class);
        }

    }

    @Click
    void btn_start_or_stop(View view) {

        if (AppUtils.isServiceRunning(this, "com.d180523.frpv.service.FrpcService")) {
            if (mFrpcServiceIntent != null) {
                stopService(mFrpcServiceIntent);
            }
            if (myReceiver != null) {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
            }
            btn_start_or_stop.setText(R.string.btn_start_service);
        } else {
            if (mFrpcServiceIntent == null) {
                mFrpcServiceIntent = new Intent(this, FrpcService.class);
            }
            mFrpcServiceIntent.putExtra(INI_FILENAME, iniFileName);
            this.startService(mFrpcServiceIntent);

            if (myReceiver == null) {
                myReceiver = new MyReceiver();
            }
            IntentFilter filter = new IntentFilter();
            filter.addAction(SERVICE_ACION);
            LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, filter);
            btn_start_or_stop.setText(R.string.btn_stop_service);
        }


    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            mLogStr.append(bundle.getString(BR_LOG_INI));
            mLogStr.append("\n");
            tv_start_log.setText(mLogStr.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (myReceiver != null) {
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
//        }
//        if (mFrpcServiceIntent != null) {
//            stopService(mFrpcServiceIntent);
//        }
        Log.d(TAG, "onDestroy: ");
    }
}
