package com.moe.neko.target;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageViewTarget extends ViewTarget<ImageView>{
    
    public ImageViewTarget(ImageView view){
        super(view);
    }

    @Override
    public void onLoadSuccess(Drawable s) {
        //reSize(s);
        getView().setImageDrawable(s);
        if(s instanceof Animatable)
            ((Animatable)s).start();
    }

    @Override
    public void onLoadStart(Drawable d) {
        getView().setImageDrawable(d);
    }

    @Override
    public void onLoadCleared(Drawable c) {
        getView().setImageDrawable(c);
    }

    @Override
    public void onLoadFailed(Drawable e) {
        getView().setImageDrawable(e);
    }
    
}
