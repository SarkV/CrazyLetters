package com.avtdev.crazyletters.utils;

import android.util.Log;

public class Logger {

    private static final String TAG = "Crazy Letters";

    private static String createMessage(String tag, Object ...messages){
        StringBuilder builder = new StringBuilder();
        builder.append(tag);
        if (messages != null) {
            for(Object msg : messages){
                if(msg != null){
                    builder.append(" - ");
                    builder.append(msg);
                }
            }
        }
        return builder.toString();
    }

    private static String createTag(String tag, String method){
        if(method != null){
            tag = "[" + tag + "." + method + "]";
        }else{
            tag = "[" + tag + "]";
        }
        return tag;
    }

    public static void i(String tag, String method, Object... messages){
        if(tag != null){
            tag = createTag(tag, method);
            Log.i(TAG, createMessage(tag, messages));
        }
    }

    public static void d(String tag, String method, Object... messages){
        if(tag != null){
            tag = createTag(tag, method);
            Log.d(TAG, createMessage(tag, messages));
        }
    }

    public static void w(String tag, String method, Object... messages){
        if(tag != null){
            tag = createTag(tag, method);
            Log.w(TAG, createMessage(tag, messages));
        }
    }

    public static void e(String tag, String method, Object... messages){
        if(tag != null){
            tag = createTag(tag, method);
            Log.e(TAG, createMessage(tag, messages));
        }
    }
}
