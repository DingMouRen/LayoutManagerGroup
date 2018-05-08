package com.flurgle.blurkit;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * A {@link ViewGroup} that blurs all content behind it. Automatically creates bitmap of parent content
 * and finds its relative position to the top parent to draw properly regardless of where the layout is
 * placed.
 */
public class BlurLayout extends FrameLayout {

    public static final float DEFAULT_DOWNSCALE_FACTOR = 0.12f;
    public static final int DEFAULT_BLUR_RADIUS = 12;
    public static final int DEFAULT_FPS = 60;
    public static final float DEFAULT_CORNER_RADIUS = 0.f;

    // Customizable attributes

    /** Factor to scale the view bitmap with before blurring. */
    private float mDownscaleFactor;

    /** Blur radius passed directly to stackblur library. */
    private int mBlurRadius;

    /** Number of blur invalidations to do per second.  */
    private int mFPS;

    /** Corner radius for the layouts blur. To make rounded rects and circles. */
    private float mCornerRadius;

    /** Is blur running? */
    private boolean mRunning;

    /** Is window attached? */
    private boolean mAttachedToWindow;

    /** Do we need to recalculate the position each invalidation? */
    private boolean mPositionLocked;

    /** Do we need to regenerate the view bitmap each invalidation? */
    private boolean mViewLocked;

    // Calculated class dependencies

    /** ImageView to show the blurred content. */
    private RoundedImageView mImageView;

    /** Reference to View for top-parent. For retrieval see {@link #getActivityView() getActivityView}. */
    private WeakReference<View> mActivityView;

    /** A saved point to re-use when {@link #lockPosition()} called. */
    private Point mLockedPoint;

    /** A saved bitmap for the view to re-use when {@link #lockView()} called. */
    private Bitmap mLockedBitmap;

    public BlurLayout(Context context) {
        super(context, null);
    }

