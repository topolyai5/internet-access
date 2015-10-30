package com.topolyai.internet.access;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class HttpExecuteHelper {

    public static  String executeRequest(HttpUriRequest request, HttpClient client) throws ExecuteException, ExtractResponseException {
        request.addHeader("Content-Type", "text/html; charset=UTF-8");
        client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
        client.getParams().setParameter("http.protocol.content-charset", "UTF-8");
        try {
            return extractResponseEntity(client.execute(request));
        } catch (IOException e) {
            throw new ExecuteException(e.getMessage(), e);
        }
    }

    private static String extractResponseEntity(HttpResponse response) throws ExtractResponseException {
        HttpEntity resEntity = response.getEntity();
        if (resEntity != null) {
            try {
                return EntityUtils.toString(resEntity, "UTF-8");
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
