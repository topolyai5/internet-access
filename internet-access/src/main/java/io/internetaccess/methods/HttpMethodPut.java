package io.internetaccess.methods;

import cz.msebera.android.httpclient.client.methods.HttpEntityEnclosingRequestBase;
import cz.msebera.android.httpclient.client.methods.HttpPut;

public class HttpMethodPut extends AbstractEntityEnclosingHttpMethod {

    @Override
    public HttpEntityEnclosingRequestBase getHttpRequest(String url) {
        return new HttpPut(url);
    }

}
