package com.topolyai.internet.access;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.topolyai.vlogger.Logger;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class HandleResponse {

    private static final Logger LOGGER = Logger.get(HandleResponse.class);

    private static Gson gson = new Gson();

    private static ErrorHandler errorHandler;

    private HandleResponse(ErrorHandler errorHandler) {
        HandleResponse.errorHandler = errorHandler;
    }

    private HandleResponse(ErrorHandler errorHandler, Gson gson) {
        this(errorHandler);
        this.gson = gson;
    }

    public static void create(ErrorHandler errorHandler) {
        new HandleResponse(errorHandler);
    }

    public static void create(ErrorHandler errorHandler, Gson gson) {
        new HandleResponse(errorHandler, gson);
    }

    public static void asNoContent(ResponseStatus status) {
        if (status.getHttpStatus() != 200) {
            LOGGER.w("Response from server: {} with http code: {}", status.getResponse(), status.getHttpStatus());
            RuntimeException exception = errorHandler.toException(status);
            if (exception != null) {
                throw exception;
            }
        }
    }

    public static <T> T asSingle(ResponseStatus status, Class<T> clzz) {
        if (status.getHttpStatus() != 200) {
            LOGGER.w("Response from server: {} with http code: {}", status.getResponse(), status.getHttpStatus());
            RuntimeException exception = errorHandler.toException(status);
            if (exception != null) {
                throw exception;
            } else {
                return errorHandler.handle(status, clzz);
            }
        }
        return gson.fromJson(status.getResponse(), clzz);

    }

    public static <T> List<T> asList(ResponseStatus status, Class<T> clzz) {
        if (status.getHttpStatus() != 200) {
            LOGGER.w("Response from server: {} with http code: {}", status.getResponse(), status.getHttpStatus());
            RuntimeException exception = errorHandler.toException(status);
            if (exception != null) {
                throw exception;
            } else {
                return errorHandler.handle(status, clzz);
            }
        }
        final List<T> ret = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(status.getResponse());
            for (int i = 0; i < array.length(); i++) {
                ret.add(gson.fromJson(array.get(i).toString(), clzz));
            }
        } catch (JSONException e) {
            throw new RuntimeException(String.format("Failed to parse json: %s. Error message: %s", e, e.getMessage()));
        }
        return ret;
    }
}
