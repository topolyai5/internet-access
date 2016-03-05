package com.topolyai.internet.access;

import com.topolyai.vlogger.Logger;

import java.util.ArrayList;
import java.util.List;

public class InterceptorManager {

    private static final Logger LOGGER = Logger.get(RequestServiceExecutorContext.class);

    private static InterceptorManager interceptorManager;

    private List<Interceptor> interceptors = new ArrayList<>();

    public static InterceptorManager get() {
        if (interceptorManager == null) {
            synchronized (LOGGER) {
                if (interceptorManager == null) {
                    interceptorManager = new InterceptorManager();
                }
            }
        }
        return interceptorManager;
    }

    public void register(Interceptor interceptor) {
        if (!interceptors.contains(interceptor)) {
            interceptors.add(interceptor);
        }
    }

    List<Interceptor> getInterceptors() {
        return interceptors;
    }
}
