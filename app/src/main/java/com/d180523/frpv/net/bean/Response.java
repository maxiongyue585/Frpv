package com.d180523.frpv.net.bean;


import com.d180523.frpv.utils.MessageDefault;

public class Response<T> {

    MessageDefault msg;

    T data;

    public void setMsg(MessageDefault msg){
        this.msg = msg;
    }

    public MessageDefault getMsg(){
        return msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {

        return "Response{" +
                "msg=" + msg +
                ", data=" + data +
                '}';
    }
}
