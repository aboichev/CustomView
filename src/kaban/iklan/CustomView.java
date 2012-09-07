package kaban.iklan;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

class CustomView extends View {

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private static final int mTextColor = Color.GREEN;
    private static final int mTextX = 10;
    private static final int mTextY = 20;

    private Paint mTextPaint;
    private Paint mShadowPaint;
    private Paint mRectPaint;

    private float mTextWidth;
    private float mTextHeight;

    RectF mBounds;

    private void init() {

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        if (mTextHeight == 0) {
            mTextHeight = mTextPaint.getTextSize();
        } else {
            mTextPaint.setTextSize(mTextHeight);
        }

        mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setColor(Color.RED);
        mRectPaint.setStrokeWidth(2f);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setTextSize(mTextHeight);

        mShadowPaint = new Paint(0);
        mShadowPaint.setColor(0xff101010);
        mShadowPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(mBounds, mRectPaint);
        // Draw the label text
        canvas.drawText("This is my view", mTextX, mTextY, mTextPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mBounds = new RectF( getPaddingLeft(), getPaddingTop(),
                                 w - getPaddingRight(), h - getPaddingBottom());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        int minh = MeasureSpec.getSize(w) - (int) mTextWidth + getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(minh - (int) mTextWidth, heightMeasureSpec, 0);

        setMeasuredDimension(resolveSize(getWidth(), widthMeasureSpec),
                resolveSize(getHeight(), heightMeasureSpec));

        setMeasuredDimension(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // Do nothing. Do not call the superclass method--that would start a layout pass
        // on this view's children. This class lays out its children in onSizeChanged().
    }
}
