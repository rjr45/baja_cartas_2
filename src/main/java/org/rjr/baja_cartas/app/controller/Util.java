package org.rjr.baja_cartas.app.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.swing.JInternalFrame;
import org.rjr.baja_cartas.app.ui.BajaCartolas;

public class Util {

    public static Gson getGsonBuilder() {
        GsonBuilder gbuild = new GsonBuilder();
        gbuild.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Gson gson = gbuild.serializeSpecialFloatingPointValues().serializeNulls().create();
        return gson;
    }

    public static boolean isNotOpen(String frameName, BajaCartolas bajaCartolas) {
        for (JInternalFrame jit : bajaCartolas.dskMain.getAllFrames()) {
            if (frameName.equalsIgnoreCase(jit.getTitle())) {
                jit.toFront();
                return false;
            }
        }
        return true;
    }

    public static String limpiarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        return texto
                .replaceAll("[\\\\/:*?\"<>|]", "")
                .replaceAll("[\\r\\n\\t]", " ")
                .trim();
    }
}
