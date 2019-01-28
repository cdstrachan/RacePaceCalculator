package com.cds.paceservices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalTime;
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

public class SpreadsheetUtils {

	private static final Logger log = LoggerFactory.getLogger(PaceCalculatorController.class);

	public String CreateSpreadsheet(PaceChartTO paceChart) throws IOException {
		// create a spreadsheet
		log.info("createspreadsheet - about to create spreadsheet");
		Workbook wb = new XSSFWorkbook();
		LocalTime lastFinishTime = null;
		int colOffset = 1;
		Sheet sheet = null;

		for (PaceChartInstanceTO instanceTO : paceChart.paceChartInstances) {

			log.info("createspreadsheet - new finish time:  " + instanceTO.getPlannedRaceTime().toString());

			// new spreadsheet when the finish time changes
			if (lastFinishTime != instanceTO.getPlannedRaceTime()) {
				// create the sheet name
				String sheetName = instanceTO.getPlannedRaceTime().toString().replace(":", "m") + "s ";
				log.info("createspreadsheet - spreatsheet is called: " + sheetName);

				sheet = wb.createSheet(sheetName);
				lastFinishTime = instanceTO.getPlannedRaceTime();
				colOffset = 1;
			}

			// start creating the actual spreadsheet
			Map<String, CellStyle> styles = createStyles(wb);
			int rowOffset = 0;
			Row row;
			Cell cell;
			rowOffset++;

			log.info("createspreadsheet - creating records");
			// start populating the spreadsheet
			row = createRow(sheet, rowOffset);
			cell = CreateCell(styles, row, "styleTitleMain", colOffset, "Race");
			cell = CreateCell(styles, row, "styleTitleSub", colOffset + 1,
					paceChart.getRaceName() + " " + (int) instanceTO.getFade() + "% fade");
			cell = CreateCell(styles, row, "styleTitleSub", colOffset + 2, "");
			cell = CreateCell(styles, row, "styleTitleSub", colOffset + 3, "");
			cell = CreateCell(styles, row, "styleTitleSub", colOffset + 4, "");
			rowOffset++;

			row = createRow(sheet, rowOffset);
			cell = CreateCell(styles, row, "styleTitleMain", colOffset, "Time");
			cell = CreateCell(styles, row, "styleTitleSub", colOffset + 1,
					PaceUtils.formatTime(instanceTO.getPlannedRaceTime(),true));
			cell = CreateCell(styles, row, "styleTitleMain", colOffset + 2, "Start Delay");
			cell = CreateCell(styles, row, "styleTitleSub", colOffset + 3,
					PaceUtils.formatTime(paceChart.getStartDelay(),false));
			cell = CreateCell(styles, row, "styleTitleSub", colOffset + 4, "");
			rowOffset++;

			row = createRow(sheet, rowOffset);
			cell = CreateCell(styles, row, "styleTitleMain", colOffset, "Moving Pace");
			cell = CreateCell(styles, row, "styleTitleSub", colOffset + 1,
					PaceUtils.formatTime(instanceTO.getAverageMovingPace(),false));
			cell = CreateCell(styles, row, "styleTitleMain", colOffset + 2, "Avg. Pace");
			cell = CreateCell(styles, row, "styleTitleSub", colOffset + 3,
					PaceUtils.formatTime(instanceTO.getAverageEndToEndPace(),false));
			cell = CreateCell(styles, row, "styleTitleSub", colOffset + 4, "");
			rowOffset++;

			String first = PaceUtils.getCharForNumber(colOffset + 1);
			String second = PaceUtils.getCharForNumber(colOffset + 4);
			String range = "$" + first + "$" + 2 + ":$" + second + "$" + 2;
			sheet.addMergedRegion(CellRangeAddress.valueOf(range));

			first = PaceUtils.getCharForNumber(colOffset + 3);
			second = PaceUtils.getCharForNumber(colOffset + 4);
			range = "$" + first + "$" + 3 + ":$" + second + "$" + 3;
			sheet.addMergedRegion(CellRangeAddress.valueOf(range));

			first = PaceUtils.getCharForNumber(colOffset + 3);
			second = PaceUtils.getCharForNumber(colOffset + 4);
			range = "$" + first + "$" + 4 + ":$" + second + "$" + 4;
			sheet.addMergedRegion(CellRangeAddress.valueOf(range));

			row = createRow(sheet, rowOffset);
			row.setHeightInPoints(35);
			cell = CreateCell(styles, row, "styleTitleMain", colOffset, "Distance");
			cell = CreateCell(styles, row, "styleTitleMain", colOffset + 1, "Split time");
			cell = CreateCell(styles, row, "styleTitleMain", colOffset + 2, "Elapsed");
			cell = CreateCell(styles, row, "styleTitleMain", colOffset + 3, "Pace");
			cell = CreateCell(styles, row, "styleTitleMain", colOffset + 4, "Elev.");

			sheet.setColumnWidth(colOffset, 12 * 256); // split number
			sheet.setColumnWidth(colOffset + 1, 10 * 256); // split time
			sheet.setColumnWidth(colOffset + 2, 10 * 256); // elapsed time
			sheet.setColumnWidth(colOffset + 3, 10 * 256); // pace
			sheet.setColumnWidth(colOffset + 4, 8 * 256); // tel

			rowOffset++;
			double distance = 0;
			boolean isOddRow = false;
			for (SplitTO raceSplit : instanceTO.getRaceSplits()) {

				String cellStype;
				if (isOddRow)
					cellStype = "styleCleanOdd";
				else
					cellStype = "styleClean";

				isOddRow = !isOddRow;

				row = createRow(sheet, rowOffset);
				distance += raceSplit.getSplitDistance();
				if (Math.ceil(distance) > distance) // we are at a fraction - the last split. EG 21.1
					cell = CreateCell(styles, row, "styleRightAligned", colOffset, String.valueOf(distance));
				else
					cell = CreateCell(styles, row, "styleRightAligned", colOffset,
							String.valueOf(raceSplit.getSplitNumber()));
				cell = CreateCell(styles, row, cellStype, colOffset + 1,
						PaceUtils.formatTime(raceSplit.getFinalPace(),false));
				cell = CreateCell(styles, row, cellStype, colOffset + 2,
						PaceUtils.formatTime(raceSplit.getFinalTime(),false));
				cell = CreateCell(styles, row, cellStype, colOffset + 3,
						PaceUtils.formatTime(raceSplit.getFinalElapsedTime(),true));
				cell = CreateCell(styles, row, cellStype, colOffset + 4,
						String.valueOf((int) raceSplit.getElevation()));
				rowOffset++;
			}
			log.info("createspreadsheet - creating records complete");
			colOffset += 6;

		}
		log.info("About to create file");
		File excelFile = File.createTempFile("results_", ".xlsx");
		FileOutputStream excelFileStream = new FileOutputStream(excelFile);
		wb.write(excelFileStream);
		excelFileStream.close();

		return excelFile.getPath();

	}

