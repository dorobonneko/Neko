package com.moe.neko;
import java.util.Set;
import java.util.Objects;
import java.util.Collections;
import java.util.WeakHashMap;
import java.io.File;
import android.os.Handler;
import android.os.Looper;

public class RequestManager {
    Handler mHandler=new Handler(Looper.getMainLooper());
    RequestTracker requestTracker=new RequestTracker();
    CachePool mCache=new CachePool();
    Set<Target> targetTracker=Collections.newSetFromMap(new WeakHashMap<Target,Boolean>());
    public void clear(Target<?> request){
        untrackOrDelegate(request);
    }
    private void untrackOrDelegate(Target<?> target) {
        boolean isOwnedByUs = untrack(target);
        if (!isOwnedByUs && !targetTracker.remove(target) && target.getRequest() != null) {
            Request request = target.getRequest();
            target.setRequest(null);
            request.clear();
        }
    }
    private boolean untrack(Target<?> t){
        return requestTracker.untrack(t.getRequest());
    }
    public void track(Request request,Target t){
        targetTracker.add(t);
        requestTracker.runRequest(request);
        
    }
    
    private Builder mBuilder;
    private RequestManager(Builder builder){
        mBuilder=builder;
    }
    public Builder builder(){
        return mBuilder;
    }
    Engine getEngine(){
        return mBuilder.mEngine;
    }
    Handler getHandler(){
        return mHandler;
    }
    CachePool getCachePool(){
        return mCache;
    }
    public RequestOptions load(String url){
        return new RequestOptions.RequestBitmapOptions(this,new RequestOptions.UrlData(url));
    }
    public File getCache(){
        
        File path=new File(mBuilder.cachePath);
        if(path.isFile())
            path.delete();
        if(!path.exists())
            path.mkdirs();
        return path;
    }
    public static class Builder{
        private String cachePath="/sdcard/NekoCache";
        private Engine mEngine;
        public Builder setCachePath(String path){
            Objects.requireNonNull(path);
            cachePath=path;
            return this;
        }
        protected RequestManager build(Engine engine){
            mEngine=engine;
            return new RequestManager(this);
        }
    }
}
