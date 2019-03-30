package com.cds.paceservices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalTime;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
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
			
			// setup the font
			String defaultFont = "Calibri";
			XSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short) 11);
			font.setFontName(defaultFont);

			XSSFFont fontBold = wb.createFont();
			fontBold.setFontHeightInPoints((short) 11);
			fontBold.setFontName(defaultFont);
			fontBold.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
			
			// setup cell styles - title main
			XSSFCellStyle styleTitleMain = wb.createCellStyle();
			styleTitleMain.setFont(fontBold);
			styleTitleMain.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			styleTitleMain.setBorderTop(XSSFCellStyle.BORDER_THIN);
			styleTitleMain.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			styleTitleMain.setBorderRight(XSSFCellStyle.BORDER_THIN);
			styleTitleMain.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			styleTitleMain.setTopBorderColor(IndexedColors.BLACK.getIndex());
			styleTitleMain.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			styleTitleMain.setRightBorderColor(IndexedColors.BLACK.getIndex());
			styleTitleMain.setFillForegroundColor(new XSSFColor(new java.awt.Color(197, 217, 238)));
			styleTitleMain.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			styleTitleMain.setWrapText(true);
			styleTitleMain.setAlignment(XSSFCellStyle.ALIGN_LEFT);
			styleTitleMain.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
			
			XSSFCellStyle styleTitleSub = wb.createCellStyle();
			styleTitleSub.setFont(font);
			styleTitleSub.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			styleTitleSub.setBorderTop(XSSFCellStyle.BORDER_THIN);
			styleTitleSub.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			styleTitleSub.setBorderRight(XSSFCellStyle.BORDER_THIN);
			styleTitleSub.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			styleTitleSub.setTopBorderColor(IndexedColors.BLACK.getIndex());
			styleTitleSub.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			styleTitleSub.setRightBorderColor(IndexedColors.BLACK.getIndex());
			styleTitleSub.setFillForegroundColor(new XSSFColor(new java.awt.Color(254, 233, 219)));
			styleTitleSub.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			styleTitleSub.setWrapText(true);
			styleTitleSub.setAlignment(XSSFCellStyle.ALIGN_LEFT);
			styleTitleSub.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
			
			XSSFCellStyle styleRight = wb.createCellStyle();
			styleRight = wb.createCellStyle();
			styleRight.setFont(fontBold);
			styleRight.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			styleRight.setBorderTop(XSSFCellStyle.BORDER_THIN);
			styleRight.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			styleRight.setBorderRight(XSSFCellStyle.BORDER_THIN);
			styleRight.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			styleRight.setTopBorderColor(IndexedColors.BLACK.getIndex());
			styleRight.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			styleRight.setRightBorderColor(IndexedColors.BLACK.getIndex());
			styleRight.setFillForegroundColor(new XSSFColor(new java.awt.Color(197, 217, 238)));
			styleRight.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			styleRight.setWrapText(true);
			styleRight.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
			styleRight.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
			

			XSSFCellStyle styleClean = wb.createCellStyle();
			styleClean = wb.createCellStyle();
			styleClean.setFont(font);
			styleClean.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			styleClean.setBorderTop(XSSFCellStyle.BORDER_THIN);
			styleClean.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			styleClean.setBorderRight(XSSFCellStyle.BORDER_THIN);
			styleClean.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			styleClean.setTopBorderColor(IndexedColors.BLACK.getIndex());
			styleClean.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			styleClean.setRightBorderColor(IndexedColors.BLACK.getIndex());
			styleClean.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 255, 255)));
			styleClean.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			styleClean.setWrapText(true);
			styleClean.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
			styleClean.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);

			XSSFCellStyle styleCleanOdd = wb.createCellStyle();
			styleCleanOdd = wb.createCellStyle();
			styleCleanOdd.setFont(font);
			styleCleanOdd.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			styleCleanOdd.setBorderTop(XSSFCellStyle.BORDER_THIN);
			styleCleanOdd.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			styleCleanOdd.setBorderRight(XSSFCellStyle.BORDER_THIN);
			styleCleanOdd.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			styleCleanOdd.setTopBorderColor(IndexedColors.BLACK.getIndex());
			styleCleanOdd.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			styleCleanOdd.setRightBorderColor(IndexedColors.BLACK.getIndex());
			styleCleanOdd.setFillForegroundColor(new XSSFColor(new java.awt.Color(231, 239, 248)));
			styleCleanOdd.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			styleCleanOdd.setWrapText(true);
			styleCleanOdd.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
			styleCleanOdd.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);

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
					PaceUtils.formatTime(instanceTO.getPlannedRaceTime(),true));
			cell = CreateCell(styleTitleMain, row, colOffset + 2, "Start Delay");
			cell = CreateCell(styleTitleSub, row, colOffset + 3,
					PaceUtils.formatTime(paceChart.getStartDelay(),false));
			cell = CreateCell(styleTitleSub, row, colOffset + 4, "");
			rowOffset++;

			row = createRow(sheet, rowOffset);
			cell = CreateCell(styleTitleMain, row, colOffset, "Mov. Pace");
			cell = CreateCell(styleTitleSub, row, colOffset + 1,
					PaceUtils.formatTime(instanceTO.getAverageMovingPace(),false));
			cell = CreateCell(styleTitleMain, row, colOffset + 2, "Avg. Pace");
			cell = CreateCell(styleTitleSub, row, colOffset + 3,
					PaceUtils.formatTime(instanceTO.getAverageEndToEndPace(),false));
			cell = CreateCell(styleTitleSub, row, colOffset + 4, "");
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
			row.setHeightInPoints(30);
			cell = CreateCell(styleTitleMain, row, colOffset, "Split");
			cell = CreateCell(styleTitleMain, row, colOffset + 1, "Time");
			cell = CreateCell(styleTitleMain, row, colOffset + 2, "Pace");
			cell = CreateCell(styleTitleMain, row, colOffset + 3, "Elapsed");
			cell = CreateCell(styleTitleMain, row, colOffset + 4, "Elev.");

			sheet.setColumnWidth(colOffset, 6 * 256); // split number
			sheet.setColumnWidth(colOffset + 1, 9 * 256); // split time
			sheet.setColumnWidth(colOffset + 2, 6 * 256); // split pace
			sheet.setColumnWidth(colOffset + 3, 9 * 256); // elapsed
			sheet.setColumnWidth(colOffset + 4, 6 * 256); // elevation

			rowOffset++;
			double distance = 0;
			boolean isOddRow = false;
			for (SplitTO raceSplit : instanceTO.getRaceSplits()) {

				XSSFCellStyle cellStype;
				if (isOddRow)
					cellStype =  styleCleanOdd;
				else
					cellStype = styleClean;

				isOddRow = !isOddRow;

				row = createRow(sheet, rowOffset);
				distance += raceSplit.getSplitDistance();
				if (Math.ceil(distance) > distance) // we are at a fraction - the last split. EG 21.1
					cell = CreateCell(styleRight, row, colOffset, String.valueOf(distance));
				else
					cell = CreateCell(styleRight, row, colOffset,
							String.valueOf(raceSplit.getSplitNumber()));
				cell = CreateCell(cellStype, row, colOffset + 1,
						PaceUtils.formatTime(raceSplit.getFinalTime(),false));
				cell = CreateCell(cellStype, row, colOffset + 2,
						PaceUtils.formatTime(raceSplit.getFinalPace(),false));
				cell = CreateCell(cellStype, row, colOffset + 3,
						PaceUtils.formatTime(raceSplit.getFinalElapsedTime(),true));
				cell = CreateCell(cellStype, row, colOffset + 4,
						String.valueOf((int) raceSplit.getElevation()));
				rowOffset++;
			}
			log.info("createspreadsheet - creating records complete");
			colOffset += 6;

		}
		log.info("createspreadsheet - About to create file");
		File excelFile = File.createTempFile("results_", ".xlsx");
		FileOutputStream excelFileStream = new FileOutputStream(excelFile);
		wb.write(excelFileStream);
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
