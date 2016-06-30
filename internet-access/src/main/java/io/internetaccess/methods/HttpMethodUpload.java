package io.internetaccess.methods;

import io.internetaccess.ExecuteException;
import io.internetaccess.RequestParams;
import io.internetaccess.ResponseStatus;

import java.io.File;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;

public class HttpMethodUpload extends HttpMethod {
    @Override
    public ResponseStatus execute(RequestParams requestParams, HttpClient httpClient) {
        return uploadFile(requestParams);
    }

    public ResponseStatus uploadFile(RequestParams requestParams) throws ExecuteException {
        HttpClient httpClient = getHttpClient(null);
        HttpPost postRequest = new HttpPost(requestParams.getUrl());
        File file = new File(requestParams.getFilePath());

        MultipartEntityBuilder reqEntity = MultipartEntityBuilder
                .create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        if (requestParams.getBitmap() != null) {
//            requestParams.getBitmap().
//            reqEntity.addBinaryBody(file.getName(), new ByteArrayInputStream())
        } else {
            reqEntity.addBinaryBody(file.getName(), file, requestParams.getContentType(), file.getName());
        }
        postRequest.setEntity(reqEntity.build());
        return HttpExecuteHelper.executeRequest(postRequest, null, httpClient, requestParams.getHeaders());
    }

}
