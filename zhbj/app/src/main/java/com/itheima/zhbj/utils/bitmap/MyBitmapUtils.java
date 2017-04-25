package com.itheima.zhbj.utils.bitmap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by pb on 2016/7/14.
 * 自定义三级缓存工具
 */
public class MyBitmapUtils {

    private static final String TAG = "MyBitmapUtils";
    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private final MemoryCacheUtils mMemoryCacheUtils;
    private Bitmap bitmap;

    public MyBitmapUtils(Activity mActivity) {
        mMemoryCacheUtils = new MemoryCacheUtils();
        mLocalCacheUtils = new LocalCacheUtils();
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);
    }

    //加载图片进行展示
    public void display(ImageView imageView, String url) {
        //内存缓存
        bitmap = mMemoryCacheUtils.getMemroyCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            Log.i(TAG, "从内存加载图片啦....");
            return;
        }

        //本地缓存
        bitmap = mLocalCacheUtils.getLocalCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            Log.i(TAG, "从本地加载图片啦....");
            mMemoryCacheUtils.setMemoryCache(url, bitmap);
            return;
        }

        //网络缓存
        mNetCacheUtils.getBitmapFromNet(imageView, url);
    }
}
