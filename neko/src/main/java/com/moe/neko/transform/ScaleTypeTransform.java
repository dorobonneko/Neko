package com.moe.neko.transform;
import com.moe.neko.Transform;
import android.graphics.Bitmap;
import com.moe.neko.BitmapPool;
import android.widget.ImageView.ScaleType;
import android.graphics.Canvas;

public class ScaleTypeTransform implements Transform {
    ScaleType type;
    public ScaleTypeTransform(ScaleType type){
        this.type=type;
    }
    @Override
    public Bitmap transform(BitmapPool pool, Bitmap bitmap, int outWidth, int outHeight) {
        int width=bitmap.getWidth(),height=bitmap.getHeight();
        float scale=1;
        float transX=0,transY=0;
        switch(type){
            case CENTER_CROP:
                width=outWidth;
                height=outHeight;
                scale=Math.max(width/(float)bitmap.getWidth(),height/(float)bitmap.getHeight());
                transX=(bitmap.getWidth()*scale-width)/2;
                transY=(bitmap.getHeight()*scale-height)/2;
                break;
            default:
                return bitmap;
        }
        Bitmap target=pool.get(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(target);
        canvas.scale(scale,scale);
        canvas.translate(-transX/scale,-transY/scale);
        canvas.drawBitmap(bitmap,0,0,null);
        pool.recycle(bitmap);
        return target;
    }

    @Override
    public String getId() {
        return null;
    }


    
    
    
}
