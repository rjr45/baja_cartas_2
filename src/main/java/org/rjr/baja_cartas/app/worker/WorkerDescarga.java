package org.rjr.baja_cartas.app.worker;

import com.google.gson.Gson;
import java.net.URL;
import java.util.HashMap;
import javax.swing.SwingWorker;
import net.coobird.thumbnailator.Thumbnails;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.rjr.baja_cartas.app.controller.Util;
import org.rjr.baja_cartas.app.model.Cards;

public class WorkerDescarga extends SwingWorker<Void, Void> {

    private final HashMap<String, String> data;
    private final String URLBaseData = "https://api.myl.cl/cards/edition/%s/";
    private final String URLBaseCard = "https://api.myl.cl/static/cards/%s/%s.png";

    public WorkerDescarga(HashMap<String, String> data) {
        this.data = data;
    }

    @Override
    protected Void doInBackground() throws Exception {
        String slug = this.data.get("slug");
        boolean generaTXT = this.data.get("txt").equals("true");
        boolean generaXLS = this.data.get("xls").equals("true");
        int horizontal = this.data.get("size_h").length() > 0 ? Integer.parseInt(this.data.get("size_h")) : 709;
        int vertical = this.data.get("size_v").length() > 0 ? Integer.parseInt(this.data.get("size_v")) : 1016;
        
        String URLEdicion = String.format(this.URLBaseData, slug);
        OkHttpClient cliente = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLEdicion)
                .build();
        
        try(Response response = cliente.newCall(request).execute()){
            Gson gson = Util.getGsonBuilder();
            String responseString = response.body().string();
            Cards cards = gson.fromJson(responseString, Cards.class);
            
            if(!cards.getCards().isEmpty()){
                for(int i = 1; i < cards.getCards().size(); i++){
                    String index = String.format("%03d", i);
                    URL url = new URL(String.format(URLBaseCard, cards.getCards().get(i).getEd_edid(), index));
                    String destino = String.format(this.data.get("ruta") + "\\%s.jpg", index);
                    Thumbnails.of(url)
                            .size(horizontal, vertical)
                            .outputFormat("jpg")
                            .toFile(destino);
                }
            }
        }
        
        
        
        return null;
    }
    
    

}
