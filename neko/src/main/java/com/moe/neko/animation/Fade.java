package com.moe.neko.animation;
import com.moe.neko.AnimatableDrawable;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.graphics.Paint;
import com.moe.neko.Anime;

public class Fade implements AnimatableDrawable.Callback ,Anime{
   private Paint paint;
   private long duration;
    public Fade(long duration){
        this.duration=duration;
        paint=new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    public String getId() {
        return "Fade:"+duration;
    }

    
    @Override
    public void draw(Bitmap bitmap, Rect bounds, Canvas canvas, float fraction) {
        paint.setAlpha((int)(fraction*255));
        canvas.drawBitmap(bitmap,0,0,paint);
    }

    
    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public TimeInterpolator getInterpolator() {
        return new LinearInterpolator();
    }

    @Override
    public int getRepeatMode() {
        return ValueAnimator.RESTART;
    }

    
    
    
}
