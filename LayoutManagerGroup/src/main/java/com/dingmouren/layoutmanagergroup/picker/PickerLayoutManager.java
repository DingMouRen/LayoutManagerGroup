package com.dingmouren.layoutmanagergroup.picker;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */


public class PickerLayoutManager extends LinearLayoutManager {
    private static final String TAG = "PickerLayoutManager";

    private float scaleDownBy = 0.66f;
    private float scaleDownDistance = 0.9f;
    private boolean changeAlpha = false;
    private LinearSnapHelper mLinearSnapHelper;
    private onScrollStopListener onScrollStopListener;
    private int mItemViewWidth;
    private int mItemViewHeight;
    private int mItemCount = -1;
    public PickerLayoutManager(Context context, int orientation, boolean reverseLayout,int itemCount) {
        super(context, orientation, reverseLayout);
        this.mLinearSnapHelper = new LinearSnapHelper();
        this.mItemCount = itemCount;
        if (mItemCount != 0) setAutoMeasureEnabled(false);
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mLinearSnapHelper.attachToRecyclerView(view);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        if (getItemCount() != 0 && mItemCount != 0) {
            View view = recycler.getViewForPosition(0);
            measureChild(view, widthSpec, heightSpec);
            int measuredWidth = View.MeasureSpec.getSize(widthSpec);
            int measuredHeight = view.getMeasuredHeight();
            int orientation = getOrientation();
            if (orientation == HORIZONTAL) {
                setMeasuredDimension(measuredWidth * mItemCount, measuredHeight);
            } else if (orientation == VERTICAL) {
                setMeasuredDimension(measuredWidth, measuredHeight * mItemCount);
            }
        }else {
            super.onMeasure(recycler,state,widthSpec,heightSpec);
        }

    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (getItemCount() < 0 || state.isPreLayout()) return;

        View child = recycler.getViewForPosition(0);
        measureChildWithMargins(child,0,0);
        mItemViewWidth = getDecoratedMeasuredWidth(child);
        mItemViewHeight = getDecoratedMeasuredHeight(child);
        Log.e(TAG,"onLayoutChildren  itemWidth:"+mItemViewWidth+" itemHeight:"+mItemViewHeight);


        int orientation = getOrientation();
        if (orientation == HORIZONTAL){
            scaleHorizontalChildView();
        }else if (orientation == VERTICAL){
            scaleVerticalChildView();
        }

    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        scaleHorizontalChildView();
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        scaleVerticalChildView();
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    private void scaleHorizontalChildView() {
        float mid = getWidth() / 2.0f;
        float unitScaleDownDist = scaleDownDistance * mid;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            float childMid = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2.0f;
            float scale = 1.0f + (-1 * scaleDownBy) * (Math.min(unitScaleDownDist, Math.abs(mid - childMid))) / unitScaleDownDist;
            child.setScaleX(scale);
            child.setScaleY(scale);
            if (changeAlpha) {
                child.setAlpha(scale);
            }
        }
    }

    private void scaleVerticalChildView(){
        float mid = getHeight() / 2.0f;
        float unitScaleDownDist = scaleDownDistance * mid;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            float childMid = (getDecoratedTop(child) + getDecoratedBottom(child)) / 2.0f;
            float scale = 1.0f + (-1 * scaleDownBy) * (Math.min(unitScaleDownDist, Math.abs(mid - childMid))) / unitScaleDownDist;
            child.setScaleX(scale);
            child.setScaleY(scale);
            if (changeAlpha) {
                child.setAlpha(scale);
            }
        }
    }



    @Override
    public void onScrollStateChanged(int state) {

        super.onScrollStateChanged(state);
        if (state == 0) {
            if (onScrollStopListener != null && mLinearSnapHelper != null) {
                View view = mLinearSnapHelper.findSnapView(this);
                int position = getPosition(view);
                Log.e(TAG, "position:" + position);
                onScrollStopListener.selectedView(view);
            }
        }
    }


    public float getScaleDownBy() {
        return scaleDownBy;
    }

    public void setScaleDownBy(float scaleDownBy) {
        this.scaleDownBy = scaleDownBy;
    }

    public float getScaleDownDistance() {
        return scaleDownDistance;
    }

    public void setScaleDownDistance(float scaleDownDistance) {
        this.scaleDownDistance = scaleDownDistance;
    }

    public boolean isChangeAlpha() {
        return changeAlpha;
    }

    public void setChangeAlpha(boolean changeAlpha) {
        this.changeAlpha = changeAlpha;
    }

    public void setOnScrollStopListener(onScrollStopListener onScrollStopListener) {
        this.onScrollStopListener = onScrollStopListener;
    }

    public interface onScrollStopListener {
        public void selectedView(View view);
    }
}
