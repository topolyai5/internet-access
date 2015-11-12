package com.topolyai.internet.access;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class HandleResponse {

    private static Gson gson = new Gson();

    private static ErrorHandler errorHandler;

    public HandleResponse(ErrorHandler errorHandler) {
        HandleResponse.errorHandler = errorHandler;
    }

    public static void asNoContent(ResponseStatus status) {
        if (status.getHttpStatus() != 200) {
            throw errorHandler.handle(status);
        }
    }

    public static <T> T asSingle(ResponseStatus status, Class<T> clzz) {
        if (status.getHttpStatus() != 200) {
            throw errorHandler.handle(status);
        }
        return gson.fromJson(status.getResponse(), clzz);

    }

    public static <T> List<T> asList(ResponseStatus status, Class<T> clzz) {
        if (status.getHttpStatus() != 200) {
            throw errorHandler.handle(status);
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
