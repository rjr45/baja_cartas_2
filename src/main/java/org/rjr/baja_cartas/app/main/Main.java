package org.rjr.baja_cartas.app.main;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import org.rjr.baja_cartas.app.controller.ControladorBajaCartas;
import org.rjr.baja_cartas.app.ui.BajaCartas;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        BajaCartas bajaCartas = new BajaCartas();
        ControladorBajaCartas controladorBajaCartas = new ControladorBajaCartas(bajaCartas);
        controladorBajaCartas.run();
        
    }
}
