package com.d180523.frpv;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.d180523.frpv.activity.LoginActivity_;
import com.d180523.frpv.fragment.FrpvFragment;
import com.d180523.frpv.utils.AppUtils;
import com.d180523.frpv.utils.SPUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import static com.d180523.frpv.common.Const.SP_KEY.IS_FRPC;


@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @ViewById(R.id.main_drawer_layout)
    DrawerLayout mDrawerLayout;

    @ViewById(R.id.layout_left_drawer)
    RelativeLayout mLLleft_drawer;

    //抽屉菜单对象
    ActionBarDrawerToggle mDrawerToggle;

    @ViewById(R.id.tv_online)
    TextView mOnlineTv;

    @ViewById(R.id.tv_local)
    TextView mLocalTv;

    @ViewById(R.id.tv_logout)
    TextView mlogoutTv;


    private Fragment mCurrentFragment;

    private Fragment mLocalFragment;

    private Fragment mOnlineFragment;

    private ActionBar mActionBar;

    private TextView mTvTitle;

    private ImageButton mIbMenu;

    @AfterViews
    void initViews() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {

//            mActionBar.setDisplayHomeAsUpEnabled(true);
//            mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
            mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Enable自定义的View
            mActionBar.setCustomView(R.layout.layout_main_title);
            mTvTitle = mActionBar.getCustomView().findViewById(R.id.tv_title);
            mIbMenu = mActionBar.getCustomView().findViewById(R.id.ib_menu);
            mTvTitle.setText("本地列表");

            mIbMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDrawerLayout.isDrawerOpen(mLLleft_drawer)) {
                        mDrawerLayout.closeDrawers();
                    } else {
                        mDrawerLayout.openDrawer(mLLleft_drawer);
                    }
                }
            });
        }

        //设置菜单内容之外其他区域的背景色
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);

        initListener();
    }

    private void initListener() {

        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout,
                R.string.open_desc, R.string.close_desc) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.d(TAG, "onDrawerOpened: ");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d(TAG, "onDrawerClosed: ");
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mLocalFragment = FrpvFragment.newInstance("本地");
        mCurrentFragment = mLocalFragment;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_main, mCurrentFragment)
                .commit();

        if (!SPUtils.getInstance().getBoolean(IS_FRPC)) {

            if (!AppUtils.copyFromAssets(this)) {
                SPUtils.getInstance().setProperty(IS_FRPC, false);
                Log.e(TAG, "frpc解压失败,请重新启动应用否则无法使用");
            }
        }

    }

    @Click
    void tv_local() {
        Log.d(TAG, "onClick: local");
        if (mLocalFragment == null) {
            mLocalFragment = FrpvFragment.newInstance("本地");
        }
        switchContent(mLocalFragment);

        mDrawerLayout.closeDrawers();

//        mActionBar.setTitle("本地列表");
        mTvTitle.setText("本地列表");
    }

    @Click
    void tv_online() {
        Log.d(TAG, "onClick: online");
        if (mOnlineFragment == null) {
            mOnlineFragment = FrpvFragment.newInstance("在线");
        }
        switchContent(mOnlineFragment);

        mDrawerLayout.closeDrawers();

//        mActionBar.setTitle("在线列表");
        mTvTitle.setText("在线列表");
    }

    @Click
    void tv_logout() {
        LoginActivity_.intent(this).start();
        finish();
        SPUtils.getInstance().removeProperty(SPUtils.TOKEN);
    }

    public void switchContent(Fragment to) {
        if (mCurrentFragment != to) {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            if (!to.isAdded()) { // 先判断是否被add过
                transaction.hide(mCurrentFragment).add(R.id.fragment_main, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(mCurrentFragment).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
            mCurrentFragment = to;
        }
    }

}
