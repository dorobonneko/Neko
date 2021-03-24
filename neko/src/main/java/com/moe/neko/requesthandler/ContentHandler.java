package com.moe.neko.requesthandler;
import android.net.Uri;
import com.moe.neko.RequestManager;
import com.moe.neko.RequestOptions;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;

public class ContentHandler extends RequestHandler<RequestOptions.UrlData> {
    
    @Override
    public void dispatch(RequestOptions.UrlData data, RequestManager options, RequestHandler.Callback callback) {
        Uri uri=data.getUrl();
        String url=uri.toString();
        if(url.startsWith("/"))
            uri=Uri.parse("file://"+url);
        try {
            InputStream input=options.getContext().getContentResolver().openInputStream(uri);
            String filekey=data.getKey();
            File file=new File(options.getCache(),filekey);
            savedata(input,file);
            callback.onResult(file);
        } catch (Exception e) {
            callback.onError(e);
        }

    }

  

    @Override
    public boolean handle(RequestOptions.Data data) {
        if(data instanceof RequestOptions.UrlData){
            RequestOptions.UrlData url=(RequestOptions.UrlData) data;
            Uri uri=url.getUrl();
            String scheme=uri.getScheme();
            if(scheme!=null)
            switch(scheme){
                case "content":
                case "file":
                    return true;
            }
            else
                if(uri.toString().startsWith("/"))
                    return true;
        }
        return false;
    }

    
    
    
}
