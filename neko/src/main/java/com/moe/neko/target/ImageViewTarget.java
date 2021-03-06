package com.moe.neko.target;
import android.widget.ImageView;
import android.graphics.drawable.Drawable;

public class ImageViewTarget extends ViewTarget<ImageView>{
    
    public ImageViewTarget(ImageView view){
        super(view);
    }

    @Override
    public void onLoadSuccess(Drawable s) {
        getView().setImageDrawable(s);
    }
    
}
