package com.d180523.frpv.net.bean;

import android.content.Context;
import android.util.Log;

import com.d180523.frpv.utils.AppUtils;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FrpcProfile {

    private static final String TAG = "FrpcProfile";

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("note")
    private String note;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Map<String, Attr> labels = new HashMap<>();

    public class Attr {

        private String id;

        private String name;

        private String value;

        private String label;

        public Attr(JSONObject obj) {
        }

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

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    /**
     * 将对象转化为格式：
     * #name -- note
     * [label1]
     * key1 = val1
     * key2 = val2
     * <p>
     * [label2]
     * key3 = val3
     * key4 = val3
     *
     * @return
     */
    public static String parse(JSONObject jsonObject) {
        StringBuffer iniMsg = new StringBuffer();
        Iterator iterator = jsonObject.keys();

        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            try {
                if (jsonObject.get(key) instanceof JSONObject) {
                    if (jsonObject.getJSONObject(key).keys().hasNext()) {
                        iniMsg.append(String.format("[%s]", key));
                        iniMsg.append("\n");
                        Iterator lableIterator = jsonObject.getJSONObject(key).keys();
                        JSONObject lableObj = jsonObject.getJSONObject(key);
                        while (lableIterator.hasNext()) {
                            String labKey = (String) lableIterator.next();
                            iniMsg.append(String.format("%s = %s", labKey, lableObj.getString(labKey)));
                            iniMsg.append("\n");
                        }
                        iniMsg.append("\n");
                    }
                } else {
                    iniMsg.append(String.format("# %s:%s", key, jsonObject.getString(key)));
                    iniMsg.append("\n");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return iniMsg.toString();
    }


    /**
     * 将pase后的字符串保存到frpc/pro_{name}.ini文件
     *
     * @return
     */
    public static boolean persist(JSONObject jsonObject, Context context) {

        String ini_path = context.getApplicationContext().getFilesDir() + File.separator + "ini";
        File file = new File(ini_path + File.separator + String.format("pro_%s.ini", getFrpcFileName(jsonObject)));
        if (file != null && file.exists()) {
            Log.d(TAG, "persist: 文件已存在");
            return true;
        } else {
            //保存ini到本地
            AppUtils.saveIniFile(context, parse(jsonObject), getFrpcFileName(jsonObject));
        }
        return true;
    }

    /**
     * 返回frpcName
     *
     * @param jsonObject
     * @return
     */
    private static String getFrpcFileName(JSONObject jsonObject) {
        String name = null;
        Iterator iterator = jsonObject.keys();

        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            try {
                if (jsonObject.get(key) instanceof String && key.equals("name")) {
                    name = jsonObject.getString(key);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return name;
    }
}
