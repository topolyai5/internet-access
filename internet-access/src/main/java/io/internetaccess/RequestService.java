package io.internetaccess;

import android.os.AsyncTask;

import io.internetaccess.methods.HttpMethodFactory;
import io.vlogger.Logger;

import java.util.concurrent.ExecutionException;

public class RequestService<T> extends AsyncTask<RequestParams, Void, ResponseStatus> {

    private static final Logger LOGGER = Logger.get(RequestService.class);
    private PreRequestListener preRequestListener = null;
    private PostRequestListener<T> postRequestListener = null;
    private ProgressHandler progressHandler = null;
    private Class<T> clzz;

    private RequestServiceExecutorContext executorContext = RequestServiceExecutorContext.get();

    public RequestService() {
    }

    public RequestService(PreRequestListener preRequestListener, PostRequestListener<T> postRequestListener, Class clzz) {
        this(preRequestListener, postRequestListener, clzz, null);
    }

    public RequestService(PreRequestListener preRequestListener, PostRequestListener<T> postRequestListener, Class clzz, ProgressHandler progressHandler) {
        this.preRequestListener = preRequestListener;
        this.postRequestListener = postRequestListener;
        this.progressHandler = progressHandler;
        this.clzz = clzz;
    }

    public static RequestService with(PreRequestListener listener, PostRequestListener postRequestListener, Class clzz) {
        return new RequestService(listener, postRequestListener, clzz);
    }

    @Override
    protected void onPreExecute() {
        if (preRequestListener != null) {
            preRequestListener.execute();
        }
    }

    @Override
    protected void onPostExecute(ResponseStatus result) {

        for (Interceptor interceptor : InterceptorManager.get().getInterceptors()) {
            interceptor.post(result);
        }

        if (postRequestListener != null) {
            T res = null;
            if (clzz.isInstance(result)) {
                res = (T) result;
            } else {
                if (result.getResponse().startsWith("[")) {
                    res = (T) HandleResponse.asList(result, clzz);
                } else if (result.getResponse().startsWith("{")) {
                    res = HandleResponse.asSingle(result, clzz);
                } else {
                    HandleResponse.asNoContent(result);
                }
            }
            postRequestListener.execute(res, result.getHttpStatus(), result.getResponse());
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

    protected ResponseStatus executeInSync(AsyncTask<RequestParams, Void, ResponseStatus> execute) {
        try {
            ResponseStatus response = execute.get();
            return response;
        } catch (InterruptedException | ExecutionException e) {
            throw new ExecuteException(e);
        }
    }

    protected ResponseStatus sendRequest(RequestParams requestParams) {
        try {
            requestParams.setAsyncTask(this);
            return HttpMethodFactory.get(requestParams.getRequestMethod()).execute(requestParams);
        } catch (ConnectionErrorException e) {
            LOGGER.e("Error when execute HTTP Request: {}", e, e.getMessage());
            executorContext.add(new RequestService(preRequestListener, postRequestListener, clzz), requestParams);
            return ResponseStatus.builder().httpStatus(503).response("SERVICE_UNAVAILABLE").build();
        } catch (ExecuteException e) {
            LOGGER.e("Error when execute HTTP Request: {}", e, e.getMessage());
            return ResponseStatus.builder().httpStatus(400).response("FAILED_TO_EXECUTE").build();
        }
    }

    @Override
    protected ResponseStatus doInBackground(RequestParams... params) {
        return sendRequest(params[0]);
    }
}
