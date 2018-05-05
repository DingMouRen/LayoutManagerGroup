package com.dingmouren.layoutmanagergroup.echelon;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;

/**
 * Created by thunderPunch on 2017/2/15
 * Description:
 */

public class EchelonLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = "LadderLayoutManager";

    private Context mContext;
    /*所有ItemView的总高度*/
    private int mTotalHeight = 0;
    /*偏移量*/
    private int mVerticalScrollOffset = 0;
    /*ItemView的宽度*/
    private int mItemViewWidth;
    /*ItemView的高度*/
    private int mItemViewHeight;

    public EchelonLayoutManager(Context context) {
        this.mContext = context;
        mItemViewWidth = (int) (getHorizontalSpace() * 0.87f);//item的宽
        mItemViewHeight = (int) (mItemViewWidth * 1.46f);//item的高
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0 || state.isPreLayout()) return; //sate.isPreLayout()正在测量视图
//        removeAndRecycleAllViews(recycler);
//        detachAndScrapAttachedViews(recycler);
        layoutChild(recycler);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
      return dy;
    }



    @Override
    public boolean canScrollVertically() {
        return true;
    }


    //--------------------------------------------------------------


    private void layoutChild(RecyclerView.Recycler recycler){
        View child = recycler.getViewForPosition(0);
        addView(child);
        layoutDecorated(child,0,0,1080,500);
    }

    private void measureChildWithExactlySize(View child) {
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(
                getHorizontalSpace() - lp.leftMargin - lp.rightMargin, View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(
                getHorizontalSpace() - lp.topMargin - lp.bottomMargin, View.MeasureSpec.EXACTLY);
        child.measure(widthSpec, heightSpec);
    }

    /**
     * 获取item在竖直方向上可以显示的距离
     *
     * @return
     */
    public int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    /**
     * 获取item在水平方向上可以显示的距离
     *
     * @return
     */
    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    /**
     * 获取系统状态栏的高度
     * @param context
     * @return
     */
    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}

