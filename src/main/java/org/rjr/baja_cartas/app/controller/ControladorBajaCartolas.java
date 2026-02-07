package org.rjr.baja_cartas.app.controller;

import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.rjr.baja_cartas.app.ui.BajaCartas;
import org.rjr.baja_cartas.app.ui.BajaCartolas;
import org.rjr.baja_cartas.app.ui.DeckBuilder;

public class ControladorBajaCartolas {

    private final BajaCartolas bajaCartolas;

    public ControladorBajaCartolas(BajaCartolas bajaCartolas) {
        this.bajaCartolas = bajaCartolas;
    }

    public void run() {
        this.bajaCartolas.menBajaCartas.addActionListener(e -> runBajaCartas());
        this.bajaCartolas.btnDickBuilder.addActionListener(e -> {
            try {
                runDeckBuilder();
            } catch (PropertyVetoException ex) {
                Logger.getLogger(ControladorBajaCartolas.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        this.bajaCartolas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.bajaCartolas.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.bajaCartolas.setResizable(true);
        this.bajaCartolas.setLocationRelativeTo(null);
        this.bajaCartolas.setVisible(true);
    }

    private void runBajaCartas() {
        BajaCartas bajaCartas = new BajaCartas();
        bajaCartas.setMaximizable(false);
        bajaCartas.setClosable(true);
        bajaCartas.setIconifiable(true);
        ControladorBajaCartas controladorBajaCartas = new ControladorBajaCartas(bajaCartas, bajaCartolas);
        controladorBajaCartas.run();
    }

    private void runDeckBuilder() throws PropertyVetoException {
        DeckBuilder deckBuilder = new DeckBuilder();
        deckBuilder.setMaximizable(true);
        deckBuilder.setClosable(true);
        deckBuilder.setIconifiable(true);
        deckBuilder.setResizable(true);
        ControladorDeckBuilder controladorDeckBuilder = new ControladorDeckBuilder(deckBuilder, bajaCartolas);
        controladorDeckBuilder.run();
    }

}
