package com.moe.neko;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Animatable;
import android.graphics.ColorFilter;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;

public class AnimatableDrawable extends Drawable implements Animatable {
    private ValueAnimator mAnimator;
    private Callback callback;
    private Bitmap bitmap;
    public AnimatableDrawable(Bitmap bitmap,Anime callback){
        this.bitmap=bitmap;
        this.callback=callback;
        mAnimator=ObjectAnimator.ofFloat(new float[]{0,1});
        mAnimator.setDuration(this.callback.getDuration());
        mAnimator.setInterpolator(this.callback.getInterpolator());
        mAnimator.setRepeatMode(this.callback.getRepeatMode());
        mAnimator.setStartDelay(this.callback.getStartDelay());
        mAnimator.addUpdateListener(new UpdateListener());
    }
    @Override
    public void draw(Canvas p1) {
        callback.draw(bitmap,getBounds(),p1, mAnimator.getAnimatedFraction());
    }

    @Override
    public void setAlpha(int p1) {
    }

    @Override
    public void setColorFilter(ColorFilter p1) {
    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public void start() {
        mAnimator.start();
    }

    @Override
    public void stop() {
        mAnimator.cancel();
    }

    @Override
    public boolean isRunning() {
        return mAnimator.isRunning();
    }

    @Override
    public int getIntrinsicWidth() {
        return bitmap.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return bitmap.getHeight();
    }

    public interface Callback{
        void draw(Bitmap bitmap,Rect bounds,Canvas canvas,float fraction);
        public long getDuration();
        public TimeInterpolator getInterpolator();
        public int getRepeatMode();
        public long getStartDelay();
    }
    class UpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator p1) {
            if(getCallback()!=null)
            getCallback().invalidateDrawable(AnimatableDrawable.this);
        }
        
        
    }
    
}
