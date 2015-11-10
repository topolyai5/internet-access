package com.topolyai.internet.access.methods;

import com.topolyai.internet.access.RequestParams;
import com.topolyai.internet.access.ResponseStatus;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpRequestBase;

public abstract class AbstractHttpMethod extends HttpMethod {
    @Override
    public ResponseStatus execute(RequestParams requestParams, HttpClient httpClient) {
        HttpClient client = getHttpClient(httpClient);
        String url = validateUrl(requestParams.getUrl());
        HttpRequestBase method = getHttpRequest(url);
        return HttpExecuteHelper.executeRequest(method, requestParams.getContentType(), client);
    }

    public abstract HttpRequestBase getHttpRequest(String url);
}
