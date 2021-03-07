package com.moe.neko;
import android.graphics.drawable.Drawable;

public interface Target<T> {
    
    void setRequest(Request request);
    Request getRequest();
    void getSize(SizeReady callback);
    void onLoadStart(Drawable d);
    void onLoadFailed(Drawable e);
    void onLoadSuccess(Drawable s);
    void onLoadCleared(Drawable c);
    void removeCallback(SizeReady callback);
}
