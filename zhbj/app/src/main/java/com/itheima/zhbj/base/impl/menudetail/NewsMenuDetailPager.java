package com.itheima.zhbj.base.impl.menudetail;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.itheima.zhbj.MainActivity;
import com.itheima.zhbj.R;
import com.itheima.zhbj.base.BaseMenuDetailPager;
import com.itheima.zhbj.base.impl.TabDetailPager;
import com.itheima.zhbj.domain.NewsMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

/**
 * Created by pb on 2016/7/6.
 * 菜单详情页 - 新闻
 *
 * ViewPagerIndicator:
 * 1.引入ViewPagerIndicator库
 * 2.解决v4冲突,用大的版本覆盖小的版本,注意关联源码
 * 3.仿照smaple中的程序进行拷贝SampleTabsDefault
 * 4.拷贝布局文件和相关代码,将ViewPager和Indicator关联在一起
 * 5.重写PagerAdapter和getPagerTitle方法返回指示器标题
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager implements ViewPager.OnPageChangeListener {

    private static final String TAG = "NewsMenuDetailPager";

    @ViewInject(R.id.vp_news_menu_detail)
    private ViewPager mViewPager;
    @ViewInject(R.id.indicator)
    private TabPageIndicator mIndicator;

    private  ArrayList<NewsMenu.NewsTabData> children;
    private ArrayList<BaseMenuDetailPager> mPagers;

    public NewsMenuDetailPager(Activity activity, ArrayList<NewsMenu.NewsTabData> children) {
        super(activity);
        this.children = children;
    }

    @Override
    public View initViews() {
        /*TextView view = new TextView(mActivity);
        view.setText("菜单详情页-新闻");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);*/
        View view = View.inflate(mActivity, R.layout.pager_news_menu_detail, null);
        ViewUtils.inject(this, view);
//        mIndicator.getParent().requestDisallowInterceptTouchEvent(true);
        return view;
    }

    @Override
    public void initData() {
        //初始化12个页签对象
        //以服务器为准
        mPagers = new ArrayList<>();
        for (int i = 0; i < children.size(); i++) {
            TabDetailPager pager = new TabDetailPager(mActivity, children.get(i));
            mPagers.add(pager);
        }

        mViewPager.setAdapter(new NewsMenuDetailAdapter());
        mIndicator.setViewPager(mViewPager);//将ViewPager和Indicator关联在一起; 注意:必须setAdapter之后

        //设置页面触摸监听
        //当ViewPager和indicator绑定,事件需要设置给indicator
        mIndicator.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.i(TAG, "onPageSelected: " + position);
        if (position == 0) {
            setSlidingMenuEnable(true);
        } else {
            setSlidingMenuEnable(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 控制侧边栏滑动
     * @param enable
     */
    private void setSlidingMenuEnable(boolean enable) {
        //获取侧边栏对象
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();

        if (enable) {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

    class NewsMenuDetailAdapter extends PagerAdapter{

        @Override
        public CharSequence getPageTitle(int position) {
            return children.get(position).title;
        }

        @Override
        public int getCount() {
            return mPagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BaseMenuDetailPager pager = mPagers.get(position);
            View view = pager.mRootView;
            pager.initData();//初始化数据
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @OnClick(R.id.btn_next) //通过注解方式绑定事件,注意:在xml中配置onClick属性只适用activity
    public void nextPager(View view){
        //调到下一个页面
        int currentPos = mViewPager.getCurrentItem();
        mViewPager.setCurrentItem(++currentPos);
    }
}
