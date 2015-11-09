package com.topolyai.internet.access;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.topolyai.vlogger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class RequestService extends AsyncTask<RequestParams, Void, ResponseStatus> {

    private static final Logger LOGGER = Logger.get(RequestService.class);
    protected UrlService urlService = new UrlService();
    private RequestListener requestListener = null;
    private ProgressHandler progressHandler = null;

    private RequestServiceExecutorContext executorContext = RequestServiceExecutorContext.get();

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

    public ResponseStatus get(RequestParams requestParams) {
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

    public void async(RequestParams requestParams) {
        if (requestParams.isOnNewThread()) {
            execute(requestParams);
        } else {
            sendRequest(requestParams);
        }
    }

    private ResponseStatus executeInSync(AsyncTask<RequestParams, Void, ResponseStatus> execute) throws ExecuteException {
        try {
            ResponseStatus response = execute.get();
            return response;
        } catch (InterruptedException | ExecutionException e) {
            throw new ExecuteException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected ResponseStatus sendRequest(RequestParams requestParams) {
        String response;
        try {
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
                case UPLOAD:
                    response = urlService.uploadFile(requestParams);
                case OPTION:
                    throw new UnsupportedOperationException();
                case DELETE:
                    throw new UnsupportedOperationException();
                case PUT:
                    throw new UnsupportedOperationException();
                default:
                    throw new IllegalArgumentException(String.format("Invalid request type: %s", requestParams.getRequestMethod()));
            }
        } catch (ExecuteException e) {
            executorContext.add(new RequestService(requestListener), requestParams);
            return ResponseStatus.builder().httpStatus("503").response("{'code':'NO_INTERNET_CONNECTION'}").build();
        }
        LOGGER.d("requested url: %s,\r\nresponse: %s", requestParams.getUrl(), response.replaceAll("private", "privates"));
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
