package com.itheima.zhbj.domain;

import java.util.ArrayList;

/**
 * Created by pb on 2016/7/6.
 * 分类数据封装
 * 使用Gson解析,对象书写方式:
 * 1.逢{}创建对象
 * 2.逢[]创建集合
 * 3.对象中所有字段名称必须和json中的字段完全一致
 * 4.创建类的时候,类名称可以随意定义
 */
public class NewsMenu {

    public int retcode;
    public ArrayList<NewsMenuData> data;
    public ArrayList<String> extend;

    public class NewsMenuData {
        public String id;
        public String title;
        public int type;
        public ArrayList<NewsTabData> children;

        @Override
        public String toString() {
            return "NewsMenuData{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", type=" + type +
                    ", children=" + children +
                    '}';
        }
    }

    public class NewsTabData{
        public String id;
        public String title;
        public int type;
        public String url;

        @Override
        public String toString() {
            return "NewsTabData{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", type=" + type +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NewsMenu{" +
                "retcode=" + retcode +
                ", data=" + data +
                ", extend=" + extend +
                '}';
    }
}
