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
import com.dingmouren.example.layoutmanagergroup.widget.SmileView;
import com.dingmouren.layoutmanagergroup.slide.ItemConfig;
import com.dingmouren.layoutmanagergroup.slide.ItemTouchHelperCallback;
import com.dingmouren.layoutmanagergroup.slide.OnSlideListener;
import com.dingmouren.layoutmanagergroup.slide.SlideLayoutManager;
import com.flurgle.blurkit.BlurLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by dingmouren on 2018/5/7.
 */

public class SlideFragment extends Fragment {
    private static final String TAG = "SlideFragment";
    private RecyclerView mRecyclerView;
    private SmileView mSmileView;
    private SlideLayoutManager mSlideLayoutManager;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelperCallback mItemTouchHelperCallback;
    private MyAdapter mAdapter;
    private static List<Integer> mImgList = new ArrayList<>();
    private int mLikeCount = 50;
    private int mDislikeCount = 50;
    private int mCurrentPosition = 50;

    static {
        mImgList.add(R.mipmap.img_slide_1);
        mImgList.add(R.mipmap.img_slide_2);
        mImgList.add(R.mipmap.img_slide_3);
        mImgList.add(R.mipmap.img_slide_4);
        mImgList.add(R.mipmap.img_slide_5);
        mImgList.add(R.mipmap.img_slide_6);
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
        mSmileView = rootView.findViewById(R.id.smile_view);

        mSmileView.setLike(mLikeCount);
        mSmileView.setDisLike(mDislikeCount);

        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
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
                if (direction == ItemConfig.SLIDING_LEFT){
                    Log.e(TAG,"onSling--左   radio:"+ratio+"  position:"+viewHolder.getLayoutPosition());
                }else if (direction == ItemConfig.SLIDING_RIGHT){
                    Log.e(TAG,"onSling--右   radio:"+ratio+"  position:"+viewHolder.getLayoutPosition());
                }
            }

            @Override
            public void onSlided(RecyclerView.ViewHolder viewHolder, Object o, int direction) {
                if (direction == ItemConfig.SLIDED_LEFT){
                    mDislikeCount--;
                    mSmileView.setDisLike(mDislikeCount);
                    mSmileView.disLikeAnimation();
                }else if (direction == ItemConfig.SLIDED_RIGHT){
                    mLikeCount++;
                    mSmileView.setLike(mLikeCount);
                    mSmileView.likeAnimation();
                }
                mCurrentPosition--;
                mAdapter.notifyItemChanged(0);
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
        private int[] icons = {R.mipmap.header_icon_1,R.mipmap.header_icon_2,R.mipmap.header_icon_3,R.mipmap.header_icon_4};
        private String[] titles = {"Acknowledging","Belief","Confidence","Dreaming","Happiness"};
        private String[] says = {
                "Do one thing at a time, and do well.",
                "Keep on going never give up.",
                "Whatever is worth doing is worth doing well.",
                "I can because i think i can.",
                "Jack of all trades and master of none."
        };
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MyApplication.sContext).inflate(R.layout.item_slide,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.imgBg.setImageResource(mImgList.get(mCurrentPosition%6));
            holder.userIcon.setImageResource(icons[mCurrentPosition%4]);
            holder.tvTitle.setText(titles[mCurrentPosition%5]);
            holder.userSay.setText(says[mCurrentPosition%5]);
        }

        @Override
        public int getItemCount() {
            return 50;
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
