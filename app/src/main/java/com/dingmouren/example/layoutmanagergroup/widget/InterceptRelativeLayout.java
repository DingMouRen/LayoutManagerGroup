package com.dingmouren.example.layoutmanagergroup.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by dingmouren
 * email: naildingmouren@gmail.com
 * github: https://github.com/DingMouRen
 */

public class InterceptRelativeLayout extends RelativeLayout {

    private boolean mIntercept = true;

    public InterceptRelativeLayout(Context context) {
        super(context);
    }

    public InterceptRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mIntercept){
            return true;
        }else {
            return super.dispatchTouchEvent(ev);
        }
    }

    public void setIntercept(boolean intercept){
        this.mIntercept = intercept;
    }
}
