package org.rjr.baja_cartas.app.worker;

import java.util.HashMap;
import javax.swing.SwingWorker;

public class WorkerDescarga extends SwingWorker<Void, Void> {

    private final HashMap<String, String> data;
    private final String URLBaseData = "https://api.myl.cl/cards/edition/%s/";
    private final String URLBaseCard = "https://api.myl.cl/static/cards/%s/%s.png";

    public WorkerDescarga(HashMap<String, String> data) {
        this.data = data;
    }

    @Override
    protected Void doInBackground() throws Exception {
        System.err.println("Inicio");
        Thread.sleep(5000);
        System.out.println("fin");
        return null;
    }

}
