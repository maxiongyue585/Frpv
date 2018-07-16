package com.d180523.frpv.net.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Frpc实体类
 */

public class FrpcBean implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("role")
    private String role;

    @SerializedName("note")
    private String note;

    @SerializedName("create_time")
    private String create_time;

    @SerializedName("update_time")
    private String update_time;

    @SerializedName("disable")
    private int disable;

    @SerializedName("del")
    private int del;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public int getDisable() {
        return disable;
    }

    public void setDisable(int disable) {
        this.disable = disable;
    }

    public int getDel() {
        return del;
    }

    public void setDel(int del) {
        this.del = del;
    }
}
