package io.internetaccess.methods;

import cz.msebera.android.httpclient.client.methods.HttpOptions;
import cz.msebera.android.httpclient.client.methods.HttpRequestBase;

public class HttpMethodOptions extends AbstractHttpMethod {

    @Override
    public HttpRequestBase getHttpRequest(String url) {
        return new HttpOptions(url);
    }
}
