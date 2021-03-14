package com.moe.neko.target;
import android.view.View;
import android.graphics.drawable.Drawable;
import com.moe.neko.Target;
import com.moe.neko.Request;
import com.moe.neko.SizeReady;
import android.graphics.drawable.Animatable;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.View.OnLayoutChangeListener;

public class  ViewTarget<V extends View> implements Target {
    private V view;
    private SizeReady size;
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
        //reSize(s);
        getView().setBackground(s);
        if(s instanceof Animatable)
            ((Animatable)s).start();
    }
   /* protected void reSize(Drawable s){
        int width=getView().getRight()-getView().getLeft();
        if(width>0&&height>0)
        {

            ViewGroup.LayoutParams params=getView().getLayoutParams();
            if(params.width==-1)
                params.width=width;
            else
                width=params.width;
            params.height=(int)Math.ceil(width/(double)s.getIntrinsicWidth()*s.getIntrinsicHeight());
            getView().setLayoutParams(params);

        }
    }*/
    @Override
    public void onLoadCleared(Drawable c) {
        getView().setBackground(c);
    }

    @Override
    public void removeCallback(SizeReady callback) {
        size=null;
    }





    
    
    @Override
    public void getSize(final SizeReady callback) {
        size=callback;
        if(getView().getWidth()>0){
            int w=getView().getWidth();
            int h=getView().getHeight();

            
            if(size!=null)
                size.onSizeReady(w,h);
        }else
           getView().post(new Runnable(){

                @Override
                public void run() {
                    int w=getView().getWidth();
                    int h=getView().getHeight();
                    
                    
                    if(size!=null)
                    size.onSizeReady(w,h);
                }
            });
    }

    
    
}
