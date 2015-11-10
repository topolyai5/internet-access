package com.topolyai.internet.access;

import com.topolyai.vlogger.Logger;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

public class RequestServiceExecutorContext {

    private Queue<ExecutionProperties> queue = new ArrayDeque<>();

    private static final Logger LOGGER = Logger.get(RequestServiceExecutorContext.class);

    private static RequestServiceExecutorContext executorContext;

    public static RequestServiceExecutorContext get() {
        if (executorContext == null) {
            synchronized (LOGGER) {
                if (executorContext == null) {
                    executorContext = new RequestServiceExecutorContext();
                }
            }
        }
        return executorContext;
    }

    public void add(RequestService requestService, RequestParams requestParams) {
        boolean found = false;
        Iterator<ExecutionProperties> iterator = queue.iterator();
        while (iterator.hasNext() && !found){
            ExecutionProperties next = iterator.next();
            if (next.requestParams.getUrl().equals(requestParams.getUrl())) {
                found = true;
            }
        }
        if (!found) {
            queue.add(new ExecutionProperties(requestService, requestParams));
        }
    }

    public void process() {
        while (!queue.isEmpty()) {
            ExecutionProperties poll = queue.poll();
            LOGGER.i("Resend data: %s", poll.requestParams.getUrl());
            poll.requestParams.setAsync(true);
            poll.requestParams.setOnNewThread(true);
            poll.requestService.get(poll.requestParams);
        }
    }

    private class ExecutionProperties {
        private RequestService requestService;
        private RequestParams requestParams;

        public ExecutionProperties(RequestService requestService, RequestParams requestParams) {
            this.requestService = requestService;
            this.requestParams = requestParams;
        }
    }
}
