package com.moe.neko;
import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import android.view.View;
import com.moe.neko.requesthandler.RequestHandler;
import java.util.ArrayList;
import java.util.List;
import com.moe.neko.requesthandler.HttpHandler;

public class Neko {
    private static Map<Object,RequestManager> requestManagers=new HashMap<>();
    private static RequestManager.Builder builder;
    private static List<RequestHandler> requestHandler=null;
    private static Engine mEngine=null;
    static{
        requestHandler=new ArrayList<>();
    mEngine=new Engine(requestHandler);
    requestHandler.add(new HttpHandler());
    }
    public static RequestManager with(Object obj){
        RequestManager req=requestManagers.get(obj);
        if(req==null){
            synchronized(obj){
                req=requestManagers.get(obj);
                if(req==null){
                    checkBuilder();
                    req=builder.build(mEngine);
                    }
            }
        }
        return req;
    }
    private static void checkBuilder(){
        if(builder==null){
            builder=new RequestManager.Builder();
        }
    }
    public static RequestManager with(View view){
        return with(view.getContext());
    }
    public static void setDefaultBuilder(RequestManager.Builder builder){
        Neko.builder=builder;
    }
    
}
