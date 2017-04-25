package com.itheima.zhbj;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

/**
 * 新闻详情页
 */
public class NewsDetailActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "NewsDetailActivity";
    @ViewInject(R.id.but_back)
    private ImageButton btnBack;
    @ViewInject(R.id.but_menu)
    private ImageButton btnMenu;
    @ViewInject(R.id.ll_control)
    private LinearLayout llControl;
    @ViewInject(R.id.but_textsize)
    private ImageButton btnTextSize;
    @ViewInject(R.id.but_share)
    private ImageButton btnShare;
    @ViewInject(R.id.webview)
    private WebView mWebView;
    @ViewInject(R.id.pb_loading)
    private ProgressBar pbLoading;
    private int mTempWhich;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_news_detail);

        ViewUtils.inject(this);
        initView();

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);//启用js功能
        settings.setBuiltInZoomControls(true);//支持缩放按钮(不支持已经适配好移动端的页面)
        settings.setUseWideViewPort(true);//实现双击缩放

        String url = getIntent().getStringExtra("url");
        //开始加载网页
        mWebView.loadUrl(url);

        mWebView.setWebViewClient(new WebViewClient(){
            //页面开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pbLoading.setVisibility(View.VISIBLE);
            }

            //跳转链接
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //所有跳转链接强制在当前webview加载,不跳浏览器
                mWebView.loadUrl(url);
                return true;
            }

            //加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbLoading.setVisibility(View.GONE);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient(){
            //获取网页标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.i(TAG, "onReceivedTitle: " + title);
            }
            //进度发生变化
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.i(TAG, "onProgressChanged: " + newProgress);
            }
        });
    }

    private void initView() {
        btnBack.setVisibility(View.VISIBLE);
        btnMenu.setVisibility(View.GONE);
        llControl.setVisibility(View.VISIBLE);

        btnBack.setOnClickListener(this);
        btnTextSize.setOnClickListener(this);
        btnShare.setOnClickListener(this);
    }

    //拦截物理返回键
    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {//判断是否可以返回
            mWebView.goBack();//返回上一个网页

//            mWebView.goForward();//调到下一个网页(前提是之前浏览过,有历史记录)
//            mWebView.canGoForward();//判断是否可以调到下一个网页
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.but_back:
                finish();
                break;

            case R.id.but_textsize:
                showChooseDialog();
                break;

            case R.id.but_share:
                showShare();
                break;

            default:
                break;
        }
    }

    private int mCurrentWhich = 2;

    //显示选择字体的窗体
    private void showChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = new String[]{"超大号字体", "大号字体", "正常字体", "小号字体", "超小号字体"};

        //显示单选框,参数1:单选字符串数组, 参数2:当前默认选中的位置, 参数3:选中监听
        builder.setSingleChoiceItems(items, mCurrentWhich, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "onClick: " + which);
                mTempWhich = which;
            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WebSettings settings = mWebView.getSettings();
                switch (mTempWhich) {
                    case 0:
                        //超大号
                        settings.setTextSize(WebSettings.TextSize.LARGEST);
//                        settings.setTextZoom(30);
                        break;
                    case 1:
                        settings.setTextSize(WebSettings.TextSize.LARGER);
                        break;
                    case 2:
                        settings.setTextSize(WebSettings.TextSize.NORMAL);
                        break;
                    case 3:
                        settings.setTextSize(WebSettings.TextSize.SMALLER);
                        break;
                    case 4:
                        settings.setTextSize(WebSettings.TextSize.SMALLEST);
                        break;
                    default:
                        break;
                }
                mCurrentWhich = mTempWhich;
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();

        oks.setTheme(OnekeyShareTheme.CLASSIC);

        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.ssdk_oks_share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }
}