	private Cell CreateCell(Map<String, CellStyle> styles, Row row, String cellStyle, int col, String value) {
		Cell cell = row.createCell(col);
		cell.setCellStyle(styles.get(cellStyle));
		cell.setCellValue(value);
		return cell;
	}

	// utility to get a row or create it if it does not already exist
	private Row createRow(Sheet sheet, int rowOffset) {
		Row row = sheet.getRow(rowOffset);
		if (row == null)
			row = sheet.createRow(rowOffset);
		return row;
	}

	private static Map<String, CellStyle> createStyles(Workbook wb) {
		String defaultFont = "Calibri";
		Map<String, CellStyle> styles = new HashMap<>();

		// setup the font
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 12);
		font.setFontName(defaultFont);

		Font fontBold = wb.createFont();
		fontBold.setFontHeightInPoints((short) 12);
		fontBold.setFontName(defaultFont);
		fontBold.setBoldweight(Font.BOLDWEIGHT_BOLD);
		
		CellStyle style = wb.createCellStyle();
		style.setFont(fontBold);
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
		styles.put("styleTitleMain", style);

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
		style.setFillForegroundColor(IndexedColors.TAN.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setWrapText(true);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		styles.put("styleTitleSub", style);

		style = wb.createCellStyle();
		style.setFont(fontBold);
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
		style.setAlignment(CellStyle.ALIGN_RIGHT);
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
		style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setWrapText(true);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		styles.put("styleClean", style);

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
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setWrapText(true);
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		styles.put("styleCleanOdd", style);

		return styles;
	}
}
