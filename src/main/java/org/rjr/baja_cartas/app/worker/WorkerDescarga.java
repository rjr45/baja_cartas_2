package org.rjr.baja_cartas.app.worker;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import javax.swing.SwingWorker;
import net.coobird.thumbnailator.Thumbnails;
import org.rjr.baja_cartas.app.model.Card;

public class WorkerDescarga extends SwingWorker<Void, Void> {

    private final HashMap<String, String> data;
    private final String URLBaseCard = "https://api.myl.cl/static/cards/%s/%s.png";
    private final List<Card> cardList;

    public WorkerDescarga(HashMap<String, String> data, List<Card> cardList) {
        this.data = data;
        this.cardList = cardList;
    }

    @Override
    protected Void doInBackground() throws Exception {
        int horizontal = this.data.get("size_h").length() > 0 ? Integer.parseInt(this.data.get("size_h")) : 709;
        int vertical = this.data.get("size_v").length() > 0 ? Integer.parseInt(this.data.get("size_v")) : 1016;
        if (!this.cardList.isEmpty()) {
            for (Card card : this.cardList) {
                String index = card.getEdid();
                URL url = new URL(String.format(URLBaseCard, card.getEd_edid(), index));
                String destino = String.format(this.data.get("ruta") + "\\%s.jpg", index);
                Thumbnails.of(url)
                        .size(horizontal, vertical)
                        .outputFormat("jpg")
                        .toFile(destino);
            }
        }
        return null;

    }
}
