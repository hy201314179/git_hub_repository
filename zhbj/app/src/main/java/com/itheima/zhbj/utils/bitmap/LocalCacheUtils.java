package com.itheima.zhbj.utils.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.itheima.zhbj.utils.MD5Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by pb on 2016/7/14.
 * 本地缓存工具类
 */
public class LocalCacheUtils {
    //缓存文件夹
    private String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/zhbj_cache/";

    //写缓存
    public void setLocalCache(String url, Bitmap bitmap) {
        //将图片保存在本地文件中
        File dir = new File(PATH);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();//创建文件夹
        }
        try {
            File cacheFile = new File(dir, MD5Encoder.encode(url));
            //将图片压缩保存到本地,参数1:图片的格式,参数2:压缩比(0-100), 参数3:输出流
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(cacheFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //读缓存
    public Bitmap getLocalCache(String url) {
        try {
            File cacheFile = new File(PATH, MD5Encoder.encode(url));
            if (cacheFile.exists()) {
                //缓存存在
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(cacheFile));
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
