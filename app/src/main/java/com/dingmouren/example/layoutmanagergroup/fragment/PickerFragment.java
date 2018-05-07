package com.dingmouren.example.layoutmanagergroup.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dingmouren.example.layoutmanagergroup.MyApplication;
import com.dingmouren.example.layoutmanagergroup.R;
import com.dingmouren.layoutmanagergroup.picker.PickerLayoutManager;

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */


public class PickerFragment extends Fragment implements PickerLayoutManager.onScrollStopListener {
    private static final String TAG = "PickerFragment";
    private RecyclerView mRecyclerView;
    private PickerLayoutManager mPickerLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_picker,container,false);
        initView(rootView);
        initListener();
        return rootView;
    }

    private void initView(View rootView) {
        mRecyclerView = rootView.findViewById(R.id.recycler);

        mPickerLayoutManager = new PickerLayoutManager(getContext(), PickerLayoutManager.HORIZONTAL, false,0);
        mRecyclerView.setLayoutManager(mPickerLayoutManager);
        mRecyclerView.setAdapter(new MyAdapter());

    }

    private void initListener(){
        mPickerLayoutManager.setOnScrollStopListener(this);
    }

    @Override
    public void selectedView(View view) {
        TextView textView = (TextView) view;
        Toast.makeText(MyApplication.sContext,textView.getText(),Toast.LENGTH_SHORT).show();
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
        private int[] mColors = {Color.YELLOW,Color.RED};

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MyApplication.sContext).inflate(R.layout.item_picker,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tvText.setText(position+"");
            holder.tvText.setBackgroundColor(mColors[position%2]);
        }

        @Override
        public int getItemCount() {
            return 100;
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
