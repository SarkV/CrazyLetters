package com.avtdev.crazyletters.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.function.Function;

import static com.avtdev.crazyletters.utils.Constants.ARRAY_SEPARATOR;

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
                    returnData = String.valueOf(data);
                }else{
                    returnData += ARRAY_SEPARATOR + data;
                }
            }
        }
        return returnData;
    }

    public static <T> T[] stringToList(String data, Class<T> toClass){
        T[] listData = (T[]) Array.newInstance(toClass, 0);
        if(data != null){
            String[] stringData = data.split(ARRAY_SEPARATOR);
            listData = (T[]) Array.newInstance(toClass, stringData.length);

            for(int i = 0; i < listData.length; i++) {
                if(toClass.isEnum()){
                    listData[i] = (T) Enum.valueOf((Class) toClass, stringData[i]);
                }else if(toClass == Integer.class){
                    Integer t = Integer.parseInt(stringData[i]);
                    listData[i] = (T) t;
                }else if(toClass == String.class){
                    listData[i] = (T) stringData[i];
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
            }else if(value instanceof Long){
                sharedPreferences.putLong(key, (long) value);
            }else{
                sharedPreferences.putString(key, String.valueOf(value));
            }
            sharedPreferences.apply();
        }catch (Exception ex){
            Logger.e(TAG,"setSharedPreferences", ex);
        }
    }

    public static String getStringSharedPreferences(Context context, String key, @NonNull String defaultValue){
        String value = defaultValue;
        try{
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.Preferences.NAME.name(), Context.MODE_PRIVATE);
            value = sharedPreferences.getString(key, defaultValue);
        }catch (Exception ex){
            Logger.e(TAG,"getStringSharedPreferences", ex);
        }
        return value;
    }

    public static Boolean getBooleanSharedPreferences(Context context, String key, @NonNull Boolean defaultValue){
        Boolean value = defaultValue;
        try{
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.Preferences.NAME.name(), Context.MODE_PRIVATE);
            value = sharedPreferences.getBoolean(key, defaultValue);
        }catch (Exception ex){
            Logger.e(TAG,"getBooleanSharedPreferences", ex);
        }
        return value;
    }

    public static Integer getIntSharedPreferences(Context context, String key, @NonNull Integer defaultValue){
        Integer value = defaultValue;
        try{
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.Preferences.NAME.name(), Context.MODE_PRIVATE);
            value = sharedPreferences.getInt(key, defaultValue);
        }catch (Exception ex){
            Logger.e(TAG,"getIntSharedPreferences", ex);
        }
        return value;
    }

    public static Long getLongSharedPreferences(Context context, String key, @NonNull Long defaultValue){
        Long value = defaultValue;
        try{
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.Preferences.NAME.name(), Context.MODE_PRIVATE);
            value = sharedPreferences.getLong(key, defaultValue);
        }catch (Exception ex){
            Logger.e(TAG,"getIntSharedPreferences", ex);
        }
        return value;
    }

    public static long getUTCDate(){
        String pattern = "yyyyMMddHHmm";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime date = LocalDateTime.now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
            return Long.parseLong(date.format(format));
        }
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Long.parseLong(simpleDateFormat.format(date));
    }

    public static float getRandomFloat(float min, float max){
        return min + (new Random().nextFloat() * (max - min));
    }
}