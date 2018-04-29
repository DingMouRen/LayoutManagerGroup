package com.dingmouren.layoutmanagergroup.echelon;

import android.support.v7.widget.RecyclerView;

/**
 * Created by dingmouren on 2018/4/29.
 * 梯形布局
 */

public class EchelonLayoutManager extends RecyclerView.LayoutManager{

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }
}
