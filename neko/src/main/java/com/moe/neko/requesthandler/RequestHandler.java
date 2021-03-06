package com.moe.neko.requesthandler;
import java.io.InputStream;
import com.moe.neko.RequestOptions;
import java.io.File;
import com.moe.neko.Request;
import com.moe.neko.RequestManager;
import java.io.IOException;

public interface RequestHandler {
    
    void dispatch(RequestOptions.Data data,RequestManager options,Callback callback);
    void savedata(InputStream input,File path) throws IOException;
    boolean handle(RequestOptions.Data data);
    public static interface Callback{
        void onResult(File file);
        void onError(Exception e);
    }
}
