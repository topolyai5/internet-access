package io.internetaccess.methods;

import io.internetaccess.RequestParams;
import io.internetaccess.ResponseStatus;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public abstract class HttpMethod {

    public abstract ResponseStatus execute(RequestParams requestParams, HttpClient httpClient);

    public ResponseStatus execute(RequestParams requestParams) {
        return execute(requestParams, null);
    }

    protected HttpClient getHttpClient(HttpClient client) {
        return (client == null) ? new DefaultHttpClient() : client;
    }

    protected String validateUrl(String url) {
        return url.replaceAll(" ", "%20").replace("|", "%7C");
    }

}
