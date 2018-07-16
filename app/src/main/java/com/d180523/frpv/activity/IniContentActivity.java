package com.d180523.frpv.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

import com.d180523.frpv.R;
import com.d180523.frpv.utils.AppUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import static com.d180523.frpv.common.Const.FILE.INI_FILENAME_PARMS;

@EActivity(R.layout.activity_ini_content)
public class IniContentActivity extends AppCompatActivity {

    @ViewById
    TextView tv_content;

    private String iniContentStr;

    private String INI_PATH = null;

    @AfterViews
    void init() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("文件内容");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        
        iniContentStr = getIntent().getStringExtra(INI_FILENAME_PARMS);
        if (!TextUtils.isEmpty(iniContentStr)) {
            tv_content.setText(iniContentStr);

            INI_PATH = getFilesDir() + File.separator + "ini" + File.separator + iniContentStr;

            File ini = new File(INI_PATH);
            if (ini != null && ini.exists()) {
                tv_content.setText(AppUtils.showInfo(ini));
            } else {
                tv_content.setText("文件不存在");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
