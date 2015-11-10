package com.topolyai.internet.access.methods;

import com.topolyai.internet.access.RequestMethod;

public class HttpMethodFactory {
    public static HttpMethod get(RequestMethod method) {
        switch (method) {
            case GET:
                return new HttpMethodGet();
            case POST:
                return new HttpMethodPost();
            case DOWNLOAD:
                return new HttpMethodDownload();
            case UPLOAD:
                return new HttpMethodUpload();
            case OPTION:
                return new HttpMethodOptions();
            case DELETE:
                return new HttpMethodDelete();
            case PUT:
                return new HttpMethodPut();
        }
        throw new IllegalArgumentException(String.format("Invalid request type: %s", method));
    }
}
