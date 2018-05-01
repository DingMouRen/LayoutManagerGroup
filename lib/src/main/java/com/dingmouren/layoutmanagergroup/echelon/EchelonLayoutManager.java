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
    /*保存ItemView的位置信息*/
    private SparseArray<Rect> mAllItemRects = new SparseArray<>();
    /*保存ItemView是否可见的信息*/
    private SparseBooleanArray mAllItemStates = new SparseBooleanArray();
    /*保存ItemView的缩放大小信息*/
    private SparseArray<Float> mAllItemScales = new SparseArray<>();
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
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0 || state.isPreLayout()) return; //sate.isPreLayout()正在测量视图
//        detachAndScrapAttachedViews(recycler);
        calculateChildSite(recycler);//计算并保存每个ItemView的位置
        fillViews(recycler,state);//填充ItemView
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int travel = dy;
        if (mVerticalScrollOffset+dy < 0){
            travel = -mVerticalScrollOffset;
        }else if (mVerticalScrollOffset + dy > mTotalHeight - getVerticalSpace()){
            travel = mTotalHeight - getVerticalSpace() - mVerticalScrollOffset;
        }
        offsetChildrenVertical(-travel);
        fillViews(recycler,state);
        mVerticalScrollOffset+=travel;
        return travel;
    }



    @Override
    public boolean canScrollVertically() {
        return true;
    }


    //--------------------------------------------------------------

    /**
     * 计算并保存每个ItemView的位置
     * @param recycler
     */
    private void calculateChildSite(RecyclerView.Recycler recycler){
        for (int i = 0; i < getItemCount(); i++) {
            View item = recycler.getViewForPosition(i);
            addView(item);
            measureChildWithMargins(item,0,100);
            calculateItemDecorationsForChild(item,new Rect());
            int width = getDecoratedMeasuredWidth(item);
            int height = getDecoratedMeasuredHeight(item);
            Rect tmpRect = mAllItemRects.get(i);
            if (tmpRect == null) tmpRect = new Rect();
            tmpRect.set(0,mTotalHeight,width,mTotalHeight+getHorizontalSpace());
            mAllItemRects.put(i,tmpRect);//保存ItemView的位置信息
            mAllItemStates.put(i,false);//保存所有的ItemView为不可见状态
            mAllItemScales.put(i,1f);//保存ItemView的缩放大小
            mTotalHeight+= height;
        }
    }
    /**
     * 填充ItemView
     * @param recycler
     */
    private void fillViews(RecyclerView.Recycler recycler, RecyclerView.State state){
        if (state.getItemCount() == 0 || state.isPreLayout()) return; //sate.isPreLayout()正在测量视图
        /*当前滑动状态下的可见区域*/
        Rect displayRect = new Rect(0,mVerticalScrollOffset,getHorizontalSpace(),mVerticalScrollOffset+getVerticalSpace());
        /*将滑出屏幕的ItemView回收到Recycler缓存中*/
        Rect childRect = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            childRect.left = getDecoratedLeft(childView);
            childRect.top = getDecoratedTop(childView);
            childRect.right = getDecoratedRight(childView);
            childRect.bottom = getDecoratedBottom(childView);
            if (!Rect.intersects(displayRect,childRect)){
                removeAndRecycleView(childView,recycler);
                mAllItemStates.put(i,false);
            }
        }
        detachAndScrapAttachedViews(recycler);
        /*显示需要出现在屏幕上的ItemView*/
        for (int i = 0; i < getItemCount(); i++) {
            if (Rect.intersects(displayRect,mAllItemRects.get(i))){
                View itemView = recycler.getViewForPosition(i);
//                measureChildWithMargins(itemView,0,0);
                addView(itemView);
                measureChildWithExactlySize(itemView);

                Rect rect = mAllItemRects.get(i);
                layoutDecoratedWithMargins(itemView,
                        rect.left,
                        rect.top - mVerticalScrollOffset,
                        rect.right,
                        rect.top+getHorizontalSpace() - mVerticalScrollOffset);
                mAllItemStates.put(i,true);

            }
        }

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

