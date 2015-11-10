package com.topolyai.internet.access.methods;

import com.topolyai.internet.access.ExecuteException;
import com.topolyai.internet.access.RequestParams;
import com.topolyai.internet.access.ResponseStatus;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
            if (contentType.equals(ContentType.APPLICATION_JSON)) {
                if (nameValuePairs.size() != 1) {
                    throw new ExecuteException("Invalid NameValuePairs size.");
                }
                NameValuePair nameValuePair = nameValuePairs.get(0);
                String value = nameValuePair.getValue();
                httpPost.setEntity(new ByteArrayEntity(value.getBytes(contentType.getCharset())));
            } else {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            }
        } catch (UnsupportedEncodingException e) {
            throw new ExecuteException(e.getMessage(), e);
        }
        return HttpExecuteHelper.executeRequest(httpPost, contentType, client);
    }

    public abstract HttpEntityEnclosingRequestBase getHttpRequest(String url);
}
