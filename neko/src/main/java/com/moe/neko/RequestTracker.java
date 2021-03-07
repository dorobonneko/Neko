package com.moe.neko;
import java.util.Set;
import java.util.Collections;
import java.util.WeakHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class RequestTracker {
    private final Set<Request> requests=Collections.newSetFromMap(new WeakHashMap<Request,Boolean>());
    
    private final List<Request> pendingRequests = new ArrayList<>();
    private boolean isPaused;

    /**
     * Starts tracking the given request.
     */
    public void runRequest(Request request) {
        requests.add(request);
        if (!isPaused) {
            request.begin();
        } else {
            pendingRequests.add(request);
        }
    }

   public boolean untrack(Request r){
       return requests.remove(r)||pendingRequests.remove(r);
   }

    /**
     * Stops tracking the given request, clears, and recycles it, and returns {@code true} if the
     * request was removed or {@code false} if the request was not found.
     */
    public boolean clearRemoveAndRecycle(Request request) {
        if (request == null) {
            return false;
        }
        boolean isOwnedByUs = requests.remove(request);
        // Avoid short circuiting.
        isOwnedByUs = pendingRequests.remove(request) || isOwnedByUs;
        if (isOwnedByUs) {
            request.clear();
            request.recycle();
        }
        return isOwnedByUs;
    }

    /**
     * Returns {@code true} if requests are currently paused, and {@code false} otherwise.
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Stops any in progress requests.
     */
    public void pauseRequests() {
        isPaused = true;
        for (Request request : getSnapshot(requests)) {
            if (request.isRunning()) {
                request.pause();
                pendingRequests.add(request);
            }
        }
    }

    /**
     * Starts any not yet completed or failed requests.
     */
    public void resumeRequests() {
        isPaused = false;
        for (Request request : getSnapshot(requests)) {
            if (!request.isComplete() && !request.isCancelled() && !request.isRunning()) {
                request.begin();
            }
        }
        pendingRequests.clear();
    }

    /**
     * Cancels all requests and clears their resources.
     *
     * <p>After this call requests cannot be restarted.
     */
    public void clearRequests() {
        for (Request request : getSnapshot(requests)) {
            clearRemoveAndRecycle(request);
        }
        pendingRequests.clear();
    }

    /**
     * Restarts failed requests and cancels and restarts in progress requests.
     */
    public void restartRequests() {
        for (Request request : getSnapshot(requests)) {
            if (!request.isComplete() && !request.isCancelled()) {
                // Ensure the request will be restarted in onResume.
                request.pause();
                if (!isPaused) {
                    request.begin();
                } else {
                    pendingRequests.add(request);
                }
            }
        }
    }

    @Override
    public String toString() {
        return super.toString() + "{numRequests=" + requests.size() + ", isPaused=" + isPaused + "}";
    }
    <T> List<T> getSnapshot(Collection<T> other){
        List<T> list=new ArrayList<T>();
        for(T t:other){
            if(t!=null)
                list.add(t);
        }
        return list;
    }
}
