/*
package com.dingmouren.layoutmanagergroup.copy;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

*/
/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 *//*


@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue"})
public  class BannerLayoutManager extends RecyclerView.LayoutManager {
    public static final int DETERMINE_BY_MAX_AND_MIN = -1;

    public static final int HORIZONTAL = OrientationHelper.HORIZONTAL;

    public static final int VERTICAL = OrientationHelper.VERTICAL;

    private static final int DIRECTION_NO_WHERE = -1;

    private static final int DIRECTION_FORWARD = 0;

    private static final int DIRECTION_BACKWARD = 1;

    protected static final int INVALID_SIZE = Integer.MAX_VALUE;

    private SparseArray<View> positionCache = new SparseArray<>();

    protected int mDecoratedMeasurement;

    protected int mDecoratedMeasurementInOther;

    */
/**
     * Current orientation. Either {@link #HORIZONTAL} or {@link #VERTICAL}
     *//*

    int mOrientation;

    protected int mSpaceMain;

    protected int mSpaceInOther;

    */
/**
     * The offset of property which will change while scrolling
     *//*

    protected float mOffset;

    protected OrientationHelper mOrientationHelper;

    */
/**
     * Defines if layout should be calculated from end to start.
     *//*

    private boolean mReverseLayout = false;

    */
/**
     * This keeps the final value for how LayoutManager should start laying out views.
     * It is calculated by checking {@link #getReverseLayout()} and View's layout direction.
     * {@link #onLayoutChildren(RecyclerView.Recycler, RecyclerView.State)} is run.
     *//*

    private boolean mShouldReverseLayout = false;

    */
/**
     * Works the same way as {@link android.widget.AbsListView#setSmoothScrollbarEnabled(boolean)}.
     * see {@link android.widget.AbsListView#setSmoothScrollbarEnabled(boolean)}
     *//*

    private boolean mSmoothScrollbarEnabled = true;

    */
/**
     * When LayoutManager needs to scroll to a position, it sets this variable and requests a
     * layout which will check this variable and re-layout accordingly.
     *//*

    private int mPendingScrollPosition = NO_POSITION;


    protected float mInterval; //the mInterval of each item's mOffset

    private boolean mRecycleChildrenOnDetach;

    private boolean mInfinite = true;

    private boolean mEnableBringCenterToFront;

    private int mLeftItems;

    private int mRightItems;

    */
/**
     * max visible item count
     *//*

    private int mMaxVisibleItemCount = DETERMINE_BY_MAX_AND_MIN;

    private Interpolator mSmoothScrollInterpolator;

    private int mDistanceToBottom = INVALID_SIZE;

    */
/**
     * use for handle focus
     *//*

    private View currentFocusView;

    */
/**
     * @return the mInterval of each item's mOffset
     *//*

    */
/**
     * @return the mInterval of each item's mOffset
     *//*

    private int itemSpace = 20;

    private float centerScale = 1.2f;
   private float  moveSpeed=1.0f;



    */
/**
     * Creates a horizontal ViewPagerLayoutManager
     *//*

    public BannerLayoutManager(Context context) {
        this(context, HORIZONTAL, false);
    }

    */
/**
     * @param orientation   Layout orientation. Should be {@link #HORIZONTAL} or {@link #VERTICAL}
     *//*

    public BannerLayoutManager(Context context, int orientation) {
        this(context,orientation,false);
    }

    public BannerLayoutManager(Context context, int orientation, boolean reverseLayout) {
        setEnableBringCenterToFront(true);
        setMaxVisibleItemCount(3);
        setOrientation(orientation);
        setReverseLayout(reverseLayout);
        setAutoMeasureEnabled(true);
        setItemPrefetchEnabled(false);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0 || state.isPreLayout()) {
            removeAndRecycleAllViews(recycler);
            mOffset = 0;
            return;
        }

        ensureLayoutState();
        resolveShouldLayoutReverse();

        View childView = recycler.getViewForPosition(0);
        measureChildWithMargins(childView, 0, 0);
        mDecoratedMeasurement = mOrientationHelper.getDecoratedMeasurement(childView);
        mDecoratedMeasurementInOther = mOrientationHelper.getDecoratedMeasurementInOther(childView);
        mSpaceMain = (mOrientationHelper.getTotalSpace() - mDecoratedMeasurement) / 2;
        if (mDistanceToBottom == INVALID_SIZE) {
            mSpaceInOther = (getRecyclerViewTotalSpace() - mDecoratedMeasurementInOther) / 2;
        } else {
            mSpaceInOther =getRecyclerViewTotalSpace() - mDecoratedMeasurementInOther - mDistanceToBottom;
        }

        mInterval = setInterval();
        setUp();
        mLeftItems = (int) Math.abs(minRemoveOffset() / mInterval) + 1;
        mRightItems = (int) Math.abs(maxRemoveOffset() / mInterval) + 1;

        if (mPendingScrollPosition != NO_POSITION) {
            mOffset = mShouldReverseLayout ? mPendingScrollPosition * -mInterval : mPendingScrollPosition * mInterval;
        }

        detachAndScrapAttachedViews(recycler);
        layoutItems(recycler);
    }


    */
/**
     * @return true if {@link #getOrientation()} is {@link #HORIZONTAL}
     *//*

    @Override
    public boolean canScrollHorizontally() {
        return mOrientation == HORIZONTAL;
    }

    */
/**
     * @return true if {@link #getOrientation()} is {@link #VERTICAL}
     *//*

    @Override
    public boolean canScrollVertically() {
        return mOrientation == VERTICAL;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        new LinearSnapHelper().attachToRecyclerView(view);
    }


    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mOrientation == VERTICAL) {
            return 0;
        }
        return scrollBy(dx, recycler, state);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mOrientation == HORIZONTAL) {
            return 0;
        }
        return scrollBy(dy, recycler, state);
    }


    private int computeScrollOffset() {
        if (getChildCount() == 0) {
            return 0;
        }

        if (!mSmoothScrollbarEnabled) {
            return !mShouldReverseLayout ?
                    getCurrentPosition() : getItemCount() - getCurrentPosition() - 1;
        }

        final float realOffset = getOffsetOfRightAdapterPosition();
        return !mShouldReverseLayout ? (int) realOffset : (int) ((getItemCount() - 1) * mInterval + realOffset);
    }

    */
/**
     * Returns the current orientation of the layout.
     *
     * @return Current orientation,  either {@link #HORIZONTAL} or {@link #VERTICAL}
     * @see #setOrientation(int)
     *//*

    public int getOrientation() {
        return mOrientation;
    }

    */
/**
     * Sets the orientation of the layout. {@link BannerLayoutManager}
     * will do its best to keep scroll position.
     *
     * @param orientation {@link #HORIZONTAL} or {@link #VERTICAL}
     *//*

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException("invalid orientation:" + orientation);
        }
        assertNotInLayoutOrScroll(null);
        if (orientation == mOrientation) {
            return;
        }
        mOrientation = orientation;
        mOrientationHelper = null;
        mDistanceToBottom = INVALID_SIZE;
        removeAllViews();
    }

    */
/**
     * Returns the max visible item count, {@link #DETERMINE_BY_MAX_AND_MIN} means it haven't been set now
     * And it will use {@link #maxRemoveOffset()} and {@link #minRemoveOffset()} to handle the range
     *
     * @return Max visible item count
     *//*

    public int getMaxVisibleItemCount() {
        return mMaxVisibleItemCount;
    }

    */
/**
     * Set the max visible item count, {@link #DETERMINE_BY_MAX_AND_MIN} means it haven't been set now
     * And it will use {@link #maxRemoveOffset()} and {@link #minRemoveOffset()} to handle the range
     *
     * @param mMaxVisibleItemCount Max visible item count
     *//*

    public void setMaxVisibleItemCount(int mMaxVisibleItemCount) {
        assertNotInLayoutOrScroll(null);
        if (this.mMaxVisibleItemCount == mMaxVisibleItemCount) return;
        this.mMaxVisibleItemCount = mMaxVisibleItemCount;
        removeAllViews();
    }

    */
/**
     * Calculates the view layout order. (e.g. from end to start or start to end)
     * RTL layout support is applied automatically. So if layout is RTL and
     * {@link #getReverseLayout()} is {@code true}, elements will be laid out starting from left.
     *//*

    private void resolveShouldLayoutReverse() {
        if (mOrientation == HORIZONTAL && getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL) {
            mReverseLayout = !mReverseLayout;
        }
    }

    */
/**
     * Returns if views are laid out from the opposite direction of the layout.
     *
     * @return If layout is reversed or not.
     * @see #setReverseLayout(boolean)
     *//*

    public boolean getReverseLayout() {
        return mReverseLayout;
    }

    */
/**
     * Used to reverse item traversal and layout order.
     * This behaves similar to the layout change for RTL views. When set to true, first item is
     * laid out at the end of the UI, second item is laid out before it etc.
     * <p>
     * For horizontal layouts, it depends on the layout direction.
     * When set to true, If {@link RecyclerView} is LTR, than it will
     * layout from RTL, if {@link RecyclerView}} is RTL, it will layout
     * from LTR.
     *//*

    public void setReverseLayout(boolean reverseLayout) {
        assertNotInLayoutOrScroll(null);
        if (reverseLayout == mReverseLayout) {
            return;
        }
        mReverseLayout = reverseLayout;
        removeAllViews();
    }

    public void setSmoothScrollInterpolator(Interpolator smoothScrollInterpolator) {
        this.mSmoothScrollInterpolator = smoothScrollInterpolator;
    }



    private int computeScrollExtent() {
        if (getChildCount() == 0) {
            return 0;
        }

        if (!mSmoothScrollbarEnabled) {
            return 1;
        }

        return (int) mInterval;
    }

    private int computeScrollRange() {
        if (getChildCount() == 0) {
            return 0;
        }

        if (!mSmoothScrollbarEnabled) {
            return getItemCount();
        }

        return (int) (getItemCount() * mInterval);
    }


    private int scrollBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getChildCount() == 0 || dy == 0) {
            return 0;
        }
        ensureLayoutState();
        int willScroll = dy;

        float realDx = dy / getDistanceRatio();
        if (Math.abs(realDx) < 0.00000001f) {
            return 0;
        }
        float targetOffset = mOffset + realDx;

        //handle the boundary
        if (!mInfinite && targetOffset < getMinOffset()) {
            willScroll -= (targetOffset - getMinOffset()) * getDistanceRatio();
        } else if (!mInfinite && targetOffset > getMaxOffset()) {
            willScroll = (int) ((getMaxOffset() - mOffset) * getDistanceRatio());
        }

        realDx = willScroll / getDistanceRatio();

        mOffset += realDx;

        //handle recycle
        layoutItems(recycler);

        return willScroll;
    }

    private void layoutItems(RecyclerView.Recycler recycler) {
        detachAndScrapAttachedViews(recycler);
        positionCache.clear();

        final int itemCount = getItemCount();
        if (itemCount == 0) return;

        // make sure that current position start from 0 to 1
        final int currentPos = mShouldReverseLayout ?
                -getCurrentPositionOffset() : getCurrentPositionOffset();
        int start = currentPos - mLeftItems;
        int end = currentPos + mRightItems;

        // handle max visible count
        if (useMaxVisibleCount()) {
            boolean isEven = mMaxVisibleItemCount % 2 == 0;
            if (isEven) {
                int offset = mMaxVisibleItemCount / 2;
                start = currentPos - offset + 1;
                end = currentPos + offset + 1;
            } else {
                int offset = (mMaxVisibleItemCount - 1) / 2;
                start = currentPos - offset;
                end = currentPos + offset + 1;
            }
        }

        if (!mInfinite) {
            if (start < 0) {
                start = 0;
                if (useMaxVisibleCount()) end = mMaxVisibleItemCount;
            }
            if (end > itemCount) end = itemCount;
        }

        float lastOrderWeight = Float.MIN_VALUE;
        for (int i = start; i < end; i++) {
            if (useMaxVisibleCount() || !removeCondition(getProperty(i) - mOffset)) {
                // start and end base on current position,
                // so we need to calculate the adapter position
                int adapterPosition = i;
                if (i >= itemCount) {
                    adapterPosition %= itemCount;
                } else if (i < 0) {
                    int delta = (-adapterPosition) % itemCount;
                    if (delta == 0) delta = itemCount;
                    adapterPosition = itemCount - delta;
                }
                final View scrap = recycler.getViewForPosition(adapterPosition);
                measureChildWithMargins(scrap, 0, 0);
                resetViewProperty(scrap);
                // we need i to calculate the real offset of current view
                final float targetOffset = getProperty(i) - mOffset;
                layoutScrap(scrap, targetOffset);
                final float orderWeight = mEnableBringCenterToFront ?
                        setViewElevation(scrap, targetOffset) : adapterPosition;
                if (orderWeight > lastOrderWeight) {
                    addView(scrap);
                } else {
                    addView(scrap, 0);
                }
                if (i == currentPos) currentFocusView = scrap;
                lastOrderWeight = orderWeight;
                positionCache.put(i, scrap);
            }
        }

        currentFocusView.requestFocus();
    }

    private int getMovement(int direction) {
        if (mOrientation == VERTICAL) {
            if (direction == View.FOCUS_UP) {
                return mShouldReverseLayout ? DIRECTION_FORWARD : DIRECTION_BACKWARD;
            } else if (direction == View.FOCUS_DOWN) {
                return mShouldReverseLayout ? DIRECTION_BACKWARD : DIRECTION_FORWARD;
            } else {
                return DIRECTION_NO_WHERE;
            }
        } else {
            if (direction == View.FOCUS_LEFT) {
                return mShouldReverseLayout ? DIRECTION_FORWARD : DIRECTION_BACKWARD;
            } else if (direction == View.FOCUS_RIGHT) {
                return mShouldReverseLayout ? DIRECTION_BACKWARD : DIRECTION_FORWARD;
            } else {
                return DIRECTION_NO_WHERE;
            }
        }
    }

    void ensureLayoutState() {
        if (mOrientationHelper == null) {
            mOrientationHelper = OrientationHelper.createOrientationHelper(this, mOrientation);
        }
    }

    */
/**
     * You can set up your own properties here or change the exist properties like mSpaceMain and mSpaceInOther
     *//*

    protected void setUp() {

    }

    private float getProperty(int position) {
        return mShouldReverseLayout ? position * -mInterval : position * mInterval;
    }


    private boolean useMaxVisibleCount() {
        return mMaxVisibleItemCount != DETERMINE_BY_MAX_AND_MIN;
    }

    private boolean removeCondition(float targetOffset) {
        return targetOffset > maxRemoveOffset() || targetOffset < minRemoveOffset();
    }

    private void resetViewProperty(View v) {
        v.setRotation(0);
        v.setRotationY(0);
        v.setRotationX(0);
        v.setScaleX(1f);
        v.setScaleY(1f);
        v.setAlpha(1f);
    }

    */
/* package *//*
 float getMaxOffset() {
        return !mShouldReverseLayout ? (getItemCount() - 1) * mInterval : 0;
    }

    */
/* package *//*
 float getMinOffset() {
        return !mShouldReverseLayout ? 0 : -(getItemCount() - 1) * mInterval;
    }

    private void layoutScrap(View scrap, float targetOffset) {

        final int left = calItemLeft(scrap, targetOffset);
        final int top = calItemTop(scrap, targetOffset);
        if (mOrientation == VERTICAL) {
            layoutDecorated(scrap, mSpaceInOther + left, mSpaceMain + top,
                    mSpaceInOther + left + mDecoratedMeasurementInOther, mSpaceMain + top + mDecoratedMeasurement);
        } else {
            layoutDecorated(scrap, mSpaceMain + left, mSpaceInOther + top,
                    mSpaceMain + left + mDecoratedMeasurement, mSpaceInOther + top + mDecoratedMeasurementInOther);
        }
        setItemViewProperty(scrap, targetOffset);
    }

    protected int calItemLeft(View itemView, float targetOffset) {
        return mOrientation == VERTICAL ? 0 : (int) targetOffset;
    }

    protected int calItemTop(View itemView, float targetOffset) {
        return mOrientation == VERTICAL ? (int) targetOffset : 0;
    }

    */
/**
     * when the target offset reach this,
     * the view will be removed and recycled in {@link #layoutItems(RecyclerView.Recycler)}
     *//*

    protected float maxRemoveOffset() {
        return mOrientationHelper.getTotalSpace() - mSpaceMain;
    }

    */
/**
     * when the target offset reach this,
     * the view will be removed and recycled in {@link #layoutItems(RecyclerView.Recycler)}
     *//*

    protected float minRemoveOffset() {
        return -mDecoratedMeasurement - mOrientationHelper.getStartAfterPadding() - mSpaceMain;
    }




    @Override
    public View findViewByPosition(int position) {
        final int itemCount = getItemCount();
        if (itemCount == 0) return null;
        for (int i = 0; i < positionCache.size(); i++) {
            final int key = positionCache.keyAt(i);
            if (key >= 0) {
                if (position == key % itemCount) return positionCache.valueAt(i);
            } else {
                int delta = key % itemCount;
                if (delta == 0) delta = -itemCount;
                if (itemCount + delta == position) return positionCache.valueAt(i);
            }
        }
        return null;
    }
    public int getCurrentPosition() {
        if (getItemCount() == 0) return 0;

        int position = getCurrentPositionOffset();
        if (!mInfinite) return Math.abs(position);

        position = !mShouldReverseLayout ?
                //take care of position = getItemCount()
                (position >= 0 ?
                        position % getItemCount() :
                        getItemCount() + position % getItemCount()) :
                (position > 0 ?
                        getItemCount() - position % getItemCount() :
                        -position % getItemCount());
        return position == getItemCount() ? 0 : position;
    }
    */
/**
     * Returns whether LayoutManager will recycle its children when it is detached from
     * RecyclerView.
     *
     * @return true if LayoutManager will recycle its children when it is detached from
     * RecyclerView.
     *//*

    public boolean getRecycleChildrenOnDetach() {
        return mRecycleChildrenOnDetach;
    }

    */
/**
     * Set whether LayoutManager will recycle its children when it is detached from
     * RecyclerView.
     * <p>
     * If you are using a {@link RecyclerView.RecycledViewPool}, it might be a good idea to set
     * this flag to <code>true</code> so that views will be available to other RecyclerViews
     * immediately.
     * <p>
     * Note that, setting this flag will result in a performance drop if RecyclerView
     * is restored.
     *
     * @param recycleChildrenOnDetach Whether children should be recycled in detach or not.
     *//*

    public void setRecycleChildrenOnDetach(boolean recycleChildrenOnDetach) {
        mRecycleChildrenOnDetach = recycleChildrenOnDetach;
    }

    private int getCurrentPositionOffset() {
        return Math.round(mOffset / mInterval);
    }

    */
/**
     * Sometimes we need to get the right offset of matching adapter position
     * cause when {@link #mInfinite} is set true, there will be no limitation of {@link #mOffset}
     *//*

    private float getOffsetOfRightAdapterPosition() {
        if (mShouldReverseLayout)
            return mInfinite ?
                    (mOffset <= 0 ?
                            (mOffset % (mInterval * getItemCount())) :
                            (getItemCount() * -mInterval + mOffset % (mInterval * getItemCount()))) :
                    mOffset;
        else
            return mInfinite ?
                    (mOffset >= 0 ?
                            (mOffset % (mInterval * getItemCount())) :
                            (getItemCount() * mInterval + mOffset % (mInterval * getItemCount()))) :
                    mOffset;
    }

    */
/**
     *
     * @return the dy between center and current position
     *//*

    public int getOffsetToCenter() {
        if (mInfinite)
            return (int) ((getCurrentPositionOffset() * mInterval - mOffset) * getDistanceRatio());
        return (int) ((getCurrentPosition() *
                (!mShouldReverseLayout ? mInterval : -mInterval) - mOffset) * getDistanceRatio());
    }

    public int getOffsetToPosition(int position) {
        if (mInfinite)
            return (int) (((getCurrentPositionOffset() +
                    (!mShouldReverseLayout ? position - getCurrentPosition() : getCurrentPosition() - position)) *
                    mInterval - mOffset) * getDistanceRatio());
        return (int) ((position *
                (!mShouldReverseLayout ? mInterval : -mInterval) - mOffset) * getDistanceRatio());
    }


    public void setInfinite(boolean enable) {
        assertNotInLayoutOrScroll(null);
        if (enable == mInfinite) {
            return;
        }
        mInfinite = enable;
        requestLayout();
    }

    public boolean getInfinite() {
        return mInfinite;
    }

    public int getDistanceToBottom() {
        return mDistanceToBottom == INVALID_SIZE ? (getRecyclerViewTotalSpace() - mDecoratedMeasurementInOther) / 2 : mDistanceToBottom;
    }

    public void setDistanceToBottom(int mDistanceToBottom) {
        assertNotInLayoutOrScroll(null);
        if (this.mDistanceToBottom == mDistanceToBottom) return;
        this.mDistanceToBottom = mDistanceToBottom;
        removeAllViews();
    }

    */
/**
     * When smooth scrollbar is enabled, the position and size of the scrollbar thumb is computed
     * based on the number of visible pixels in the visible items. This however assumes that all
     * list items have similar or equal widths or heights (depending on list orientation).
     * If you use a list in which items have different dimensions, the scrollbar will change
     * appearance as the user scrolls through the list. To avoid this issue,  you need to disable
     * this property.
     * <p>
     * When smooth scrollbar is disabled, the position and size of the scrollbar thumb is based
     * solely on the number of items in the adapter and the position of the visible items inside
     * the adapter. This provides a stable scrollbar as the user navigates through a list of items
     * with varying widths / heights.
     *
     * @param enabled Whether or not to enable smooth scrollbar.
     * @see #setSmoothScrollbarEnabled(boolean)
     *//*

    public void setSmoothScrollbarEnabled(boolean enabled) {
        mSmoothScrollbarEnabled = enabled;
    }

    public void setEnableBringCenterToFront(boolean bringCenterToTop) {
        assertNotInLayoutOrScroll(null);
        if (mEnableBringCenterToFront == bringCenterToTop) {
            return;
        }
        this.mEnableBringCenterToFront = bringCenterToTop;
        requestLayout();
    }

    public boolean getEnableBringCenterToFront() {
        return mEnableBringCenterToFront;
    }



    public int getRecyclerViewTotalSpace() {
        if (mOrientation == HORIZONTAL) {
            return getHeight() - getPaddingTop() - getPaddingBottom();
        } else {
            return getWidth() - getPaddingLeft() - getPaddingRight();
        }
    }


    protected float getDistanceRatio() {
        if (moveSpeed == 0) return Float.MAX_VALUE;
        return 1 / moveSpeed;
    }


    protected float setInterval() {
        return mDecoratedMeasurement * ((centerScale - 1) / 2 + 1) + itemSpace;
    }

    public void setItemSpace(int itemSpace) {
        this.itemSpace = itemSpace;
    }

    public void setCenterScale(float centerScale) {
        this.centerScale = centerScale;
    }
    public void setMoveSpeed(float moveSpeed) {
        assertNotInLayoutOrScroll(null);
        if (this.moveSpeed == moveSpeed) return;
        this.moveSpeed = moveSpeed;
    }
    protected void setItemViewProperty(View itemView, float targetOffset) {
        float scale = calculateScale(targetOffset + mSpaceMain);
        itemView.setScaleX(scale);
        itemView.setScaleY(scale);
    }
    */
/**
     * @param x start positon of the view you want scale
     * @return the scale rate of current scroll mOffset
     *//*

    private float calculateScale(float x) {
        float deltaX = Math.abs(x - (mOrientationHelper.getTotalSpace() - mDecoratedMeasurement) / 2f);
        float diff = 0f;
        if ((mDecoratedMeasurement - deltaX) > 0) diff = mDecoratedMeasurement - deltaX;
        return (centerScale - 1f) / mDecoratedMeasurement * diff + 1;
    }

    */
/**
     * cause elevation is not support below api 21,
     * so you can set your elevation here for supporting it below api 21
     * or you can just setElevation in {@link #setItemViewProperty(View, float)}
     *//*

    protected float setViewElevation(View itemView, float targetOffset) {
        return 0;
    }


    */
/**
     * Returns the current state of the smooth scrollbar feature. It is enabled by default.
     *
     * @return True if smooth scrollbar is enabled, false otherwise.
     * @see #setSmoothScrollbarEnabled(boolean)
     *//*

    public boolean getSmoothScrollbarEnabled() {
        return mSmoothScrollbarEnabled;
    }


}
*/
