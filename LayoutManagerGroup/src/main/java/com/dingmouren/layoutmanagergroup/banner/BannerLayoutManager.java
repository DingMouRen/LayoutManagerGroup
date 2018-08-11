package com.dingmouren.layoutmanagergroup.banner;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.NO_POSITION;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;
import static android.view.View.DRAG_FLAG_GLOBAL;

/**
 * Created by 钉某人
 * github: https://github.com/DingMouRen
 * email: naildingmouren@gmail.com
 */

public  class BannerLayoutManager extends LinearLayoutManager{
    private static final String TAG = "BannerLayoutManager";
    private LinearSnapHelper mLinearSnapHelper;
    private RecyclerView mRecyclerView;
    private OnSelectedViewListener mOnSelectedViewListener;
    private int mRealCount;
    private int mCurrentPosition = 0;
    private TaskHandler mHandler;
    private long mTimeDelayed = 1000;
    private int mOrientation;
    private float mTimeSmooth = 150f;

    public BannerLayoutManager(Context context,RecyclerView recyclerView ,int realCount) {
        super(context);
        this.mLinearSnapHelper = new LinearSnapHelper();
        this.mRealCount = realCount;
        this.mHandler = new TaskHandler(this);
        this.mRecyclerView = recyclerView;
        setOrientation(HORIZONTAL);
        this.mOrientation = HORIZONTAL;
    }
    public BannerLayoutManager(Context context,RecyclerView recyclerView ,int realCount,int orientation) {
        super(context);
        this.mLinearSnapHelper = new LinearSnapHelper();
        this.mRealCount = realCount;
        this.mHandler = new TaskHandler(this);
        this.mRecyclerView = recyclerView;
        setOrientation(orientation);
        this.mOrientation = orientation;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mLinearSnapHelper.attachToRecyclerView(view);
    }


    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller smoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
                    // 返回：滑过1px时经历的时间(ms)。
                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return mTimeSmooth / displayMetrics.densityDpi;
                    }
                };

        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }




    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE){//滑动停止
            if ( mLinearSnapHelper != null) {

                View view = mLinearSnapHelper.findSnapView(this);
                mCurrentPosition = getPosition(view);

                if (mOnSelectedViewListener != null)mOnSelectedViewListener.onSelectedView(view,mCurrentPosition % mRealCount);


                mHandler.setSendMsg(true);
                Message msg = Message.obtain();
                mCurrentPosition++;
                msg.what = mCurrentPosition;
                mHandler.sendMessageDelayed(msg,mTimeDelayed);

            }
        }else if (state == SCROLL_STATE_DRAGGING){//拖动
            mHandler.setSendMsg(false);
        }
    }

    public void setTimeDelayed(long timeDelayed) {
        this.mTimeDelayed = timeDelayed;
    }

    public void setTimeSmooth(float timeSmooth){
        this.mTimeSmooth = timeSmooth;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        mHandler.setSendMsg(true);
        Message msg = Message.obtain();
        msg.what = mCurrentPosition + 1;
        mHandler.sendMessageDelayed(msg,mTimeDelayed);
    }

    public void setOnSelectedViewListener(OnSelectedViewListener listener) {
        this.mOnSelectedViewListener = listener;
    }

    public RecyclerView getRecyclerView(){
        return mRecyclerView;
    }




    /**
     * 停止时，显示在中间的View的监听
     */
    public interface OnSelectedViewListener {
        void onSelectedView(View view,int position);
    }

    private static class TaskHandler extends Handler{
        private WeakReference<BannerLayoutManager> mWeakBanner;
        private boolean mSendMsg;
        public TaskHandler(BannerLayoutManager bannerLayoutManager){
            this.mWeakBanner = new WeakReference<BannerLayoutManager>(bannerLayoutManager);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg != null && mSendMsg){
                int position = msg.what;
                BannerLayoutManager bannerLayoutManager = mWeakBanner.get();
                if (bannerLayoutManager != null){
                    bannerLayoutManager.getRecyclerView().smoothScrollToPosition(position);
                }
            }
        }

        public void setSendMsg(boolean sendMsg){
            this.mSendMsg = sendMsg;
        }

    }
}
