package com.topolyai.internet.access;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.topolyai.vlogger.Logger;

public class RequestService<T> extends AsyncTask<RequestParams, Void, T> {

    protected UrlService urlService = new UrlService();
    private Gson gson = new Gson();
    Logger logger = Logger.get(getClass());

    private RequestListener<T> requestListener = null;

    public RequestService() {
    }

    public RequestService(RequestListener<T> requestListener) {
        this.requestListener = requestListener;
    }

    @Override
    protected void onPreExecute() {
        if (requestListener != null) {
            requestListener.preExecute();
        }
    }

    @Override
    protected void onPostExecute(T result) {
        if (requestListener != null) {
            requestListener.postExecute(result);
        }
    }

    public T get(RequestParams requestParams) {
        T response = null;
        if (requestParams.isOnNewThread()) {
            AsyncTask<RequestParams, Void, T> execute = execute(requestParams);
            if (!requestParams.isAsync()) {
                response = executeInSync(execute);
            }
        } else {
            response = sendRequest(requestParams);
        }
        return response;
    }

    private T executeInSync(AsyncTask<RequestParams, Void, T> execute) {
        T response = null;
        try {
            response = execute.get();
        } catch (InterruptedException e) {
            logger.e(e.getMessage(), e);
        } catch (ExecutionException e) {
            logger.e(e.getMessage(), e);
        }
        return response;
    }

    @SuppressWarnings("unchecked")
    protected T sendRequest(RequestParams requestParams) {
        String response;
        if (requestParams.getRequestType().toLowerCase(Locale.getDefault()).equals("get")) {
            response = urlService.get(requestParams.getUrl(), null);
        } else {
            response = urlService.post(requestParams.getUrl(), requestParams.getNameValuePairs());
        }
        logger.d("requested url: " + requestParams.getUrl() + ",\r\n response:" + response);
        try {
            ResponseStatus<T> ret = (ResponseStatus<T>) gson.fromJson(response.replaceAll("private", "privates"),
                    requestParams.getResponseClass());
            return ret.getResponse();
        } catch (Exception e) {
            logger.e(e.getMessage(), e);
            try {
                return (T) requestParams.getResponseClass().newInstance();
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } finally {
            }
            return null;
        }
    }

    @Override
    protected T doInBackground(RequestParams... params) {
        RequestParams param = params[0];
        return sendRequest(param);
    }
}
