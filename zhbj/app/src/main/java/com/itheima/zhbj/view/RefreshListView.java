package com.itheima.zhbj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itheima.zhbj.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pb on 2016/7/10.
 * 下拉刷新的ListView
 */
public class RefreshListView extends ListView implements AbsListView.OnScrollListener{

    private static final String TAG = "RefreshListView";
    private View mHeaderView;
    private int mHeaderViewHeight;

    private static final int STATE_PULL_TO_REFRESH = 0;//下拉刷新状态
    private static final int STATE_RELEASE_TO_REFRESH = 1;//松开刷新
    private static final int STATE_REFRESHING = 2;//正在刷新

    private int mCurrentState = STATE_PULL_TO_REFRESH;//当前状态,默认下拉刷新状态

    private TextView tvState;
    private TextView tvTime;
    private ImageView ivArrow;
    private ProgressBar pbLoading;
    private RotateAnimation animUp;
    private RotateAnimation animDown;
    private View mFooterView;
    private int mFooterViewHeight;

    private OnRefreshListener mListener;

    private boolean isLoadMore = false;//表示是否正在加载更多

    public RefreshListView(Context context) {
//        super(context);
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
//        super(context, attrs);
        this(context, attrs, -1);

    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView();
        initFooterView();
    }

    //初始化脚布局
    private void initFooterView() {
        mFooterView = View.inflate(getContext(), R.layout.pull_to_refresh_foot, null);
        addFooterView(mFooterView);

        mFooterView.measure(0, 0);
        mFooterViewHeight = mFooterView.getMeasuredHeight();
        //隐藏脚布局
        mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);

        //设置滑动监听
        setOnScrollListener(this);
    }

    //初始化头布局
    private void initHeaderView() {
        mHeaderView = View.inflate(getContext(), R.layout.pull_to_refresh_header, null);
        addHeaderView(mHeaderView);//给ListView添加头布局

        tvState = (TextView) mHeaderView.findViewById(R.id.tv_state);
        tvTime = (TextView) mHeaderView.findViewById(R.id.tv_time);
        ivArrow = (ImageView) mHeaderView.findViewById(R.id.iv_arrow);
        pbLoading = (ProgressBar) mHeaderView.findViewById(R.id.pb_loading);

        initArrowAnim();
        setRefreshTime();

        //隐藏头布局
        //获取当前头布局的高度,然后设置负paddingTop,布局就会向上走
        //不能这样获取,控件没有绘制完成
//        int height = mHeaderView.getHeight();
        //手动测量,宽高传0表示不参与具体宽高的设定,全有系统决定
        mHeaderView.measure(0, 0);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        Log.i(TAG, "height: " + mHeaderView);

        mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
    }

    private int startY = -1;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (startY == -1) { //没有获取到按下的事件(按住头条新闻滑动时,按下事件被ViewPager消费了)
                    startY = (int) ev.getY();//重新获取起点位置
                }
                int endY = (int) ev.getY();
                int dy = endY - startY;

                //如果正在刷新,什么都不做
                if (mCurrentState == STATE_REFRESHING) {
                    break;
                }

                int firstVisiblePosition = this.getFirstVisiblePosition();
                if (dy > 0 && firstVisiblePosition == 0) {
                    //下拉动作&&当前在ListView顶部
                    int padding = -mHeaderViewHeight + dy;

                    if (padding > 0 && mCurrentState != STATE_RELEASE_TO_REFRESH) {
                        //切换到松开刷新状态
                        mCurrentState = STATE_RELEASE_TO_REFRESH;
                        refreshState();
                    } else if (padding <= 0 && mCurrentState != STATE_PULL_TO_REFRESH) {
                        mCurrentState = STATE_PULL_TO_REFRESH;
                        refreshState();
                    }

                    //通过修改padding来设置当前刷新控件的最新值
                    mHeaderView.setPadding(0, padding, 0, 0);
                    return true;//消费此事件,处理下拉刷新滑动,不需要listview原生效果参与
                }
                break;

            case MotionEvent.ACTION_UP:
                startY = -1;//起始坐标归零
                if (mCurrentState == STATE_RELEASE_TO_REFRESH) {
                    //切换成正在刷新
                    mCurrentState = STATE_REFRESHING;

                    //完整显示刷新控件
                    mHeaderView.setPadding(0, 0, 0, 0);
                    refreshState();
                } else if (mCurrentState == STATE_PULL_TO_REFRESH) {
                    //隐藏下拉刷新控件
                    mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
                }

                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    //初始化箭头动画
    private void initArrowAnim() {
        //箭头向上动画
        animUp = new RotateAnimation(0, -180,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animUp.setDuration(300);
        animUp.setFillAfter(true); //保持住动画结束的状态

        //箭头向下动画
        animDown = new RotateAnimation(-180, 0,
               Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animUp.setDuration(300);
        animUp.setFillAfter(true); //保持住动画结束的状态
    }

    //根据当前状态刷新界面
    private void refreshState() {
        switch (mCurrentState) {
            case STATE_PULL_TO_REFRESH:
                tvState.setText("下拉刷新");
                pbLoading.setVisibility(INVISIBLE);
                ivArrow.setVisibility(VISIBLE);
                ivArrow.startAnimation(animDown);
                break;
            case STATE_RELEASE_TO_REFRESH:
                tvState.setText("松开刷新");
                pbLoading.setVisibility(INVISIBLE);
                ivArrow.setVisibility(VISIBLE);
                ivArrow.startAnimation(animUp);
                break;
            case STATE_REFRESHING:
                tvState.setText("正在刷新");
                pbLoading.setVisibility(VISIBLE);
                ivArrow.clearAnimation();//清理动画,才能隐藏
                ivArrow.setVisibility(INVISIBLE);
                if (mListener != null) {
                    mListener.onRefresh();
                }
                break;
            default:
                break;
        }
    }

    //设置刷新时间
    private void setRefreshTime() {
        //HH:24小时制, hh:12小时制
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Date());
        tvTime.setText(time);
    }

    //刷新结束,隐藏控件
    public void onRefreshComplete() {
        if (isLoadMore) {
            //隐藏加载更多的控件
            mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
            isLoadMore = false;
        } else {
            //隐藏状态
            mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

            //初始化状态
            tvState.setText("下拉刷新");
            pbLoading.setVisibility(INVISIBLE);
            ivArrow.setVisibility(VISIBLE);
            mCurrentState = STATE_PULL_TO_REFRESH;

            //更新刷新时间
            setRefreshTime();
        }
    }

    //滑动状态发生变化
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {//空闲状态
            int lastVisiblePosition = getLastVisiblePosition();
            if (lastVisiblePosition == getCount() - 1 && !isLoadMore) {
                Log.i(TAG, "到底了");
                //显示加载中
                mFooterView.setPadding(0, 0, 0, 0);
                setSelection(getCount() - 1);

                isLoadMore = true;

                //加载更多数据
                if (mListener != null) {
                    mListener.onLoadMore();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    //回调接口,通知刷新状态
    public interface OnRefreshListener{
        //下拉刷新的回调
        void onRefresh();

        //加载更多的回调
        void onLoadMore();
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.mListener = onRefreshListener;
    }
}
