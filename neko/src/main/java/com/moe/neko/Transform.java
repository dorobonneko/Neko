package com.moe.neko;
import android.graphics.Bitmap;

public interface Transform<T> {
    
    Bitmap transform(BitmapPool pool,Bitmap bitmap,int outWidth,int outHeight);
    String getId();
}
