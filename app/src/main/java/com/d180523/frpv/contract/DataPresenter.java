package com.d180523.frpv.contract;


import android.content.Context;
import android.util.Log;

import com.d180523.frpv.R;
import com.d180523.frpv.api.ApiServer;
import com.d180523.frpv.net.NetUtils;
import com.d180523.frpv.net.bean.FrpcBean;
import com.d180523.frpv.net.bean.FrpcProfile;
import com.d180523.frpv.net.bean.QueryFrpcResponseBean;
import com.d180523.frpv.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * mxy
 * Presenter
 */

public class DataPresenter implements DataContract.Presenter {

    private static final String TAG = "DataPresenter";

    private final DataContract.View mView;

    private ApiServer mAs;

    private List<FrpcBean> mListData = new ArrayList<>();

    private String mType;

    private int mCurrentPage;

    private Context mContext;

    public DataPresenter(Context context, DataContract.View mView, String mType) {

        mAs = NetUtils.retrofit.create(ApiServer.class);

        mListData.clear();
        this.mView = mView;
        this.mType = mType;

        this.mContext = context;
    }

    @Override
    public void onViewCreate() {

        if ("在线".equals(mType)) {
            mCurrentPage = 1;
            loadData(0);
        } else {
            loadLocal(0);
        }
    }

    @Override
    public void onRefresh() {

        mCurrentPage = 1;
        if ("在线".equals(mType)) {
            loadData(1);
        } else {
            loadLocal(1);
        }

    }

    @Override
    public void onLoadMore() {
        mCurrentPage = mCurrentPage + 1;
        loadData(2);
    }

    /**
     * 0:初始化请求
     * 1:refresh请求
     * 2:loadMore请求
     */
    private void loadData(final int requestDataType) {

        mAs.queryFrpc(mCurrentPage, 15).enqueue(new Callback<QueryFrpcResponseBean>() {
            @Override
            public void onResponse(Call<QueryFrpcResponseBean> call, Response<QueryFrpcResponseBean> response) {

                QueryFrpcResponseBean responseBean = response.body();
                if (responseBean != null && "1111".equals(responseBean.getCode())) {//token失效
                    mView.login(responseBean);
                    return;
                }

                Log.d(TAG, "onResponse: " + response.body().getData());


                switch (requestDataType) {
                    case 0: // init
                        mListData.clear();
                        mListData.addAll(response.body().getData());
                        mView.setListData(mListData);
                        break;
                    case 1: // refresh
                        mListData.clear();
                        mListData.addAll(response.body().getData());
                        mView.onRefreshComplete();
                        break;
                    case 2: // load more
                        if (response.body().getData() != null && response.body().getData().size() == 0) {
                            mCurrentPage = mCurrentPage - 1;
                            ToastUtil.showToast(mContext, "没有更多了", 0);
                        } else {
                            mListData.addAll(response.body().getData());
                        }

                        mView.onRefreshComplete();
                        break;
                }

            }

            @Override
            public void onFailure(Call<QueryFrpcResponseBean> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                mView.onInitLoadFailed(t.getMessage());
            }
        });

    }

    /**
     * 加载本地文件
     *
     * @param requestDataType 0:初始化  1:刷新
     */
    private void loadLocal(final int requestDataType) {

        mListData.clear();

        String ini_path = mContext.getApplicationContext().getFilesDir() + File.separator + "ini";
        File files = new File(ini_path);
        if (files.exists() && files.length() > 0) {
            for (File f : files.listFiles()) {
                FrpcBean frpcBean = new FrpcBean();
                frpcBean.setName(f.getName());
                mListData.add(frpcBean);
            }

        } else {
            mView.onInitLoadFailed(mContext.getResources().getString(R.string.no_file));
        }

        switch (requestDataType) {
            case 0:
                mView.setListData(mListData);
                break;
            case 1:
                mView.onRefreshComplete();
                break;
        }
    }

    //加载frpcFile
    public void loadFrpcFile(final FrpcBean frpcBean) {

        mAs.loadFrpcFile(frpcBean.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    FrpcProfile.persist(new JSONObject(new String(response.body().bytes())), mContext);

                    onRefresh();

                    ToastUtil.showToast(mContext, "下载成功", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

}
