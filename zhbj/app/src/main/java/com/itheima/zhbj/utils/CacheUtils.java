package com.itheima.zhbj.utils;

import android.content.Context;

/**
 * Created by pb on 2016/7/7.
 */
public class CacheUtils {
    //写缓存
    //以url为key,以json为值,保存在本地SP
    public static void setCache(Context ctx, String key, String json) {
        PrefUtils.putString(ctx, key, json);
    }

    //读缓存
    public static String getCache(Context ctx, String key) {
       return PrefUtils.getString(ctx, key, null);
    }
}
