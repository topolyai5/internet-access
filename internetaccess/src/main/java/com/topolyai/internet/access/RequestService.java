package com.topolyai.internet.access;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class RequestService extends AsyncTask<RequestParams, Void, ResponseStatus> {

    private static final String LOGTAG = RequestService.class.getSimpleName();
    protected UrlService urlService = new UrlService();
    private Gson gson = new Gson();
    private RequestListener requestListener = null;
    private ProgressHandler progressHandler = null;

    public RequestService() {
    }

    public RequestService(RequestListener requestListener) {
        this.requestListener = requestListener;
    }

    public RequestService(RequestListener requestListener, ProgressHandler progressHandler) {
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
    protected void onPostExecute(ResponseStatus result) {
        if (requestListener != null) {
            requestListener.postExecute(result);
        }
    }

    public ResponseStatus get(RequestParams requestParams) throws ExecuteException, ExtractResponseException, CanceledException {
        ResponseStatus response = null;
        if (requestParams.isOnNewThread()) {
            AsyncTask<RequestParams, Void, ResponseStatus> execute = execute(requestParams);
            if (!requestParams.isAsync()) {
                response = executeInSync(execute);
            }
        } else {
            response = sendRequest(requestParams);
        }
        return response;
    }

    private ResponseStatus executeInSync(AsyncTask<RequestParams, Void, ResponseStatus> execute) throws ExecuteException {
        try {
            ResponseStatus response = execute.get();
            return response;
        } catch (InterruptedException | ExecutionException e) {
            throw new ExecuteException(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    protected ResponseStatus sendRequest(RequestParams requestParams) {
        String response;
        switch (requestParams.getRequestMethod()) {
            case GET:
                response = urlService.get(requestParams.getUrl());
                break;
            case POST:
                response = urlService.post(requestParams.getUrl(), requestParams.getNameValuePairs());
                break;
            case BINARY:
                response = urlService.binary(this, requestParams.getUrl(), requestParams.getFilePath(), progressHandler);
                break;
            case OPTION:
                throw new UnsupportedOperationException();
            case DELETE:
                throw new UnsupportedOperationException();
            case PUT:
                throw new UnsupportedOperationException();
            default:
                throw new IllegalArgumentException(String.format("Invalid request type: %s", requestParams.getRequestMethod()));
        }
        Log.d(LOGTAG, "requested url: " + requestParams.getUrl() + ",\r\n response:" + response.replaceAll("private", "privates"));
        try {
            JSONObject jsonObject = new JSONObject(response);
            return ResponseStatus.builder().response(jsonObject.getString("response")).httpStatus(jsonObject.getString("httpStatus")).build();
        } catch (JSONException e) {
            throw new JSONParseException(e);
        }
    }

    @Override
    protected ResponseStatus doInBackground(RequestParams... params) {
        return sendRequest(params[0]);
    }
}
