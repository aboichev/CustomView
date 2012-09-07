package kaban.iklan;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class ScrollImageView extends View {


    private final int DEFAULT_PADDING = 10;
    private Display mDisplay;
    private Bitmap mImage;

    /* Current x and y of the touch */
    private float mCurrentX = 0;
    private float mCurrentY = 0;

    private float mTotalX = 0;
    private float mTotalY = 0;
    /* The touch distance change from the current touch */
    private float mDeltaX = 0;
    private float mDeltaY = 0;

    int mDisplayWidth;
    int mDisplayHeight;
    int mPadding;
    RectF mBounds;
    Paint mRectPaint;

    public ScrollImageView(Context context) {
        super(context);
        initScrollImageView(context);
    }

    public ScrollImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initScrollImageView(context);
    }

    private void initScrollImageView(Context context) {
        mDisplay = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        mPadding = getPadding();
        setImage(BitmapFactory.decodeResource(getResources(), R.drawable.piano));
        mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setColor(Color.RED);
        mRectPaint.setStrokeWidth(2f);
        mRectPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBounds = new RectF( getPaddingLeft(), getPaddingTop(),
                w - getPaddingRight(), h - getPaddingBottom());


        int height = (int) (mBounds.height() * 0.7);
        int width = (int)( mBounds.width() * (height / mBounds.height()));
        // rescale the image
        mImage = Bitmap.createScaledBitmap(mImage, width, height, true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public Bitmap getImage() {
        return mImage;
    }
    public void setImage(Bitmap image) {
        mImage = image;
    }

    public int getPadding() {
        return mPadding;
    }

    public void setPadding(int padding) {
        this.mPadding = padding;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mCurrentX = event.getRawX();
            mCurrentY = event.getRawY();
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float x = event.getRawX();
            float y = event.getRawY();

            // Update how much the touch moved
            mDeltaX = x - mCurrentX;
            mDeltaY = y - mCurrentY;

            mCurrentX = x;
            mCurrentY = y;

            invalidate();
        }
        // Consume event
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mImage == null) {
            return;
        }

        float newTotalX = mTotalX + mDeltaX;
        // Don't scroll off the left or right edges of the bitmap.
        if (mPadding > newTotalX && newTotalX > getMeasuredWidth() - mImage.getWidth() - mPadding)
            mTotalX += mDeltaX;

        float newTotalY = mTotalY + mDeltaY;
        // Don't scroll off the top or bottom edges of the bitmap.
        if (mPadding > newTotalY && newTotalY > getMeasuredHeight() - mImage.getHeight() - mPadding)
            mTotalY += mDeltaY;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        canvas.drawBitmap(mImage, mTotalX, mTotalY, paint);
        canvas.drawRect(mBounds, mRectPaint);
    }
}