package com.dingmouren.example.layoutmanagergroup.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dingmouren.example.layoutmanagergroup.R;


/**
 * Created by DELL on 2017/7/25.
 */
public class SmileView extends LinearLayout implements Animator.AnimatorListener {


    //分割线间距
    private int dividerMargin = 20;
    private float count;
    private int defalutBottom = 70;
    private String defaultLike = "喜欢";
    private String defalutDis = "无感";
    private int defalutTextColor = Color.WHITE;
    private String defaluteShadow = "#7F484848";
    private int defalutGravity = Gravity.CENTER_HORIZONTAL;
    private int defalutSize = dip2px(getContext(), 25);

    private int like = 20;
    private int disLike = 20; //点赞数,差评数
    private float fLike, fDis;
    private ImageView imageLike;
    private ImageView imageDis;

    private TextView likeNum, disNum, likeText, disText;
    private LinearLayout likeBack, disBack, likeAll, disAll;
    private AnimationDrawable animLike, animDis; //笑脸帧动画
    private ValueAnimator animatorBack; //背景拉伸动画

    private int type = 0; //选择执行帧动画的笑脸 //0 笑脸 1 哭脸
    private boolean isClose = false; //判断收起动画

    public SmileView setDefalutBottom(int defalutBottom) {
        this.defalutBottom = defalutBottom;
        return this;
    }

    public void notifyChange() {
        init();
        bindListener();
    }


    public void setDefalutGravity(int defalutGravity) {
        this.defalutGravity = defalutGravity;
    }

    public void setDefalutDis(String defalutDis) {
        this.defalutDis = defalutDis;
    }

    public void setDefaultLike(String defaultLike) {
        this.defaultLike = defaultLike;

    }

    public SmileView setDividerMargin(int dividerMargin) {
        this.dividerMargin = dividerMargin;
        return this;
    }


    public void setDefalutSize(int defalutSize) {
        this.defalutSize = defalutSize;
    }

    public void setNum(int like, int dislike) {
        //设置百分比
        count = like + dislike;
        fLike = like / count;
        fDis = dislike / count;
        this.like = (int) (fLike * 100);
        this.disLike = 100 - this.like;
        setLike(this.like);
        setDisLike(this.disLike);
    }

    public void setLike(int like) {
        likeNum.setText(like + "");
    }

    public void setDisLike(int disLike) {
        disNum.setText(disLike + "");
    }


    public SmileView(Context context) {
        this(context, null);
    }

