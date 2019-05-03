package com.guli.poc.util;

import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public class JsonHelper {
    private JsonHelper() {
    }

    public static <T> T getFromJsonNode(com.mashape.unirest.http.JsonNode json, Type typeOfT) {
        return new GsonBuilder().create().fromJson(json.toString(), typeOfT);
    }
}
