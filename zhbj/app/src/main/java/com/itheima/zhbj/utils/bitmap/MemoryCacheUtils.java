package com.itheima.zhbj.utils.bitmap;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pb on 2016/7/14.
 * 内存缓存
 */
public class MemoryCacheUtils {
    private static final String TAG = "MemoryCacheUtils";
    private LruCache<String, Bitmap> mLruCache;

    public MemoryCacheUtils() {
        //获取虚拟机分配的最大内存,默认16M
        long maxMemory = Runtime.getRuntime().maxMemory();
        Log.i(TAG, "maxMemory: " + maxMemory);
        //maxSize,内存缓存上限
        mLruCache = new LruCache<String, Bitmap>((int) (maxMemory/8)){
            //返回单个对象占用内存的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                int byteCount = value.getRowBytes() * value.getHeight();
                return byteCount;
            }
        };
    }
    //写缓存
    public void setMemoryCache(String url, Bitmap bitmap) {
        mLruCache.put(url, bitmap);
    }

    //读缓存
    public Bitmap getMemroyCache(String  url) {
        return mLruCache.get(url);
    }
}
