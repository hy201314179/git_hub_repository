package com.itheima.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.itheima.zhbj.base.BasePager;

/**
 * Created by pb on 2016/7/4.
 * 智慧服务
 */
public class SmartServicePager extends BasePager {
    public SmartServicePager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        //给帧布局填充布局对象
        TextView view = new TextView(mActivity);
        view.setText("智慧服务");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);
        flContainer.addView(view);

        tvTitle.setText("智慧服务");
        btnMenu.setVisibility(View.VISIBLE);
    }
}
