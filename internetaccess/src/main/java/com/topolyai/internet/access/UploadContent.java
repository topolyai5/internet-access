package com.topolyai.internet.access;

import android.util.Log;

import com.topolyai.vlogger.Logger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UploadContent {

    private static final Logger LOGGER = Logger.get(UploadContent.class);

    public ResponseStatus uploadFile(RequestParams requestParams) throws IOException {

        String fileName = requestParams.getFilePath();

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(fileName);

        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist :" + fileName);
            return ResponseStatus.builder().httpStatus("404").response("{'code': 'NO_FILE'}").build();
        }

        // open a URL connection to the Servlet
        FileInputStream fileInputStream = new FileInputStream(sourceFile);
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


        return ResponseStatus.builder().response(serverResponseMessage).httpStatus(serverResponseCode + "").build();

    }

}
