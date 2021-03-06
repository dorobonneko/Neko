package com.moe.neko.transform;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import com.moe.neko.BitmapPool;
import com.moe.neko.Transform;

class PolygonTransform implements Transform {
    private int polygon;
    private float degress;
    public PolygonTransform(int polygonSize,float degress){
        polygon=Math.min(Math.max(3,polygonSize),360);
        this.degress=degress;
    }
    
    @Override
    public Bitmap transform(BitmapPool mBitmapPool, Bitmap bitmap, int w, int h) {
        double degress=360d/polygon;//*Math.PI/180;
        int size=Math.min(bitmap.getWidth(),bitmap.getHeight());
        if(w>0)
            size=Math.min(size,w);
        if(h>0)
            size=Math.min(size,h);
        float scale=Math.min(size/(float)bitmap.getWidth(),size/(float)bitmap.getHeight());

        float radius=size/2f;
        float minx=radius;
        Path lines=new Path();
        for(int i=0;i<polygon;i++){
            double value=degress*i+this.degress;
            float x=radius+(float)(radius*Math.cos(Math.toRadians(value)));
            float y=radius+(float)(radius*Math.sin(Math.toRadians(value)));
            minx=Math.min(x,minx);
            if(i==0)
                lines.moveTo(x,y);
            else
                lines.lineTo(x,y);
        }
        lines.close();
        Bitmap buff=mBitmapPool.get(size,size,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(buff);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG|Paint.FILTER_BITMAP_FLAG));
        canvas.rotate(this.degress,radius,radius);
        canvas.translate(-minx/2,0);
        canvas.clipPath(lines);
        canvas.rotate(-this.degress,radius,radius);
        //canvas.drawColor(0xffffffff);
        Matrix m=new Matrix();
       // m.setScale(scale,scale);
        m.postTranslate(-(bitmap.getWidth()-size)/2f,-(bitmap.getHeight()-size)/2f);
        //canvas.drawBitmap(bitmap,0,0,null);
        canvas.drawBitmap(bitmap,m,null);
        mBitmapPool.recycle(bitmap);
		return buff;
    }

    @Override
    public String getId() {
        return "polygon:"+polygon+"#degress:"+degress;
    }


    
    
    
}
