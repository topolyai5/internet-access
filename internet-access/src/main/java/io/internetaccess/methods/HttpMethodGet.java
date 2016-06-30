package io.internetaccess.methods;

import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpRequestBase;

public class HttpMethodGet extends AbstractHttpMethod {

    @Override
    public HttpRequestBase getHttpRequest(String url) {
        return new HttpGet(url);
    }

}
