package org.rjr.baja_cartas.app.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Util {

    public static Gson getGsonBuilder() {
        GsonBuilder gbuild = new GsonBuilder();
        gbuild.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Gson gson = gbuild.serializeSpecialFloatingPointValues().serializeNulls().create();
        return gson;
    }
}
