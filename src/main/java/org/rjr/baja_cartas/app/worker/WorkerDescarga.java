package org.rjr.baja_cartas.app.worker;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.SwingWorker;
import net.coobird.thumbnailator.Thumbnails;
import org.rjr.baja_cartas.app.enums.CardType;
import org.rjr.baja_cartas.app.model.Card;

public class WorkerDescarga extends SwingWorker<Void, String> {

    private final HashMap<String, String> data;
    private final String URLBaseCard = "https://api.myl.cl/static/cards/%s/%s.png";
    private final List<Card> cardList;
    private final Consumer<String> onLog;

    public WorkerDescarga(HashMap<String, String> data, List<Card> cardList, Consumer<String> onLog) {
        this.data = data;
        this.cardList = cardList;
        this.onLog = onLog;
    }

    @Override
    protected Void doInBackground() throws Exception {
        int horizontal = this.data.get("size_h").length() > 0 ? Integer.parseInt(this.data.get("size_h")) : 709;
        int vertical = this.data.get("size_v").length() > 0 ? Integer.parseInt(this.data.get("size_v")) : 1016;
        List<Card> slugs = this.cardList.stream().filter(distinctByKey(Card::getEd_slug)).collect(Collectors.toList());

        if (!this.cardList.isEmpty()) {
            publish("Creando rutas");
            for (Card card : slugs) {
                String rutaBase = String.format(this.data.get("ruta") + "\\%s", card.getEd_slug());
                String rutaBaseNoTipo = rutaBase + "\\no_encontrado";
                String rutaBaseAliado = rutaBase + "\\aliado";
                String rutaBaseTalisman = rutaBase + "\\talisman";
                String rutaBaseArma = rutaBase + "\\arma";
                String rutaBaseTotem = rutaBase + "\\totem";
                String rutaBaseoro = rutaBase + "\\oro";
                String rutaBaseMonumento = rutaBase + "\\monumento";

                Files.createDirectories(Paths.get(rutaBase));
                Files.createDirectories(Paths.get(rutaBaseNoTipo));
                Files.createDirectories(Paths.get(rutaBaseAliado));
                Files.createDirectories(Paths.get(rutaBaseTalisman));
                Files.createDirectories(Paths.get(rutaBaseArma));
                Files.createDirectories(Paths.get(rutaBaseTotem));
                Files.createDirectories(Paths.get(rutaBaseoro));
                Files.createDirectories(Paths.get(rutaBaseMonumento));
            }

            int currentCard = 1;
            publish("Iniciando descarga");

            ExecutorService pool = Executors.newFixedThreadPool(6);
            AtomicInteger completadas = new AtomicInteger(0);
            int total = this.cardList.size();

            for (Card card : this.cardList) {
                pool.submit(() -> {
                    try {
                        descargarCarta(card);
                        int hechas = completadas.incrementAndGet();
                        publish(String.format(
                                "Carta %d de %d descargada",
                                hechas,
                                total
                        ));
                        firePropertyChange("cartaActual", null, hechas);

                    } catch (IOException e) {
                        publish("Error descargando " + card.getName());
                    }
                });
                currentCard++;
            }
            publish("Proceso terminado");
            pool.shutdown();
            pool.awaitTermination(1, TimeUnit.HOURS);
        }
        return null;

    }

    private void descargarCarta(Card card) throws IOException {
        String index = card.getEdid();
        URL url = new URL(String.format(URLBaseCard, card.getEd_edid(), index));

        String destino = String.format(
                data.get("ruta") + "\\%s\\%s\\%s.png",
                card.getEd_slug(),
                CardType.fromId(String.valueOf(card.getType())).getSlug(),
                card.getId() + "_" + card.getName()
        );

        Thumbnails.of(url)
                .scale(1)
                .outputFormat("png")
                .toFile(destino);
    }

    @Override
    protected void process(List<String> chunks) {
        for (String msg : chunks) {
            onLog.accept(msg);
        }
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
