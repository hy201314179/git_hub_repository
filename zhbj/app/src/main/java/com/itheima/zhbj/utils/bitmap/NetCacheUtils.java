package com.itheima.zhbj.utils.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by pb on 2016/7/14.
 * 网络缓存工具类
 */
public class NetCacheUtils {
    private static final String TAG = "NetCacheUtils";
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    public NetCacheUtils(LocalCacheUtils mLocalCacheUtils, MemoryCacheUtils mMemoryCacheUtils) {
        this.mMemoryCacheUtils = mMemoryCacheUtils;
        this.mLocalCacheUtils = mLocalCacheUtils;
    }

    public void getBitmapFromNet(ImageView imageView, String url) {
        //异步下载图片
        new BitmapTask(imageView, url).execute(imageView, url);
    }

    class BitmapTask extends AsyncTask<Object, Void, Bitmap> {
        private ImageView imageView;
        private String url;
        public BitmapTask(ImageView imageView, String url) {
            this.imageView = imageView;
            this.url = url;
            this.imageView.setTag(this.url);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            //使用url下载图片
            Bitmap bitmap = download(url);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            //给ImageView设置图片
            //由于ListView的重用机制,
            // 导致某个item有可能展示它所重用的那个item的图片,导致图片错乱
            //解决方案:确保当前设置的图片和当前显示的imageView完全匹配
            if (result != null) {
                String url = (String) imageView.getTag();
                //判断当前下载的图片的url是否和ImageView的url一致,如果一致,说明完全匹配
                if (this.url.equals(url)) {
                    imageView.setImageBitmap(result);
                    Log.i(TAG, "onPostExecute: 从网络上下载图片啦");
                    //写本地缓存
                    mLocalCacheUtils.setLocalCache(url, result);
                    mMemoryCacheUtils.setMemoryCache(url, result);
                }
            }
        }
    }

    private Bitmap download(String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(6000);//设置连接超时
            conn.setReadTimeout(6000);//设置读取超时
            conn.connect();//连接
            int responseCode = conn.getResponseCode();//获取返回码
            if (responseCode == 200) {
                InputStream in = conn.getInputStream();//获取输入流
                //使用输入流生成Bitmap对象
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }
}
