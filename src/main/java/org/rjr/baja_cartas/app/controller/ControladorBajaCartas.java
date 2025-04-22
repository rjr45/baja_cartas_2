package org.rjr.baja_cartas.app.controller;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import org.rjr.baja_cartas.app.ui.BajaCartas;
import org.rjr.baja_cartas.app.worker.WorkerDescarga;

public class ControladorBajaCartas {

    private final BajaCartas bajaCartas;
    private String destino;

    public ControladorBajaCartas(BajaCartas bajaCartas) {
        this.bajaCartas = bajaCartas;
        this.destino = "";
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
        this.bajaCartas.pgrEstado.setIndeterminate(true);
        this.bajaCartas.btnDescargar.setEnabled(false);
        this.bajaCartas.btnSetDestination.setEnabled(false);

        HashMap<String, String> data = this.getData();

        WorkerDescarga workerDescarga = new WorkerDescarga(data);
        workerDescarga.addPropertyChangeListener(e -> onWorkerReady(e));
        workerDescarga.execute();
    }

    private HashMap<String, String> getData() {
        HashMap<String, String> data = new HashMap<>(5);
        data.put("slug", this.bajaCartas.txtEdicion.getText());
        data.put("txt", String.valueOf(this.bajaCartas.chkTxt.isSelected()));
        data.put("xls", String.valueOf(this.bajaCartas.chkXLS.isSelected()));
        data.put("size_h", this.bajaCartas.txtTamanoH.getText());
        data.put("size_v", this.bajaCartas.txtTamanoV.getText());
        data.put("ruta", this.destino);
        return data;
    }

    private void onWorkerReady(PropertyChangeEvent e) {
        if (e.getNewValue().equals(SwingWorker.StateValue.DONE)) {
            this.bajaCartas.pgrEstado.setIndeterminate(false);
            this.bajaCartas.btnDescargar.setEnabled(true);
            this.bajaCartas.btnSetDestination.setEnabled(true);
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

}
