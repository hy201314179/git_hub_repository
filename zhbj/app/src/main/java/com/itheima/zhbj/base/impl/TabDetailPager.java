package com.itheima.zhbj.base.impl;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itheima.zhbj.NewsDetailActivity;
import com.itheima.zhbj.R;
import com.itheima.zhbj.base.BaseMenuDetailPager;
import com.itheima.zhbj.domain.NewsMenu;
import com.itheima.zhbj.domain.NewsTab;
import com.itheima.zhbj.global.GlobalConstants;
import com.itheima.zhbj.utils.CacheUtils;
import com.itheima.zhbj.utils.PrefUtils;
import com.itheima.zhbj.view.RefreshListView;
import com.itheima.zhbj.view.TopNewsViewPager;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

/**
 * Created by pb on 2016/7/7.
 * 页签详情页,北京,中国,国家.....
 */
public class TabDetailPager extends BaseMenuDetailPager {

    private static final String TAG = "TabDetailPager";
    private NewsMenu.NewsTabData newsTabData;//当前页签的网络数据

    @ViewInject(R.id.vp_tab_detail)
    private TopNewsViewPager mViewPager;
    @ViewInject(R.id.tv_title)
    private TextView mTitle;
    @ViewInject(R.id.indicator)
    private CirclePageIndicator mCircleIndicator;
    @ViewInject(R.id.lv_list)
    private RefreshListView mListView;

    private final String mUrl;
    private ArrayList<NewsTab.TopNews> mTopNewsList;
    private ArrayList<NewsTab.News> mNewsList;
    private String mMoreUrl;
    private NewsAdapter mNewsAdapter;
    private Handler mHandler;

    public TabDetailPager(Activity activity, NewsMenu.NewsTabData newsTabData) {
        super(activity);
        this.newsTabData = newsTabData;
        mUrl = GlobalConstants.SERVER_URL + newsTabData.url;
    }

    @Override
    public View initViews() {

        /*view = new TextView(mActivity);
//        view.setText("页签详情页-北京..");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);*/

        View view = View.inflate(mActivity, R.layout.pager_tab_detail, null);
        //加载头条新闻的头布局
        View hearderView = View.inflate(mActivity, R.layout.list_item_header, null);
        ViewUtils.inject(this, view);
        ViewUtils.inject(this, hearderView);

        //给listView添加头布局
        mListView.addHeaderView(hearderView);

        //设置下拉刷新监听
        mListView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新数据
                Log.i(TAG, "亲,下拉刷新");
                getDataFromServer();
            }

