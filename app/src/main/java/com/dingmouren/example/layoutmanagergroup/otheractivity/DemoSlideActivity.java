package com.dingmouren.example.layoutmanagergroup.otheractivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dingmouren.example.layoutmanagergroup.MyApplication;
import com.dingmouren.example.layoutmanagergroup.R;
import com.dingmouren.example.layoutmanagergroup.fragment.SlideFragment;
import com.dingmouren.example.layoutmanagergroup.widget.SmileView;
import com.dingmouren.layoutmanagergroup.slide.ItemConfig;
import com.dingmouren.layoutmanagergroup.slide.ItemTouchHelperCallback;
import com.dingmouren.layoutmanagergroup.slide.OnSlideListener;
import com.dingmouren.layoutmanagergroup.slide.SlideLayoutManager;
import com.flurgle.blurkit.BlurLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/8.
 */

public class DemoSlideActivity extends AppCompatActivity {
    private static final String TAG = "DemoSlideActivity";
    private RecyclerView mRecyclerView;
    private SmileView mSmileView;
    private SlideLayoutManager mSlideLayoutManager;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelperCallback mItemTouchHelperCallback;
    private static List<Integer> mImgList = new ArrayList<>();
    private int mLikeCount = 50;
    private int mDislikeCount = 50;


    static {
        mImgList.add(R.mipmap.img_slide_1);
        mImgList.add(R.mipmap.img_slide_2);
        mImgList.add(R.mipmap.img_slide_3);
        mImgList.add(R.mipmap.img_slide_4);
        mImgList.add(R.mipmap.img_slide_5);
        mImgList.add(R.mipmap.img_slide_6);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_slide);

        initView();

        initListener();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mSmileView = findViewById(R.id.smile_view);

        mSmileView.setLike(mLikeCount);
        mSmileView.setDisLike(mDislikeCount);


        mRecyclerView.setAdapter(new MyAdapter());
        mItemTouchHelperCallback = new ItemTouchHelperCallback(mRecyclerView.getAdapter(),mImgList);
        mItemTouchHelper = new ItemTouchHelper(mItemTouchHelperCallback);
        mSlideLayoutManager = new SlideLayoutManager(mRecyclerView,mItemTouchHelper);
        mRecyclerView.setLayoutManager(mSlideLayoutManager);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void initListener() {
        mItemTouchHelperCallback.setOnSlideListener(new OnSlideListener() {
            @Override
            public void onSliding(RecyclerView.ViewHolder viewHolder, float ratio, int direction) {
                if (direction == ItemConfig.SLIDING_LEFT){
                }else if (direction == ItemConfig.SLIDING_RIGHT){
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
            }

            @Override
            public void onClear() {
                    Log.e(TAG,"onClear");
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
