package com.yang.me.lib.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class GsonUtil {
    private static final String TAG = "GsonUtil";

    public static <T> String parseListToStr(List<T> list) {
        Gson gson = new Gson();
        String jsonListStr = gson.toJson(list);

        Log.w(TAG, "getString: " + jsonListStr);

        return jsonListStr;
    }

    public static <T> List<T> parseListFromString(String json) {
        List<T> data = new Gson().fromJson(json, new TypeToken<List<T>>() {
        }.getType());
        return data;
    }

    /**
     * 将对象转为JSON串，此方法能够满足大部分需求
     *
     * @param src :将要被转化的对象
     * @return :转化后的JSON串
     */
    public static String toJson(Object src) {
        if (null == src) {
            return new Gson().toJson(JsonNull.INSTANCE);
        }
        try {
            return new Gson().toJson(src);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用来将JSON串转为对象，但此方法不可用来转带泛型的集合
     *
     * @param json     json
     * @param classOfT classOfT
     * @return T Object
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return new Gson().fromJson(json, (Type) classOfT);
        } catch (JsonSyntaxException e) {
            System.out.println(e.toString() + "------------------------------");
            e.printStackTrace();
        }
        return null;
    }

}
