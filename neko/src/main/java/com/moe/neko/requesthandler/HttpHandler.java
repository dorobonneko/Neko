package com.moe.neko.requesthandler;
import com.moe.neko.Request;
import java.io.File;
import java.io.InputStream;
import com.moe.neko.RequestOptions.Data;
import com.moe.neko.RequestOptions;
import android.net.Uri;
import java.net.URL;
import java.io.IOException;
import com.moe.neko.RequestManager;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;

public class HttpHandler extends RequestHandler {

    @Override
    public void dispatch(RequestOptions.Data data, RequestManager options,Callback callback) {
        RequestOptions.UrlData url=(RequestOptions.UrlData) data;
        Uri uri=url.getUrl();
        try {
            InputStream input=new URL(uri.toString()).openStream();
            String filekey=data.getKey();
            File file=new File(options.getCache(),filekey);
            savedata(input,file);
            try {
                input.close();
            } catch (IOException e) {}
            callback.onResult(file);
        } catch (IOException e) {
            callback.onError(e);
        }
    }
    


    @Override
    public boolean handle(RequestOptions.Data data) {
        if(data instanceof RequestOptions.UrlData){
            RequestOptions.UrlData urldata=(RequestOptions.UrlData) data;
            Uri uri=urldata.getUrl();
            String scheme=uri.getScheme();
            if(scheme!=null){
                switch(scheme){
                    case "http":
                    case "https":
                        return true;
                }
            }
        }
        return false;
    }


    
    
    
}
