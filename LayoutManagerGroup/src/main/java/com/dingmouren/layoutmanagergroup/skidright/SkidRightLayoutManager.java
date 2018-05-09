package com.dingmouren.layoutmanagergroup.skidright;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by dingmouren on 2018/5/8.
 */

public class SkidRightLayoutManager extends LayoutManager {
    private static final int INVALIDATE_SCROLL_OFFSET = Integer.MAX_VALUE;
    private static final float DEFAULT_CHILD_LAYOUT_OFFSET = 0.2f;
    private int mItemViewWidth;
    private int mItemViewHeight;
    private int mChildPeekSize;
    private int mScrollOffset = INVALIDATE_SCROLL_OFFSET;
    private float mItemHeightWidthRatio;
    private float mScale;
    private int mChildCount;
    SkidRightSnapHelper mSkidRightSnapHelper;

    public SkidRightLayoutManager(float itemHeightWidthRatio, float scale) {
        this.mItemHeightWidthRatio = itemHeightWidthRatio;
        this.mScale = scale;
        this.mSkidRightSnapHelper = new SkidRightSnapHelper();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mSkidRightSnapHelper.attachToRecyclerView(view);
    }

    public int getFixedScrollPosition(int direction, float fixValue) {
        if (mScrollOffset % mItemViewWidth == 0) {
            return RecyclerView.NO_POSITION;
        }
        float position = mScrollOffset * 1.0f / mItemViewWidth;
        return (int) (direction > 0 ? position + fixValue : position + (1 - fixValue)) - 1;
    }


    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        mItemViewHeight = getVerticalSpace();
        mItemViewWidth = (int) (mItemViewHeight / mItemHeightWidthRatio);
        mChildPeekSize = (int) (mItemViewWidth * DEFAULT_CHILD_LAYOUT_OFFSET);
        mChildCount = getItemCount();
        mScrollOffset = makeScrollOffsetWithinRange(mScrollOffset);
        fill(recycler);
    }

    private void fill(RecyclerView.Recycler recycler) {
        int bottomItemPosition = (int) Math.floor(mScrollOffset / mItemViewWidth);
        int bottomItemVisibleSize = mScrollOffset % mItemViewWidth;
        final float offsetPercent = bottomItemVisibleSize * 1.0f / mItemViewWidth;
        final int space = getHorizontalSpace();

        ArrayList<ItemLayoutInfo> layoutInfos = new ArrayList<>();
        for (int i = bottomItemPosition - 1, j = 1, remainSpace = space - mItemViewWidth;
             i >= 0; i--, j++) {
            double maxOffset = mChildPeekSize * Math.pow(mScale, j);
            int start = (int) (remainSpace - offsetPercent * maxOffset);
            ItemLayoutInfo info = new ItemLayoutInfo(start,
                    (float) (Math.pow(mScale, j - 1) * (1 - offsetPercent * (1 - mScale))),
                    offsetPercent,
                    start * 1.0f / space
            );
            layoutInfos.add(0, info);

            remainSpace -= maxOffset;
            if (remainSpace <= 0) {
                info.start = (int) (remainSpace + maxOffset);
                info.positionOffsetPercent = 0;
                info.layoutPercent = info.start / space;
                info.scaleXY = (float) Math.pow(mScale, j - 1);
                break;
            }
        }

        if (bottomItemPosition < mChildCount) {
            final int start = space - bottomItemVisibleSize;
            layoutInfos.add(new ItemLayoutInfo(start, 1.0f,
                    bottomItemVisibleSize * 1.0f / mItemViewWidth, start * 1.0f / space).
                    setIsBottom());
        } else {
            bottomItemPosition -= 1;
        }

        int layoutCount = layoutInfos.size();

        final int startPos = bottomItemPosition - (layoutCount - 1);
        final int endPos = bottomItemPosition;
        final int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View childView = getChildAt(i);
            int pos = getPosition(childView);
            if (pos > endPos || pos < startPos) {
                removeAndRecycleView(childView, recycler);
            }
        }
        detachAndScrapAttachedViews(recycler);

        for (int i = 0; i < layoutCount; i++) {
            fillChild(recycler.getViewForPosition(startPos + i), layoutInfos.get(i));
        }
    }

    private void fillChild(View view, ItemLayoutInfo layoutInfo) {
        addView(view);
        measureChildWithExactlySize(view);
        final int scaleFix = (int) (mItemViewWidth * (1 - layoutInfo.scaleXY) / 2);


        int top = (int) (getPaddingTop());
        layoutDecoratedWithMargins(view, layoutInfo.start - scaleFix, top
                , layoutInfo.start + mItemViewWidth - scaleFix, top + mItemViewHeight);
        ViewCompat.setScaleX(view, layoutInfo.scaleXY);
        ViewCompat.setScaleY(view, layoutInfo.scaleXY);
    }

    private void measureChildWithExactlySize(View child) {
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(
                mItemViewWidth - lp.leftMargin - lp.rightMargin, View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(
                mItemViewHeight - lp.topMargin - lp.bottomMargin, View.MeasureSpec.EXACTLY);
        child.measure(widthSpec, heightSpec);
    }

    private int makeScrollOffsetWithinRange(int scrollOffset) {
        return Math.min(Math.max(mItemViewWidth, scrollOffset), mChildCount * mItemViewWidth);
    }



    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int pendingScrollOffset = mScrollOffset + dx;
        mScrollOffset = makeScrollOffsetWithinRange(pendingScrollOffset);
        fill(recycler);
        return mScrollOffset - pendingScrollOffset + dx;
    }


    public int calculateDistanceToPosition(int targetPos) {
        int pendingScrollOffset = mItemViewWidth * (targetPos + 1);
        return pendingScrollOffset - mScrollOffset;
    }




    @Override
    public boolean canScrollHorizontally() {
        return true;
    }


    public int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }


    private static class ItemLayoutInfo {
        float scaleXY;
        float layoutPercent;
        float positionOffsetPercent;
        int start;
        boolean isBottom;

        ItemLayoutInfo(int top, float scale, float positonOffset, float percent) {
            this.start = top;
            this.scaleXY = scale;
            this.positionOffsetPercent = positonOffset;
            this.layoutPercent = percent;
        }

        ItemLayoutInfo setIsBottom() {
            isBottom = true;
            return this;
        }

    }
}
