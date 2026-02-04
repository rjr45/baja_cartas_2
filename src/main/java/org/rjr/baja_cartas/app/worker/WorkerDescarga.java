package org.rjr.baja_cartas.app.worker;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import net.coobird.thumbnailator.Thumbnails;
import org.rjr.baja_cartas.app.enums.CardType;
import org.rjr.baja_cartas.app.model.Card;

public class WorkerDescarga extends SwingWorker<Void, String> {

    private final HashMap<String, String> data;
    private final List<Card> cardList;
    private final Consumer<String> onLog;

    private static final String URL_BASE_CARD = "https://api.myl.cl/static/cards/%s/%s.png";

    private final AtomicInteger completadas = new AtomicInteger(0);

    public WorkerDescarga(HashMap<String, String> data, List<Card> cardList, Consumer<String> onLog) {
        this.data = data;
        this.cardList = cardList;
        this.onLog = onLog;
    }

    @Override
    protected Void doInBackground() throws Exception {

        int horizontal = data.get("size_h").length() > 0
                ? Integer.parseInt(data.get("size_h"))
                : -1;

        int vertical = data.get("size_v").length() > 0
                ? Integer.parseInt(data.get("size_v"))
                : -1;

        List<Card> slugs = cardList.stream()
                .filter(distinctByKey(Card::getEd_slug))
                .collect(Collectors.toList());

        publish("Creando rutas...");
        for (Card card : slugs) {
            String base = data.get("ruta") + "\\" + card.getEd_slug();
            Files.createDirectories(Paths.get(base, "no_encontrado"));
            Files.createDirectories(Paths.get(base, "aliado"));
            Files.createDirectories(Paths.get(base, "talisman"));
            Files.createDirectories(Paths.get(base, "arma"));
            Files.createDirectories(Paths.get(base, "totem"));
            Files.createDirectories(Paths.get(base, "oro"));
            Files.createDirectories(Paths.get(base, "monumento"));
        }

        publish("Iniciando descarga");

        int total = cardList.size();

        ExecutorService downloadPool = Executors.newFixedThreadPool(6);
        ExecutorService processPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ExecutorService diskPool = Executors.newFixedThreadPool(2);

        CountDownLatch latch = new CountDownLatch(total);

        for (Card card : cardList) {
            downloadPool.submit(() -> {
                if (isCancelled()) {
                    latch.countDown();
                    return;
                }

                try {
                    byte[] bytes = descargarBytes(card);
                    processPool.submit(() -> {
                        try {
                            BufferedImage img = procesarImagen(bytes, horizontal, vertical);

                            diskPool.submit(() -> {
                                try {
                                    guardarImagen(card, img);

                                    int hechas = completadas.incrementAndGet();
                                    actualizarProgreso(hechas, total, card);

                                } catch (IOException e) {
                                    publish("Error guardando " + card.getName());
                                } finally {
                                    latch.countDown();
                                }
                            });

                        } catch (IOException e) {
                            publish("Error procesando " + card.getName());
                            latch.countDown();
                        }
                    });

                } catch (IOException e) {
                    publish("Error descargando " + card.getName());
                    latch.countDown();
                }
            });
        }

        downloadPool.shutdown();
        latch.await();

        processPool.shutdown();
        diskPool.shutdown();

        publish("Proceso terminado");
        return null;
    }

    private byte[] descargarBytes(Card card) throws IOException {
        String index = card.getEdid();
        URL url = new URL(String.format(URL_BASE_CARD, card.getEd_edid(), index));

        try (InputStream in = url.openStream(); ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            byte[] bData = new byte[8192];
            int n;
            while ((n = in.read(bData)) != -1) {
                buffer.write(bData, 0, n);
            }
            return buffer.toByteArray();
        }
    }

    private BufferedImage procesarImagen(byte[] data, int h, int v) throws IOException {

        BufferedImage original = ImageIO.read(new ByteArrayInputStream(data));

        if (h <= 0 || v <= 0) {
            return Thumbnails.of(original)
                    .scale(1.0)
                    .asBufferedImage();
        }

        return Thumbnails.of(original)
                .size(h, v)
                .asBufferedImage();
    }

    private void guardarImagen(Card card, BufferedImage img) throws IOException {
        String destino = String.format(
                data.get("ruta") + "\\%s\\%s\\%s.png",
                card.getEd_slug(),
                CardType.fromId(String.valueOf(card.getType())).getSlug(),
                card.getId() + "_" + card.getName()
        );

        ImageIO.write(img, "png", Paths.get(destino).toFile());
    }

    private void actualizarProgreso(int hechas, int total, Card card) {
        firePropertyChange("cartaActual", null, hechas);

        publish(String.format(
                "Carta %d/%d - %s - %s",
                hechas,
                total,
                card.getEd_slug(),
                card.getName()
        ));
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
