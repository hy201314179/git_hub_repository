package com.itheima.zhbj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.itheima.zhbj.utils.PrefUtils;

public class SplashActivity extends BaseActivity {

    private RelativeLayout rlRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        rlRoot = (RelativeLayout) findViewById(R.id.rl_root);

        //旋转动画
        RotateAnimation animRotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animRotate.setDuration(1000); //动画时间
        animRotate.setFillAfter(true); //保持动画结束状态

        //缩放动画
        ScaleAnimation animScale = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF
                , 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animScale.setDuration(1000);
        animScale.setFillAfter(true);//保持动画结束状态

        //渐变动画
        AlphaAnimation animAlpha = new AlphaAnimation(0, 1);
        animAlpha.setDuration(2000);
        animAlpha.setFillAfter(true);

        //动画集合
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(animRotate);
        set.addAnimation(animScale);
        set.addAnimation(animAlpha);

        //启动动画
        rlRoot.startAnimation(set);

        set.setAnimationListener(new Animation.AnimationListener() {

            private Intent intent;

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画结束,跳转页面
                //如果是第一次进入,跳新手引导
                //否则跳主页面
                boolean isFirstEnter = PrefUtils.getBoolean(SplashActivity.this, "isFirstEnter", true);
                if (isFirstEnter) {
                    //新手引导
                    intent = new Intent(SplashActivity.this, GuideActivity.class);
                }else {
                    //主页面
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
