package com.moe.neko.transform;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import com.moe.neko.BitmapPool;
import com.moe.neko.Transform;

public class RoundTransform implements Transform {

    private float radius;
    public RoundTransform(int dp){
        radius=Resources.getSystem().getDisplayMetrics().density*dp;
    }
    @Override
    public String getId() {
        return "rount:"+radius;
    }

    @Override
    public Bitmap transform(BitmapPool pool, Bitmap source, int outWidth, int outHeight) {
        if (source == null) return null;

        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rectF, radius, radius, paint);
        return result;

          }


   
    
    
    
}
