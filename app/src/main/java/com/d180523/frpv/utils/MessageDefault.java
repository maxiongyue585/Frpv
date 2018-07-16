package com.d180523.frpv.utils;



public class MessageDefault implements IMessage {

    private String code;

    private String desc;

    public MessageDefault(){}

    public MessageDefault(String code, String desc){

        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isSuccess(){
        return CODE_SUCCESS.equals(code);
    }

    @Override
    public String toString() {

        return "MessageDefault{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
