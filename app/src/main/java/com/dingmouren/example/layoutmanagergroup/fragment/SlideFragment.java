package com.dingmouren.example.layoutmanagergroup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dingmouren.example.layoutmanagergroup.MyApplication;
import com.dingmouren.example.layoutmanagergroup.R;
import com.dingmouren.layoutmanagergroup.slide.ItemTouchHelperCallback;
import com.dingmouren.layoutmanagergroup.slide.OnSlideListener;
import com.dingmouren.layoutmanagergroup.slide.SlideLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dingmouren on 2018/5/7.
 */

public class SlideFragment extends Fragment {
    private static final String TAG = "SlideFragment";
    private RecyclerView mRecyclerView;
    private SlideLayoutManager mSlideLayoutManager;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelperCallback mItemTouchHelperCallback;
    private static List<Integer> mImgList = new ArrayList<>();

    static {
        mImgList.add(R.mipmap.bg_1);
        mImgList.add(R.mipmap.bg_1);
        mImgList.add(R.mipmap.bg_1);
        mImgList.add(R.mipmap.bg_1);
        mImgList.add(R.mipmap.bg_1);
        mImgList.add(R.mipmap.bg_1);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_slide,container,false);
        initView(rootView);
        initListener();
        return rootView;
    }

    private void initView(View rootView) {
        mRecyclerView = rootView.findViewById(R.id.recycler_view);

        mRecyclerView.setAdapter(new MyAdapter());
        mItemTouchHelperCallback = new ItemTouchHelperCallback(mRecyclerView.getAdapter(),mImgList);
        mItemTouchHelper = new ItemTouchHelper(mItemTouchHelperCallback);
        mSlideLayoutManager = new SlideLayoutManager(mRecyclerView,mItemTouchHelper);
        mRecyclerView.setLayoutManager(mSlideLayoutManager);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void initListener(){
        mItemTouchHelperCallback.setOnSlideListener(new OnSlideListener() {
            @Override
            public void onSliding(RecyclerView.ViewHolder viewHolder, float ratio, int direction) {

            }

            @Override
            public void onSlided(RecyclerView.ViewHolder viewHolder, Object o, int direction) {

            }

            @Override
            public void onClear() {

            }
        });
    }

    /**
     * 适配器
     */
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MyApplication.sContext).inflate(R.layout.item_slide,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.imgBg.setImageResource(mImgList.get(position));
        }

        @Override
        public int getItemCount() {
            return mImgList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgBg;
            public ViewHolder(View itemView) {
                super(itemView);
                imgBg = itemView.findViewById(R.id.img_bg);
            }
        }
    }
}
