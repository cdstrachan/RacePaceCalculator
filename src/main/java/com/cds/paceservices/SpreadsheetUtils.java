package com.cds.paceservices;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalTime;

import com.cds.paceservices.TO.PaceChartInstanceTO;
import com.cds.paceservices.TO.PaceChartTO;
import com.cds.paceservices.TO.SplitTO;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpreadsheetUtils {

	private static final Logger log = LoggerFactory.getLogger(PaceCalculatorController.class);

	public String CreateSpreadsheet(PaceChartTO paceChart) throws IOException {
		// create a spreadsheet
		log.info("createspreadsheet - about to create spreadsheet");

		XSSFWorkbook wb = new XSSFWorkbook();
		LocalTime lastFinishTime = null;
		int colOffset = 1;
		XSSFSheet sheet = null;

		for (PaceChartInstanceTO instanceTO : paceChart.getPaceChartInstances()) {

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

			// start creating the spreadsheet
			
			// setup colours
			//254, 233, 219 - orange
			byte[] rgb = new byte[3];
			rgb[0] = (byte) 254; // red
			rgb[1] = (byte) 233; // green
			rgb[2] = (byte) 219; // blue
			XSSFColor colorHeader = new XSSFColor(rgb, new DefaultIndexedColorMap());

			//197, 217, 238 - blue
			rgb[0] = (byte) 197; // red
			rgb[1] = (byte) 217; // green
			rgb[2] = (byte) 238; // blue
			XSSFColor colorBody = new XSSFColor(rgb, new DefaultIndexedColorMap());

			//255, 255, 255 - white
			rgb[0] = (byte) 255; // red
			rgb[1] = (byte) 255; // green
			rgb[2] = (byte) 255; // blue
			XSSFColor colorClean = new XSSFColor(rgb, new DefaultIndexedColorMap());

			//231, 239, 248 - grey - odd numbering
			rgb[0] = (byte) 231; // red
			rgb[1] = (byte) 239; // green
			rgb[2] = (byte) 248; // blue
			XSSFColor colorCleanOdd = new XSSFColor(rgb, new DefaultIndexedColorMap());
			
			// setup the font
			String defaultFont = "Calibri";
			short defaultFontSize = 10;
			XSSFFont font = wb.createFont();
			font.setFontHeightInPoints(defaultFontSize);
			font.setFontName(defaultFont);

			XSSFFont fontBold = wb.createFont();
			fontBold.setFontHeightInPoints(defaultFontSize);
			fontBold.setFontName(defaultFont);
			fontBold.setBold(true);

			// setup cell styles - title main
			XSSFCellStyle styleTitleMain = wb.createCellStyle();
			styleTitleMain.setFont(fontBold);
			styleTitleMain.setBorderBottom(BorderStyle.THIN);
			styleTitleMain.setBorderTop(BorderStyle.THIN);
			styleTitleMain.setBorderLeft(BorderStyle.THIN);
			styleTitleMain.setBorderRight(BorderStyle.THIN);
			styleTitleMain.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			styleTitleMain.setTopBorderColor(IndexedColors.BLACK.getIndex());
			styleTitleMain.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			styleTitleMain.setRightBorderColor(IndexedColors.BLACK.getIndex());
			styleTitleMain.setFillForegroundColor(colorBody);
			styleTitleMain.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			styleTitleMain.setWrapText(false);
			styleTitleMain.setAlignment(HorizontalAlignment.LEFT);
			styleTitleMain.setVerticalAlignment(VerticalAlignment.TOP);

			XSSFCellStyle styleTitleSub = wb.createCellStyle();
			styleTitleSub.setFont(font);
			styleTitleSub.setBorderBottom(BorderStyle.THIN);
			styleTitleSub.setBorderTop(BorderStyle.THIN);
			styleTitleSub.setBorderLeft(BorderStyle.THIN);
			styleTitleSub.setBorderRight(BorderStyle.THIN);
			styleTitleSub.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			styleTitleSub.setTopBorderColor(IndexedColors.BLACK.getIndex());
			styleTitleSub.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			styleTitleSub.setRightBorderColor(IndexedColors.BLACK.getIndex());
		
			styleTitleSub.setFillForegroundColor(colorHeader);
			styleTitleSub.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			styleTitleSub.setWrapText(true);
			styleTitleSub.setAlignment(HorizontalAlignment.LEFT);
			styleTitleSub.setVerticalAlignment(VerticalAlignment.TOP);

			XSSFCellStyle styleRight = wb.createCellStyle();
			styleRight = wb.createCellStyle();
			styleRight.setFont(fontBold);
			styleRight.setBorderBottom(BorderStyle.THIN);
			styleRight.setBorderTop(BorderStyle.THIN);
			styleRight.setBorderLeft(BorderStyle.THIN);
			styleRight.setBorderRight(BorderStyle.THIN);
			styleRight.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			styleRight.setTopBorderColor(IndexedColors.BLACK.getIndex());
			styleRight.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			styleRight.setRightBorderColor(IndexedColors.BLACK.getIndex());
			styleRight.setFillForegroundColor(colorBody);
			styleRight.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			styleRight.setWrapText(true);
			styleRight.setAlignment(HorizontalAlignment.LEFT);
			styleRight.setVerticalAlignment(VerticalAlignment.TOP);

			XSSFCellStyle styleClean = wb.createCellStyle();
			styleClean = wb.createCellStyle();
			styleClean.setFont(font);
			styleClean.setBorderBottom(BorderStyle.THIN);
			styleClean.setBorderTop(BorderStyle.THIN);
			styleClean.setBorderLeft(BorderStyle.THIN);
			styleClean.setBorderRight(BorderStyle.THIN);
			styleClean.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			styleClean.setTopBorderColor(IndexedColors.BLACK.getIndex());
			styleClean.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			styleClean.setRightBorderColor(IndexedColors.BLACK.getIndex());
			styleClean.setFillForegroundColor(colorClean);
			styleClean.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			styleClean.setWrapText(true);
			styleClean.setAlignment(HorizontalAlignment.LEFT);
			styleClean.setVerticalAlignment(VerticalAlignment.TOP);

			XSSFCellStyle styleCleanOdd = wb.createCellStyle();
			styleCleanOdd = wb.createCellStyle();
			styleCleanOdd.setFont(font);
			styleCleanOdd.setBorderBottom(BorderStyle.THIN);
			styleCleanOdd.setBorderTop(BorderStyle.THIN);
			styleCleanOdd.setBorderLeft(BorderStyle.THIN);
			styleCleanOdd.setBorderRight(BorderStyle.THIN);
			styleCleanOdd.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			styleCleanOdd.setTopBorderColor(IndexedColors.BLACK.getIndex());
			styleCleanOdd.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			styleCleanOdd.setRightBorderColor(IndexedColors.BLACK.getIndex());
			styleCleanOdd.setFillForegroundColor(colorCleanOdd);
			styleCleanOdd.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			styleCleanOdd.setWrapText(true);
			styleCleanOdd.setAlignment(HorizontalAlignment.LEFT);
			styleCleanOdd.setVerticalAlignment(VerticalAlignment.TOP);

			int rowOffset = 0;
			XSSFRow row;
			XSSFCell cell;
			rowOffset++;

			log.info("createspreadsheet - creating records");
			// start populating the spreadsheet
			row = createRow(sheet, rowOffset);
			cell = CreateCell(styleTitleMain, row, colOffset, "Race");
			cell = CreateCell(styleTitleSub, row, colOffset + 1,
					paceChart.getRaceName() + " " + (int) instanceTO.getFade() + "% fade");
			cell = CreateCell(styleTitleSub, row, colOffset + 2, "");
			cell = CreateCell(styleTitleSub, row, colOffset + 3, "");
			cell = CreateCell(styleTitleSub, row, colOffset + 4, "");
			rowOffset++;

			row = createRow(sheet, rowOffset);
			cell = CreateCell(styleTitleMain, row, colOffset, "Time");
			cell = CreateCell(styleTitleSub, row, colOffset + 1,
					PaceUtils.formatTime(instanceTO.getPlannedRaceTime(), true));
			cell = CreateCell(styleTitleMain, row, colOffset + 3, "Start Delay");
			cell = CreateCell(styleTitleSub, row, colOffset + 4,
					PaceUtils.formatTime(paceChart.getStartDelay(), false));
			cell = CreateCell(styleTitleSub, row, colOffset + 2, "");
			rowOffset++;

			row = createRow(sheet, rowOffset);
			cell = CreateCell(styleTitleMain, row, colOffset, "Mov.");
			cell = CreateCell(styleTitleSub, row, colOffset + 1,
					PaceUtils.formatTime(instanceTO.getAverageMovingPace(), false));
			cell = CreateCell(styleTitleMain, row, colOffset + 3, "Avg.");
			cell = CreateCell(styleTitleSub, row, colOffset + 4,
					PaceUtils.formatTime(instanceTO.getAverageEndToEndPace(), false));
			cell = CreateCell(styleTitleSub, row, colOffset + 2, "");
			rowOffset++;

			// race name merge
			String first = PaceUtils.getCharForNumber(colOffset + 1);
			String second = PaceUtils.getCharForNumber(colOffset + 4);
			String range = "$" + first + "$" + 2 + ":$" + second + "$" + 2;
			sheet.addMergedRegion(CellRangeAddress.valueOf(range));

			// time merge
			first = PaceUtils.getCharForNumber(colOffset + 1);
			second = PaceUtils.getCharForNumber(colOffset + 2);
			range = "$" + first + "$" + 3 + ":$" + second + "$" + 3;
			sheet.addMergedRegion(CellRangeAddress.valueOf(range));

			// moving pace merge
			first = PaceUtils.getCharForNumber(colOffset + 1);
			second = PaceUtils.getCharForNumber(colOffset + 2);
			range = "$" + first + "$" + 4 + ":$" + second + "$" + 4;
			sheet.addMergedRegion(CellRangeAddress.valueOf(range));

			row = createRow(sheet, rowOffset);
			// row.setHeightInPoints(30);
			cell = CreateCell(styleTitleMain, row, colOffset, "Split");
			cell = CreateCell(styleTitleMain, row, colOffset + 1, "Time");
			cell = CreateCell(styleTitleMain, row, colOffset + 2, "Pace");
			cell = CreateCell(styleTitleMain, row, colOffset + 3, "Elapsed");
			cell = CreateCell(styleTitleMain, row, colOffset + 4, "Elev.");

			sheet.setColumnWidth(colOffset + 0, 5 * 256); // split number
			sheet.setColumnWidth(colOffset + 1, 6 * 256); // split time
			sheet.setColumnWidth(colOffset + 2, 6 * 256); // split pace
			sheet.setColumnWidth(colOffset + 3, 8 * 256); // elapsed
			sheet.setColumnWidth(colOffset + 4, 5 * 256); // elevation

			rowOffset++;
			double distance = 0;
			// boolean isOddRow = false;
			for (SplitTO raceSplit : instanceTO.getRaceSplits()) {

				XSSFCellStyle cellStype;

				row = createRow(sheet, rowOffset);
				distance += raceSplit.getSplitDistance();
				if (distance % 5 == 0)
					cellStype = styleCleanOdd;
				else
					cellStype = styleClean;

				if (Math.ceil(distance) > distance) // we are at a fraction - the last split. EG 21.1
					cell = CreateCell(styleRight, row, colOffset, String.valueOf(distance));
				else
					cell = CreateCell(styleRight, row, colOffset, String.valueOf(raceSplit.getSplitNumber()));
				cell = CreateCell(cellStype, row, colOffset + 1, PaceUtils.formatTime(raceSplit.getFinalTime(), false));
				cell = CreateCell(cellStype, row, colOffset + 2, PaceUtils.formatTime(raceSplit.getFinalPace(), false));
				cell = CreateCell(cellStype, row, colOffset + 3,
						PaceUtils.formatTime(raceSplit.getFinalElapsedTime(), true));
				cell = CreateCell(cellStype, row, colOffset + 4, String.valueOf((int) raceSplit.getElevation()));
				rowOffset++;
			}
			log.info("createspreadsheet - creating records complete");
			colOffset += 6;

		}
		log.info("createspreadsheet - about to save spreadsheet");
		File excelFile = File.createTempFile("results_", ".xlsx");
		FileOutputStream excelFileStream = new FileOutputStream(excelFile);
		wb.write(excelFileStream);
		wb.close();
		excelFileStream.close();

		return excelFile.getPath();

	}

	private XSSFCell CreateCell(XSSFCellStyle styles, XSSFRow row, int col, String value) {
		XSSFCell cell = row.createCell(col);
		cell.setCellStyle(styles);
		cell.setCellValue(value);
		return cell;
	}

	// utility to get a row or create it if it does not already exist
	private XSSFRow createRow(XSSFSheet sheet, int rowOffset) {
		XSSFRow row = sheet.getRow(rowOffset);
		if (row == null)
			row = sheet.createRow(rowOffset);
		return row;
	}

}
