package io.internetaccess.methods;

import cz.msebera.android.httpclient.client.methods.HttpEntityEnclosingRequestBase;
import cz.msebera.android.httpclient.client.methods.HttpPost;

public class HttpMethodPost extends AbstractEntityEnclosingHttpMethod {

    @Override
    public HttpEntityEnclosingRequestBase getHttpRequest(String url) {
        return new HttpPost(url);
    }
}
