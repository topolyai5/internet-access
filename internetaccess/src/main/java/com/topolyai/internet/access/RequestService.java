package com.topolyai.internet.access;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.util.concurrent.ExecutionException;

public class RequestService<T> extends AsyncTask<RequestParams, Void, T> {

    public static final String GET = "get";
    public static final String POST = "post";
    public static final String BINARY = "binary";

    private static final String LOGTAG = RequestService.class.getSimpleName();
    protected UrlService urlService = new UrlService();
    private Gson gson = new Gson();
    private RequestListener<T> requestListener = null;
    private ProgressHandler progressHandler = null;

    public RequestService() {
    }

    public RequestService(RequestListener<T> requestListener, ProgressHandler progressHandler) {
        this.requestListener = requestListener;
        this.progressHandler = progressHandler;
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

    public T get(RequestParams requestParams) throws ExecuteException, ExtractResponseException, CanceledException {
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

    private T executeInSync(AsyncTask<RequestParams, Void, T> execute) throws ExecuteException {
        try {
            T response = execute.get();
            return response;
        } catch (InterruptedException | ExecutionException e) {
            throw new ExecuteException(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    protected T sendRequest(RequestParams requestParams) throws ExecuteException, ExtractResponseException, CanceledException {
        String response;
        switch (requestParams.getRequestType()) {
            case GET:
                response = urlService.get(requestParams.getUrl());
                break;
            case POST:
                response = urlService.post(requestParams.getUrl(), requestParams.getNameValuePairs());
                break;
            case BINARY:
                response = urlService.binary(this, requestParams.getUrl(), requestParams.getFilePath(), progressHandler);
                break;
            default:
                throw new IllegalArgumentException(String.format("Invalid request type: %s", requestParams.getRequestType()));
        }
        Log.d(LOGTAG, "requested url: " + requestParams.getUrl() + ",\r\n response:" + response.replaceAll("private", "privates"));
        ResponseStatus<T> ret;
        try {
            ret = (ResponseStatus<T>) gson.fromJson(response, requestParams.getResponseClass());
        } catch (Exception e) {
            ret = (ResponseStatus<T>) gson.fromJson(response, StringResponseStatus.class);
        }
        return ret.getResponse();
    }

    @Override
    protected T doInBackground(RequestParams... params) {
        RequestParams param = params[0];
        try {
            return sendRequest(param);
        } catch (ExtractResponseException | ExecuteException e) {
            Log.e(LOGTAG, e.getMessage(), e);
            throw new RuntimeException("Cannot be execute this.", e);
        } catch (CanceledException e) {
            Log.e(LOGTAG, e.getMessage(), e);
            throw new RuntimeException("Canncelled.", e);
        }
    }
}
