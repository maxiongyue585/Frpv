package com.d180523.frpv.net.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * mxy
 * 查询frpc响应体
 */

public class QueryFrpcResponseBean implements Serializable {

    @SerializedName("code")
    private String code;

    @SerializedName("msg")
    private String msg;

    @SerializedName("data")
    private List<FrpcBean> data;

    @SerializedName("count")
    private int count;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<FrpcBean> getData() {
        return data;
    }

    public void setData(List<FrpcBean> data) {
        this.data = data;
    }
}
