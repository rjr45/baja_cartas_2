package org.rjr.baja_cartas.app.worker;

import com.google.gson.Gson;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.SwingWorker;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.rjr.baja_cartas.app.controller.Util;
import org.rjr.baja_cartas.app.enums.CardType;
import org.rjr.baja_cartas.app.model.Card;
import org.rjr.baja_cartas.app.model.Cards;

public class WorkerCardList extends SwingWorker<List, Void> {

    private final HashMap<String, String> data;
    private final String URLBaseData = "https://api.myl.cl/cards/edition/%s/";

    public WorkerCardList(HashMap<String, String> data) {
        this.data = data;
    }

    @Override
    protected List doInBackground() throws Exception {
        String slug = this.data.get("slug");

        String URLEdicion = String.format(this.URLBaseData, slug);
        OkHttpClient cliente = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLEdicion)
                .build();

        try (Response response = cliente.newCall(request).execute()) {
            Gson gson = Util.getGsonBuilder();
            String responseString = response.body().string();
            Cards cards = gson.fromJson(responseString, Cards.class);
            
            List<Card> cardListFiltered = cards.getCards().stream().filter(card -> !Files.exists(Paths.get(
                    String.format(this.data.get("ruta") + "\\%s\\%s\\%s.png",
                            card.getEd_slug(),
                            CardType.fromId(String.valueOf(card.getType())).getSlug(),
                            card.getId() + "_" + card.getName()))))
                    .collect(Collectors.toList());
            
            return cardListFiltered;
        }
    }

}
