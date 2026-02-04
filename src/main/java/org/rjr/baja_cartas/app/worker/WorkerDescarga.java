package org.rjr.baja_cartas.app.worker;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import net.coobird.thumbnailator.Thumbnails;
import org.rjr.baja_cartas.app.enums.CardType;
import org.rjr.baja_cartas.app.model.Card;

public class WorkerDescarga extends SwingWorker<Void, String> {

    private final HashMap<String, String> data;
    private final List<Card> cardList;
    private final BiConsumer<String, Color> onLog;

    private static final String URL_BASE_CARD = "https://api.myl.cl/static/cards/%s/%s.png";

    private final AtomicInteger completadas = new AtomicInteger(0);

    public WorkerDescarga(HashMap<String, String> data, List<Card> cardList, BiConsumer<String, Color> onLog) {
        this.data = data;
        this.cardList = cardList;
        this.onLog = onLog;
    }

    @Override
    protected Void doInBackground() throws Exception {
        ImageIO.setUseCache(false);

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

        ThreadPoolExecutor downloadPool = new ThreadPoolExecutor(
                8,
                8,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(32),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        ThreadPoolExecutor processPool = new ThreadPoolExecutor(
                6,
                6,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(12),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        CountDownLatch latch = new CountDownLatch(total);

        for (Card card : cardList) {
            downloadPool.execute(() -> {
                if (isCancelled()) {
                    latch.countDown();
                    return;
                }

                try {
                    byte[] bytes = descargarBytes(card);
                    processPool.execute(() -> {
                        try {
                            int hechas = completadas.incrementAndGet();
                            procesarYGuardar(bytes, horizontal, vertical, card);
                            
                            if (hechas % 20 == 0) {
                                System.gc();
                            }
                            
                            if (hechas % 20 == 0) {
                                publish(String.format(
                                        "POOL D[%d/%d] P[%d/%d]",
                                        downloadPool.getActiveCount(),
                                        downloadPool.getQueue().size(),
                                        processPool.getActiveCount(),
                                        processPool.getQueue().size()
                                ));
                            }
                            actualizarProgreso(hechas, total, card, "ok");
                        } catch (IOException ex) {
                            actualizarProgreso(completadas.get(), total, card, "error");
                        } finally {
                            latch.countDown();
                        }
                    });

                } catch (IOException e) {
                    actualizarProgreso(completadas.incrementAndGet(), total, card, "error");
                    latch.countDown();
                }
            });
        }

        downloadPool.shutdown();
        latch.await();
        processPool.shutdown();

        downloadPool.awaitTermination(1, TimeUnit.HOURS);
        processPool.awaitTermination(1, TimeUnit.HOURS);

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

    private void procesarYGuardar(byte[] dataImage, int h, int v, Card card) throws IOException {
        String destino = String.format(
                this.data.get("ruta") + "\\%s\\%s\\%s.png",
                card.getEd_slug(),
                CardType.fromId(String.valueOf(card.getType())).getSlug(),
                card.getId() + "_" + card.getName()
        );

        try (ByteArrayInputStream in = new ByteArrayInputStream(dataImage)) {

            Thumbnails.Builder<?> builder = Thumbnails.of(in);

            if (h > 0 && v > 0) {
                builder.size(h, v);
            } else {
                builder.scale(1);
            }

            builder.outputFormat("png")
                    .toFile(destino);
        }
    }

    private void actualizarProgreso(int hechas, int total, Card card, String estado) {
        firePropertyChange("cartaActual", null, hechas);

        if ("ok".equals(estado)) {
            publish(String.format(
                    "Carta %d/%d - %s - %s",
                    hechas,
                    total,
                    card.getEd_slug(),
                    card.getName()
            ));
            return;
        }

        publish(String.format(
                "Error, no encontrada: Carta %d/%d - %s - %s",
                hechas,
                total,
                card.getEd_slug(),
                card.getName()
        ));
    }

    @Override
    protected void process(List<String> chunks) {
        for (String msg : chunks) {
            if (msg.startsWith("Error")) {
                onLog.accept(msg, Color.RED);
            } else {
                onLog.accept(msg, new Color(0, 150, 0));
            }
        }
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
