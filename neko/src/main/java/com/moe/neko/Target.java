package com.moe.neko;
import android.graphics.drawable.Drawable;

public interface Target {
    
    void setRequest(Request request);
    Request getRequest();
    void getSize(SizeReady callback);
    void onLoadStart(Drawable d);
    void onLoadFailed(Drawable e);
    void onLoadSuccess(Drawable s);
}
