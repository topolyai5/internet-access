package com.topolyai.internet.access;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpVersion;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.util.EntityUtils;

class HttpExecuteHelper {

    public static  String executeRequest(HttpUriRequest request, ContentType contentType, HttpClient client) throws ExecuteException, ExtractResponseException {
        request.addHeader("Content-Type", contentType.toString());
        client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
        client.getParams().setParameter("http.protocol.content-charset", contentType.getCharset());
        try {
            return extractResponseEntity(client.execute(request), contentType.getCharset());
        } catch (IOException e) {
            throw new ExecuteException(e.getMessage(), e);
        }
    }

    private static String extractResponseEntity(HttpResponse response, Charset charset) throws ExtractResponseException {
        HttpEntity resEntity = response.getEntity();
        if (resEntity != null) {
            try {
                return EntityUtils.toString(resEntity, charset);
            } catch (IOException e) {
                throw new ExtractResponseException(e.getMessage(), e);
            }
        }

        return "";
    }

    public static void extractDownloadedFile(AsyncTask task, InputStream input, OutputStream output, int fileLength , ProgressHandler progressHandler) throws IOException, CanceledException {
        byte data[] = new byte[4096];
        long total = 0;
        int count;
        while ((count = input.read(data)) != -1) {
            // allow canceling with back button
            if (task.isCancelled()) {
                input.close();
                throw new CanceledException();
            }
            total += count;
            // publishing the progress....
            if (progressHandler != null && fileLength > 0) { // only if total length is known
                progressHandler.progressHandling((int) (total * 100 / fileLength));
            }
            output.write(data, 0, count);
        }
    }
}