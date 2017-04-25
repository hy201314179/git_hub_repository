package com.itheima.zhbj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.zhbj.utils.DensityUtils;
import com.itheima.zhbj.utils.PrefUtils;

import java.util.ArrayList;

/**
 * 新手引导界面
 */
public class GuideActivity extends BaseActivity {

    private static final String TAG = "GuideActivity";
    private ViewPager mViewPager;

    private ArrayList<ImageView> mImageViewList; //ImageView集合

    //引导页图片id数组
    private int[] mImageIds = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
    private LinearLayout llContainer;
    private ImageView ivRedPoint; //小红点
    private int mPointDis;//小红点移动距离
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题,必须在setContentView之前
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题
        setContentView(R.layout.activity_guide);

        mViewPager = (ViewPager) findViewById(R.id.vp_guide);
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
        ivRedPoint = (ImageView) findViewById(R.id.iv_red);
        btnStart = (Button) findViewById(R.id.btn_start);

        initData();
        mViewPager.setAdapter(new GuideAdapter());
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //当页面滑动过程中的回调
                Log.i(TAG, "当前位置: " + position +  " ,移动偏移百分比:　" + positionOffset);
                //更新小红点移动距离
                int leftMargin = (int) (mPointDis * positionOffset) + position * mPointDis;//计算小红点更改距离
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivRedPoint.getLayoutParams();
                params.leftMargin = leftMargin;//修改左边距
                ivRedPoint.setLayoutParams(params);//重新设置布局参数
            }

            @Override
            public void onPageSelected(int position) {
                //某个页面被选中
                if (position == mImageViewList.size() - 1) {//最后一个页面显示
                    btnStart.setVisibility(View.VISIBLE);
                } else {
                    btnStart.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //页面状态发生变化的回调
            }
        });

        //计算两个圆点的距离
        //移动距离=第二个圆点left值-第一个圆点left值
        //measure-->layout-->draw(activity的onCreate方法执行结束之后才会走此流程)
        //监听layout方法结束的事件,位置确定好之后再获取圆点间距
        ivRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //移除回调,避免重复回调
                ivRedPoint.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //layout方法执行结束的回调
                mPointDis = llContainer.getChildAt(1).getLeft() - llContainer.getChildAt(0).getLeft();
                System.out.println("小点点的距离啊: " + mPointDis);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //更新SP
                PrefUtils.putBoolean(getApplicationContext(), "isFirstEnter", false);
                //跳到主页面
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }



    //初始化数据
    private void initData() {
        mImageViewList = new ArrayList<>();
        for (int i = 0; i < mImageIds.length; i++) {
            ImageView view = new ImageView(this);
            view.setBackgroundResource(mImageIds[i]);
            mImageViewList.add(view);

            //初始化小圆点
            ImageView point = new ImageView(this);
            point.setImageResource(R.drawable.shape_point_gray);//设置图片(形状)

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT );

            if (i > 0) {
                //从第二个点开始设置左边距
                params.leftMargin = DensityUtils.dip2px(10,this);
            }
            point.setLayoutParams(params);
            llContainer.addView(point);//添加到容器
        }
    }

    class GuideAdapter extends PagerAdapter{

        //item的个数
        @Override
        public int getCount() {
            return mImageViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //初始化item布局
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = mImageViewList.get(position);
            container.addView(view);
            return view;
        }

        //销毁item
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
