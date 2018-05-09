package com.dingmouren.example.layoutmanagergroup.fragment;

import android.graphics.BlurMaskFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingmouren.example.layoutmanagergroup.MyApplication;
import com.dingmouren.example.layoutmanagergroup.R;
import com.dingmouren.example.layoutmanagergroup.bean.SlideBean;
import com.dingmouren.example.layoutmanagergroup.widget.SmileView;
import com.dingmouren.layoutmanagergroup.slide.ItemConfig;
import com.dingmouren.layoutmanagergroup.slide.ItemTouchHelperCallback;
import com.dingmouren.layoutmanagergroup.slide.OnSlideListener;
import com.dingmouren.layoutmanagergroup.slide.SlideLayoutManager;
import com.flurgle.blurkit.BlurLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */

public class SlideFragment extends Fragment {
    private static final String TAG = "SlideFragment";
    private RecyclerView mRecyclerView;
    private SmileView mSmileView;
    private SlideLayoutManager mSlideLayoutManager;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelperCallback mItemTouchHelperCallback;
    private MyAdapter mAdapter;
    private  List<SlideBean> mList = new ArrayList<>();
    private int mLikeCount = 50;
    private int mDislikeCount = 50;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_slide, container, false);
        initView(rootView);
        initListener();
        return rootView;
    }

    private void initView(View rootView) {
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mSmileView = rootView.findViewById(R.id.smile_view);

        mSmileView.setLike(mLikeCount);
        mSmileView.setDisLike(mDislikeCount);

        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
        addData();
        mItemTouchHelperCallback = new ItemTouchHelperCallback(mRecyclerView.getAdapter(), mList);
        mItemTouchHelper = new ItemTouchHelper(mItemTouchHelperCallback);
        mSlideLayoutManager = new SlideLayoutManager(mRecyclerView, mItemTouchHelper);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(mSlideLayoutManager);

    }

    private void initListener() {
        mItemTouchHelperCallback.setOnSlideListener(new OnSlideListener() {
            @Override
            public void onSliding(RecyclerView.ViewHolder viewHolder, float ratio, int direction) {
                if (direction == ItemConfig.SLIDING_LEFT) {
                } else if (direction == ItemConfig.SLIDING_RIGHT) {
                }
            }

            @Override
            public void onSlided(RecyclerView.ViewHolder viewHolder, Object o, int direction) {
                if (direction == ItemConfig.SLIDED_LEFT) {
                    mDislikeCount--;
                    mSmileView.setDisLike(mDislikeCount);
                    mSmileView.disLikeAnimation();
                } else if (direction == ItemConfig.SLIDED_RIGHT) {
                    mLikeCount++;
                    mSmileView.setLike(mLikeCount);
                    mSmileView.likeAnimation();
                }
                int position = viewHolder.getAdapterPosition();
                Log.e(TAG, "onSlided--position:" + position);
            }

            @Override
            public void onClear() {
                addData();
            }
        });
    }

    /**
     * 向集合中添加数据
     */
    private void addData(){
        int[] icons = {R.mipmap.header_icon_1, R.mipmap.header_icon_2, R.mipmap.header_icon_3,
                R.mipmap.header_icon_4, R.mipmap.header_icon_1, R.mipmap.header_icon_2};
        String[] titles = {"Acknowledging", "Belief", "Confidence", "Dreaming", "Happiness", "Confidence"};
        String[] says = {
                "Do one thing at a time, and do well.",
                "Keep on going never give up.",
                "Whatever is worth doing is worth doing well.",
                "I can because i think i can.",
                "Jack of all trades and master of none.",
                "Keep on going never give up.",
                "Whatever is worth doing is worth doing well.",
        };
        int[] bgs = {
                R.mipmap.img_slide_1,
                R.mipmap.img_slide_2,
                R.mipmap.img_slide_3,
                R.mipmap.img_slide_4,
                R.mipmap.img_slide_5,
                R.mipmap.img_slide_6
        };

        for (int i = 0; i < 6; i++) {
            mList.add(new SlideBean(bgs[i],titles[i],icons[i],says[i]));
        }
    }


    /**
     * 适配器
     */
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MyApplication.sContext).inflate(R.layout.item_slide, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
                SlideBean bean = mList.get(position);
                holder.imgBg.setImageResource(bean.getItemBg());
                holder.tvTitle.setText(bean.getTitle());
                holder.userIcon.setImageResource(bean.getUserIcon());
                holder.userSay.setText(bean.getUserSay());
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgBg;
            ImageView userIcon;
            TextView tvTitle;
            TextView userSay;

            public ViewHolder(View itemView) {
                super(itemView);
                imgBg = itemView.findViewById(R.id.img_bg);
                userIcon = itemView.findViewById(R.id.img_user);
                tvTitle = itemView.findViewById(R.id.tv_title);
                userSay = itemView.findViewById(R.id.tv_user_say);
            }
        }
    }
}
