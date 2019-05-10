package com.dingmouren.layoutmanagergroup.skidright;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.dingmouren.layoutmanagergroup.echelon.ItemViewInfo;
import java.util.ArrayList;

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */
public class SkidRightLayoutManager extends RecyclerView.LayoutManager {
    private boolean mHasChild = false;
    private int mItemViewWidth;
    private int mItemViewHeight;
    private int mScrollOffset = Integer.MAX_VALUE;
    private final float mItemHeightWidthRatio;
    private final float mScale;
    private int mItemCount;
    private final SkidRightSnapHelper mSkidRightSnapHelper;

    /**
     * Sets true to scroll layout in left direction.
     */
    public boolean isReverseDirection = false;

    public SkidRightLayoutManager(float itemHeightWidthRatio, float scale) {
        this.mItemHeightWidthRatio = itemHeightWidthRatio;
        this.mScale = scale;
        mSkidRightSnapHelper = new SkidRightSnapHelper();
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
        if (mHasChild) {
            if (mScrollOffset % mItemViewWidth == 0) {
                return RecyclerView.NO_POSITION;
            }
            float itemPosition = position();
            int layoutPosition = (int) (direction > 0 ? itemPosition + fixValue : itemPosition + (1 - fixValue)) - 1;
            return convert2AdapterPosition(layoutPosition);
        }
        return RecyclerView.NO_POSITION;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0 || state.isPreLayout()) return;
        removeAndRecycleAllViews(recycler);
        if (!mHasChild) {
            mItemViewHeight = getVerticalSpace();
            mItemViewWidth = (int) (mItemViewHeight / mItemHeightWidthRatio);
            mHasChild = true;
        }
        mItemCount = getItemCount();
        mScrollOffset = makeScrollOffsetWithinRange(mScrollOffset);
        fill(recycler);
    }

    private float position() {
        return isReverseDirection ?
                (mScrollOffset + mItemCount * mItemViewWidth) * 1.0F/ mItemViewWidth :
                mScrollOffset * 1.0F / mItemViewWidth;
    }

    public void fill(RecyclerView.Recycler recycler) {
        int bottomItemPosition = (int) Math.floor(position());
        final int space = getHorizontalSpace();
        final int bottomItemVisibleSize = isReverseDirection ?
                ((mItemCount - 1) * mItemViewWidth + mScrollOffset) % mItemViewWidth :
                mScrollOffset % mItemViewWidth;
        final float offsetPercent = bottomItemVisibleSize * 1.0f / mItemViewWidth;

        int remainSpace = isReverseDirection ? 0 : space - mItemViewWidth;

        final int baseOffsetSpace = isReverseDirection ?
                mItemViewWidth :
                getHorizontalSpace() - mItemViewWidth;

        ArrayList<ItemViewInfo> layoutInfos = new ArrayList<>();
        for (int i = bottomItemPosition - 1, j = 1;
             i >= 0; i--, j++) {
            double maxOffset = baseOffsetSpace / 2 * Math.pow(mScale, j);

            float adjustedPercent = isReverseDirection ? -offsetPercent : +offsetPercent;
            int start = (int) (remainSpace - adjustedPercent * maxOffset);

            float scaleXY = (float) (Math.pow(mScale, j - 1) * (1 - offsetPercent * (1 - mScale)));
            float percent = start * 1.0f / space;
            ItemViewInfo info = new ItemViewInfo(start, scaleXY, offsetPercent, percent);

            layoutInfos.add(0, info);

            double delta = isReverseDirection ? maxOffset : -maxOffset;
            remainSpace += delta;

            boolean isOutOfSpace = isReverseDirection ?
                    remainSpace > getHorizontalSpace() :
                    remainSpace <= 0;
            if (isOutOfSpace) {
                info.setTop((int) (remainSpace - delta));
                info.setPositionOffset(0);
                info.setLayoutPercent(info.getTop() / space);
                info.setScaleXY( (float) Math.pow(mScale, j - 1));
                break;
            }
        }

        if (bottomItemPosition < mItemCount) {
            final int start = isReverseDirection ?
                    bottomItemVisibleSize - mItemViewWidth :
                    space - bottomItemVisibleSize;
            layoutInfos.add(
                    new ItemViewInfo(start,
                            1.0f,
                            offsetPercent,
                            start * 1.0f / space).setIsBottom());
        } else {
            bottomItemPosition -= 1;
        }

        int layoutCount = layoutInfos.size();

        final int startPos = bottomItemPosition - (layoutCount - 1);
        final int endPos = bottomItemPosition;
        final int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View childView = getChildAt(i);
            int pos = convert2LayoutPosition(getPosition(childView));
            if (pos > endPos || pos < startPos) {
                removeAndRecycleView(childView, recycler);
            }
        }
        detachAndScrapAttachedViews(recycler);

        for (int i = 0; i < layoutCount; i++) {
            int position = convert2AdapterPosition(startPos + i);
            fillChild(recycler.getViewForPosition(position), layoutInfos.get(i));
        }
    }

    private void fillChild(View view, ItemViewInfo layoutInfo) {
        addView(view);
        measureChildWithExactlySize(view);
        final int scaleFix = (int) (mItemViewWidth * (1 - layoutInfo.getScaleXY()) / 2);

        int left = layoutInfo.getTop() - scaleFix;
        int top = getPaddingTop();
        int right = layoutInfo.getTop() + mItemViewWidth - scaleFix;
        int bottom = top + mItemViewHeight;

        layoutDecoratedWithMargins(view, left, top, right, bottom);
        ViewCompat.setScaleX(view, layoutInfo.getScaleXY());
        ViewCompat.setScaleY(view, layoutInfo.getScaleXY());
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
        if (isReverseDirection) {
            return Math.max(Math.min(0, scrollOffset), -(mItemCount - 1) * mItemViewWidth);
        } else {
            return Math.min(Math.max(mItemViewWidth, scrollOffset), mItemCount * mItemViewWidth);
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int delta = isReverseDirection ? -dx : dx;
        int pendingScrollOffset = mScrollOffset + delta;
        mScrollOffset = makeScrollOffsetWithinRange(pendingScrollOffset);
        fill(recycler);
        return mScrollOffset - pendingScrollOffset + delta;
    }

    public int calculateDistanceToPosition(int targetPos) {
        if (isReverseDirection)  {
            return mItemViewWidth * targetPos + mScrollOffset;
        }
        int pendingScrollOffset = mItemViewWidth * (convert2LayoutPosition(targetPos) + 1);
        return pendingScrollOffset - mScrollOffset;
    }

    @Override
    public void scrollToPosition(int position) {
        if (position > 0 && position < mItemCount) {
            mScrollOffset = mItemViewWidth * (convert2LayoutPosition(position) + 1);
            requestLayout();
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    public int convert2AdapterPosition(int layoutPosition) {
        return mItemCount - 1 - layoutPosition;
    }

    public int convert2LayoutPosition(int adapterPostion) {
        return mItemCount - 1 - adapterPostion;
    }

    public int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }
}
