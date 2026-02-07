package org.rjr.baja_cartas.app.worker;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import javax.swing.SwingWorker;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.rjr.baja_cartas.app.controller.Util;
import org.rjr.baja_cartas.app.enums.CardType;
import org.rjr.baja_cartas.app.enums.Race;
import org.rjr.baja_cartas.app.enums.Rarity;
import org.rjr.baja_cartas.app.model.Card;

public class WorkerXLS extends SwingWorker<Void, Void> {

    private final HashMap<String, String> data;
    private final List<Card> cardList;

    public WorkerXLS(HashMap<String, String> data, List<Card> cardList) {
        this.data = data;
        this.cardList = cardList;
    }

    @Override
    protected Void doInBackground() throws Exception {
        boolean generaXLS = this.data.get("xlsx").equals("true");

        if (generaXLS) {
            String slug = this.data.get("slug");
            String destino = this.data.get("ruta");

            int rowCount = 0;

            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet(slug);

            XSSFRow header = sheet.createRow(rowCount++);
            header.createCell(0).setCellValue("Nombre");
            header.createCell(1).setCellValue("Edición");
            header.createCell(2).setCellValue("Id");
            header.createCell(3).setCellValue("Tipo");
            header.createCell(4).setCellValue("Rareza");
            header.createCell(5).setCellValue("Raza");
            header.createCell(6).setCellValue("Daño");
            header.createCell(7).setCellValue("Costo");
            header.createCell(8).setCellValue("habilidad");

            try {
                for (Card card : this.cardList) {
                    XSSFRow row = sheet.createRow(rowCount++);
                    row.createCell(0).setCellValue(card.getName());
                    row.createCell(1).setCellValue(card.getEd_slug());
                    row.createCell(2).setCellValue(card.getEdid());
                    row.createCell(3).setCellValue(CardType.fromId(String.valueOf(card.getType())).getDisplayName());
                    row.createCell(4).setCellValue(Rarity.fromId(String.valueOf(card.getRarity())).getDisplayName());
                    row.createCell(5).setCellValue(Race.fromId(String.valueOf(card.getRace())).getDisplayName());
                    row.createCell(6).setCellValue(card.getDamage());
                    row.createCell(7).setCellValue(card.getCost());
                    row.createCell(8).setCellValue(Util.limpiarTexto(card.getAbility()));
                }
            } catch (Exception ex) {
                System.out.println("X_X");
            }

            try (FileOutputStream out = new FileOutputStream(String.format("%s\\%s.xlsx", destino, slug))) {
                wb.write(out);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        return null;
    }

}
