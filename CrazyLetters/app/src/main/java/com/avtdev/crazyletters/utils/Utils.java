package com.avtdev.crazyletters.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Array;
import java.util.List;

public class Utils {

    public static final String TAG = "Utils";

    public static boolean isNull(Object object){
        return object == null || (object instanceof String && String.valueOf(object).trim().isEmpty());
    }

    public static <T> String listToString(List<T> listData){
        String returnData = null;
        if(listData != null){
            for(T data : listData){
                if(returnData == null){
                    returnData = String.valueOf(listData);
                }else{
                    returnData += "," + String.valueOf(listData);
                }
            }
        }
        return returnData;
    }

    public static <T> T[] stringToList(String data, Class<T> toClass){
        T[] listData = (T[]) Array.newInstance(toClass, 0);
        if(data != null){
            String[] stringData = data.split(";");
            listData = (T[]) Array.newInstance(toClass, stringData.length);
            for(int i = 0; i < listData.length; i++) {
                if(toClass.isEnum()){
                    listData[i] = (T) Enum.valueOf((Class) toClass, stringData[i]);
                }else{
                    listData[i] = toClass.cast(stringData[i]);
                }
            }
        }
        return listData;
    }


    public static void setSharedPreferences(Context context, String key, Object value){
        try {
            SharedPreferences.Editor sharedPreferences = context.getSharedPreferences(Constants.Preferences.NAME.name(),
                    Context.MODE_PRIVATE).edit();

            if(value == null){
                sharedPreferences.remove(key);
            }else if(value instanceof Boolean){
                sharedPreferences.putBoolean(key, (Boolean) value);
            }else if(value instanceof Integer){
                sharedPreferences.putInt(key, (int) value);
            }else if(value instanceof Float){
                sharedPreferences.putFloat(key, (float) value);
            }else{
                sharedPreferences.putString(key, String.valueOf(value));
            }
            sharedPreferences.apply();
        }catch (Exception ex){
            Logger.log(Logger.LOGGER_TYPE.ERROR, TAG,"setSharedPreferences", ex);
        }
    }

    public static String getStringSharedPreferences(Context context, String key, String defaultValue){
        String value = defaultValue;
        try{
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.Preferences.NAME.name(), Context.MODE_PRIVATE);
            value = sharedPreferences.getString(key, defaultValue);
        }catch (Exception ex){
            Logger.log(Logger.LOGGER_TYPE.ERROR, TAG,"getStringSharedPreferences", ex);
        }
        return value;
    }

    public static Boolean getBooleanSharedPreferences(Context context, String key, Boolean defaultValue){
        Boolean value = defaultValue;
        try{
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.Preferences.NAME.name(), Context.MODE_PRIVATE);
            value = sharedPreferences.getBoolean(key, defaultValue);
        }catch (Exception ex){
            Logger.log(Logger.LOGGER_TYPE.ERROR, TAG,"getBooleanSharedPreferences", ex);
        }
        return value;
    }

    public static Integer getIntSharedPreferences(Context context, String key, Integer defaultValue){
        Integer value = defaultValue;
        try{
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.Preferences.NAME.name(), Context.MODE_PRIVATE);
            value = sharedPreferences.getInt(key, defaultValue);
        }catch (Exception ex){
            Logger.log(Logger.LOGGER_TYPE.ERROR, TAG,"getIntSharedPreferences", ex);
        }
        return value;
    }
}