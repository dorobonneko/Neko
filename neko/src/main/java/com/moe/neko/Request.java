package com.moe.neko;
import android.graphics.drawable.BitmapDrawable;

public class Request implements SizeReady,LoadCallback{
    private RequestOptions.RequestBitmapOptions options;
    private Target target;
    private Status status;
    private Resource resource;
    private boolean cancel,recycle;
    private Engine mEngine;
    Request(RequestOptions.RequestBitmapOptions options,Target t){
        this.options=options;
        this.target=t;
        this.mEngine=options.requestManager.getEngine();
        }

    public boolean isEquivalentTo(Request previous) {
        return false;
    }
    public void begin(){
        if(recycle)
            throw new IllegalStateException("it is recycle ,can't run");
        if (status == Status.RUNNING) {
            throw new IllegalArgumentException("Cannot restart a running request");
        }

       if (status == Status.COMPLETE) {
            //如果当前状态处于已经加载完成，则回调资源已经加载完毕
            onResourceReady(resource);
            return;
        }

        status=Status.WAITING_FOR_SIZE;
        if((options.w>0&&options.h>0)||options.w==-2||options.h==-2){
            onSizeReady(options.w,options.h);
        }else{
            target.getSize(this);
        }
        if(status==Status.RUNNING||status==Status.WAITING_FOR_SIZE){
            target.onLoadStart(options.placeHolder);
        }
    }
    @Override
    public void onSizeReady(int w,int h){
        if (status != Status.WAITING_FOR_SIZE) {
            //如果当前状态不处于等待获取View大小，则返回结束
            return;
        }
        status=Status.RUNNING;
        //判断内存缓存
        Resource<Image> res=options.requestManager.getCachePool().getCache(options.getKey());
        if(res!=null){
            onResourceReady(res);
            return;
        }
        //加载图片
        mEngine.load(options.data,w,h,options.requestManager,options,this);
    }

    @Override
    public void onResourceReady(final Resource<Image> res) {
        status=Status.COMPLETE;
        resource=res;
        options.requestManager.getHandler().post(new Runnable(){
            public void run(){
        res.acquire();
        target.onLoadSuccess(new BitmapDrawable(res.image.getBitmap()));
        }});
    }

    @Override
    public void notifyFailed() {
        status=Status.FAILD;
        
        target.onLoadFailed(options.error);
    }
    public boolean isRunning(){
        return status==Status.RUNNING;
    }
    public void pause(){
        
    }
    public boolean isComplete(){
        return status==Status.COMPLETE;
    }
    public boolean isCancelled(){
        return cancel;
    }
    public void clear(){
        cancel=true;
        if(status==Status.COMPLETE){
            resource.release();
        }
    }
    public void recycle(){
        recycle=true;
    }
    enum Status{
        WAITING_FOR_SIZE,RUNNING,COMPLETE,FAILD;
    }
}
