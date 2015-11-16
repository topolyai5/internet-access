package com.topolyai.internet.access;

import android.os.AsyncTask;

import com.topolyai.internet.access.methods.HttpMethodFactory;
import com.topolyai.vlogger.Logger;

import java.util.concurrent.ExecutionException;

public class RequestService extends AsyncTask<RequestParams, Void, ResponseStatus> {

    private static final Logger LOGGER = Logger.get(RequestService.class);
    private RequestListener requestListener = null;
    private ProgressHandler progressHandler = null;

    private RequestServiceExecutorContext executorContext = RequestServiceExecutorContext.get();

    public RequestService() {
    }

    public RequestService(RequestListener requestListener) {
        this.requestListener = requestListener;
    }

    public static RequestService with(RequestListener listener) {
        return new RequestService(listener);
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

    private ResponseStatus executeInSync(AsyncTask<RequestParams, Void, ResponseStatus> execute) {
        try {
            ResponseStatus response = execute.get();
            return response;
        } catch (InterruptedException | ExecutionException e) {
            throw new ExecuteException(e);
        }
    }

    protected ResponseStatus sendRequest(RequestParams requestParams) {
        try {
            return HttpMethodFactory.get(requestParams.getRequestMethod()).execute(requestParams);
        } catch (ConnectionErrorException e) {
            LOGGER.e("Error when execute HTTP Request: %s", e, e.getMessage());
            executorContext.add(new RequestService(requestListener), requestParams);
            return ResponseStatus.builder().httpStatus(503).response("NO_INTERNET_CONNECTION").build();
        } catch (ExecuteException e) {
            LOGGER.e("Error when execute HTTP Request: %s", e, e.getMessage());
            return ResponseStatus.builder().httpStatus(400).response("{FAILED_TO_EXECUTE").build();
        }
    }

    @Override
    protected ResponseStatus doInBackground(RequestParams... params) {
        return sendRequest(params[0]);
    }
}
