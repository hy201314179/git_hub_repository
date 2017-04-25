package com.itheima.zhbj;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by pb on 2016/7/17.
 */
public class BaseActivity extends Activity {

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
