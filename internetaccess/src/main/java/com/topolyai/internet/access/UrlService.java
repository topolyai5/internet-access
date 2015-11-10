package com.topolyai.internet.access;

import android.os.AsyncTask;
import android.util.Log;

import com.topolyai.vlogger.Logger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpOptions;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class UrlService {

    private static final Logger LOGGER = Logger.get(UrlService.class);

    public String post(String targetUrl, List<NameValuePair> nameValuePairs, ContentType contentType, HttpClient client) {
        targetUrl = targetUrl.replaceAll(" ", "+");
        client = (client == null) ? new DefaultHttpClient() : client;

        if (nameValuePairs == null) {
            targetUrl = validateUrl(targetUrl);
            nameValuePairs = new ArrayList<>();
        }
        HttpPost httpPost = new HttpPost(targetUrl);
        try {
            if (contentType.equals(ContentType.APPLICATION_JSON)) {
                if (nameValuePairs.size() != 1 ) {
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

    public String post(String targetUrl, List<NameValuePair> nameValuePairs, ContentType contentType) throws ExecuteException, ExtractResponseException {
        return post(targetUrl, nameValuePairs, contentType, null);
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
            LOGGER.w(e.getMessage());
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
    }

    public String get(String url, ContentType contentType, HttpClient client) throws ExecuteException {
        client = (client == null) ? new DefaultHttpClient() : client;
        url = validateUrl(url);
        HttpGet httpGet = new HttpGet(url);
        return HttpExecuteHelper.executeRequest(httpGet, contentType, client);
    }

    public String put(String url, ContentType contentType, HttpClient client) throws ExecuteException {
        client = (client == null) ? new DefaultHttpClient() : client;
        url = validateUrl(url);
        HttpPut httpGet = new HttpPut(url);
        return HttpExecuteHelper.executeRequest(httpGet, contentType, client);
    }

    public String put(String url, ContentType contentType) {
        return put(url, contentType, null);
    }

    public String get(String url, ContentType contentType) throws ExecuteException {
        return get(url, contentType, null);
    }

    private String validateUrl(String url) {
        return url.replaceAll(" ", "%20").replace("|", "%7C");
    }

    public String uploadFile(RequestParams requestParams) throws ExecuteException {
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        FileInputStream fileInputStream = null;
        try {

            String fileName = requestParams.getFilePath();


            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024 * 1024;
            File sourceFile = new File(fileName);

            if (!sourceFile.isFile()) {
                Log.e("uploadFile", "Source File not exist :" + fileName);
                return "{'httpStatus': 404, 'code': 'FILE_NOT_FOUND'}";
            }

            // open a URL connection to the Servlet
            fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(requestParams.getUrl());

            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);

            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);

            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {

                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            LOGGER.i("HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();


            return serverResponseMessage;
        } catch (IOException e) {
            throw new ExecuteException(e.getMessage());
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                    conn.disconnect();
                }
            } catch (IOException e) {
                LOGGER.e("Error when close streams...");
                throw new ExecuteException(e.getMessage());
            }

        }
    }

    public String delete(String url, ContentType contentType, HttpClient client) {
        client = (client == null) ? new DefaultHttpClient() : client;
        url = validateUrl(url);
        HttpDelete request = new HttpDelete(url);
        return HttpExecuteHelper.executeRequest(request, contentType, client);
    }

    public String delete(String url, ContentType contentType) {
        return delete(url, contentType, null);
    }

    public String options(String url, ContentType contentType) {
        return options(url, contentType, null);
    }

    public String options(String url, ContentType contentType, HttpClient client) {
        client = (client == null) ? new DefaultHttpClient() : client;
        url = validateUrl(url);
        HttpOptions request = new HttpOptions(url);
        return HttpExecuteHelper.executeRequest(request, contentType, client);
    }
}
