package com.topolyai.internet.access;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UrlService {

    public static final String LOGTAG = UrlService.class.getName();

    public String post(String targetUrl, List<NameValuePair> nameValuePairs, HttpClient client) throws ExecuteException, ExtractResponseException {
        targetUrl = targetUrl.replaceAll(" ", "+");
        client = (client == null) ? new DefaultHttpClient() : client;

        if (nameValuePairs == null) {
            targetUrl = validateUrl(targetUrl);
            nameValuePairs = new ArrayList<>();
        }
        HttpPost httpPost = new HttpPost(targetUrl);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            throw new ExecuteException(e.getMessage(), e);
        }
        return HttpExecuteHelper.executeRequest(httpPost, client);
    }

    public String post(String targetUrl, List<NameValuePair> nameValuePairs) throws ExecuteException, ExtractResponseException {
        return post(targetUrl, nameValuePairs, null);
    }

    public String binary(AsyncTask task, String targetUrl, String filePath, ProgressHandler progressHandler) throws ExecuteException, CanceledException {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(targetUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return String.format("{\"response\": \"Server returned HTTP: response msg: %s\", \"status\":\"%s\"}", connection.getResponseMessage(), connection.getResponseCode());
            }
            input = connection.getInputStream();
            output = new FileOutputStream(filePath);
            HttpExecuteHelper.extractDownloadedFile(task, input, output, connection.getContentLength(), progressHandler);
            return "{\"response\": \"Successfully\", \"status\":\"OK\"}";
        } catch (CanceledException e) {
            throw e;
        } catch (Exception e) {
            Log.w(LOGTAG, e.getMessage(), e);
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
                Log.w(LOGTAG, e.getMessage(), e);
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public String get(String url, HttpClient client) throws ExecuteException, ExtractResponseException {
        client = (client == null) ? new DefaultHttpClient() : client;
        url = validateUrl(url);
        HttpGet httpGet = new HttpGet(url);
        return HttpExecuteHelper.executeRequest(httpGet, client);
    }

    public String get(String url) throws ExecuteException, ExtractResponseException {
        return get(url, null);
    }

    private String validateUrl(String url) {
        String ret = url;
        return ret.replaceAll(" ", "%20").replace("|", "%7C");
    }
}
