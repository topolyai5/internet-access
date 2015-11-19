package com.topolyai.internet.access.methods;

import android.os.AsyncTask;

import com.topolyai.internet.access.CanceledException;
import com.topolyai.internet.access.ConnectionErrorException;
import com.topolyai.internet.access.ExecuteException;
import com.topolyai.internet.access.ExtractResponseException;
import com.topolyai.internet.access.ProgressHandler;
import com.topolyai.internet.access.ResponseStatus;
import com.topolyai.vlogger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpVersion;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.util.EntityUtils;

class HttpExecuteHelper {

    private static final Logger LOGGER = Logger.get(HttpExecuteHelper.class);

    public static ResponseStatus executeRequest(HttpUriRequest request, ContentType contentType, HttpClient client, List<Header> headers) throws ExecuteException, ExtractResponseException {
        request.addHeader("Content-Type", contentType.toString());
        for (Header header : headers) {
            request.addHeader(header);
        }
        client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
        client.getParams().setParameter("http.protocol.content-charset", contentType.getCharset());
        try {
            return extractResponseEntity(client.execute(request), contentType.getCharset());
        } catch (IOException e) {
            throw new ConnectionErrorException(e.getMessage(), e);
        }
    }

    private static ResponseStatus extractResponseEntity(HttpResponse response, Charset charset) throws ExtractResponseException {
        HttpEntity resEntity = response.getEntity();
        int statusCode = response.getStatusLine().getStatusCode();
        String responseString = "";
        if (resEntity != null) {
            try {
                responseString = EntityUtils.toString(resEntity, charset);
            } catch (IOException e) {
                LOGGER.e(e.getMessage(), e);
                throw new ExtractResponseException(e.getMessage(), e);
            }
        }
        Header[] allHeaders = response.getAllHeaders();
        return ResponseStatus.builder().response(responseString).httpStatus(statusCode).headers(Arrays.asList(allHeaders)).build();
    }

    public static void extractDownloadedFile(AsyncTask task, InputStream input, OutputStream output, int fileLength, ProgressHandler progressHandler) throws IOException, CanceledException {
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
