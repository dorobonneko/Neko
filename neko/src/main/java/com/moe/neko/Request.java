package com.moe.neko;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;


public class Request implements SizeReady,LoadCallback {
    private RequestOptions.RequestBitmapOptions options;
    private Target target;
    private Status status;
    private Resource<Image> resource;
    private boolean recycle;
    private Engine mEngine;
    private Engine.EngineJob job;
    Request(RequestOptions.RequestBitmapOptions options, Target t) {
        this.options = options;
        this.target = t;
        this.mEngine = options.requestManager.getEngine();
    }

    public boolean isEquivalentTo(Request previous) {
        return options.getKey().equals(previous.options.getKey())&&!previous.recycle;
    }
    public void begin() {
        if (recycle){
            return;
            }
        if (status == Status.RUNNING) {
            throw new IllegalArgumentException("Cannot restart a running request");
        }

        if (status == Status.COMPLETE) {
            //如果当前状态处于已经加载完成，则回调资源已经加载完毕
            onResourceReady(resource);
            return;
        }
        status = Status.WAITING_FOR_SIZE;
        if ((options.w > 0 && options.h > 0) || options.w == -2 || options.h == -2) {
            onSizeReady(options.w, options.h);
        } else {
            target.getSize(this);
        }
        if (status == Status.RUNNING || status == Status.WAITING_FOR_SIZE) {
            target.onLoadStart(options.placeHolder);
        }
    }
    @Override
    public void onSizeReady(int w, int h) {
        if (status != Status.WAITING_FOR_SIZE||isCancelled()) {
            //如果当前状态不处于等待获取View大小，则返回结束
            return;
        }
        status = Status.RUNNING;
        //判断内存缓存
        Resource<Image> res=options.requestManager.getCachePool().getCache(options.getKey());
        if (res != null) {
            onResourceReady(res);
            return;
        }
        //加载图片
        job=mEngine.load(options.data, w, h, options.requestManager, options, this);
    }
    Drawable image=null;
    
    @Override
    public void onResourceReady(final Resource<Image> res) {
        if (isCancelled()) {
            res.release();
            return;
        }
        status = Status.COMPLETE;
        resource = res;
        resource.acquire();
         if(options.anime!=null)
        image=new AnimatableDrawable(resource.image.getBitmap(),options.anime);
        else
            image=new BitmapDrawable(resource.image.getBitmap());
            
        options.requestManager.getHandler().post(new Runnable(){
                public void run() {
                     
                    target.onLoadSuccess(image);
                    
                }});
    }

    @Override
    public void notifyFailed() {
        status = Status.FAILD;
        options.requestManager.getHandler().post(new Runnable(){
                public void run() {
                    target.onLoadFailed(options.error);
                }});
    }
    public boolean isRunning() {
        return status == Status.RUNNING||status==Status.WAITING_FOR_SIZE;
    }
    public void pause() {

    }
    public boolean isComplete() {
        return status == Status.COMPLETE;
    }
    public boolean isCancelled() {
        return status == Status.CANCEL;
    }
    public void clear() {
        if(isCancelled())return;
        if (resource!=null) {
            resource.release();
        }
        if(target!=null)
        target.removeCallback(this);
        status = Status.CANCEL;
        if(job!=null){
        job.cancel();
        job=null;
        }
        if(target!=null)
        target.onLoadCleared(options.placeHolder);
        
    }
    public void recycle() {
        recycle = true;
        target=null;
    }
    enum Status {
        WAITING_FOR_SIZE,RUNNING,COMPLETE,FAILD,CANCEL;
    }
}
