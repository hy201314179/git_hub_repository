package com.itheima.zhbj.domain;

import java.util.ArrayList;

/**
 * Created by pb on 2016/7/13.
 * 组图网络数据
 */
public class PhotosBean {

    public  PhotoData data;

    public class  PhotoData{
        public ArrayList<PhotoNews> news;

        @Override
        public String toString() {
            return "PhotoData{" +
                    "news=" + news +
                    '}';
        }
    }

    public class PhotoNews{
        public String title;
        public String listimage;

        @Override
        public String toString() {
            return "PhotoNews{" +
                    "title='" + title + '\'' +
                    ", listimage='" + listimage + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PhotosBean{" +
                "data=" + data +
                '}';
    }
}
