package com.itheima.zhbj.domain;

import java.util.ArrayList;

/**
 * Created by pb on 2016/7/8.
 * 页签网络数据
 */
public class NewsTab {

    public NewsTabData data;

    public class NewsTabData{
        public String more;
        public ArrayList<TopNews> topnews;
        public ArrayList<News> news;

        @Override
        public String toString() {
            return "NewsTabData{" +
                    "more='" + more + '\'' +
                    ", topnews=" + topnews +
                    ", news=" + news +
                    '}';
        }
    }

    public class TopNews{
        public String id;
        public String pubdate;
        public String title;
        public String topimage;
        public String url;

        @Override
        public String toString() {
            return "TopNews{" +
                    "id='" + id + '\'' +
                    ", pubdate='" + pubdate + '\'' +
                    ", title='" + title + '\'' +
                    ", topimage='" + topimage + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    public class News{
        public String id;
        public String pubdate;
        public String title;
        public String listimage;
        public String url;

        @Override
        public String toString() {
            return "News{" +
                    "id='" + id + '\'' +
                    ", pubdate='" + pubdate + '\'' +
                    ", title='" + title + '\'' +
                    ", listimage='" + listimage + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NewsTab{" +
                "data=" + data +
                '}';
    }
}
