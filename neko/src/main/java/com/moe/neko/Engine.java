package com.moe.neko;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.SynchronousQueue;
import java.util.List;
import com.moe.neko.requesthandler.RequestHandler;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.io.File;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.widget.ImageView.ScaleType;
import com.moe.neko.transform.ScaleTypeTransform;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Paint;
import java.util.concurrent.LinkedBlockingDeque;

public class Engine {
    ThreadPoolExecutor tpe;
    private List<RequestHandler> requestHandlers;
    public Engine(List<RequestHandler> requestHandlers) {
        tpe = new ThreadPoolExecutor(12, 24, 50, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        this.requestHandlers = requestHandlers;
    }
    public EngineJob load(RequestOptions.Data request, int w, int h,RequestManager rm, RequestOptions options, LoadCallback callback) {
        EngineJob job=new EngineJob(request, w, h,rm, options, callback);
        tpe.execute(job);
        return job;
    }

    class EngineJob implements Runnable,RequestHandler.Callback {
        RequestOptions.Data request;
        int w,h;
        RequestOptions.RequestBitmapOptions options;
        LoadCallback callback;
        RequestManager rm;
        EngineJob(RequestOptions.Data request, int w, int h,RequestManager rm, RequestOptions options, LoadCallback callback) {
            this.request = request;
            this.w = w;
            this.h = h;
            this.options = (RequestOptions.RequestBitmapOptions) options;
            this.callback = callback;
            this.rm=rm;
        }
        public void cancel(){
            //取消加载
            callback=null;
        }
        @Override
        public void onError(Exception e) {
            if(callback!=null)
            callback.notifyFailed();
        }

        @Override
        public void onResult(File file) {
            if(callback==null)return;
            //解码和处理图片
            BitmapFactory.Options opt=new BitmapFactory.Options();
            opt.inJustDecodeBounds=true;
            BitmapFactory.decodeFile(file.getAbsolutePath(),opt);
            opt.inSampleSize=calculateInSampleSize(opt,w,h);
            BitmapFactory.decodeFile(file.getAbsolutePath(),opt);
            opt.inJustDecodeBounds=false;
            opt.inBitmap=BitmapPool.getDefault().get(opt.outWidth,opt.outHeight,opt.outConfig);
            Bitmap bitmap=BitmapFactory.decodeFile(file.getAbsolutePath(),opt);
            if(bitmap==null){
                onError(new RuntimeException("image decode error"));
                return;
            }
            //变换图片
            if(options.circleCrop){
                bitmap=circleCrop(BitmapPool.getDefault(),bitmap,w,h);
            }else
            if(options.type!=null)
                bitmap=new ScaleTypeTransform(options.type).transform(BitmapPool.getDefault(),bitmap,w,h);
            if(options.trans!=null){
                for(Transform tran:options.trans){
                    bitmap=tran.transform(BitmapPool.getDefault(),bitmap,w,h);
                    if(bitmap==null)
                        throw new RuntimeException("transform is get null result");
                }
            }
            Resource<Image> res=new Resource<Image>(options.getKey(),Image.parse(bitmap));
            rm.getCachePool().putCache(res);
            if(callback!=null){
            callback.onResourceReady(res);
            }else{
                res.release();
            }
        }
        private Bitmap circleCrop(BitmapPool pool,Bitmap bitmap,int w,int h){
            float size=Math.min(w,h)/2f;
            float scale=Math.max(size/(float)bitmap.getWidth(),size/(float)bitmap.getHeight())*2;
            float transX=(bitmap.getWidth()*scale-w)/2;
            float transY=(bitmap.getHeight()*scale-h)/2;
            Bitmap target=pool.get(w,h,Bitmap.Config.ARGB_8888);
            Canvas canvas=new Canvas(target);
            Paint paint=new Paint();
            canvas.drawCircle(canvas.getWidth()/2f,canvas.getHeight()/2f,size,paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.scale(scale,scale);
            canvas.translate(-transX/scale,-transY/scale);
            canvas.drawBitmap(bitmap,0,0,paint);
            pool.recycle(bitmap);
            return target;
        }
        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
        {
            final int width = options.outWidth;
            final int height = options.outHeight;
            int inSampleSize = 1;
            if ((reqHeight == 0 && reqWidth == 0)||reqHeight<=0||reqWidth<=0)
            {
                return inSampleSize;
            }
            else
            if (reqHeight == 0)
            {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
            else if (reqWidth == 0)
            {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            }
            else
            if (height > reqHeight || width > reqWidth)
            {
                //计算图片高度和我们需要高度的最接近比例值
                final int heightRatio = Math.round((float) height / (float) reqHeight);
                //宽度比例值
                final int widthRatio = Math.round((float) width / (float) reqWidth);
                //取比例值中的较大值作为inSampleSize
                inSampleSize = Math.min(heightRatio,widthRatio);
            }

            return inSampleSize;
        }
        
        @Override
        public void run() {
            //判断本地缓存
            File cacheFile=new File(rm.getCache(),request.getKey());
            if(cacheFile.exists()){
                onResult(cacheFile);
                return;
            }
            //加载远程数据
            boolean find=requestHandlers.stream().anyMatch(new Predicate<RequestHandler>(){

                    @Override
                    public boolean test(RequestHandler p1) {
                        boolean flag=p1.handle(request);
                        if(flag){
                            p1.dispatch(request,rm,EngineJob.this);
                        }
                        return flag;
                    }


                });
           if(!find)
               onError(new IllegalAccessException("未找到可以处理（"+request.toString()+"）的Handler"));
           
        }


    }
}
