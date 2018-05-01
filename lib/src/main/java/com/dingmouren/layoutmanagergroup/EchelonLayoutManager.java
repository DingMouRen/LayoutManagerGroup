package com.dingmouren.layoutmanagergroup;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

/**
 * Created by thunderPunch on 2017/2/15
 * Description:
 */

public class EchelonLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = "LadderLayoutManager";
    private static final int INVALIDATE_SCROLL_OFFSET = Integer.MAX_VALUE;
    private static final float DEFAULT_CHILD_LAYOUT_OFFSET = 0.2f;
    private static final int UNLIMITED = 0;
    private static final int VERTICAL = 1;
    private static final int HORIZONTAL = 0;

    private boolean mCheckedChildSize;
    private int[] mChildSize;
    private int mChildPeekSize;
    private int mChildPeekSizeInput;
    private int mScrollOffset = INVALIDATE_SCROLL_OFFSET;
    private float mItemHeightWidthRatio;//childview的纵横比。所有childview都会按该纵横比展示
    private float mScale;//chidview每一层级相对于上一层级的缩放量
    private int mChildCount;
    private float mVanishOffset = 0;//vanish消失
    private Interpolator mInterpolator;//插值器
    private int mOrientation;//布局方向
    private int mMaxItemLayoutCount;
    private Context mContext;

    public EchelonLayoutManager(Context context) {

        this(1F, 0.9f, VERTICAL);
        this.mContext = context;
    }

    /**
     * @param itemHeightWidthRatio childview的纵横比。所有childview都会按该纵横比展示
     * @param scale                chidview每一层级相对于上一层级的缩放量
     * @param orientation          布局方向
     */
    public EchelonLayoutManager(float itemHeightWidthRatio, float scale, int orientation) {
        this.mItemHeightWidthRatio = itemHeightWidthRatio;
        this.mOrientation = orientation;
        this.mScale = scale;
        this.mChildSize = new int[2];
        this.mInterpolator = new LinearInterpolator();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }


    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        super.onMeasure(recycler, state, widthSpec, heightSpec);
        mCheckedChildSize = false;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0 || state.isPreLayout()) return; //sate.isPreLayout()正在测量视图
            removeAndRecycleAllViews(recycler);

        if (!mCheckedChildSize) {//mCheckedChildSize为false才会执行，确定mChildSize数组的值、mChildPeekSize
            if (mOrientation == VERTICAL) {
                mChildSize[0] = getHorizontalSpace();//item的宽
                mChildSize[1] = (int) (mItemHeightWidthRatio * mChildSize[0]);//item的高
            } /*else {
                mChildSize[1] = getVerticalSpace();
                mChildSize[0] = (int) (mChildSize[1] / mItemHeightWidthRatio);
                Log.e(TAG, "onLayoutChildren--水平" + mChildSize);
            }*/
//            mChildPeekSize = mChildPeekSizeInput == 0 ? (int) (mChildSize[mOrientation] * DEFAULT_CHILD_LAYOUT_OFFSET) : mChildPeekSizeInput;
            mChildPeekSize = (int) (mChildSize[1] * 0.1f);
            mCheckedChildSize = true;
        }
        mChildCount =  getItemCount();
        mScrollOffset = makeScrollOffsetWithinRange(mScrollOffset);
//        Log.e(TAG,"scrollOffset初始值:"+mScrollOffset);
//        Log.e(TAG,"mChildSize[0]:"+mChildSize[0]+"  mChildSize[1]:"+mChildSize[1]);

        fill(recycler);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int pendingScrollOffset = mScrollOffset + dy;
        mScrollOffset = makeScrollOffsetWithinRange(pendingScrollOffset);

