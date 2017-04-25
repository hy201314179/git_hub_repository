package com.itheima.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itheima.zhbj.MainActivity;
import com.itheima.zhbj.base.BaseMenuDetailPager;
import com.itheima.zhbj.base.BasePager;
import com.itheima.zhbj.base.impl.menudetail.InteractMenuDetailPager;
import com.itheima.zhbj.base.impl.menudetail.NewsMenuDetailPager;
import com.itheima.zhbj.base.impl.menudetail.PhotosMenuDetailPager;
import com.itheima.zhbj.base.impl.menudetail.TopicMenuDetailPager;
import com.itheima.zhbj.domain.NewsMenu;
import com.itheima.zhbj.fragment.LeftMenuFragment;
import com.itheima.zhbj.global.GlobalConstants;
import com.itheima.zhbj.utils.CacheUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;

/**
 * Created by pb on 2016/7/4.
 * 新闻中心
 */
public class NewsCenterPager extends BasePager {
    private static final String TAG = "NewsCenterPager";
    private ArrayList<BaseMenuDetailPager> mPagers;
    private NewsMenu mNewsMenu;

    public NewsCenterPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        //给帧布局填充布局对象
        /*TextView view = new TextView(mActivity);
        view.setText("新闻中心");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);
        flContainer.addView(view);
        tvTitle.setText("新闻中心");*/

        btnMenu.setVisibility(View.VISIBLE);

        String cache = CacheUtils.getCache(mActivity, GlobalConstants.CATEGORY_URL);
        if (!TextUtils.isEmpty(cache)) {
            Log.i(TAG, "有缓存啦: " + cache);
            //有缓存
            processData(cache);
        }

        //从服务器获取数据
        getDataFromServer();
    }

    //从服务器获取数据
    //加权限
    private void getDataFromServer() {
        //XUtils
        HttpUtils utils = new HttpUtils();
        //原生模拟器: 10.0.2.2;     Genymotion: 10.0.3.2
        utils.send(HttpRequest.HttpMethod.GET, GlobalConstants.CATEGORY_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i(TAG, "服务器分类数据: " + result);
                processData(result);

                //保存到缓存
                CacheUtils.setCache(mActivity, GlobalConstants.CATEGORY_URL, result);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
            }
        });
    }

    private void processData(String result) {
        Gson gson = new Gson();
        mNewsMenu = gson.fromJson(result, NewsMenu.class);
        Log.i(TAG, "解析后的数据: " + mNewsMenu.toString());

        //找到侧边栏对象
        MainActivity mainUI = (MainActivity) mActivity;
        LeftMenuFragment fragment = mainUI.getLeftMenuFragment();
        fragment.setMenuData(mNewsMenu.data);

        //网络请求成功后,初始化四个菜单详情页
        mPagers = new ArrayList<>();
        mPagers.add(new NewsMenuDetailPager(mActivity, mNewsMenu.data.get(0).children));
        mPagers.add(new TopicMenuDetailPager(mActivity));
        mPagers.add(new PhotosMenuDetailPager(mActivity, mBtnDisplay));
        mPagers.add(new InteractMenuDetailPager(mActivity));

        //设置新闻菜单详情页为默认页面
        setMenuDetailPager(0);
    }

    //修改菜单详情页
    public void setMenuDetailPager(int position) {
        Log.i(TAG, "修改菜单详情页啦: " + position);
        BaseMenuDetailPager pager = mPagers.get(position);

        //判断是否是组图,如果是,显示切换按钮,否则隐藏
        if (pager instanceof PhotosMenuDetailPager) {
            mBtnDisplay.setVisibility(View.VISIBLE);
        } else {
            mBtnDisplay.setVisibility(View.GONE);
        }

        //清除之前帧布局显示的内容
        flContainer.removeAllViews();
        //修改当前帧布局显示的内容
        flContainer.addView(pager.mRootView);
        //初始化当前页面数据
        pager.initData();

        //修改标题
        tvTitle.setText(mNewsMenu.data.get(position).title);
    }
}
