package com.d180523.frpv.contract;

import com.d180523.frpv.net.bean.FrpcBean;
import com.d180523.frpv.net.bean.QueryFrpcResponseBean;

import java.util.List;

/**
 * mxy
 */
public interface DataContract {
    interface View {

        void setListData(List<FrpcBean> listData);

        void onRefreshComplete();

        void onInitLoadFailed(String hint);

        void login(QueryFrpcResponseBean responseBean);
    }

    interface Presenter {
        void onViewCreate();

        void onRefresh();

        void onLoadMore();
    }
}