    public BlurLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            com.flurgle.blurkit.BlurKit.init(context);
        }

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                com.flurgle.blurkit.R.styleable.BlurLayout,
                0, 0);

        try {
            mDownscaleFactor = a.getFloat(R.styleable.BlurLayout_blk_downscaleFactor, DEFAULT_DOWNSCALE_FACTOR);
            mBlurRadius = a.getInteger(R.styleable.BlurLayout_blk_blurRadius, DEFAULT_BLUR_RADIUS);
            mFPS = a.getInteger(R.styleable.BlurLayout_blk_fps, DEFAULT_FPS);
            mCornerRadius = a.getDimension(R.styleable.BlurLayout_blk_cornerRadius, DEFAULT_CORNER_RADIUS);
        } finally {
            a.recycle();
        }

        mImageView = new RoundedImageView(getContext());
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(mImageView);

        setCornerRadius(mCornerRadius);
    }

    /** Choreographer callback that re-draws the blur and schedules another callback. */
    private Choreographer.FrameCallback invalidationLoop = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            invalidate();
            Choreographer.getInstance().postFrameCallbackDelayed(this, 1000 / mFPS);
        }
    };

    /** Start BlurLayout continuous invalidation. **/
    public void startBlur() {
        if (mRunning) {
            return;
        }

        if (mFPS > 0) {
            mRunning = true;
            Choreographer.getInstance().postFrameCallback(invalidationLoop);
        }
    }

    /** Pause BlurLayout continuous invalidation. **/
    public void pauseBlur() {
        if (!mRunning) {
            return;
        }

        mRunning = false;
        Choreographer.getInstance().removeFrameCallback(invalidationLoop);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachedToWindow = true;
        startBlur();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttachedToWindow = false;
        pauseBlur();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidate();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        Bitmap bitmap = blur();
        if (bitmap != null) {
            mImageView.setImageBitmap(bitmap);
        }
    }

    /**
     * Recreates blur for content and sets it as the background.
     */
    private Bitmap blur() {
        if (getContext() == null || isInEditMode()) {
            return null;
        }

        // Check the reference to the parent view.
        // If not available, attempt to make it.
        if (mActivityView == null || mActivityView.get() == null) {
            mActivityView = new WeakReference<>(getActivityView());
            if (mActivityView.get() == null) {
                return null;
            }
        }

        Point pointRelativeToActivityView;
        if (mPositionLocked) {
            // Generate a locked point if null.
            if (mLockedPoint == null) {
                mLockedPoint = getPositionInScreen();
            }

            // Use locked point.
            pointRelativeToActivityView = mLockedPoint;
        } else {
            // Calculate the relative point to the parent view.
            pointRelativeToActivityView = getPositionInScreen();
        }

        // Set alpha to 0 before creating the parent view bitmap.
        // The blur view shouldn't be visible in the created bitmap.
        setAlpha(0);

        // Screen sizes for bound checks
        int screenWidth = mActivityView.get().getWidth();
        int screenHeight = mActivityView.get().getHeight();

        // The final dimensions of the blurred bitmap.
        int width = (int) (getWidth() * mDownscaleFactor);
        int height = (int) (getHeight() * mDownscaleFactor);

        // The X/Y position of where to crop the bitmap.
        int x = (int) (pointRelativeToActivityView.x * mDownscaleFactor);
        int y = (int) (pointRelativeToActivityView.y * mDownscaleFactor);

        // Padding to add to crop pre-blur.
        // Blurring straight to edges has side-effects so padding is added.
        int xPadding = getWidth() / 8;
        int yPadding = getHeight() / 8;

        // Calculate padding independently for each side, checking edges.
        int leftOffset = -xPadding;
        leftOffset = x + leftOffset >= 0 ? leftOffset : 0;

        int rightOffset = xPadding;
        rightOffset = x + getWidth() + rightOffset <= screenWidth ? rightOffset : screenWidth - getWidth() - x;

        int topOffset = -yPadding;
        topOffset = y + topOffset >= 0 ? topOffset : 0;

        int bottomOffset = yPadding;
        bottomOffset = y + height + bottomOffset <= screenHeight ? bottomOffset : 0;

        // Parent view bitmap, downscaled with mDownscaleFactor
        Bitmap bitmap;
        if (mViewLocked) {
            // It's possible for mLockedBitmap to be null here even with view locked.
            // lockView() should always properly set mLockedBitmap if this code is reached
            // (it passed previous checks), so recall lockView and assume it's good.
            if (mLockedBitmap == null) {
                lockView();
            }

            if (width == 0 || height == 0) {
                return null;
            }

            bitmap = Bitmap.createBitmap(mLockedBitmap, x, y, width, height);
        } else {
            try {
                // Create parent view bitmap, cropped to the BlurLayout area with above padding.
                bitmap = getDownscaledBitmapForView(
                        mActivityView.get(),
                        new Rect(
                                pointRelativeToActivityView.x + leftOffset,
                                pointRelativeToActivityView.y + topOffset,
                                pointRelativeToActivityView.x + getWidth() + Math.abs(leftOffset) + rightOffset,
                                pointRelativeToActivityView.y + getHeight() + Math.abs(topOffset) + bottomOffset
                        ),
                        mDownscaleFactor
                );
            } catch (com.flurgle.blurkit.BlurKitException e) {
                return null;
            } catch (NullPointerException e) {
                return null;
            }

        }

        if (!mViewLocked) {
            // Blur the bitmap.
            bitmap = com.flurgle.blurkit.BlurKit.getInstance().blur(bitmap, mBlurRadius);

            //Crop the bitmap again to remove the padding.
            bitmap = Bitmap.createBitmap(
                    bitmap,
                    (int) (Math.abs(leftOffset) * mDownscaleFactor),
                    (int) (Math.abs(topOffset) * mDownscaleFactor),
                    width,
                    height
            );

        }

        // Make self visible again.
        setAlpha(1);

        // Set background as blurred bitmap.
        return bitmap;
    }

    /**
     * Casts context to Activity and attempts to create a view reference using the window decor view.
     * @return View reference for whole activity.
     */
    private View getActivityView() {
        Activity activity;
        try {
            activity = (Activity) getContext();
        } catch (ClassCastException e) {
            return null;
        }

        return activity.getWindow().getDecorView().findViewById(android.R.id.content);
    }

    /**
     * Returns the position in screen. Left abstract to allow for specific implementations such as
     * caching behavior.
     */
    private Point getPositionInScreen() {
        PointF pointF = getPositionInScreen(this);
        return new Point((int) pointF.x, (int) pointF.y);
    }

    /**
     * Finds the Point of the parent view, and offsets result by self getX() and getY().
     * @return Point determining position of the passed in view inside all of its ViewParents.
     */
    private PointF getPositionInScreen(View view) {
        if (getParent() == null) {
            return new PointF();
        }

        ViewGroup parent;
        try {
            parent = (ViewGroup) view.getParent();
        } catch (Exception e) {
            return new PointF();
        }

        if (parent == null) {
            return new PointF();
        }

        PointF point = getPositionInScreen(parent);
        point.offset(view.getX(), view.getY());
        return point;
    }

    /**
     * Users a View reference to create a bitmap, and downscales it using the passed in factor.
     * Uses a Rect to crop the view into the bitmap.
     * @return Bitmap made from view, downscaled by downscaleFactor.
     * @throws NullPointerException
     */
    private Bitmap getDownscaledBitmapForView(View view, Rect crop, float downscaleFactor) throws com.flurgle.blurkit.BlurKitException, NullPointerException {
        View screenView = view.getRootView();

        int width = (int) (crop.width() * downscaleFactor);
        int height = (int) (crop.height() * downscaleFactor);

        if (screenView.getWidth() <= 0 || screenView.getHeight() <= 0 || width <= 0 || height <= 0) {
            throw new com.flurgle.blurkit.BlurKitException("No screen available (width or height = 0)");
        }

        float dx = -crop.left * downscaleFactor;
        float dy = -crop.top * downscaleFactor;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Matrix matrix = new Matrix();
        matrix.preScale(downscaleFactor, downscaleFactor);
        matrix.postTranslate(dx, dy);
        canvas.setMatrix(matrix);
        screenView.draw(canvas);

        return bitmap;
    }

    /**
     * Sets downscale factor to use pre-blur.
     * See {@link #mDownscaleFactor}.
     */
    public void setDownscaleFactor(float downscaleFactor) {
        this.mDownscaleFactor = downscaleFactor;

        // This field is now bad (it's pre-scaled with downscaleFactor so will need to be re-made)
        this.mLockedBitmap = null;

        invalidate();
    }

    /**
     * Sets blur radius to use on downscaled bitmap.
     * See {@link #mBlurRadius}.
     */
    public void setBlurRadius(int blurRadius) {
        this.mBlurRadius = blurRadius;

        // This field is now bad (it's pre-blurred with blurRadius so will need to be re-made)
        this.mLockedBitmap = null;

        invalidate();
    }

    /**
     * Sets FPS to invalidate blur with.
     * See {@link #mFPS}.
     */
    public void setFPS(int fps) {
        if (mRunning) {
            pauseBlur();
        }

        this.mFPS = fps;

        if (mAttachedToWindow) {
            startBlur();
        }
    }

    public void setCornerRadius(float cornerRadius) {
        this.mCornerRadius = cornerRadius;
        if (mImageView != null) {
            mImageView.setCornerRadius(cornerRadius);
        }
        invalidate();
    }

    /**
     * Save the view bitmap to be re-used each frame instead of regenerating.
     */
    public void lockView() {
        mViewLocked = true;

        if (mActivityView != null && mActivityView.get() != null) {
            View view = mActivityView.get().getRootView();
            try {
                setAlpha(0f);
                mLockedBitmap = getDownscaledBitmapForView(view, new Rect(0, 0, view.getWidth(), view.getHeight()), mDownscaleFactor);
                setAlpha(1f);
                mLockedBitmap = com.flurgle.blurkit.BlurKit.getInstance().blur(mLockedBitmap, mBlurRadius);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * Stop using saved view bitmap. View bitmap will now be re-made each frame.
     */
    public void unlockView() {
        mViewLocked = false;
        mLockedBitmap = null;
    }

    /**
     * Save the view position to be re-used each frame instead of regenerating.
     */
    public void lockPosition() {
        mPositionLocked = true;
        mLockedPoint = getPositionInScreen();
    }

    /**
     * Stop using saved point. Point will now be re-made each frame.
     */
    public void unlockPosition() {
        mPositionLocked = false;
        mLockedPoint = null;
    }

}
