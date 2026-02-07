package org.rjr.baja_cartas.app.ui;

public class BajaCartolas extends javax.swing.JFrame {

    public BajaCartolas() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dskMain = new javax.swing.JDesktopPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        menMain = new javax.swing.JMenu();
        menBajaCartas = new javax.swing.JMenuItem();
        btnDickBuilder = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(800, 600));
        setMinimumSize(new java.awt.Dimension(800, 600));

        javax.swing.GroupLayout dskMainLayout = new javax.swing.GroupLayout(dskMain);
        dskMain.setLayout(dskMainLayout);
        dskMainLayout.setHorizontalGroup(
            dskMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );
        dskMainLayout.setVerticalGroup(
            dskMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 577, Short.MAX_VALUE)
        );

        menMain.setText("BajaCartolas");

        menBajaCartas.setText("Bajar Cartolas");
        menMain.add(menBajaCartas);

        btnDickBuilder.setText("Dick Builder");
        menMain.add(btnDickBuilder);

        jMenuBar1.add(menMain);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dskMain)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dskMain, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JMenuItem btnDickBuilder;
    public javax.swing.JDesktopPane dskMain;
    private javax.swing.JMenuBar jMenuBar1;
    public javax.swing.JMenuItem menBajaCartas;
    public javax.swing.JMenu menMain;
    // End of variables declaration//GEN-END:variables
}
