package com.itheima.zhbj.base;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.itheima.zhbj.MainActivity;
import com.itheima.zhbj.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * Created by pb on 2016/7/4.
 * 五个标签页的基类
 */
public class BasePager {
    public Activity mActivity;
    public TextView tvTitle;
    public ImageButton btnMenu;
    public FrameLayout flContainer;//空的帧布局对象,要动态添加布局
    public View mRootView;//当前页面的布局对象

    public ImageButton mBtnDisplay;//组图的切换按钮
    public BasePager(Activity activity){
        this.mActivity = activity;
        mRootView = initView();
    }

    public View initView(){
        View view = View.inflate(mActivity, R.layout.base_pager, null);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        btnMenu = (ImageButton) view.findViewById(R.id.but_menu);
        flContainer = (FrameLayout) view.findViewById(R.id.fl_container);
        mBtnDisplay = (ImageButton) view.findViewById(R.id.btn_diplay);
        //点击侧边栏按钮,控制侧边栏开关
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        return view;
    }

    //控制侧边栏开关
    private void toggle() {
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        slidingMenu.toggle();//如果当前为开,则关;反之亦然
    }

    public void initData(){
    }
}
