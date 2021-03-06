package com.moe.neko;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import com.moe.neko.target.ImageViewTarget;
import java.util.Objects;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.widget.ImageView.ScaleType;
import android.view.View;
import com.moe.neko.target.ViewTarget;
import com.moe.neko.animation.Fade;
import android.content.res.Resources;

public abstract class RequestOptions<T extends RequestOptions> {
    Data data;
    RequestManager requestManager;
    int w=-1,h=-1;//-1自适应，-2原图
    Drawable placeHolder,error;
    String key=null;
    ScaleType type;
    boolean circleCrop;
    Anime anime;
    RequestOptions(RequestManager neko, Data data) {
        this.requestManager = neko;
        this.data = data;
    }

    public abstract RequestBitmapOptions asBitmap();
    public T fade(long duration,long delay){
        anime=new Fade(duration,delay);
        return (T)this;
    }
    public T fade(long duration){
     return fade(duration,0);
    }
    public T circleCrop() {
        circleCrop = true;
        return (T)this;
    }
    public T scaleType(ScaleType type) {
        this.type = type;
        return (T)this;
    }
    public T placeHolder(Drawable placeHolder) {
        this.placeHolder = placeHolder;
        return (T)this;
    }
    public T error(Drawable error) {
        this.error = error;
        return (T)this;
    }
    public T placeHolder(Context c,int placeHolder) {
        this.placeHolder = c.getResources().getDrawable(placeHolder,c.getTheme());
        return (T)this;
    }
    public T error(Context c,int error) {
        this.error = c.getResources().getDrawable(error,c.getTheme());
        return (T)this;
    }
    
    public T placeHolder(int placeHolder) {
        Context c=requestManager.getContext();
        if(c!=null)
            return placeHolder(c,placeHolder);
        this.placeHolder = Resources.getSystem().getDrawable(placeHolder);
        return (T)this;
    }
    public T error(int error) {
        Context c=requestManager.getContext();
        if(c!=null)
            return error(c,error);
        this.error = Resources.getSystem().getDrawable(error);
        return (T)this;
    }
    public T override(int w, int h) {
        this.w = w;
        this.h = h;
        return (T)this;
    }
    public ImageViewTarget into(ImageView view) {
       return (ImageViewTarget)into(new ImageViewTarget(view));
    }
    public ViewTarget<View> into(View view) {
       return (ViewTarget<View>)into(new ViewTarget<View>(view));
    }
    public Target into(Target t) {
        if(data.isNull())return t;
        Objects.requireNonNull(t);
        Request previous=t.getRequest();

        Request request=buildRequest(t);
        //判断前后request是否一致
        if (previous != null) {
            if (request.isEquivalentTo(previous)) {
                request.recycle();
                if(!previous.isRunning()){
                    previous.begin();
                    return t;
                    }
            } 
        }
        requestManager.clear(t);
        t.setRequest(request);
        requestManager.track(request, t);
        return t;
    }
    private Request buildRequest(Target t) {
        return new Request((RequestBitmapOptions)this, t);
    }
    public static class RequestBitmapOptions extends RequestOptions<RequestBitmapOptions> {
        boolean isBitmap;
        Transform[] trans;
        RequestBitmapOptions(RequestManager neko, Data data) {
            super(neko, data);
        }

        String getKey() {
            StringBuilder sb=new StringBuilder();
            sb.append(data.getKey());
            sb.append("bitmap:" + isBitmap);
            sb.append("circleCrop" + circleCrop);
            sb.append("scaleType:" + type);
            if(anime!=null)
            sb.append("anime:"+anime.getId());
            if (trans != null)
                for (Transform t:trans)
                    sb.append(t.getId());
            return Util.upKey(sb.toString());
        }

        //变换图片
        public RequestBitmapOptions transform(Transform... trans) {
            this.trans = trans;
            return this;
        }
        //使用动画
        public RequestBitmapOptions apply() {
            return this;
        }

        /*  @Override
         public RequestBitmapOptions circleCrop() {
         super.circleCrop();
         return this;
         }

         @Override
         public RequestBitmapOptions override(int w, int h) {
         super.override(w, h);
         return this;
         }

         @Override
         public RequestBitmapOptions placeHolder(Drawable placeHolder) {
         super.placeHolder(placeHolder);
         return this;
         }

         @Override
         public RequestBitmapOptions error(Drawable error) {
         super.error(error);
         return this;
         }*/


        @Override
        public RequestOptions.RequestBitmapOptions asBitmap() {
            isBitmap = true;
            return this;
        }


    }
    public static abstract class Data {
        public abstract String getKey();
        public abstract boolean isNull();
    }
    public static class UrlData extends Data {
        private String url;
        UrlData(String url) {
            this.url = url;
        }
        public Uri getUrl() {
            return Uri.parse(url);
        }

        @Override
        public String getKey() {
            return Util.upKey(url);
        }

        @Override
        public String toString() {
            return url;
        }

        @Override
        public boolean isNull() {
            return url==null||"null".equals(url)||"undefined".equals(url);
        }



    }
    public static class ResourceData extends Data {
        private Context context;
        private int id;
        ResourceData(Context context, int id) {
            this.context = context;
            this.id = id;
        }

        @Override
        public String getKey() {
            return Util.upKey("resource:" + id);

        }

        @Override
        public String toString() {
            return "id:"+id;
        }

        @Override
        public boolean isNull() {
            return id<=0;
        }



    }
}
