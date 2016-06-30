package io.internetaccess.methods;

import android.text.TextUtils;

import io.internetaccess.ExecuteException;
import io.internetaccess.RequestParams;
import io.internetaccess.ResponseStatus;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpEntityEnclosingRequestBase;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.ContentType;

public abstract class AbstractEntityEnclosingHttpMethod extends HttpMethod {
    @Override
    public ResponseStatus execute(RequestParams requestParams, HttpClient httpClient) {
        String targetUrl = requestParams.getUrl().replaceAll(" ", "+");
        HttpClient client = getHttpClient(httpClient);
        targetUrl = validateUrl(targetUrl);
        List<NameValuePair> nameValuePairs;
        if (requestParams.getNameValuePairs() == null) {
            nameValuePairs = new ArrayList<>();
        } else {
            nameValuePairs = requestParams.getNameValuePairs();
        }
        HttpEntityEnclosingRequestBase httpPost = getHttpRequest(targetUrl);
        ContentType contentType = requestParams.getContentType();
        try {
            if (contentType != null && contentType.equals(ContentType.APPLICATION_JSON)) {
//                String value = nameValuePair.getValue();
                if (!TextUtils.isEmpty(requestParams.getJson())) {
                    httpPost.setEntity(new ByteArrayEntity(requestParams.getJson().getBytes(contentType.getCharset())));
                }
            } else {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            }
        } catch (UnsupportedEncodingException e) {
            throw new ExecuteException(e.getMessage(), e);
        }
        List<Header> headers = requestParams.getHeaders();
        if (headers == null) {
            headers = new ArrayList<>();
        }
        return HttpExecuteHelper.executeRequest(httpPost, contentType, client, headers);
    }

    public abstract HttpEntityEnclosingRequestBase getHttpRequest(String url);
}
