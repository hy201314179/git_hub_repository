package com.itheima.zhbj.base.impl.menudetail;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itheima.zhbj.R;
import com.itheima.zhbj.base.BaseMenuDetailPager;
import com.itheima.zhbj.domain.PhotosBean;
import com.itheima.zhbj.global.GlobalConstants;
import com.itheima.zhbj.utils.CacheUtils;
import com.itheima.zhbj.utils.bitmap.MyBitmapUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

/**
 * Created by pb on 2016/7/6.
 * 菜单详情页 - 图片
 */
public class PhotosMenuDetailPager extends BaseMenuDetailPager implements View.OnClickListener{

    @ViewInject(R.id.lv_list)
    private ListView mLvList;

    @ViewInject(R.id.gv_list)
    private GridView mGvList;
    private ArrayList<PhotosBean.PhotoNews> mPhotoList;
    private ImageButton mBtnDisplay;

    public PhotosMenuDetailPager(Activity activity, ImageButton mBtnDisplay) {
        super(activity);
        this.mBtnDisplay = mBtnDisplay;
        mBtnDisplay.setOnClickListener(this);
    }

    @Override
    public View initViews() {
        /*TextView view = new TextView(mActivity);
        view.setText("菜单详情页-图片");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);*/

        View view = View.inflate(mActivity, R.layout.pager_photos_menu_detail, null);
        ViewUtils.inject(this, view);

        float density = mActivity.getResources().getDisplayMetrics().density;

        return view;
    }

    @Override
    public void initData() {
        String cache = CacheUtils.getCache(mActivity, GlobalConstants.PHOTOS_URL);
        if (!TextUtils.isEmpty(cache)) {
            processData(cache);
        }
        getDataFromServer();
    }

    public void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, GlobalConstants.PHOTOS_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result);
                CacheUtils.setCache(mActivity, GlobalConstants.PHOTOS_URL, result);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void processData(String result) {
        Gson gson = new Gson();
        PhotosBean photosBean = gson.fromJson(result, PhotosBean.class);
        PhotosBean.PhotoData data = photosBean.data;
        mPhotoList = data.news;

        //给ListView设置数据
        mLvList.setAdapter(new PhotoAdapter());

        //给GridView设置数据
        mGvList.setAdapter(new PhotoAdapter());
    }

    class PhotoAdapter extends BaseAdapter{

        private ViewHolder holder;
//        private final BitmapUtils mBitmapUtils;
        private MyBitmapUtils myBitmapUtils;

        public PhotoAdapter() {
//            mBitmapUtils = new BitmapUtils(mActivity);
//            mBitmapUtils.configDefaultLoadingImage(R.drawable.pic_item_list_default);

            myBitmapUtils = new MyBitmapUtils(mActivity);
        }

        @Override
        public int getCount() {
            return mPhotoList.size();
        }

        @Override
        public PhotosBean.PhotoNews getItem(int position) {
            return mPhotoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.list_item_photo, null);
                holder = new ViewHolder();
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                holder.ivPic = (ImageView) convertView.findViewById(R.id.iv_pic);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            PhotosBean.PhotoNews item = getItem(position);
            holder.tvTitle.setText(item.title);
//            mBitmapUtils.display(holder.ivPic, item.listimage);
            myBitmapUtils.display(holder.ivPic, item.listimage);

            return convertView;
        }
    }

    static class ViewHolder{
        public TextView tvTitle;
        public ImageView ivPic;
    }

    private boolean isListView = true;//判断是否是ListView
    @Override
    public void onClick(View v) {
        if (isListView) {
            mLvList.setVisibility(View.GONE);
            mGvList.setVisibility(View.VISIBLE);
            isListView = false;
            mBtnDisplay.setImageResource(R.drawable.icon_pic_list_type);
        } else {
            mLvList.setVisibility(View.VISIBLE);
            mGvList.setVisibility(View.GONE);
            isListView = true;
            mBtnDisplay.setImageResource(R.drawable.icon_pic_grid_type);
        }
    }

}
