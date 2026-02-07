package org.rjr.baja_cartas.app.main;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import org.rjr.baja_cartas.app.controller.ControladorBajaCartolas;
import org.rjr.baja_cartas.app.ui.BajaCartolas;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        BajaCartolas bajaCartolas = new BajaCartolas();
        ControladorBajaCartolas controladorBajaCartolas = new ControladorBajaCartolas(bajaCartolas);
        controladorBajaCartolas.run();
        
    }
}
