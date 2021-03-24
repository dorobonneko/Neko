package com.moe.neko.requesthandler;
import java.io.InputStream;
import com.moe.neko.RequestOptions;
import java.io.File;
import com.moe.neko.Request;
import com.moe.neko.RequestManager;
import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class  RequestHandler <T extends RequestOptions.Data> {
    
   public abstract void dispatch(T data,RequestManager options,Callback callback);
   public void savedata(InputStream input,File cache) throws IOException{
        RandomAccessFile file=new RandomAccessFile(cache, "rw");
        byte[] buff=new byte[128*1024];
        int len=-1;
        while((len=input.read(buff))!=-1){
            file.write(buff,0,len);
        }
    };
   public abstract boolean handle(RequestOptions.Data data);
    public static interface Callback{
        void onResult(File file);
        void onError(Exception e);
    }
}
