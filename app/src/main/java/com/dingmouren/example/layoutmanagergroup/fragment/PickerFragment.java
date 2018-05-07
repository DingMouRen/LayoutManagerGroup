package com.dingmouren.example.layoutmanagergroup.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dingmouren.example.layoutmanagergroup.MyApplication;
import com.dingmouren.example.layoutmanagergroup.R;
import com.dingmouren.layoutmanagergroup.picker.PickerLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */


public class PickerFragment extends Fragment  {
    private static final String TAG = "PickerFragment";
    private RecyclerView mRecyclerView1;
    private RecyclerView mRecyclerView2;
    private TextView mTvHour;
    private TextView mTvMinute;
    private PickerLayoutManager mPickerLayoutManager1;
    private PickerLayoutManager mPickerLayoutManager2;
    private static List<String> mHours = new ArrayList<>();
    private static List<String> mMinutes = new ArrayList<>();

    static {
        for (int i = 1; i <= 24 ; i++) {
            if (i <= 9){
                mHours.add("0"+i);
            }else {
                mHours.add(i + "");
            }
        }

        for (int i = 0; i < 60; i++) {
            if (i <= 9){
                mMinutes.add("0"+i);
            }else {
                mMinutes.add(i + "");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_picker,container,false);
        initView(rootView);
        initListener();
        return rootView;
    }

    private void initView(View rootView) {
        mRecyclerView1 = rootView.findViewById(R.id.recycler1);
        mRecyclerView2 = rootView.findViewById(R.id.recycler2);
        mTvHour = rootView.findViewById(R.id.tv_hour);
        mTvMinute = rootView.findViewById(R.id.tv_minute);

        mPickerLayoutManager1 = new PickerLayoutManager(getContext(),mRecyclerView1, PickerLayoutManager.VERTICAL, false,3,0.4f,true);
        mRecyclerView1.setLayoutManager(mPickerLayoutManager1);
        mRecyclerView1.setAdapter(new MyAdapter(mHours));

        mPickerLayoutManager2 = new PickerLayoutManager(getContext(),mRecyclerView2, PickerLayoutManager.VERTICAL, false,3,0.4f,true);
        mRecyclerView2.setLayoutManager(mPickerLayoutManager2);
        mRecyclerView2.setAdapter(new MyAdapter(mMinutes));

    }

    private void initListener(){
        mPickerLayoutManager1.OnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position) {
                TextView textView = (TextView) view;
                Log.e(TAG,"layoutmanager1--"+textView.getText());
               if (textView != null)mTvHour.setText(textView.getText());
            }
        });

        mPickerLayoutManager2.OnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position) {
                TextView textView = (TextView) view;
                Log.e(TAG,"layoutmanager2--"+textView.getText());
                if (textView != null)mTvMinute.setText(textView.getText());
            }
        });
    }



    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
        private int[] mColors = {Color.YELLOW,Color.RED};
        private List<String> mList ;

        public MyAdapter(List<String> list) {
            this.mList = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MyApplication.sContext).inflate(R.layout.item_picker,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tvText.setText(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView tvText;
            public ViewHolder(View itemView) {
                super(itemView);
                tvText = itemView.findViewById(R.id.tv_text);
            }
        }
    }
}
