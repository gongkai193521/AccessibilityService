package com.accessibilityservice.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Gson工具类
 */
public class GsonUtils {

    /**
     * 将对象转换成json字符串
     *
     * @param bean
     * @return json字符串
     */
    public static String toJson(Object bean) {
        return new Gson().toJson(bean);
    }

    /**
     * 将json字符串转化成javabean对象,
     *
     * @param json
     * @param classOfT
     * @param <T>
     * @return javabean对象
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return new Gson().fromJson(json, classOfT);
    }

    /**
     * 将json字符串转化成List<JavaBea>对象
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> ArrayList<T> jsonToList(String json, Class<T> clazz) {
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        ArrayList<JsonObject> jsonObjs = new Gson().fromJson(json, type);
        if (jsonObjs == null || jsonObjs.size() == 0) {
            return null;
        }
        ArrayList<T> listOfT = new ArrayList<>();
        for (JsonObject jsonObj : jsonObjs) {
            T baseBean = new Gson().fromJson(jsonObj, clazz);
            listOfT.add(baseBean);
        }

        return listOfT;
    }
}