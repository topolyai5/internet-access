package io.internetaccess.methods;

import io.internetaccess.RequestParams;
import io.internetaccess.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpRequestBase;

public abstract class AbstractHttpMethod extends HttpMethod {
    @Override
    public ResponseStatus execute(RequestParams requestParams, HttpClient httpClient) {
        HttpClient client = getHttpClient(httpClient);
        String url = validateUrl(requestParams.getUrl());
        HttpRequestBase method = getHttpRequest(url);
        List<Header> headers = requestParams.getHeaders();
        if (headers == null) {
            headers = new ArrayList<>();
        }

        return HttpExecuteHelper.executeRequest(method, requestParams.getContentType(), client, headers);
    }

    public abstract HttpRequestBase getHttpRequest(String url);
}
