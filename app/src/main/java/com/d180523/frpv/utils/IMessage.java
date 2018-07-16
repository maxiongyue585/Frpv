package com.d180523.frpv.utils;


public interface IMessage {

    String CODE_SUCCESS = "0000";

    String CODE_ERROR = "1111";

    String getCode();

    void setCode(String code);

    String getDesc();

    void setDesc(String desc);
}