//        Log.e(TAG,"scrollVerticallyBy---scrollOffset初始值:"+mScrollOffset);
//        Log.e(TAG,"scrollVerticallyBy---mChildSize[0]:"+mChildSize[0]+"  mChildSize[1]:"+mChildSize[1]);
//        float y = getChildAt(0).getY();
//        Log.e(TAG,"child-y"+y+"    "+(1920-y));
//        Log.e(TAG,"状态栏高度："+getStatusBarHeight(mContext));

        fill(recycler);
        return mScrollOffset - pendingScrollOffset + dy;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int pendingScrollOffset = mScrollOffset + dx;
        mScrollOffset = makeScrollOffsetWithinRange(pendingScrollOffset);
        fill(recycler);
        return mScrollOffset - pendingScrollOffset + dx;
    }


    @Override
    public boolean canScrollVertically() {
        return mOrientation == VERTICAL;
    }

    @Override
    public boolean canScrollHorizontally() {
        return mOrientation == HORIZONTAL;
    }

    //--------------------------------------------------------------

    private void fill(RecyclerView.Recycler recycler) {
        int bottomItemPosition = (int) Math.floor(mScrollOffset / mChildSize[mOrientation]);//>=1
        int bottomItemVisibleSize = mScrollOffset % mChildSize[mOrientation];
        final float offsetPercent = mInterpolator.getInterpolation(bottomItemVisibleSize * 1.0f / mChildSize[mOrientation]);//[0,1)
        final int space = mOrientation == VERTICAL ? getVerticalSpace() : getHorizontalSpace();
//        Log.e(TAG,"bottomItemPosition:"+bottomItemPosition+"  bottomItemVisibleSize:"+bottomItemVisibleSize+" offsetPersent:"+offsetPercent+
//        " space:"+space);
        ArrayList<ItemLayoutInfo> layoutInfos = new ArrayList<>();
        for (int i = bottomItemPosition - 1, j = 1, remainSpace = space - mChildSize[mOrientation];
             i >= 0; i--, j++) {
            double maxOffset = mChildPeekSize * Math.pow(mScale, j);
            int start = (int) (remainSpace - offsetPercent * maxOffset);
            ItemLayoutInfo info = new ItemLayoutInfo(start,
                    (float) (Math.pow(mScale, j - 1) * (1 - offsetPercent * (1 - mScale))),
                    offsetPercent,
                    start * 1.0f / space
            );
            layoutInfos.add(0, info);

            if (mMaxItemLayoutCount != UNLIMITED && j == mMaxItemLayoutCount - 1) {
                if (offsetPercent != 0) {
                    info.start = remainSpace;
                    info.positionOffsetPercent = 0;
                    info.layoutPercent = remainSpace / space;
                    info.scaleXY = (float) Math.pow(mScale, j - 1);
                }
                break;
            }
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
                    bottomItemVisibleSize * 1.0f / mChildSize[mOrientation], start * 1.0f / space).
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
        final int scaleFix = (int) (mChildSize[mOrientation] * (1 - layoutInfo.scaleXY) / 2);
        final float gap = (mOrientation == VERTICAL ? getHorizontalSpace() : getVerticalSpace())
                - mChildSize[(mOrientation + 1) % 2] * layoutInfo.scaleXY;

        if (mOrientation == VERTICAL) {
            int left = (int) (getPaddingLeft() + (gap * 0.5 * mVanishOffset));
            layoutDecoratedWithMargins(view, left, layoutInfo.start - scaleFix, left + mChildSize[0], layoutInfo.start + mChildSize[1] - scaleFix);
//            Log.e(TAG,"left:"+left+" scaleFix:"+scaleFix+" gap:"+gap);
        } else {
            int top = (int) (getPaddingTop() + (gap * 0.5 * mVanishOffset));
            layoutDecoratedWithMargins(view, layoutInfo.start - scaleFix, top
                    , layoutInfo.start + mChildSize[0] - scaleFix, top + mChildSize[1]);
        }
        Log.e(TAG,"scale:"+layoutInfo.scaleXY);
        ViewCompat.setScaleX(view, layoutInfo.scaleXY);//控制缩放
        ViewCompat.setScaleY(view, layoutInfo.scaleXY);
    }

    private void measureChildWithExactlySize(View child) {
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(
                mChildSize[0] - lp.leftMargin - lp.rightMargin, View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(
                mChildSize[1] - lp.topMargin - lp.bottomMargin, View.MeasureSpec.EXACTLY);
        child.measure(widthSpec, heightSpec);
    }

    private int makeScrollOffsetWithinRange(int scrollOffset) {
        return Math.min(Math.max(mChildSize[mOrientation], scrollOffset), mChildCount * mChildSize[mOrientation]);
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


    //-----------------------------------------------------------------------------------------------
    private static class ItemLayoutInfo {
        float scaleXY;
        float layoutPercent;
        float positionOffsetPercent;
        int start;
        boolean isBottom;

        private ItemLayoutInfo(int top, float scale, float positonOffset, float percent) {
            this.start = top;
            this.scaleXY = scale;
            this.positionOffsetPercent = positonOffset;
            this.layoutPercent = percent;
        }

        private ItemLayoutInfo setIsBottom() {
            isBottom = true;
            return this;
        }

    }

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}

