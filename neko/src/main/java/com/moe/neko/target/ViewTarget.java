package com.moe.neko.target;
import android.view.View;
import android.graphics.drawable.Drawable;
import com.moe.neko.Target;
import com.moe.neko.Request;
import com.moe.neko.SizeReady;

public class  ViewTarget<V extends View> implements Target {
    private V view;
    public ViewTarget(V v){
        this.view=v;
    }
    @Override
    public void setRequest(Request request) {
        getView().setTag(request);
    }

    @Override
    public Request getRequest() {
        Object obj=getView().getTag();
        if(obj ==null)
            return null;
        if(obj instanceof Request){
            return (Request)obj;
        }
        throw new IllegalArgumentException("tag error");
    }


    public V getView(){
        return view;
    }

    @Override
    public void onLoadStart(Drawable d) {
        getView().setBackground(d);
    }

    @Override
    public void onLoadFailed(Drawable e) {
        getView().setBackground(e);
    }

    @Override
    public void onLoadSuccess(Drawable s) {
        getView().setBackground(s);
    }



    
    
    @Override
    public void getSize(final SizeReady callback) {
        getView().post(new Runnable(){

                @Override
                public void run() {
                    callback.onSizeReady(getView().getWidth(),getView().getHeight());
                }
            });
    }

    
    
}
