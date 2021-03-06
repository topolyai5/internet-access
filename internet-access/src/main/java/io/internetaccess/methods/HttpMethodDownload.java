package io.internetaccess.methods;

import android.os.AsyncTask;

import io.internetaccess.CanceledException;
import io.internetaccess.ExecuteException;
import io.internetaccess.ProgressHandler;
import io.internetaccess.RequestParams;
import io.internetaccess.ResponseStatus;
import io.vlogger.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.client.HttpClient;

public class HttpMethodDownload extends HttpMethod {

    private static final Logger LOGGER = Logger.get(HttpMethodDownload.class);

    @Override
    public ResponseStatus execute(RequestParams requestParams, HttpClient httpClient) {
        return download(requestParams.getAsyncTask(), requestParams.getUrl(), requestParams.getFilePath(), null);
    }

    private ResponseStatus download(AsyncTask task, String targetUrl, String filePath, ProgressHandler progressHandler) throws ExecuteException, CanceledException {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        int httpStatus;
        String response;
        try {
            URL url = new URL(targetUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            httpStatus = connection.getResponseCode();
            response = String.format("Server returned HTTP: %s response msg: %s", connection.getResponseCode(), connection.getResponseMessage());
            input = connection.getInputStream();
            output = new FileOutputStream(filePath);
            HttpExecuteHelper.extractDownloadedFile(task, input, output, connection.getContentLength(), progressHandler);
        } catch (CanceledException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecuteException(e.getMessage(), e);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                LOGGER.w(e.getMessage());
            }

            if (connection != null) {
                connection.disconnect();
            }
        }

        return ResponseStatus.builder().response(response).httpStatus(httpStatus).build();
    }
}
