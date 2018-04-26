package com.cds.paceservices;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaceCalculatorController {
	
	private static final Logger log = LoggerFactory.getLogger(PaceCalculatorController.class);

    @RequestMapping(value = "/pacechart", method = RequestMethod.GET)
	public PaceChartTO createPaceChart() {
    	
    	log.info("Received a REST request");
    	double[] elevation;
		double [] manualWeighting;
	
		// TODO: get the parameters from the screen
		PaceChartTO paceChartTO =  new PaceChartTO();
	    
		double distance = 10;
        
        // setup race distance
		paceChartTO.setDistance(distance);        
        
        // setup elevation profiles
        // +1 is just to make user input easier
		elevation = new double[(int) Math.ceil(distance) + 1];  // create elevation profiles
		elevation[1] = 10;
		elevation[2] = 20;
		elevation[3] = 30;
		elevation[4] = 20;
		elevation[5] = 10;
		elevation[6] = 20;
		elevation[7] = 3;
		elevation[8] = 4;
		elevation[9] = 1;
		elevation[10] = -20;
		
		paceChartTO.setElevation(elevation);
		
		int baseWeightDelta = 0; //tweaking the hills
		
		// setup manual weighting
		
		// TODO check if the elevation is  >1, and then allow for a base weighting delta of hills
		manualWeighting = new double[(int) Math.ceil(distance) + 1];  // create elevation profiles
		manualWeighting [1] = 100;
		manualWeighting [2] = 100;
		manualWeighting [3] = 100 + baseWeightDelta;
		manualWeighting	[4] = 100 + baseWeightDelta;
		manualWeighting [5] = 100 + baseWeightDelta;
		manualWeighting [6] = 100;
		manualWeighting [7] = 100;
		manualWeighting [8] = 100;
		manualWeighting [9] = 100 + baseWeightDelta;
		manualWeighting [10] = 100;
		paceChartTO.setManualWeighting(manualWeighting);
		paceChartTO.setRaceName("Test");
		paceChartTO.setPlannedRaceTimeFirst(LocalTime.of(1,00,00));
		paceChartTO.setPlannedRaceTimeLast(LocalTime.of(1,30,00));
		paceChartTO.setPlannedRaceTimeDelta(LocalTime.of(0,05,00));
		paceChartTO.setStartDelay(LocalTime.of(0,0,0));
		paceChartTO.setFirstFade(3);
		paceChartTO.setLastFade(3);

		// calculate results
		try {
			createPaceCharts(paceChartTO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 
    	
        return paceChartTO;
        
        
        
        
        
	}
    
    public void createPaceCharts(PaceChartTO paceChartTO) throws Exception {
    	
        double plannedRaceTimeFirstDec = PaceUtils.TimeToDouble(paceChartTO.getPlannedRaceTimeFirst()); 
        double plannedRaceTimeLastDec = PaceUtils.TimeToDouble(paceChartTO.getPlannedRaceTimeLast());
        double plannedRaceTimeDeltaDec = PaceUtils.TimeToDouble(paceChartTO.getPlannedRaceTimeDelta());
        
        // count through the different race times
        for (double raceTimeDec = plannedRaceTimeFirstDec; raceTimeDec <= plannedRaceTimeLastDec; raceTimeDec += plannedRaceTimeDeltaDec) {
        	LocalTime raceTime = PaceUtils.DoubleToTime(raceTimeDec);
        	String chartName = raceTime.getHour() + "h" + raceTime.getMinute();
            
            // count through the fades
	        for (int fade = paceChartTO.getFirstFade(); fade <= paceChartTO.getLastFade(); fade ++) {
	        	log.info("Creating chart for time:" + PaceUtils.formatTime(raceTime) + ", fade: " +  fade);
		        //createPaceChart(sheet, raceTime, paceChartTO.getStartDelay(), fade, colOffset);
	        }
        }
	}
    /*
	
	public void createPaceChart(Sheet sheet, LocalTime plannedRaceTime, LocalTime startDelay, double fade, int colOffset) {
		
		// calculate the average pace from the planned race time and the start delay
		double totalWeightedTimeDec = 0;
		double timeOverrunFactor;
		double finalElapsedTimeDec = 0;
		
		
		raceSplits = new ArrayList<Split>();
		
		averageMovingPace = PaceUtils.DoubleToTime((PaceUtils.TimeToDouble(plannedRaceTime) - PaceUtils.TimeToDouble(startDelay)) / distance);
		averageEndToEndPace = PaceUtils.DoubleToTime((PaceUtils.TimeToDouble(plannedRaceTime)) / distance);
		
		// calculate what we can without totals
		for (int counter = 1;counter < Math.ceil(distance) + 1; counter ++)
		{
			Split raceSplit = new Split();
			
	        // the last lap may be a different (shorter) distance
			raceSplit.splitNumber = counter;
			//System.out.println("Split #: " + counter);
	        if (counter == Math.ceil(distance) && distance < Math.ceil(distance))   // last split of an (eg) 21.1 race
	        	raceSplit.distance = ((double)Math.round((distance - Math.floor(distance))*100))/100;
	        else
	        	raceSplit.distance = 1;
	        
	        raceSplit.elevation = elevation[counter];
	       //raceSplit.manualWeighting = manualWeighting[counter];
	        raceSplit.manualWeighting = 100;//manualWeighting[counter];
	        
			// calculate the split time
	        if (counter == 1)
			{
				raceSplit.nominalTime = PaceUtils.DoubleToTime((PaceUtils.TimeToDouble(averageMovingPace) + PaceUtils.TimeToDouble(startDelay)) * raceSplit.distance);
			}
			else
				raceSplit.nominalTime = PaceUtils.DoubleToTime(PaceUtils.TimeToDouble(averageMovingPace) * raceSplit.distance);

			// cater for the fade 
			// Todo: change to be linear per split
			
			if (counter < 1+ distance/2) 
			{
				raceSplit.fadeFactor = 1 - fade/100;
				//raceSplit.fadeFactor = 1 + (raceSplit.splitNumber-1) * (fade/100/distance)*2;
			}
			else
			{
				raceSplit.fadeFactor = 1 + fade/100;
				//raceSplit.fadeFactor = 1 + (raceSplit.splitNumber-1) * (fade/100/distance)*2;
			}

	        raceSplit.calculatePacePerSplit();
	        totalWeightedTimeDec += raceSplit.weightedTimeDec;
	        raceSplits.add((raceSplit));	         
		}
		// calculate totals how much out our total weighted time is
		timeOverrunFactor = totalWeightedTimeDec / PaceUtils.TimeToDouble(plannedRaceTime);

		// calculate final times
		for (Split raceSplit : raceSplits)
		{
			raceSplit.finalTimeDec = raceSplit.weightedTimeDec / timeOverrunFactor;
			raceSplit.finalTime = PaceUtils.DoubleToTime(raceSplit.finalTimeDec);
			raceSplit.finalPace = PaceUtils.DoubleToTime(raceSplit.finalTimeDec / raceSplit.distance); 
			finalElapsedTimeDec += raceSplit.finalTimeDec;
			raceSplit.finalElapsedTime = PaceUtils.DoubleToTime(finalElapsedTimeDec);
			
		}
		
		//create spreadsheet
		Map<String, CellStyle> styles = createStyles(wb);
		int rowOffset = 0;
		Row row;
		Cell cell;
		rowOffset ++;
		
		row = createRow (sheet,rowOffset);
		cell = CreateCell(styles,row,"styleTitle",colOffset,"Race");
		cell = CreateCell(styles,row,"styleSub",colOffset + 2,raceName);
		cell = CreateCell(styles,row,"styleTitle",colOffset + 1,"");
		cell = CreateCell(styles,row,"styleTitle",colOffset + 3,"");
		cell = CreateCell(styles,row,"styleTitle",colOffset + 4,"");
		rowOffset ++;

		row = createRow (sheet,rowOffset);
		cell = CreateCell(styles,row,"styleTitle",colOffset,"Distance");
		cell = CreateCell(styles,row,"styleSub",colOffset + 2,String.valueOf(distance));
		cell = CreateCell(styles,row,"styleTitle",colOffset + 1,"");
		cell = CreateCell(styles,row,"styleTitle",colOffset + 3,"");
		cell = CreateCell(styles,row,"styleTitle",colOffset + 4,"");
		rowOffset ++;

		row = createRow(sheet, rowOffset);
		cell = CreateCell(styles,row,"styleTitle",colOffset,"Time");
		cell = CreateCell(styles,row,"styleSub",colOffset + 2,PaceUtils.formatTime(plannedRaceTime));
		cell = CreateCell(styles,row,"styleTitle",colOffset + 1,"");
		cell = CreateCell(styles,row,"styleTitle",colOffset + 3,"");
		cell = CreateCell(styles,row,"styleTitle",colOffset + 4,"");
		rowOffset ++;

		row = createRow(sheet, rowOffset);		
		cell = CreateCell(styles,row,"styleTitle",colOffset,"Start Delay");
		cell = CreateCell(styles,row,"styleSub",colOffset + 2,PaceUtils.formatTime(startDelay) + " min");
		cell = CreateCell(styles,row,"styleTitle",colOffset + 1,"");
		cell = CreateCell(styles,row,"styleTitle",colOffset + 3,"");
		cell = CreateCell(styles,row,"styleTitle",colOffset + 4,"");
		rowOffset ++;

		row = createRow(sheet, rowOffset);		
		cell = CreateCell(styles,row,"styleTitle",colOffset,"Fade");
		cell = CreateCell(styles,row,"styleSub",colOffset + 2,(int)fade + "%");
		cell = CreateCell(styles,row,"styleTitle",colOffset + 1,"");
		cell = CreateCell(styles,row,"styleTitle",colOffset + 3,"");
		cell = CreateCell(styles,row,"styleTitle",colOffset + 4,"");
		rowOffset ++;
		
		row = createRow(sheet, rowOffset);		
		cell = CreateCell(styles,row,"styleTitle",colOffset,"Average pace");
		cell = CreateCell(styles,row,"styleSub",colOffset + 2,PaceUtils.formatTime(averageEndToEndPace));
		cell = CreateCell(styles,row,"styleTitle",colOffset + 1,"");
		cell = CreateCell(styles,row,"styleTitle",colOffset + 3,"");
		cell = CreateCell(styles,row,"styleTitle",colOffset + 4,"");
		rowOffset ++;

		row = createRow(sheet, rowOffset);		
		cell = CreateCell(styles,row,"styleTitle",colOffset,"Moving pace");
		cell = CreateCell(styles,row,"styleSub",colOffset + 2,PaceUtils.formatTime(averageMovingPace));
		cell = CreateCell(styles,row,"styleTitle",colOffset + 1,"");
		cell = CreateCell(styles,row,"styleTitle",colOffset + 3,"");
		cell = CreateCell(styles,row,"styleTitle",colOffset + 4,"");
		rowOffset ++;

		// setup merged cells
		for (int counter = 2; counter < 9; counter ++) {
			String first = PaceUtils.getCharForNumber(colOffset);
			String second = PaceUtils.getCharForNumber(colOffset + 1);
			String range = "$" + first + "$" + counter + ":$" + second + "$" + counter;
			sheet.addMergedRegion(CellRangeAddress.valueOf(range));
		}
		for (int counter = 2; counter < 9; counter ++) {
			String first = PaceUtils.getCharForNumber(colOffset + 2);
			String second = PaceUtils.getCharForNumber(colOffset + 4);
			String range = "$" + first + "$" + counter + ":$" + second + "$" + counter;
			sheet.addMergedRegion(CellRangeAddress.valueOf(range));
		}
		

		rowOffset ++;
		rowOffset ++;

		row = createRow(sheet, rowOffset);
		row.setHeightInPoints(35);
		cell = CreateCell(styles,row,"styleCentreAligned",colOffset,"Km");
		cell = CreateCell(styles,row,"styleRightAligned",colOffset + 1,"Elev (m)");
		cell = CreateCell(styles,row,"styleRightAligned",colOffset + 2,"Pace");
		cell = CreateCell(styles,row,"styleRightAligned",colOffset + 3,"Split time");
		cell = CreateCell(styles,row,"styleRightAligned",colOffset + 4,"Total time");
		
        sheet.setColumnWidth(colOffset,5*256);
        sheet.setColumnWidth(colOffset + 1,7*256);
        sheet.setColumnWidth(colOffset + 2,6*256);
        sheet.setColumnWidth(colOffset + 3,6*256);
        sheet.setColumnWidth(colOffset + 4,7*256);
		
		rowOffset ++;
		double distance = 0;
		for (Split raceSplit : raceSplits)
		{	
			row = createRow(sheet, rowOffset);
			distance += raceSplit.distance;
			if (Math.ceil(distance) > distance) // we are at a fraction - the last split. EG 21.1
				cell = CreateCell(styles,row,"styleLeftAligned",colOffset,String.valueOf(distance));
			else
				cell = CreateCell(styles,row,"styleLeftAligned",colOffset,String.valueOf(raceSplit.splitNumber));
			cell = CreateCell(styles,row,"styleClean",colOffset + 1,String.valueOf((int)raceSplit.elevation));
			cell = CreateCell(styles,row,"styleClean",colOffset + 2,PaceUtils.formatTime(raceSplit.finalTime));
			cell = CreateCell(styles,row,"styleClean",colOffset + 3,PaceUtils.formatTime(raceSplit.finalPace));
			cell = CreateCell(styles,row,"styleClean",colOffset + 4,PaceUtils.formatTime(raceSplit.finalElapsedTime));
			rowOffset ++;
		}		
	}*/
}
