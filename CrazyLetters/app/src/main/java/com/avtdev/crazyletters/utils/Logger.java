package com.avtdev.crazyletters.utils;

import android.util.Log;

public class Logger {

    public static class LOGGER_TYPE{
        public static final int DEBUG = 0;
        public static final int ERROR = 1;
        public static final int INFO = 2;
        public static final int VERBOSE = 3;
        public static final int WARNING = 4;
    }
    private static final String TAG = "Crazy Letters";

    private static String createMessage(Object ...messages){
        StringBuilder builder = new StringBuilder();
        if (messages != null) {
            for(Object msg : messages){
                if(msg != null){
                    if(builder.length() != 0){
                        builder.append(" - ");
                    }else{
                        builder.append(msg);
                    }
                }
            }
        }
        return builder.toString();
    }

    public static void log(int logType, String tag, String method, Object... messages){
        if(tag != null){
            if(method != null){
                tag = "[" + tag + "." + method + "]";
            }else{
                tag = "[" + tag + "]";
            }
            String msg = createMessage(tag, messages);
            switch (logType){
                case LOGGER_TYPE.INFO:
                    Log.i(TAG, msg);
                    break;
                case LOGGER_TYPE.ERROR:
                    Log.e(TAG, msg);
                    break;
                case LOGGER_TYPE.DEBUG:
                    Log.d(TAG, msg);
                    break;
                case LOGGER_TYPE.VERBOSE:
                    Log.v(TAG, msg);
                    break;
                case LOGGER_TYPE.WARNING:
                    Log.w(TAG, msg);
                    break;
            }
        }
    }
}