            @Override
            public void onLoadMore() {
                Log.i(TAG, "亲,加载更多");

                if (mMoreUrl != null) {
                    getMoreDataFromServer();
                } else {
                    Toast.makeText(mActivity, "没有更多数据啦...", Toast.LENGTH_SHORT).show();
                    mListView.onRefreshComplete();
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //头布局也算位置,所以使用position时要将头布局个数减掉
                int headerViewsCount = mListView.getHeaderViewsCount();
                position -= headerViewsCount;
                Log.i(TAG, "onItemClick: " + position);

                NewsTab.News news = mNewsList.get(position);
                //标记已读未读: 将已读新闻id保存在sp中
                String readIds = PrefUtils.getString(mActivity, "read_ids", "");
                //判断之前是否已经保存该id
                if (!readIds.contains(news.id)) {
                    readIds += news.id + ",";
                    PrefUtils.putString(mActivity, "read_ids", readIds);
                    mNewsAdapter.notifyDataSetChanged();
                }

                //调到新闻详情页
                Intent intent = new Intent(mActivity, NewsDetailActivity.class);
                intent.putExtra("url", news.url);
                mActivity.startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void initData() {
//        view.setText(newsTabData.title);
        String cache = CacheUtils.getCache(mActivity, mUrl);
        if (!TextUtils.isEmpty(cache)) {
            processData(cache, false);
        }
        getDataFromServer();
    }

    //请求服务器获取页签的数据
    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, mUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                CacheUtils.setCache(mActivity, mUrl, result);
                processData(result, false);

                //隐藏下拉刷新
                mListView.onRefreshComplete();
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
                //隐藏下拉刷新
                mListView.onRefreshComplete();
            }
        });
    }

    //请求下一页网络数据
    private void getMoreDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, mMoreUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result, true);

                //隐藏下拉刷新
                mListView.onRefreshComplete();
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
                //隐藏下拉刷新
                mListView.onRefreshComplete();
            }
        });
    }

    private void processData(String result, boolean isMore) {
        Gson gson = new Gson();
        NewsTab newsTab = gson.fromJson(result, NewsTab.class);
        Log.i(TAG,"页签的数据: " + newsTab);

        //获取下一页数据的地址
        String more = newsTab.data.more;
        if (!TextUtils.isEmpty(more)) {
            mMoreUrl = GlobalConstants.SERVER_URL + more;
        } else {
            mMoreUrl = null;
        }

        if (isMore) {
            //加载更多
            ArrayList<NewsTab.News> moreNews = newsTab.data.news;
            mNewsList.addAll(moreNews);//追加更多数据
            mNewsAdapter.notifyDataSetChanged();//刷新listview
        } else {
            mTopNewsList = newsTab.data.topnews;
            if (mTopNewsList != null) {
                mViewPager.setAdapter(new TopNewsAdapter());

                mCircleIndicator.setViewPager(mViewPager);//将圆形指示器和viewpager绑定
                mCircleIndicator.setSnap(true);//快照展示方式
                mCircleIndicator.onPageSelected(0);//将圆点位置归零,保证圆点和页面同步

                mCircleIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        mTitle.setText(mTopNewsList.get(position).title);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
                //初始化第一页头条新闻标题
                mTitle.setText(mTopNewsList.get(0).title);

                //启动自动轮播
                if (mHandler == null) {
                    mHandler = new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            int currentItem = mViewPager.getCurrentItem();
                            if (currentItem < mTopNewsList.size() - 1) {
                                currentItem++;
                            } else {
                                currentItem = 0;
                            }
                            mViewPager.setCurrentItem(currentItem);
                            mHandler.sendEmptyMessageDelayed(0, 2000);
                        }
                    };
                    //发送延时消息,启动自动轮播
                    mHandler.sendEmptyMessageDelayed(0, 2000);
                }

                mViewPager.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                mHandler.removeCallbacksAndMessages(null);
                                break;

                            case MotionEvent.ACTION_CANCEL:
                                //事件取消: 当按住头条新闻后,突然上下滑动ListView,导致当前ViewPager事件被取消,而不响应
                                //发送延时消息,启动自动轮播
                                mHandler.sendEmptyMessageDelayed(0, 2000);
                                break;

                            case  MotionEvent.ACTION_UP:
                                //发送延时消息,启动自动轮播
                                mHandler.sendEmptyMessageDelayed(0, 2000);
                                break;
                        }
                        return false;
                    }
                });
            }

            //初始化新闻列表数据
            mNewsList = newsTab.data.news;
            if (mNewsList != null) {
                mNewsAdapter = new NewsAdapter();
                mListView.setAdapter(mNewsAdapter);
            }
        }
    }

    class NewsAdapter extends BaseAdapter{

        private ViewHolder holder;
        private final BitmapUtils mBitmapUtils;

        public NewsAdapter() {
            mBitmapUtils = new BitmapUtils(mActivity);
            mBitmapUtils.configDefaultLoadingImage(R.drawable.news_pic_default);
        }

        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public NewsTab.News getItem(int position) {
            return mNewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.list_item_news, null);
                holder = new ViewHolder();
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            NewsTab.News info = getItem(position);
            holder.tvTitle.setText(info.title);
            holder.tvTime.setText(info.pubdate);
            mBitmapUtils.display(holder.ivIcon, info.listimage);

            String readIds = PrefUtils.getString(mActivity, "read_ids", "");
            if (readIds.contains(info.id)) {
                holder.tvTitle.setTextColor(Color.GRAY);
            } else {
                holder.tvTitle.setTextColor(Color.BLACK);
            }

            return convertView;
        }
    }

    class ViewHolder{
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvTime;
    }

    class TopNewsAdapter extends PagerAdapter{
        private BitmapUtils mBitmapUtils;
        public TopNewsAdapter() {
            mBitmapUtils = new BitmapUtils(mActivity);
            mBitmapUtils.configDefaultLoadingImage(R.drawable.pic_item_list_default);
        }

        @Override
        public int getCount() {
            return mTopNewsList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = new ImageView(mActivity);
            NewsTab.TopNews topNews = mTopNewsList.get(position);
            String topimage = topNews.topimage;//图片下载的链接

            view.setScaleType(ImageView.ScaleType.FIT_XY);
            //1.根据url下载图片,2.将图片设置给ImageView, 3.图片缓存, 4.避免内存溢出
            mBitmapUtils.display(view, topimage);

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
