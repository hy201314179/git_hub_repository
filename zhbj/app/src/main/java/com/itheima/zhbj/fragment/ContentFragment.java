package com.itheima.zhbj.fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.itheima.zhbj.MainActivity;
import com.itheima.zhbj.R;
import com.itheima.zhbj.base.BasePager;
import com.itheima.zhbj.base.impl.GovAffairsPager;
import com.itheima.zhbj.base.impl.HomePager;
import com.itheima.zhbj.base.impl.NewsCenterPager;
import com.itheima.zhbj.base.impl.SettingPager;
import com.itheima.zhbj.base.impl.SmartServicePager;
import com.itheima.zhbj.view.NoScrollViewPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.view.annotation.ViewInject;


import java.util.ArrayList;

/**
 * Created by pb on 2016/7/4.
 * 主页面的fragment
 */
public class ContentFragment extends BaseFragment {

    private ArrayList<BasePager> mList;//五个标签页的集合

    @ViewInject(R.id.vp_content)
    private NoScrollViewPager mViewPager;
    @ViewInject(R.id.rg_group)
    private RadioGroup rgGroup;

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_content, null);
        com.lidroid.xutils.ViewUtils.inject(this, view);
//        mViewPager = (NoScrollViewPager) view.findViewById(R.id.vp_content);
//        rgGroup = (RadioGroup) view.findViewById(R.id.rg_group);
        return view;
    }

    @Override
    public void initData() {
        mList = new ArrayList<>();

        //添加五个标签页
        mList.add(new HomePager(getActivity()));
        mList.add(new NewsCenterPager(getActivity()));
        mList.add(new SmartServicePager(getActivity()));
        mList.add(new GovAffairsPager(getActivity()));
        mList.add(new SettingPager(getActivity()));

        mViewPager.setAdapter(new ContentAdapter());

        //底栏标签切换监听
        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_home:
                        //首页
//                        mViewPager.setCurrentItem(0);
                        //设置没有滚动的动画效果
                        mViewPager.setCurrentItem(0, false);
                        break;
                    case R.id.rb_news:
                        //新闻中心
                        mViewPager.setCurrentItem(1, false);
                        break;
                    case R.id.rb_smart:
                        //智慧服务
                        mViewPager.setCurrentItem(2, false);
                        break;
                    case R.id.rb_gov:
                        //政务
                        mViewPager.setCurrentItem(3, false);
                        break;
                    case R.id.rb_setting:
                        //设置
                        mViewPager.setCurrentItem(4, false);
                        break;
                }
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                BasePager pager = mList.get(position);
                pager.initData();

                if (position == 0 || position == mList.size() - 1) {
                    //首页和设置页要禁用侧边栏
                    setSlidingMenuEnable(false);
                } else {
                    //其他页面开启侧边栏
                    setSlidingMenuEnable(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //手动加载第一页数据
        mList.get(0).initData();

        setSlidingMenuEnable(false);
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

    class ContentAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BasePager pager = mList.get(position);
            View view = pager.mRootView;//获取当前页面对象的布局
            //初始化数据, ViewPager会默认加载下一个页面,
            //为了节省流量和性能,不要在此处调用初始化数据的方法
//            pager.initData();
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    //获取新闻中心的对象
    public NewsCenterPager getNewsCenterPager() {
        NewsCenterPager pager = (NewsCenterPager) mList.get(1);
        return pager;
    }
}
