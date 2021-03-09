package com.moe.neko.example;
 
import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import com.moe.neko.Neko;
import com.moe.neko.transform.RoundTransform;
import android.content.ClipboardManager;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView.ScaleType;
import com.moe.neko.transform.PolygonTransform;
import com.moe.neko.transform.BlurTransform;

public class MainActivity extends Activity { 
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ImageView view;
        setContentView(view=new ImageView(this));
        Neko.with(view).builder().setCachePath(getCacheDir().getAbsolutePath());
        Neko.with(view).load("https://assets.yande.re/data/preview/a0/62/a062371bdebd7626ab52a9f4b135cc4a.jpg").asBitmap().fade(1000).circleCrop().into(view);
        
    }
	
} 
