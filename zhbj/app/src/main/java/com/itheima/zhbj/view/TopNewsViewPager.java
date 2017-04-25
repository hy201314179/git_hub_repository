package com.itheima.zhbj.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by pb on 2016/7/8.
 * 头条新闻的ViewPager
 */
public class TopNewsViewPager extends ViewPager {

    private static final String TAG = "TopNewsViewPager";
    private int startX;
    private int startY;

    public TopNewsViewPager(Context context) {
        super(context);
    }

    public TopNewsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //1.上下滑动,需要拦截
    //2.左划时,最后一个需要拦截
    //3.右划时,第一个页面需要拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //请求父控件不要拦截
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //移动
                int endX = (int) ev.getX();
                int endY = (int) ev.getY();

                int dx = endX - startX;
                int dy = endY - startY;

                Log.i(TAG, "dx: " + dx + "    dy:  " + dy);

                if (Math.abs(dx) > Math.abs(dy)) {
                    //左右滑动
                    int currentItem = getCurrentItem();
                    if (dx > 0) {
                        //右划
                        if (currentItem == 0) {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    } else {
                        //左划
                        int count = getAdapter().getCount();
                        if (currentItem == count - 1) {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }
                } else {
                    //上下滑动
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }
}
