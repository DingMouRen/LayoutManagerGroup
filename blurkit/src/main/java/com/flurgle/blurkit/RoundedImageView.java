package com.flurgle.blurkit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {

    private float mCornerRadius = 0;

    public RoundedImageView(Context context) {
        super(context, null);
    }

    public RoundedImageView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable myDrawable = getDrawable();
        if (myDrawable!=null && myDrawable instanceof BitmapDrawable && mCornerRadius > 0) {
            Paint paint = ((BitmapDrawable) myDrawable).getPaint();
            final int color = 0xff000000;
            Rect bitmapBounds = myDrawable.getBounds();
            final RectF rectF = new RectF(bitmapBounds);
            int saveCount = canvas.saveLayer(rectF, null,
                    Canvas.MATRIX_SAVE_FLAG |
                            Canvas.CLIP_SAVE_FLAG |
                            Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                            Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                            Canvas.CLIP_TO_LAYER_SAVE_FLAG);
            getImageMatrix().mapRect(rectF);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, mCornerRadius, mCornerRadius, paint);

            Xfermode oldMode = paint.getXfermode();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            super.onDraw(canvas);
            paint.setXfermode(oldMode);
            canvas.restoreToCount(saveCount);
        } else {
            super.onDraw(canvas);
        }
    }

    public void setCornerRadius(float cornerRadius) {
        this.mCornerRadius = cornerRadius;
    }

}