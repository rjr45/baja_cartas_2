package org.rjr.baja_cartas.app.worker;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.swing.SwingWorker;
import static org.rjr.baja_cartas.app.controller.Util.limpiarTexto;
import org.rjr.baja_cartas.app.enums.CardType;
import org.rjr.baja_cartas.app.enums.Race;
import org.rjr.baja_cartas.app.enums.Rarity;
import org.rjr.baja_cartas.app.model.Card;

public class WorkerTXT extends SwingWorker<Void, Void> {

    private final HashMap<String, String> data;
    private final List<Card> cardList;

    public WorkerTXT(HashMap<String, String> data, List<Card> cardList) {
        this.data = data;
        this.cardList = cardList;
    }

    @Override
    protected Void doInBackground() throws Exception {
        boolean generaTXT = this.data.get("txt").equals("true");

        if (generaTXT) {
            StringBuilder sb = new StringBuilder();
            String slug = this.data.get("slug");

            sb.append("Nombre;Edición;Id;Tipo;Rareza;Raza;Daño;Costo;Habilidad;Ruta\n");

            for (Card card : this.cardList) {
                
                String imgPath = String.format(this.data.get("ruta") + "\\%s\\%s\\%s.png",
                        card.getEd_slug(),
                        CardType.fromId(String.valueOf(card.getType())).getSlug(),
                        card.getId() + "_" + card.getName());
                
                String habilidad = limpiarTexto(card.getAbility());
                
                sb.append(card.getName()).append(";");
                sb.append(card.getEd_slug()).append(";");
                sb.append(card.getEdid()).append(";");
                sb.append(CardType.fromId(String.valueOf(card.getType())).getDisplayName()).append(";");
                sb.append(Rarity.fromId(String.valueOf(card.getRarity())).getDisplayName()).append(";");
                sb.append(Race.fromId(String.valueOf(card.getRace())).getDisplayName()).append(";");
                sb.append(card.getDamage()).append(";");
                sb.append(card.getCost()).append(";");
                sb.append(habilidad).append(";");
                sb.append(imgPath);
                sb.append("\n");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(String.format(this.data.get("ruta") + "\\%s", slug + ".csv")))) {
                writer.write(sb.toString());
                System.out.println("Archivo escrito exitosamente.");
            } catch (IOException e) {
                System.err.println("Error al escribir el archivo: " + e.getMessage());
            }
        }

        return null;
    }
    
    

}
