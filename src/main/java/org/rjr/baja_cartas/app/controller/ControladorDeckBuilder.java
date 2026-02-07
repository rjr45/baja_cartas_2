package org.rjr.baja_cartas.app.controller;

import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.rjr.baja_cartas.app.model.CardBuilder;
import org.rjr.baja_cartas.app.ui.BajaCartolas;
import org.rjr.baja_cartas.app.ui.DeckBuilder;

public class ControladorDeckBuilder {

    private final DeckBuilder deckBuilder;
    private final BajaCartolas bajaCartolas;
    private List<CardBuilder> listaCartolas;
    private List<CardBuilder> listaMiMazo;
    private TableRowSorter<TableModel> sorter;
    private final double[] PORCENTAJES_TBL_CARDS = {0.06, 0.06, 0.06, 0.06, 0.06, 0.06, 0.06, 0.573};
    private final double[] PORCENTAJE_TBL_DECK = {0.7, 0.3};

    public ControladorDeckBuilder(DeckBuilder deckBuilder, BajaCartolas bajaCartolas) {
        this.deckBuilder = deckBuilder;
        this.bajaCartolas = bajaCartolas;
        this.listaCartolas = new ArrayList<>();
        this.listaMiMazo = new ArrayList<>();
    }

    public void run() throws PropertyVetoException {
        if (Util.isNotOpen(this.deckBuilder.getTitle(), bajaCartolas)) {
            this.deckBuilder.btnCargar.addActionListener((e) -> this.cargarCartas());

            this.deckBuilder.tblCards.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        int fila = deckBuilder.tblCards.rowAtPoint(e.getPoint());

                        if (fila != -1) {
                            deckBuilder.tblCards.setRowSelectionInterval(fila, fila);
                            deckBuilder.menCards.show(deckBuilder.tblCards, e.getX(), e.getY());
                        }
                    }
                }
            });

            this.deckBuilder.tblCards.getSelectionModel().addListSelectionListener(e -> {

                if (!e.getValueIsAdjusting()) {
                    filaSeleccionada(deckBuilder.tblCards);
                }

            });

            this.deckBuilder.tblMyDeck.getSelectionModel().addListSelectionListener(e -> {

                if (!e.getValueIsAdjusting()) {
                    filaSeleccionada(deckBuilder.tblMyDeck);
                }

            });

            this.deckBuilder.txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
                private void filtrar() {
                    String texto = deckBuilder.txtBuscar.getText();

                    if (texto.trim().length() == 0) {
                        sorter.setRowFilter(null);
                        return;
                    }

                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 0));
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    filtrar();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    filtrar();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    filtrar();
                }
            });

            this.deckBuilder.menAgregarUno.addActionListener((e) -> this.agregarAlMazo(1));
            this.deckBuilder.menAgregar2.addActionListener((e) -> this.agregarAlMazo(2));
            this.deckBuilder.menAgregar3.addActionListener((e) -> this.agregarAlMazo(3));
            this.deckBuilder.menVerCarton.addActionListener((e) -> this.verCarton());
            this.deckBuilder.btnCancelar.addActionListener((e) -> this.limpiarDeck());
            this.deckBuilder.btnPdf.addActionListener((e) -> this.crearPdf());

            this.eliminarFila();

            this.bajaCartolas.dskMain.add(deckBuilder);
            this.deckBuilder.pack();

            SwingUtilities.invokeLater(() -> {
                try {
                    this.deckBuilder.setMaximum(true);
                } catch (PropertyVetoException ex) {
                    Logger.getLogger(ControladorDeckBuilder.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.tblDeckConfig();
                this.deckBuilder.setVisible(true);
            });
        }
    }

    private void eliminarFila() {
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        this.deckBuilder.tblMyDeck.getInputMap(JComponent.WHEN_FOCUSED).put(delete, "deleteRow");
        this.deckBuilder.tblMyDeck.getActionMap().put("deleteRow", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int op = JOptionPane.showConfirmDialog(
                        bajaCartolas,
                        "¿Eliminar fila?",
                        "Confirmar",
                        JOptionPane.YES_NO_OPTION
                );

                if (op != JOptionPane.YES_OPTION) {
                    return;
                }

                int fila = deckBuilder.tblMyDeck.getSelectedRow();
                if (fila == -1) {
                    return;
                }

                DefaultTableModel model = (DefaultTableModel) deckBuilder.tblMyDeck.getModel();
                model.removeRow(fila);

                if (model.getRowCount() > 0) {
                    if (fila >= model.getRowCount()) {
                        fila = model.getRowCount() - 1;
                    }
                    deckBuilder.tblMyDeck.setRowSelectionInterval(fila, fila);
                }
            }
        });
    }

    private void tblDeckConfig() {
        DefaultTableModel model = (DefaultTableModel) this.deckBuilder.tblMyDeck.getModel();
        model.setRowCount(0);
        this.deckBuilder.tblMyDeck.setRowHeight(25);
        this.deckBuilder.tblMyDeck.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.deckBuilder.tblMyDeck.getColumnModel()
                .getColumn(0)
                .setCellRenderer(new TextAreaRenderer());

        this.ajustarAnchoColumnas(PORCENTAJE_TBL_DECK, this.deckBuilder.tblMyDeck);
    }

    private void agregarAlMazo(int cantidad) {
        int viewRow = this.deckBuilder.tblCards.getSelectedRow();
        if (viewRow == -1) {
            return;
        }
        int modelRow = this.deckBuilder.tblCards.convertRowIndexToModel(viewRow);

        CardBuilder card = this.listaCartolas.get(modelRow);
        card.setCopias(cantidad);
        this.listaMiMazo.add(card);

        DefaultTableModel model = (DefaultTableModel) this.deckBuilder.tblMyDeck.getModel();
        model.addRow(new Object[]{card.getNombre(), cantidad});
    }

    private void filaSeleccionada(JTable tbl) {
        int viewRow = tbl.getSelectedRow();
        if (viewRow == -1) {
            return;
        }

        int modelRow = tbl.convertRowIndexToModel(viewRow);

        String imgPath;

        if (tbl.getColumnCount() == 2) {
            imgPath = this.listaMiMazo.get(modelRow).getImgPath();
        } else {
            imgPath = this.listaCartolas.get(modelRow).getImgPath();
        }

        try {
            BufferedImage img = ImageIO.read(new File(imgPath));

            int maxWidth = 290;
            int maxHeight = 421;

            int w = img.getWidth();
            int h = img.getHeight();

            double ratio = Math.min((double) maxWidth / w, (double) maxHeight / h);

            int newW = (int) (w * ratio);
            int newH = (int) (h * ratio);

            Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);

            this.deckBuilder.lblImg.setIcon(new ImageIcon(scaled));
            this.deckBuilder.lblImg.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        } catch (HeadlessException | IOException ex) {
            this.deckBuilder.lblImg.setText("No se pudo mostrar la imagen");
        }

    }

    private void verCarton() {
        int viewRow = this.deckBuilder.tblCards.getSelectedRow();
        if (viewRow == -1) {
            return;
        }

        int modelRow = this.deckBuilder.tblCards.convertRowIndexToModel(viewRow);

        String imgPath = this.listaCartolas.get(modelRow).getImgPath();

        try {
            BufferedImage img = ImageIO.read(new File(imgPath));

            int maxWidth = 500;
            int maxHeight = 600;

            int w = img.getWidth();
            int h = img.getHeight();

            double ratio = Math.min((double) maxWidth / w, (double) maxHeight / h);

            int newW = (int) (w * ratio);
            int newH = (int) (h * ratio);

            Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);

            JLabel pic = new JLabel(new ImageIcon(scaled));
            pic.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JOptionPane.showMessageDialog(
                    this.bajaCartolas,
                    pic,
                    "Preview carta",
                    JOptionPane.PLAIN_MESSAGE
            );

        } catch (HeadlessException | IOException ex) {
            JOptionPane.showMessageDialog(null, "No se pudo cargar imagen");
        }
    }

    private void cargarCartas() {
        String desktop = System.getProperty("user.home");
        JFileChooser chooser = new JFileChooser(new File(String.format("%s/desktop", desktop)));
        chooser.setDialogTitle("Selecciona archivo CSV");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivo CSV", "csv");
        chooser.setFileFilter(filter);
        int result = chooser.showOpenDialog(this.deckBuilder);
        if (result == JFileChooser.APPROVE_OPTION) {
            File csvFile = chooser.getSelectedFile();
            String path = csvFile.getAbsolutePath();

            this.listaCartolas = cargarCSV(path);
            this.cargarTabla(listaCartolas);
        }
    }

    private List<CardBuilder> cargarCSV(String path) {

        List<CardBuilder> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String line;

            br.readLine();

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] p = line.split(";", -1);

                if (p.length < 10) {
                    continue;
                }

                CardBuilder card = new CardBuilder();
                card.setNombre(p[0]);
                card.setEdicion(p[1]);
                card.setTipo(p[3]);
                card.setRareza(p[4]);
                card.setRaza(p[5]);
                card.setDanio(p[6]);
                card.setCosto(p[7]);
                card.setHabilidad(p[8]);
                card.setImgPath(p[9]);

                lista.add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    private void cargarTabla(List<CardBuilder> lista) {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Nombre", "Edición", "Tipo", "Rareza", "Raza", "Daño", "Costo", "Habilidad"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

        };

        this.deckBuilder.tblCards.setModel(model);

        model = (DefaultTableModel) this.deckBuilder.tblCards.getModel();
        model.setRowCount(0);

        this.deckBuilder.tblCards.setRowHeight(25);
        this.deckBuilder.tblCards.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (CardBuilder c : lista) {
            model.addRow(new Object[]{
                c.getNombre(),
                c.getEdicion(),
                c.getTipo(),
                c.getRareza(),
                c.getRaza(),
                c.getDanio(),
                c.getCosto(),
                c.getHabilidad()
            });
        }

        sorter = new TableRowSorter<>(this.deckBuilder.tblCards.getModel());
        this.deckBuilder.tblCards.setRowSorter(sorter);

        this.deckBuilder.tblCards.getColumnModel()
                .getColumn(7)
                .setCellRenderer(new TextAreaRenderer());

        this.ajustarAnchoColumnas(PORCENTAJES_TBL_CARDS, this.deckBuilder.tblCards);
    }

    private void ajustarAnchoColumnas(double[] porcentajes, JTable table) {
        int total = table.getWidth();
        TableColumnModel modelo = table.getColumnModel();

        if (modelo.getColumnCount() > 0) {
            for (int i = 0; i < porcentajes.length; i++) {
                int ancho = (int) (total * porcentajes[i]);
                modelo.getColumn(i).setPreferredWidth(ancho);
            }
        }
    }

    private void limpiarDeck() {
        DefaultTableModel model = (DefaultTableModel) this.deckBuilder.tblMyDeck.getModel();
        model.setRowCount(0);
        this.listaMiMazo = new ArrayList<>();
    }

    private void crearPdf() {
        final int COLUMNAS = 3;
        final int FILAS = 3;
        final float CM_TO_PT = 28.3464567f;

        final float CARTA_ANCHO_CM = 5.59f;
        final float CARTA_ALTO_CM = 8.4f;
        final float SEPARACION_CM = 0.0f;

        float cartaAncho = CARTA_ANCHO_CM * CM_TO_PT;
        float cartaAlto = CARTA_ALTO_CM * CM_TO_PT;
        float separacion = SEPARACION_CM * CM_TO_PT;

        PDRectangle pageSize = PDRectangle.LETTER;

        float totalAnchoCartas = (COLUMNAS * cartaAncho) + ((COLUMNAS - 1) * separacion);
        float totalAltoCartas = (FILAS * cartaAlto) + ((FILAS - 1) * separacion);

        float margenX = (pageSize.getWidth() - totalAnchoCartas) / 2;
        float margenY = (pageSize.getHeight() - totalAltoCartas) / 2;

        try (PDDocument document = new PDDocument()) {

            PDPage page = null;
            PDPageContentStream contentStream = null;

            List<CardBuilder> listaExpandida = new ArrayList<>();

            for (CardBuilder c : this.listaMiMazo) {
                int copias = c.getCopias();

                for (int i = 0; i < copias; i++) {
                    listaExpandida.add(c);
                }
            }

            int totalCartas = listaExpandida.size();

            for (int i = 0; i < totalCartas; i++) {

                if (i % 9 == 0) {
                    if (contentStream != null) {
                        contentStream.close();
                    }

                    page = new PDPage(pageSize);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                }

                int pos = i % 9;
                int col = pos % COLUMNAS;
                int fila = pos / COLUMNAS;

                float x = margenX + (col * (cartaAncho + separacion));
                float y = pageSize.getHeight()
                        - margenY
                        - ((fila + 1) * cartaAlto)
                        - (fila * separacion);

                File imagenFile = new File(listaExpandida.get(i).getImgPath());

                if (!imagenFile.exists()) {
                    continue;
                }

                PDImageXObject img = PDImageXObject.createFromFileByContent(imagenFile, document);

                contentStream.drawImage(img, x, y, cartaAncho, cartaAlto);
            }

            if (contentStream != null) {
                contentStream.close();
            }

            document.save("F:\\xd.pdf");
            document.close();

            JOptionPane.showMessageDialog(null, "PDF generado");

        } catch (IOException ex) {
            Logger.getLogger(ControladorDeckBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
