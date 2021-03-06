package com.moe.neko;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class  Resource<T> {
    String key;
    T image;
    int count=0;
    private Lock lock=new ReentrantLock();
    private OnResourceListener listener;
    protected Resource(String key, T bitmap)
    {
        this.key = key;
        this.image = bitmap;
    }

    @Override
    public String toString()
    {
        StringBuilder sb=new StringBuilder();
        sb.append("count:").append(count);
        return sb.toString();
    }

    public void acquire()
    {//存在多次调用bug
        lock.lock();
        try
        {
            if(listener==null)
                throw new IllegalStateException("已经销毁");
            count++;
        }
        finally
        {
            lock.unlock();
        }
    }
    public void release()
    {
        lock.lock();
        try
        {
            if (--count <= 0&&listener!=null)
                listener.onResourceRelease(this);
        }
        finally
        {lock.unlock();}
    }
    public void setOnResourceListener(OnResourceListener<T> l)
    {
        listener = l;
    }
    public interface OnResourceListener<T>
    {
        void onResourceRelease(Resource<T> res);
	}
    
    
}
