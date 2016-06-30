package io.internetaccess.methods;

import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpRequestBase;

public class HttpMethodDelete extends AbstractHttpMethod {

    @Override
    public HttpRequestBase getHttpRequest(String url) {
        return new HttpDelete(url);
    }
}
