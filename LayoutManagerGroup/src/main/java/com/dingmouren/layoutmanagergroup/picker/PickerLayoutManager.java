package com.dingmouren.layoutmanagergroup.picker;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */


public class PickerLayoutManager extends LinearLayoutManager {
    private static final String TAG = "PickerLayoutManager";

    private float mScale = 0.5f;
    private boolean mIsAlpha = true;
    private LinearSnapHelper mLinearSnapHelper;
    private OnSelectedViewListener mOnSelectedViewListener;
    private int mItemViewWidth;
    private int mItemViewHeight;
    private int mItemCount = -1;
    private RecyclerView mRecyclerView;
    private int mOrientation;

    public PickerLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.mLinearSnapHelper = new LinearSnapHelper();
        this.mOrientation = orientation;
    }

    public PickerLayoutManager(Context context, RecyclerView recyclerView, int orientation, boolean reverseLayout, int itemCount,float scale,boolean isAlpha) {
        super(context, orientation, reverseLayout);
        this.mLinearSnapHelper = new LinearSnapHelper();
        this.mItemCount = itemCount;
        this.mOrientation = orientation;
        this.mRecyclerView = recyclerView;
        this.mIsAlpha = isAlpha;
        this.mScale = scale;
        if (mItemCount != 0) setAutoMeasureEnabled(false);
    }

    /**
     * 添加LinearSnapHelper
     * @param view
     */
    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mLinearSnapHelper.attachToRecyclerView(view);
    }

    /**
     * 没有指定显示条目的数量时，RecyclerView的宽高由自身确定
     * 指定显示条目的数量时，根据方向分别计算RecyclerView的宽高
     * @param recycler
     * @param state
     * @param widthSpec
     * @param heightSpec
     */
    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        if (getItemCount() != 0 && mItemCount != 0) {

            View view = recycler.getViewForPosition(0);
            measureChildWithMargins(view, widthSpec, heightSpec);

            mItemViewWidth = view.getMeasuredWidth();
            mItemViewHeight = view.getMeasuredHeight();

            if (mOrientation == HORIZONTAL) {
                int paddingHorizontal = (mItemCount - 1) / 2 * mItemViewWidth;
                mRecyclerView.setClipToPadding(false);
                mRecyclerView.setPadding(paddingHorizontal,0,paddingHorizontal,0);
                setMeasuredDimension(mItemViewWidth * mItemCount, mItemViewHeight);
                Log.e(TAG,"onMeasure--mItemCount不为0--recyclerview--横向--width:"+mItemViewWidth*mItemCount+" height:"+mItemViewHeight);
            } else if (mOrientation == VERTICAL) {
                int paddingVertical = (mItemCount - 1) / 2 * mItemViewHeight;
                mRecyclerView.setClipToPadding(false);
                mRecyclerView.setPadding(0,paddingVertical,0,paddingVertical);
                setMeasuredDimension(mItemViewWidth, mItemViewHeight * mItemCount);
                Log.e(TAG,"onMeasure--mItemCount不为0--recyclerview--竖向--width:"+mItemViewWidth+" height:"+mItemViewHeight*mItemCount);
            }
        }else {
            super.onMeasure(recycler,state,widthSpec,heightSpec);
            Log.e(TAG,"onMeasure默认");
        }

    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (getItemCount() < 0 || state.isPreLayout()) return;

        if (mOrientation == HORIZONTAL){
            scaleHorizontalChildView();
        }else if (mOrientation == VERTICAL){
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

    /**
     * 横向情况下的缩放
     */
    private void scaleHorizontalChildView() {
        float mid = getWidth() / 2.0f;
        for (int i = 0; i < getChildCount(); i++) {
            View child =  getChildAt(i);
            float childMid = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2.0f;
            float scale = 1.0f + (-1 * (1 - mScale)) * (Math.min(mid, Math.abs(mid - childMid))) / mid;
            child.setScaleX(scale);
            child.setScaleY(scale);
            if (mIsAlpha) {
                child.setAlpha(scale);
            }
        }
    }

    /**
     * 竖向方向上的缩放
     */
    private void scaleVerticalChildView(){
        float mid = getHeight() / 2.0f;
        for (int i = 0; i < getChildCount(); i++) {
            View child =  getChildAt(i);
            float childMid = (getDecoratedTop(child) + getDecoratedBottom(child)) / 2.0f;
            float scale = 1.0f + (-1 *  (1 - mScale)) * (Math.min(mid, Math.abs(mid - childMid))) / mid;
            child.setScaleX(scale);
            child.setScaleY(scale);
            if (mIsAlpha) {
                child.setAlpha(scale);
            }
        }
    }


    /**
     * 当滑动停止时触发回调
     * @param state
     */
    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == 0) {
            if (mOnSelectedViewListener != null && mLinearSnapHelper != null) {
                View view = mLinearSnapHelper.findSnapView(this);
                int position = getPosition(view);
                mOnSelectedViewListener.onSelectedView(view,position);
            }
        }
    }


    public void OnSelectedViewListener(OnSelectedViewListener listener) {
        this.mOnSelectedViewListener = listener;
    }

    /**
     * 停止时，显示在中间的View的监听
     */
    public interface OnSelectedViewListener {
        void onSelectedView(View view,int position);
    }
}
