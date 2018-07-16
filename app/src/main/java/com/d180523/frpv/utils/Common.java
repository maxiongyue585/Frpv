package com.d180523.frpv.utils;

import android.text.TextUtils;

public class Common {

    public static final String expMsg(Throwable t){

        StringBuffer l = new StringBuffer();
        if(!TextUtils.isEmpty(t.getMessage())){
            l.append(t.getMessage());
            l.append("\n");
        }

        if(t.getStackTrace()!=null){

            StackTraceElement[] ste = t.getStackTrace();
            for(StackTraceElement e: ste){
                l.append(String.format("[%d] %s|%s%s\n", e.getLineNumber(), e.getFileName(), e.getClassName(), e.getMethodName()));
            }
        }
        return l.toString();
    }
}
