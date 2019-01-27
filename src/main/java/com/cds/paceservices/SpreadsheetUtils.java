package com.cds.paceservices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

public class SpreadsheetUtils {
	
	private static final Logger log = LoggerFactory.getLogger(PaceCalculatorController.class);
	
	public String CreateSpreadsheet(PaceChartTO paceChart) throws IOException {
		// create a spreadsheet

		Workbook wb = new XSSFWorkbook();
		int colOffset = 1;
		
		
		for (PaceChartInstanceTO instanceTO: paceChart.paceChartInstances) {
			
			
			// create the sheet name
			// TODO: Here we put a single spreadsheet per page; rather put all the time-based spreadsheets together
			String sheetName = instanceTO.getPlannedRaceTime().toString().replace(":", "m") + "s " + instanceTO.getFade() + "%";
			// todo make the fade loose the fraction part
			Sheet sheet = wb.createSheet(sheetName);
			
			
			// start creating the actual spreadsheet
			// TODO: maybe refactor into a different function
			  Map<String, CellStyle> styles = createStyles(wb);
		        int rowOffset = 0;
		        Row row;
		        Cell cell;
		        rowOffset++;

		        
		        // start populating the spreadsheet
		        row = createRow(sheet, rowOffset);
		        cell = CreateCell(styles, row, "styleTitle", colOffset, "Race");
		        cell = CreateCell(styles, row, "styleSub", colOffset + 2, paceChart.getRaceName());
		        cell = CreateCell(styles, row, "styleTitle", colOffset + 1, "");
		        cell = CreateCell(styles, row, "styleTitle", colOffset + 3, "");
		        cell = CreateCell(styles, row, "styleTitle", colOffset + 4, "");
		        rowOffset++;
		        
		        
		        
		}
		log.info("About to create file");
        File excelFile = File.createTempFile("results_", ".xlsx");
        FileOutputStream excelFileStream = new FileOutputStream(excelFile);
        wb.write(excelFileStream);
        excelFileStream.close();
		
		return excelFile.getPath();

	}
	
	
	
	
	private Cell CreateCell(Map<String, CellStyle> styles, Row row, String cellStyle,int col, String value) {
		Cell cell = row.createCell(col);
		cell.setCellStyle(styles.get(cellStyle));
		cell.setCellValue(value);
		return cell;
	}
	
	// utility to get a rol or create it if it does not already exist
	private Row createRow(Sheet sheet,int rowOffset)
	{
		Row row = sheet.getRow(rowOffset);
		if (row == null) row = sheet.createRow(rowOffset);
		return row;		
	}
	
	
	private static Map<String, CellStyle> createStyles(Workbook wb){
		String defaultFont = "Calibri";
        Map<String, CellStyle> styles = new HashMap<>();
        
        // setup the font
        Font font = wb.createFont();
        font.setFontHeightInPoints((short)12);
        font.setFontName(defaultFont);
        
        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());        
        style.setFillForegroundColor(IndexedColors.TAN.getIndex());
        style.setWrapText(true);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        styles.put("styleTitle", style);
        
        style = wb.createCellStyle();
        style.setFont(font);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());        
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setWrapText(true);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        styles.put("styleLeftAligned", style);

        style = wb.createCellStyle();
        style.setFont(font);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());        
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setWrapText(true);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        styles.put("styleRightAligned", style);
        
        style = wb.createCellStyle();
        style.setFont(font);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());        
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setWrapText(true);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        styles.put("styleCentreAligned", style);
        
        style = wb.createCellStyle();
        style.setFont(font);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());        
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setWrapText(true);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        styles.put("styleSub", style);
        
        style = wb.createCellStyle();
        style.setFont(font);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());        
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        styles.put("styleClean", style);

        return styles;
    }

}
