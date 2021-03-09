package com.moe.neko;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.HashMap;
import java.util.function.Consumer;

public class CachePool implements Resource.OnResourceListener{
    private Lock lock=new ReentrantLock();
    private Map<String,Resource<Image>> list=new HashMap<String,Resource<Image>>();
    public void putCache(Resource<Image> res){
        lock.lock();
        list.put(res.key,res);
        res.setOnResourceListener(this);
        lock.unlock();
    }
    public Resource<Image> getCache(final String key){
        Objects.requireNonNull(key);
       return list.get(key);
    }

    @Override
    public void onResourceRelease(Resource<Image> res) {
        Objects.requireNonNull(res);
        lock.lock();
        list.remove(res.key);
        BitmapPool.getDefault().onResourceRelease(res);
        lock.unlock();
    }

    @Override
    public String toString() {
        final StringBuilder sb=new StringBuilder();
        sb.append(list.size());
        sb.append("\r\n");
        list.values().stream().forEach(new Consumer<Resource<Image>>(){

                @Override
                public void accept(Resource<Image> p1) {
                    sb.append(p1.toString());
                    sb.append("\r\n");
                }
            });
        return sb.toString();
    }
    
    
}
