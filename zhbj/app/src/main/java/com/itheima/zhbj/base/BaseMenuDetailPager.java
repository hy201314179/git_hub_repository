package com.itheima.zhbj.base;

import android.app.Activity;
import android.view.View;

/**
 * Created by pb on 2016/7/6.
 * 菜单详情页基类,新闻专题组图互动
 */
public abstract class BaseMenuDetailPager {
    public Activity mActivity;
    public View mRootView;//菜单详情页的根部局

    public BaseMenuDetailPager(Activity activity) {
        mActivity = activity;
        mRootView = initViews();
    }
    public abstract View initViews();

    //初始化数据
    public void initData(){
    }
}