    public SmileView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        bindListener();
    }

    private void init() {
        this.removeAllViews();
        //初始化总布局
        setOrientation(HORIZONTAL);
        setGravity(defalutGravity | Gravity.BOTTOM);
        setBackgroundColor(Color.TRANSPARENT); //开始透明


        //设置百分比
        float count = like + disLike;
        fLike = like / count;
        fDis = disLike / count;
        like = (int) (fLike * 100);
        disLike = (int) (fDis * 100);

        //初始化图片
        imageLike = new ImageView(getContext());
        //添加动画资源  获得帧动画
        imageLike.setBackgroundResource(R.drawable.animation_like);
        animLike = (AnimationDrawable) imageLike.getBackground();
        //初始化文字
        likeNum = new TextView(getContext());
        likeNum.setText(like + "");
        likeNum.setTextColor(defalutTextColor);
        TextPaint likeNumPaint = likeNum.getPaint();
        likeNumPaint.setFakeBoldText(true);
        likeNum.setTextSize(20f);
        likeText = new TextView(getContext());
        likeText.setText(defaultLike);
        likeText.setTextColor(defalutTextColor);

        imageDis = new ImageView(getContext());
        imageDis.setBackgroundResource(R.drawable.animation_dislike);
        animDis = (AnimationDrawable) imageDis.getBackground();
        disNum = new TextView(getContext());
        disNum.setText(disLike + "");
        disNum.setTextColor(defalutTextColor);
        TextPaint disNumPaint = disNum.getPaint();
        disNumPaint.setFakeBoldText(true);
        disNum.setTextSize(20f);
        disText = new TextView(getContext());
        disText.setText(defalutDis);
        disText.setTextColor(defalutTextColor);


        //初始化布局
        likeBack = new LinearLayout(getContext());
        disBack = new LinearLayout(getContext());
        LayoutParams params2 = new LayoutParams(defalutSize, defalutSize);
        likeBack.addView(imageLike, params2);
        disBack.addView(imageDis, params2);
        disBack.setBackgroundResource(R.drawable.white_background);
        likeBack.setBackgroundResource(R.drawable.white_background);

        //单列总布局
        likeAll = new LinearLayout(getContext());
        disAll = new LinearLayout(getContext());
        likeAll.setOrientation(VERTICAL);
        disAll.setOrientation(VERTICAL);
        likeAll.setGravity(Gravity.CENTER_HORIZONTAL);
        disAll.setGravity(Gravity.CENTER_HORIZONTAL);
        likeAll.setBackgroundColor(Color.TRANSPARENT);
        disAll.setBackgroundColor(Color.TRANSPARENT);

        //添加文字图片放进一列
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 10, 0, 0);
        params.gravity = Gravity.CENTER;
        disAll.setGravity(Gravity.CENTER_HORIZONTAL);
        likeAll.setGravity(Gravity.RIGHT);
        disAll.addView(disNum, params);
        disAll.addView(disText, params);
        disAll.addView(disBack, params);

        likeAll.addView(likeNum, params);
        likeAll.addView(likeText, params);
        likeAll.addView(likeBack, params);

        //中间分隔线
        ImageView imageView = new ImageView(getContext());
        imageView.setBackground(new ColorDrawable(Color.GRAY));
        LayoutParams params4 = new LayoutParams(3, 80);
        params4.setMargins(dividerMargin, 10, dividerMargin, defalutBottom + 20);
        params4.gravity = Gravity.BOTTOM;


        LayoutParams params3 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params3.setMargins(30, 20, 30, defalutBottom);
        params3.gravity = Gravity.BOTTOM;
        addView(disAll, params3);
        addView(imageView, params4);
        addView(likeAll, params3);

        //隐藏文字
        setVisibities(GONE);
    }

    //
    public void setVisibities(int v) {
        likeNum.setVisibility(v);
        disNum.setVisibility(v);
        likeText.setVisibility(v);
        disText.setVisibility(v);
    }

    //绑定监听
    private void bindListener() {
        imageDis.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               /* type = 1; //设置动画对象
                animBack(); //拉伸背景
                setVisibities(VISIBLE); //隐藏文字
                //切换背景色
                setBackgroundColor(Color.TRANSPARENT);
                likeBack.setBackgroundResource(R.drawable.white_background);
                disBack.setBackgroundResource(R.drawable.yellow_background);
                //重置帧动画
                imageLike.setBackground(null);
                imageLike.setBackgroundResource(R.drawable.animation_like);
                animLike = (AnimationDrawable) imageLike.getBackground();*/
               disLikeAnimation();
            }
        });
        imageLike.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               /* type = 0;
                animBack();
                setVisibities(VISIBLE);
                setBackgroundColor(Color.TRANSPARENT);
                disBack.setBackgroundResource(R.drawable.white_background);
                likeBack.setBackgroundResource(R.drawable.yellow_background);
                imageDis.setBackground(null);
                imageDis.setBackgroundResource(R.drawable.animation_dislike);
                animDis = (AnimationDrawable) imageDis.getBackground();*/
               likeAnimation();
            }
        });
    }

    public void disLikeAnimation(){
        type = 1; //设置动画对象
        animBack(); //拉伸背景
        setVisibities(VISIBLE); //隐藏文字
        //切换背景色
        setBackgroundColor(Color.parseColor(defaluteShadow));
        likeBack.setBackgroundResource(R.drawable.white_background);
        disBack.setBackgroundResource(R.drawable.yellow_background);
        //重置帧动画
        imageLike.setBackground(null);
        imageLike.setBackgroundResource(R.drawable.animation_like);
        animLike = (AnimationDrawable) imageLike.getBackground();
    }

    public void likeAnimation(){
        type = 0;
        animBack();
        setVisibities(VISIBLE);
        setBackgroundColor(Color.parseColor(defaluteShadow));
        disBack.setBackgroundResource(R.drawable.white_background);
        likeBack.setBackgroundResource(R.drawable.yellow_background);
        imageDis.setBackground(null);
        imageDis.setBackgroundResource(R.drawable.animation_dislike);
        animDis = (AnimationDrawable) imageDis.getBackground();
    }

    //背景伸展动画
    public void animBack() {
        //动画执行中不能点击
        imageDis.setClickable(false);
        imageLike.setClickable(false);

        final int max = Math.max(like * 4, disLike * 4);
        animatorBack = ValueAnimator.ofInt(5, max);
        animatorBack.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int magrin = (int) animation.getAnimatedValue();
                LayoutParams paramsLike
                        = (LayoutParams) imageLike.getLayoutParams();
                paramsLike.bottomMargin = magrin;

                if (magrin <= like * 4) {
                    imageLike.setLayoutParams(paramsLike);
                }
                if (magrin <= disLike * 4) {
                    imageDis.setLayoutParams(paramsLike);
                }
            }
        });
        isClose = false;
        animatorBack.addListener(this);
        animatorBack.setDuration(500);
        animatorBack.start();
    }

    //背景收回动画
    public void setBackUp() {
        final int max = Math.max(like * 4, disLike * 4);
        animatorBack = ValueAnimator.ofInt(max, 5);
        animatorBack.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int magrin = (int) animation.getAnimatedValue();
                LayoutParams paramsLike
                        = (LayoutParams) imageLike.getLayoutParams();
                paramsLike.bottomMargin = magrin;

                if (magrin <= like * 4) {
                    imageLike.setLayoutParams(paramsLike);
                }
                if (magrin <= disLike * 4) {
                    imageDis.setLayoutParams(paramsLike);
                }
            }
        });
        animatorBack.addListener(this);
        animatorBack.setDuration(500);
        animatorBack.start();
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        //重置帧动画
        animDis.stop();
        animLike.stop();

        //关闭时不执行帧动画
        if (isClose) {
            //收回后可点击
            imageDis.setClickable(true);
            imageLike.setClickable(true);
            //隐藏文字
            setVisibities(GONE);
            //恢复透明
            setBackgroundColor(Color.TRANSPARENT);
            return;
        }
        isClose = true;

        if (type == 0) {
            animLike.start();
            objectY(imageLike);
        } else {
            animDis.start();
            objectX(imageDis);
        }
    }

    public void objectY(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", -10.0f, 0.0f, 10.0f, 0.0f, -10.0f, 0.0f, 10.0f, 0);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        //animator.setRepeatCount(1);
        animator.setDuration(1500);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setBackUp(); //执行回弹动画
            }
        });
    }

    public void objectX(View view) {

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", -10.0f, 0.0f, 10.0f, 0.0f, -10.0f, 0.0f, 10.0f, 0);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        // animator.setRepeatCount(1);
        animator.setDuration(1500);
        animator.start();

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setBackUp(); //执行回弹动画
            }
        });
    }

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    //dp转px
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    //px转dp
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}