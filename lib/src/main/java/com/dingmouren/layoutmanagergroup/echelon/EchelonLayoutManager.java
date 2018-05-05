package com.dingmouren.layoutmanagergroup.echelon;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by thunderPunch on 2017/2/15
 * Description:
 */

public class EchelonLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = "EchelonLayoutManager";

    private Context mContext;
    private int mItemViewWidth;
    private int mItemViewHeight;
    private int mItemCount;
    private int mScrollOffset = Integer.MAX_VALUE;
    private float mScale = 0.9f;

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
        removeAndRecycleAllViews(recycler);

        mItemViewWidth = (int) (getHorizontalSpace() * 0.87f);
        mItemViewHeight = (int) (mItemViewWidth * 1.46f);
        mItemCount = getItemCount();
        mScrollOffset = Math.min(Math.max(mItemViewHeight, mScrollOffset), mItemCount * mItemViewHeight);

        layoutChild(recycler);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int pendingScrollOffset = mScrollOffset + dy;
        mScrollOffset = Math.min(Math.max(mItemViewHeight, mScrollOffset + dy), mItemCount * mItemViewHeight);
        layoutChild(recycler);
        return mScrollOffset - pendingScrollOffset + dy;
    }


    @Override
    public boolean canScrollVertically() {
        return true;
    }


    private void layoutChild(RecyclerView.Recycler recycler) {
        int bottomItemPosition = (int) Math.floor(mScrollOffset / mItemViewHeight);
        int remainSpace = getVerticalSpace() - mItemViewHeight;

        int bottomItemVisibleSize = mScrollOffset % mItemViewHeight;
        final float offsetPercent = bottomItemVisibleSize * 1.0f / mItemViewHeight;//[0,1) 初始值0，最下面item可见高度的比例

        ArrayList<ItemViewInfo> layoutInfos = new ArrayList<>();
        for (int i = bottomItemPosition - 1, j = 1; i >= 0; i--, j++) {
            double maxOffset = (getVerticalSpace() - mItemViewHeight) / 2 * Math.pow(0.8, j);//mScale初始值0.9, mScale^j
            int start = (int) (remainSpace - offsetPercent * maxOffset);// space - mItemHeight,99个itemHeight
            float scaleXY = (float) (Math.pow(mScale, j - 1) * (1 - offsetPercent * (1 - mScale)));
            float positonOffset = offsetPercent;
            float layoutPercent = start * 1.0f / getVerticalSpace();
            ItemViewInfo info = new ItemViewInfo(start, scaleXY, positonOffset, layoutPercent);
            layoutInfos.add(0, info);
            remainSpace = (int) (remainSpace - maxOffset);
            if (remainSpace <= 0) {
                info.mTop = (int) (remainSpace + maxOffset);
                info.positionOffsetPercent = 0;
                info.layoutPercent = info.mTop / getVerticalSpace();
                info.mScaleXY = (float) Math.pow(mScale, j - 1);
                break;
            }
        }

        if (bottomItemPosition < mItemCount) {
            final int start = getVerticalSpace() - bottomItemVisibleSize;
            layoutInfos.add(new ItemViewInfo(start, 1.0f, bottomItemVisibleSize * 1.0f / mItemViewHeight, start * 1.0f / getVerticalSpace())
                    .setIsBottom());
        } else {
            bottomItemPosition = bottomItemPosition - 1;//99
        }

        int layoutCount = layoutInfos.size();
        final int startPos = bottomItemPosition - (layoutCount - 1);
        final int endPos = bottomItemPosition;
        final int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {//回收
            View childView = getChildAt(i);
            int pos = getPosition(childView);
            if (pos > endPos || pos < startPos) {
                removeAndRecycleView(childView, recycler);
            }
        }

        detachAndScrapAttachedViews(recycler);

        for (int i = 0; i < layoutCount; i++) {
            View view = recycler.getViewForPosition(startPos + i);
            ItemViewInfo layoutInfo = layoutInfos.get(i);
            addView(view);
            measureChildWithExactlySize(view);
            int left = (getHorizontalSpace() - mItemViewWidth) / 2;
            layoutDecoratedWithMargins(view, left, layoutInfo.mTop, left + mItemViewWidth, layoutInfo.mTop + mItemViewHeight);
            view.setPivotX(view.getWidth() / 2);
            view.setPivotY(0);
            ViewCompat.setScaleX(view, layoutInfo.mScaleXY);//控制缩放
            ViewCompat.setScaleY(view, layoutInfo.mScaleXY);
        }
    }

    /**
     * 测量itemview的确切大小
     * @param child
     */
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


    private static class ItemViewInfo {
        float mScaleXY;
        float layoutPercent;
        float positionOffsetPercent;
        int mTop;
        boolean mIsBottom;

        private ItemViewInfo(int top, float scale, float positonOffset, float percent) {
            this.mTop = top;
            this.mScaleXY = scale;
            this.positionOffsetPercent = positonOffset;
            this.layoutPercent = percent;
        }

        private ItemViewInfo setIsBottom() {
            mIsBottom = true;
            return this;
        }

    }
}

