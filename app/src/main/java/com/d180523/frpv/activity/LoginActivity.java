package com.d180523.frpv.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.d180523.frpv.AppContext;
import com.d180523.frpv.MainActivity_;
import com.d180523.frpv.R;
import com.d180523.frpv.net.AppNet;
import com.d180523.frpv.net.bean.LoginResponse;
import com.d180523.frpv.utils.SPUtils;
import com.d180523.frpv.utils.StringUtils;
import com.d180523.frpv.utils.ToastUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;


@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final String TAG = LoginActivity.class.getSimpleName();

    @ViewById
    EditText et_username;

    @ViewById
    EditText et_password;

    @ViewById
    Button btn_login;

    private ProgressDialog pb;

    /**
     * 需要进行检测的权限数组
     */
    protected String[] mNeedPermissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    private static final int PERMISSION_REQUESTCODE = 0;//权限检测请求码
    protected static final int APPLICATION_PERMISSIONS_REQUESTCODE = 2;//应用权限列表返回码

    @Click
    void btn_login(View view) {

        doLogin();
    }

    @AfterViews
    void initListener() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions(mNeedPermissions);
        }

        if (AppContext.getInstance().spc.getProperty(SPUtils.TOKEN) != null) {
            MainActivity_.intent(this).start();
            finish();
        }
    }

    @Background
    void doLogin() {

        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();

//        username = "admin";
//        password = "admin";


        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {

            loginIni();

            LoginResponse lr = AppNet.login(username, password);
            loginResult(lr);

        } else {
            nullTint();
        }
    }

    @UiThread
    void loginResult(LoginResponse lr) {

        if (lr != null && lr.getMsg().isSuccess()) {

            pb.dismiss();
            MainActivity_.intent(this).start();
            finish();
        } else {
            pb.dismiss();
            ToastUtil.showToast(this, lr.getMsg().getDesc(), 0);
        }
    }

    @UiThread
    void nullTint() {
        ToastUtil.showToast(this, "输入内容不能为空", 0);
    }

    @UiThread
    void loginIni() {
        pb = new ProgressDialog(this);
        pb.setMessage("登录中...");
        pb.show();
    }

    /**
     * 需要权限检查
     *
     * @param permissions
     */
    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(
                            new String[needRequestPermissonList.size()]),
                    PERMISSION_REQUESTCODE);
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUESTCODE) {
            if (!verifyPermissions(grantResults)) {
                new AlertDialog.Builder(this).setTitle("系统提示").setMessage("未取得相应权限，此功能无法使用。请前往应用权限设置打开权限。").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).setCancelable(false).create().show();
            }
        }
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, APPLICATION_PERMISSIONS_REQUESTCODE);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APPLICATION_PERMISSIONS_REQUESTCODE) {// 来自权限列表界面

        }
    }

}

