package com.dingmouren.example.layoutmanagergroup.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dingmouren.example.layoutmanagergroup.MyApplication;
import com.dingmouren.example.layoutmanagergroup.R;

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */


public class SkidRightActivity_2 extends AppCompatActivity {
    private ImageView mImgBg;
    private CardView mCardView;
    private TextView mTvTitle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skid_2);
        mImgBg = findViewById(R.id.img_bg);
        mCardView = findViewById(R.id.card_view);
        mTvTitle = findViewById(R.id.tv_title);
        if (getIntent() != null){
            int imgPath = getIntent().getIntExtra("img",R.mipmap.skid_right_3);
            String title = getIntent().getStringExtra("title");
            mTvTitle.setText(title);
            Glide.with(MyApplication.sContext).load(imgPath).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mImgBg);
        }
    }
}
