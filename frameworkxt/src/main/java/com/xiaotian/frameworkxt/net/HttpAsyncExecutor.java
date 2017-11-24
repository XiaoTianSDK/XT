package com.xiaotian.frameworkxt.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name HttpRequestTask
 * @description
 * @date 2017/5/3
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2017 小天天 Studio, All Rights Reserved.
 */
public class HttpAsyncExecutor {
    public static final String TAG = "HttpAsyncExecutor";
    private static final int KEEP_ALIVE_SECONDS = 30;
    private static int threadPoolSize = 10;
    private static int threadPriority = Thread.NORM_PRIORITY;
    private volatile static HttpAsyncExecutor instance;
    private ExecutorService taskExecutor;
    private final Map<Context, List<RequestHandle>> requestMap;

    /** Returns singleton class instance */
    public static HttpAsyncExecutor getInstance() {
        if (instance == null) {
            synchronized (HttpAsyncExecutor.class) {
                if (instance == null) {
                    instance = new HttpAsyncExecutor();
                }
            }
        }
        return instance;
    }

    public HttpAsyncExecutor() {
        taskExecutor = createTaskExecutor();
        requestMap = Collections.synchronizedMap(new WeakHashMap<Context, List<RequestHandle>>());
    }

    public <Params, Progress, Result> RequestHandle execute(Context context, RequestTask<Params, Progress, Result> request, Params... params) {
        initExecutorsIfNeed();
        if (request == null) return null;
        request.setContext(context);
        request.setExecutor(this);
        request.setParams(params);
        taskExecutor.submit(request);
        // Return request handle to operator cancel if need
        RequestHandle requestHandle = new RequestHandle(request);
        if (context != null) {
            List<RequestHandle> requestList;
            // Add request to request map
            synchronized (requestMap) {
                requestList = requestMap.get(context);
                if (requestList == null) {
                    requestList = Collections.synchronizedList(new LinkedList<RequestHandle>());
                    requestMap.put(context, requestList);
                }
            }
            requestList.add(requestHandle);
            Iterator<RequestHandle> iterator = requestList.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().shouldBeGarbageCollected()) {
                    iterator.remove();
                }
            }
        }
        return requestHandle;
    }

    // Static Method
    public void fireCallback(Runnable task) {
        initExecutorsIfNeed();
        taskExecutor.execute(task);
    }

    private void initExecutorsIfNeed() {
        if (((ExecutorService) taskExecutor).isShutdown()) {
            taskExecutor = createTaskExecutor();
        }
    }

    private ExecutorService createTaskExecutor() {
        return new ThreadPoolExecutor(threadPoolSize, Integer.MAX_VALUE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), createThreadFactory(threadPriority, "xt-async-pool-"));
    }

    private ThreadFactory createThreadFactory(int threadPriority, String threadNamePrefix) {
        return new DefaultThreadFactory(threadPriority, threadNamePrefix);
    }

    // Static Class
    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final int threadPriority;

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            group = Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) t.setDaemon(false); //标识为用户线程
            t.setPriority(threadPriority);
            return t;
        }
    }

    /**
     * Stops engine
     */
    public void stop() {
        taskExecutor.shutdownNow();
    }

    public static abstract class RequestTask<Params, Progress, Result> implements Runnable {
        protected static final int START_MESSAGE = 1;
        protected static final int FINISH_MESSAGE = 2;
        protected static final int CANCEL_MESSAGE = 3;
        protected static final int PROGRESS_MESSAGE = 4;
        private HttpAsyncExecutor executor;
        private Context context;
        //
        private final AtomicBoolean isCancelled = new AtomicBoolean();
        private volatile boolean isFinished;
        private boolean isRequestPreProcessed;
        private Handler handler;
        private WeakReference<Object> TAG = new WeakReference<Object>(null);
        private Params[] params;
        private Result result;

        public RequestTask() {
            handler = new MessageHandler(this, Looper.getMainLooper());
        }

        // Extra Method
        protected abstract Result doInBackground(Params... params);

        protected void onPreExecute() {}

        protected void onPostExecute(Result result) {}

        protected void onProgressUpdate(Progress... values) {}

        protected void onCancelled(Result result) {
            onCancelled();
        }

        protected void onCancelled() {}

        //
        @Override
        public void run() {
            if (isCancelled()) return;
            // Carry out pre-processing for this request only once.
            if (!isRequestPreProcessed) {
                isRequestPreProcessed = true;
                sendMessage(Message.obtain(handler, START_MESSAGE, null));
            }
            if (isCancelled()) return;
            //makeRequestWithRetries();
            result = doInBackground(params);
            if (isCancelled()) return;
            // Carry out post-processing for this request.
            sendMessage(Message.obtain(handler, FINISH_MESSAGE, null));
            isFinished = true;
        }

        public void sendProgressUpdate(Progress... progress) {
            sendMessage(Message.obtain(handler, PROGRESS_MESSAGE, progress));
        }

        public boolean isCancelled() {
            boolean cancelled = isCancelled.get();
            if (cancelled) {
                sendMessage(Message.obtain(handler, CANCEL_MESSAGE, null));
            }
            return cancelled;
        }

        public boolean isDone() {
            return isCancelled() || isFinished;
        }

        public boolean cancel() {
            isCancelled.set(true);
            sendMessage(Message.obtain(handler, CANCEL_MESSAGE, null));
            return isCancelled();
        }

        public RequestTask setRequestTag(Object TAG) {
            this.TAG = new WeakReference<Object>(TAG);
            return this;
        }

        public Object getTag() {
            return this.TAG.get();
        }

        protected void sendMessage(Message msg) {
            if (!Thread.currentThread().isInterrupted()) { // do not send messages if request has been cancelled
                if (handler == null) {
                    throw new AssertionError("handler should not be null!");
                }
                handler.sendMessage(msg);
            }
        }

        // Methods which emulate android's Handler and Message methods
        protected void handleMessage(Message message) {
            switch (message.what) {
                case START_MESSAGE:
                    onPreExecute();
                    break;
                case FINISH_MESSAGE:
                    onPostExecute(result);
                    break;
                case PROGRESS_MESSAGE:
                    onProgressUpdate((Progress[]) message.obj);
                case CANCEL_MESSAGE:
                    onCancelled(result);
                    break;
            }
        }

        private static class MessageHandler extends Handler {
            private final RequestTask mResponder;

            MessageHandler(RequestTask mResponder, Looper looper) {
                super(looper);
                this.mResponder = mResponder;
            }

            @Override
            public void handleMessage(Message msg) {
                mResponder.handleMessage(msg);
            }
        }

        public void setExecutor(HttpAsyncExecutor executor) {
            this.executor = executor;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setParams(Params[] params) {
            this.params = params;
        }
    }

    public static class RequestHandle {
        private final WeakReference<RequestTask> request;

        public RequestHandle(RequestTask request) {
            this.request = new WeakReference<RequestTask>(request);
        }

        public boolean cancel() {
            final RequestTask _request = request.get();
            if (_request != null) {
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            _request.cancel();
                        }
                    }).start();
                    // Cannot reliably tell if the request got immediately canceled at this point we'll assume it got cancelled
                    return true;
                } else {
                    return _request.cancel();
                }
            }
            return false;
        }

        public boolean isFinished() {
            RequestTask _request = request.get();
            return _request == null || _request.isDone();
        }

        public boolean isCancelled() {
            RequestTask _request = request.get();
            return _request == null || _request.isCancelled();
        }

        public boolean shouldBeGarbageCollected() {
            boolean should = isCancelled() || isFinished();
            if (should) request.clear();
            return should;
        }

        /**
         * Will return TAG of underlying AsyncHttpRequest if it's not already GCed
         * @return Object TAG, can be null
         */
        public Object getTag() {
            RequestTask _request = request.get();
            return _request == null ? null : _request.getTag();
        }

        /**
         * Will set Object as TAG to underlying AsyncHttpRequest
         * @param tag Object used as TAG to underlying AsyncHttpRequest
         * @return this RequestHandle to allow fluid syntax
         */
        public RequestHandle setTag(Object tag) {
            RequestTask _request = request.get();
            if (_request != null) _request.setRequestTag(tag);
            return this;
        }
    }
}
