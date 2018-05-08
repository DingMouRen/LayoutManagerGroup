package com.flurgle.blurkit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.View;

public class BlurKit {

    private static BlurKit instance;

    private RenderScript rs;

    public static void init(Context context) {
        if (instance != null) {
            return;
        }

        instance = new BlurKit();
        instance.rs = RenderScript.create(context);
    }

    public Bitmap blur(Bitmap src, int radius) {
        final Allocation input = Allocation.createFromBitmap(rs, src);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(src);
        return src;
    }

    public Bitmap blur(View src, int radius) {
        Bitmap bitmap = getBitmapForView(src, 1f);
        return blur(bitmap, radius);
    }

    public Bitmap fastBlur(View src, int radius, float downscaleFactor) {
        Bitmap bitmap = getBitmapForView(src, downscaleFactor);
        return blur(bitmap, radius);
    }

    private Bitmap getBitmapForView(View src, float downscaleFactor) {
        Bitmap bitmap = Bitmap.createBitmap(
                (int) (src.getWidth() * downscaleFactor),
                (int) (src.getHeight() * downscaleFactor),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        Matrix matrix = new Matrix();
        matrix.preScale(downscaleFactor, downscaleFactor);
        canvas.setMatrix(matrix);
        src.draw(canvas);

        return bitmap;
    }

    public static BlurKit getInstance() {
        if (instance == null) {
            throw new RuntimeException("BlurKit not initialized!");
        }

        return instance;
    }

}
