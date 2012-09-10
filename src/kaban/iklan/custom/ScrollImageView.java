package kaban.iklan.custom;

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
    private Bitmap mSliderImage;

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
    RectF sliderHandleRect;

    float screenToKeyboardRatio;

    Paint mRectPaint;

    byte octaveMultiplier;


    public ScrollImageView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mDisplay = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        mPadding = getPadding();

        mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setColor(Color.RED);
        mRectPaint.setStrokeWidth(4f);
        mRectPaint.setStyle(Paint.Style.STROKE);

        byte lowestNoteOctave = 3;
        byte highestNoteOctave = 4;
        octaveMultiplier = (byte)(highestNoteOctave - lowestNoteOctave + 1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        mBounds = new RectF( getPaddingLeft(), getPaddingTop(),
                w - getPaddingRight(), h - getPaddingBottom());

        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = true;
        Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.piano);
        // rescale the image
        int height = (int) (mBounds.height() * 0.7);
        int width = (int)( mBounds.width() * (height / mBounds.height()));
        Bitmap scaledImage = Bitmap.createScaledBitmap(original, width, height, true);
        original.recycle();

        if (mImage != null) {
            mImage.recycle();
        }

        mImage = Bitmap.createScaledBitmap(scaledImage, width * octaveMultiplier, height, true);
        Canvas tmpCanvas = new Canvas();
        tmpCanvas.setBitmap(mImage);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        for(int i= 0; i < octaveMultiplier; i++ ){
            tmpCanvas.drawBitmap(scaledImage, mTotalX + scaledImage.getWidth() * i, mTotalY, paint);
        }

        if(mSliderImage != null){
            mSliderImage.recycle();
        }

        screenToKeyboardRatio = w * 1.0f / mImage.getWidth();
        mSliderImage = Bitmap.createScaledBitmap(mImage, (int)mBounds.width(), 40, true);
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
        float x =  event.getRawX();
        float y =  event.getRawY();

        if (event.getAction() == MotionEvent.ACTION_DOWN &&
            sliderHandleRect.contains(x, y)) {

            mCurrentX = event.getRawX();
            mCurrentY = event.getRawY();
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE &&
                sliderHandleRect.contains(x, y)) {

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

        float newTotalX = mTotalX - mDeltaX;
        // Don't scroll off the left or right edges of the bitmap.
        if (mPadding > newTotalX && newTotalX > getMeasuredWidth() - mImage.getWidth() - mPadding)
            mTotalX -= mDeltaX;

        float newTotalY = mTotalY - mDeltaY;
        // Don't scroll off the top or bottom edges of the bitmap.
        if (mPadding > newTotalY && newTotalY > getMeasuredHeight() - mImage.getHeight() - mPadding)
            mTotalY -= mDeltaY;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawBitmap(mImage, mTotalX, mTotalY, paint);
        canvas.drawBitmap(mSliderImage, 0, mImage.getHeight(), paint);

        sliderHandleRect = new RectF(-mTotalX * screenToKeyboardRatio,
                                     mImage.getHeight(),
                                     -mTotalX * screenToKeyboardRatio + mSliderImage.getWidth() * screenToKeyboardRatio,
                                     mImage.getHeight() + 40);

        canvas.drawRect(sliderHandleRect, mRectPaint);


        // canvas.drawRect(mBounds, mRectPaint);
    }
}