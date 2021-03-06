package com.moe.neko;

public interface LoadCallback {
    void onResourceReady(Resource res);
    
    void notifyFailed();
}
