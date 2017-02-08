package com.cargps.android.net;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class HttpExecute implements Runnable {
    private ExecutorService executorService = null;
    private final LinkedBlockingQueue<HttpRequest<?>> mRequestQueue = new LinkedBlockingQueue<HttpRequest<?>>(3);
    public static HttpExecute httpExecute;
    private boolean isRun = false;
    private Thread thread;

    private HttpExecute() {
        executorService = Executors.newFixedThreadPool(10);
        isRun = true;
        thread = new Thread(this);
        thread.start();
    }

    public static HttpExecute getInstance() {
        return httpExecute == null ? httpExecute = new HttpExecute() : httpExecute;
    }

    public void addRequest(HttpRequest<?> httpRequest) {
        try {
            mRequestQueue.put(httpRequest);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (!isRun) {
            isRun = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    public void cancleThreadPopl() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    public void cancel() {
        isRun = false;
        thread = null;
        mRequestQueue.clear();
        cancleThreadPopl();
    }

    private void executeRequest() {
        while (isRun) {
            if (!mRequestQueue.isEmpty()) {
                try {
                    final HttpRequest<?> httpRequest = mRequestQueue.take();
                    if (null != httpRequest) {
                        executorService.execute(new Runnable() {

                            @Override
                            public void run() {
                                httpRequest.HttpHead();
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        }
    }

    @Override
    public void run() {
        executeRequest();
    }

}
