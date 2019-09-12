package com.avtdev.crazyletters.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

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

    public static long getUTCDate(Long miliseconds){
        String pattern = "yyyyMMddHHmm";
        Date date = new Date(miliseconds);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Long.parseLong(simpleDateFormat.format(date));
    }

    public static long getDifferenceDates(long currentDate, long date2){
        try{
            String pattern = "yyyyMMddHHmm";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Long d1 = simpleDateFormat.parse(String.valueOf(currentDate)).getTime();
            Long d2 = simpleDateFormat.parse(String.valueOf(date2)).getTime();
            return d2 - d1;
        }catch (Exception e){
            Logger.e(TAG, "getDifferenceDates", e);
        }
        return 0;
    }

    public static float getRandomFloat(float min, float max){
        return min + (new Random().nextFloat() * (max - min));
    }
}