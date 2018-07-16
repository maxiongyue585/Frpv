package com.d180523.frpv.api;

import com.d180523.frpv.common.UrlConstainer;
import com.d180523.frpv.net.bean.LoginResponse;
import com.d180523.frpv.net.bean.QueryFrpcResponseBean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * ApiServer
 */

public interface ApiServer {

    /**
     * 登录
     *
     * @param name     用户名
     * @param password 密码
     * @return
     */
    @FormUrlEncoded
    @POST(UrlConstainer.LOGIN)
    Call<LoginResponse> login(@Field("username") String name, @Field("password") String password);

    /**
     * 查询frpc列表
     *
     * @param page  页数
     * @param limit 每页加载数量
     * @return
     */
    @GET(UrlConstainer.QUERY)
    Call<QueryFrpcResponseBean> queryFrpc(@Query("page") int page, @Query("limit") int limit);

    /**
     * 加载frpc实体
     *
     * @param id
     * @return
     */
    @GET(UrlConstainer.LOAD_FRPCFILE)
    Call<ResponseBody> loadFrpcFile(@Path("id") String id);
}
