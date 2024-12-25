package com.wealth.demo.util;

import com.wealth.demo.model.dto.WealthDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelExporter {

    /**
     * 將收支記錄導出為 Excel 文件
     *
     * @param records 收支記錄列表
     * @return Excel 文件的字節數組
     * @throws IOException 文件操作異常
     */
    public static byte[] exportToExcel(List<WealthDTO> records) throws IOException {
        if (records == null || records.isEmpty()) {
            throw new IllegalArgumentException("沒有可用的數據來導出 Excel 文件");
        }

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Wealth Records");

            // 標題樣式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // 金額樣式
            CellStyle amountStyle = workbook.createCellStyle();
            amountStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

            // 日期樣式
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.createDataFormat().getFormat("yyyy-MM-dd HH:mm"));

            // 添加表頭
            Row headerRow = sheet.createRow(0);
            String[] headers = {"日期", "類型", "金額", "備註"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 添加數據
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            int rowIndex = 1;
            for (WealthDTO record : records) {
                Row row = sheet.createRow(rowIndex++);

                Cell dateCell = row.createCell(0);
                dateCell.setCellValue(record.getTimestamp().format(formatter));
                dateCell.setCellStyle(dateStyle);

                Cell typeCell = row.createCell(1);
                typeCell.setCellValue(record.getType().equalsIgnoreCase("INCOME") ? "收入" : "支出");

                Cell amountCell = row.createCell(2);
                amountCell.setCellValue(record.getAmount());
                amountCell.setCellStyle(amountStyle);

                Cell noteCell = row.createCell(3);
                noteCell.setCellValue(record.getNote() != null ? record.getNote() : "");
            }

            // 自動調整列寬
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 將數據寫入字節數組
            workbook.write(outputStream);

            return outputStream.toByteArray();
        }
    }
}
