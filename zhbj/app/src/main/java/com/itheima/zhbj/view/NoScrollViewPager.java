package com.itheima.zhbj.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by pb on 2016/7/4.
 */
public class NoScrollViewPager extends ViewPager {
    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //重新此方法,实现对滑动事件禁用
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //true表示拦截,false表示不拦截,传递给子控件
        return false;
    }
}
