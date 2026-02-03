package org.rjr.baja_cartas.app.controller;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.rjr.baja_cartas.app.model.Card;
import org.rjr.baja_cartas.app.ui.BajaCartas;
import org.rjr.baja_cartas.app.worker.WorkerCardList;
import org.rjr.baja_cartas.app.worker.WorkerDescarga;
import org.rjr.baja_cartas.app.worker.WorkerTXT;
import org.rjr.baja_cartas.app.worker.WorkerXLS;

public class ControladorBajaCartas {

    private final BajaCartas bajaCartas;
    private String destino;
    private boolean workerDescargaDone;
    private boolean workerTxtDone;
    private boolean workerXlsDone;
    private List<Card> cardList;
    private WorkerCardList workerCardList;
    private WorkerDescarga workerDescarga;
    private WorkerTXT workerTXT;
    private WorkerXLS workerXLS;

    private static final DateTimeFormatter LOG_TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public ControladorBajaCartas(BajaCartas bajaCartas) {
        this.bajaCartas = bajaCartas;
        this.destino = "";
        this.workerDescargaDone = true;
        this.workerTxtDone = true;
        this.workerXlsDone = true;
        this.cardList = new ArrayList<>();
        this.workerCardList = null;
        this.workerDescarga = null;
        this.workerTXT = null;
        this.workerXLS = null;
    }

    public void run() {
        this.bajaCartas.btnSetDestination.addActionListener(e -> setDestino());
        this.bajaCartas.btnDescargar.addActionListener(e -> descargar());
        this.bajaCartas.txtEdicion.addFocusListener(this.focusLostTxtEdicion());
        this.bajaCartas.setLocationRelativeTo(null);
        this.bajaCartas.setVisible(true);
    }

    private void setDestino() {
        String desktop = System.getProperty("user.home");
        JFileChooser chooser = new JFileChooser(new File(String.format("%s/desktop", desktop)));
        chooser.setDialogTitle("Selecciona Ruta Destino");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(this.bajaCartas);
        if (result == JFileChooser.APPROVE_OPTION) {
            destino = chooser.getSelectedFile().getAbsolutePath();
            this.bajaCartas.txtRuta.setText(destino);
        }
        this.bajaCartas.btnDescargar.setEnabled(bajaCartas.txtEdicion.getText().length() > 0 && bajaCartas.txtRuta.getText().length() > 0);
    }

    private void descargar() {
        this.manejarUi(true);
        bajaCartas.pgrEstado.setIndeterminate(false);
        bajaCartas.pgrEstado.setMinimum(0);
        bajaCartas.pgrEstado.setMaximum(this.cardList.size());
        bajaCartas.pgrEstado.setValue(0);
        bajaCartas.pgrEstado.setStringPainted(true);
        bajaCartas.pgrEstado.setString("0.00%");
        this.workerDescargaDone = false;
        this.workerTxtDone = false;
        this.workerXlsDone = false;
        HashMap<String, String> data = this.getData();
        workerCardList = new WorkerCardList(data);
        workerCardList.addPropertyChangeListener(e -> onGetCardsWorkerReady(e));
        workerCardList.execute();
    }

    private void manejarUi(boolean deshabilitar) {
        this.bajaCartas.pgrEstado.setIndeterminate(deshabilitar);
        this.bajaCartas.btnDescargar.setEnabled(!deshabilitar);
        this.bajaCartas.btnSetDestination.setEnabled(!deshabilitar);
        this.bajaCartas.txtEdicion.setEnabled(!deshabilitar);
        this.bajaCartas.chkTxt.setEnabled(!deshabilitar);
        this.bajaCartas.chkXLS.setEnabled(!deshabilitar);
        this.bajaCartas.txtTamanoH.setEnabled(!deshabilitar);
        this.bajaCartas.txtTamanoV.setEnabled(!deshabilitar);
    }

    private HashMap<String, String> getData() {
        HashMap<String, String> data = new HashMap<>(5);
        data.put("slug", this.bajaCartas.txtEdicion.getText());
        data.put("txt", String.valueOf(this.bajaCartas.chkTxt.isSelected()));
        data.put("xlsx", String.valueOf(this.bajaCartas.chkXLS.isSelected()));
        data.put("size_h", this.bajaCartas.txtTamanoH.getText());
        data.put("size_v", this.bajaCartas.txtTamanoV.getText());
        data.put("ruta", this.destino);
        return data;
    }

    private void onGetCardsWorkerReady(PropertyChangeEvent e) {
        if (e.getNewValue().equals(SwingWorker.StateValue.DONE)) {
            try {
                this.cardList = this.workerCardList.get();
                bajaCartas.pgrEstado.setMaximum(this.cardList.size());

                workerDescarga = new WorkerDescarga(this.getData(), cardList, this::log);
                workerTXT = new WorkerTXT(this.getData(), cardList);
                workerXLS = new WorkerXLS(this.getData(), cardList);

                workerDescarga.addPropertyChangeListener(downloadWorker -> onDownloadCardsWorkerReady(downloadWorker));
                workerTXT.addPropertyChangeListener(workerTxt -> onTxtWorkerReady(workerTxt));
                workerXLS.addPropertyChangeListener(workerXls -> onWorkerXlsReady(workerXls));

                workerDescarga.execute();
                workerTXT.execute();
                workerXLS.execute();
            } catch (InterruptedException | ExecutionException ex) {
                this.workerDescargaDone = true;
                this.workerTxtDone = true;
                this.workerXlsDone = true;
                this.habilitarUi();
                System.out.println(ex.getMessage());
                JOptionPane.showMessageDialog(this.bajaCartas, "Error xd", "Info", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDownloadCardsWorkerReady(PropertyChangeEvent e) {
        if ("cartaActual".equals(e.getPropertyName())) {
            int actual = (Integer) e.getNewValue();
            double porcentaje = (((double) actual) / this.cardList.size()) * 100;
            bajaCartas.pgrEstado.setValue(actual);
            bajaCartas.pgrEstado.setString(String.format("%.2f %%", porcentaje));
        }

        if (e.getNewValue().equals(SwingWorker.StateValue.DONE)) {
            this.workerDescargaDone = true;
            this.habilitarUi();
        }
    }

    private void onTxtWorkerReady(PropertyChangeEvent e) {
        if (e.getNewValue().equals(SwingWorker.StateValue.DONE)) {
            this.workerTxtDone = true;
            this.habilitarUi();
        }
    }

    private void onWorkerXlsReady(PropertyChangeEvent e) {
        if (e.getNewValue().equals(SwingWorker.StateValue.DONE)) {
            this.workerXlsDone = true;
            this.habilitarUi();
        }
    }

    private void habilitarUi() {
        if (this.workerDescargaDone && this.workerTxtDone && this.workerXlsDone) {
            this.manejarUi(false);
            JOptionPane.showMessageDialog(this.bajaCartas, "Todo Listo", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private FocusAdapter focusLostTxtEdicion() {
        return new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (bajaCartas.txtEdicion.getText().length() > 0 && bajaCartas.txtRuta.getText().length() > 0) {
                    bajaCartas.btnDescargar.setEnabled(true);
                } else {
                    bajaCartas.btnDescargar.setEnabled(false);
                }
            }
        };
    }

    private void log(String msg) {
        String hora = LocalTime.now().format(LOG_TIME);
        bajaCartas.txtLogDescarga.append(String.format("[%s] %s%n", hora, msg));
        bajaCartas.txtLogDescarga.setCaretPosition(bajaCartas.txtLogDescarga.getDocument().getLength());
    }

}
