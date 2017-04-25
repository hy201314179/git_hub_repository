package com.itheima.zhbj.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.zhbj.MainActivity;
import com.itheima.zhbj.R;
import com.itheima.zhbj.base.impl.NewsCenterPager;
import com.itheima.zhbj.domain.NewsMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

/**
 * Created by pb on 2016/7/4.
 * 侧边栏的fragment
 */
public class LeftMenuFragment extends BaseFragment {

    @ViewInject(R.id.lv_menu)
    private ListView lvList;

    private ArrayList<NewsMenu.NewsMenuData> data;//分类的网络数据
    private int mCurrentPos;//当前选中的菜单位置
    private LeftMenuAdapter leftMenuAdapter;

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_left_menu, null);
        ViewUtils.inject(this, view);
        return view;
    }

    public void setMenuData(ArrayList<NewsMenu.NewsMenuData> data) {
        this.data = data;

        //当前选中位置归0,避免侧边栏选中位置和菜单详情页不同步
        mCurrentPos = 0;

        leftMenuAdapter = new LeftMenuAdapter();
        lvList.setAdapter(leftMenuAdapter);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentPos = position;
                //刷新ListView
                leftMenuAdapter.notifyDataSetChanged();

                //收回侧边栏
                toggle();

                setMenuDetailPager(position);
            }
        });
    }

    //修改菜单详情页
    private void setMenuDetailPager(int position) {
        //修改新闻中心的帧布局
        //获取新闻中心的对象
        MainActivity mainUI = (MainActivity) mActivity;
        ContentFragment fragment = mainUI.getContentFragment();
        NewsCenterPager pager = fragment.getNewsCenterPager();

        pager.setMenuDetailPager(position);
    }


    //控制侧边栏开关
    private void toggle() {
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        slidingMenu.toggle();//如果当前为开,则关;反之亦然
    }
	/**
	*设置适配器
	*/
    class LeftMenuAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public NewsMenu.NewsMenuData getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view1 = View.inflate(mActivity, R.layout.list_item_left_menu, null);
            TextView tvMenu = (TextView) view1.findViewById(R.id.tv_menu);

            //设置TextView可用或者不可用来控制颜色
            if (mCurrentPos == position) {
                tvMenu.setEnabled(true);
            } else {
                tvMenu.setEnabled(false);
            }

            NewsMenu.NewsMenuData info = getItem(position);
            tvMenu.setText(info.title);

            return view1;
        }
    }

    @Override
    public void initData() {
    }
}
