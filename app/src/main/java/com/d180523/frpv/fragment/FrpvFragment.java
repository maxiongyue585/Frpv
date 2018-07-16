package com.d180523.frpv.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.d180523.frpv.R;
import com.d180523.frpv.activity.IniDetailsActivity_;
import com.d180523.frpv.activity.LoginActivity_;
import com.d180523.frpv.adpter.FrpcProfileAdapter;
import com.d180523.frpv.contract.DataContract;
import com.d180523.frpv.contract.DataPresenter;
import com.d180523.frpv.net.bean.FrpcBean;
import com.d180523.frpv.net.bean.QueryFrpcResponseBean;
import com.d180523.frpv.utils.SPUtils;
import com.d180523.frpv.utils.ToastUtil;
import com.d180523.frpv.widget.PullLoadMoreRecyclerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import static com.d180523.frpv.common.Const.FILE.INI_FILENAME_PARMS;

/**
 * mxy
 * fragment
 */
@EFragment(R.layout.fragment_frpv)
public class FrpvFragment extends Fragment implements DataContract.View, PullLoadMoreRecyclerView.PullLoadMoreListener {

    private static final String TAG = "FrpvFragment";

    private static final String DATA_KEY = "fragment_data_key";

    private FrpcProfileAdapter mFrpcProfileAdapter;

    @ViewById(R.id.pullLoadMoreRecyclerView)
    PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;

    @ViewById(R.id.tv_load_failed)
    TextView mTvHint;

    private RecyclerView mRecyclerView;

    /**
     * 创建instance
     *
     * @param type
     * @return
     */
    public static FrpvFragment newInstance(String type) {
        FrpvFragment fragment = new FrpvFragment_();
        if (!TextUtils.isEmpty(type)) {
            Bundle args = new Bundle();
            args.putString(DATA_KEY, type);
            fragment.setArguments(args);
        }
        return fragment;
    }

    private Context mContext;

    private DataPresenter mPresenter;

    private String mFragmentType;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @AfterViews
    void initDatas() {

        mFragmentType = getArguments().getString(DATA_KEY);
        mPresenter = new DataPresenter(mContext, this, mFragmentType);

        Log.d(TAG, "initDatas: mFragmentType=" + mFragmentType);

        //获取mRecyclerView对象
        mRecyclerView = mPullLoadMoreRecyclerView.getRecyclerView();
        //代码设置scrollbar无效？未解决！
        mRecyclerView.setVerticalScrollBarEnabled(true);
        //设置下拉刷新是否可见
        //mPullLoadMoreRecyclerView.setRefreshing(true);
        //设置是否可以下拉刷新
        //mPullLoadMoreRecyclerView.setPullRefreshEnable(true);
        //设置是否可以上拉刷新
        //mPullLoadMoreRecyclerView.setPushRefreshEnable(false);
        //显示下拉刷新
        mPullLoadMoreRecyclerView.setRefreshing(true);
        //设置上拉刷新文字
        mPullLoadMoreRecyclerView.setFooterViewText("loading");
        //设置上拉刷新文字颜色
        //mPullLoadMoreRecyclerView.setFooterViewTextColor(R.color.white);
        //设置加载更多背景色
        //mPullLoadMoreRecyclerView.setFooterViewBackgroundColor(R.color.colorBackground);
        mPullLoadMoreRecyclerView.setLinearLayout();

        //设置分割线
        mPullLoadMoreRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);

        mPresenter.onViewCreate();
    }


    @Override
    public void setListData(List<FrpcBean> listData) {//初始化
        Log.d(TAG, "setListData: ");

        mFrpcProfileAdapter = new FrpcProfileAdapter(this, listData, mFragmentType);
        mPullLoadMoreRecyclerView.setAdapter(mFrpcProfileAdapter);
        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();

        mFrpcProfileAdapter.setItemClickListener(new FrpcProfileAdapter.ItemClickListener() {
            @Override
            public void onItemClick(FrpcBean frpcBean) {
                if ("在线".equals(mFragmentType)) {
                    mPresenter.loadFrpcFile(frpcBean);
                } else {
                    startActivity(new Intent(mContext, IniDetailsActivity_.class).putExtra(INI_FILENAME_PARMS, frpcBean.getName()));
                }
            }
        });

    }

    @Override
    public void onRefreshComplete() {
        Log.d(TAG, "onRefreshComplete: ");
        mFrpcProfileAdapter.notifyDataSetChanged();
        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
    }

    @Override
    public void onInitLoadFailed(String hint) {
        Log.d(TAG, "onInitLoadFailed: " + hint);
        mTvHint.setText(hint);
        setHintView(true);
    }

    private void setHintView(boolean isShow) {
        mTvHint.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    //刷新
    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh: ");
        setHintView(false);
        mPresenter.onRefresh();
    }

    @Override
    public void onLoadMore() {
        Log.d(TAG, "onLoadMore: ");
        mPresenter.onLoadMore();
    }

    @Override
    public void login(QueryFrpcResponseBean responseBean) {
        Log.d(TAG, "login: ");

        LoginActivity_.intent(this).start();
        getActivity().finish();
        SPUtils.getInstance().removeProperty(SPUtils.TOKEN);
        ToastUtil.showToast(mContext, "token失效,请重新登录！", 0);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden && "本地".equals(mFragmentType)) {
            Log.d(TAG, "onHiddenChanged: " + hidden);
            onRefresh();
        }
    }
}
