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
	private PaceChartTO paceChartTO;
	
	private static final Logger log = LoggerFactory.getLogger(PaceCalculatorController.class);

    @RequestMapping(value = "/pacechart", method = RequestMethod.GET)
	public PaceChartTO createPaceChart() {
    	
    	log.info("Received a REST request");
    	double[] elevation;
		double [] manualWeighting;
	
		// TODO: get the parameters from the screen
		paceChartTO =  new PaceChartTO();
	    
		double distance = 10;
        
        // setup race distance
		paceChartTO.setDistance(distance);        
        
        // setup elevation profiles
        // +1 is just to make user input easier
		elevation = new double[(int) Math.ceil(distance) + 1];  // create elevation profiles
		elevation[1] = 1;
		elevation[2] = 2;
		elevation[3] = 3;
		elevation[4] = 4;
		elevation[5] = 5;
		elevation[6] = 4;
		elevation[7] = 3;
		elevation[8] = 2;
		elevation[9] = 1;
		elevation[10] = 0;
		
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
		paceChartTO.setPlannedRaceTimeDelta(LocalTime.of(0,55,00));
		paceChartTO.setStartDelay(LocalTime.of(0,0,0));
		paceChartTO.setFirstFade(3);
		paceChartTO.setLastFade(3);

		// calculate results
		try {
			paceChartTO = createPaceCharts(paceChartTO);
		} catch (Exception e) {
			log.info(e.getMessage());
		}
	    	
        return paceChartTO;
        
	}
    
    private PaceChartTO createPaceCharts(PaceChartTO paceChartTO) {
    	
        double plannedRaceTimeFirstDec = PaceUtils.TimeToDouble(paceChartTO.getPlannedRaceTimeFirst()); 
        double plannedRaceTimeLastDec = PaceUtils.TimeToDouble(paceChartTO.getPlannedRaceTimeLast());
        double plannedRaceTimeDeltaDec = PaceUtils.TimeToDouble(paceChartTO.getPlannedRaceTimeDelta());
		
		// initiate the instances array
		ArrayList<PaceChartInstanceTO> paceChartInstances = new ArrayList<PaceChartInstanceTO>();        
		
		// count through the different race times
        for (double raceTimeDec = plannedRaceTimeFirstDec; raceTimeDec <= plannedRaceTimeLastDec; raceTimeDec += plannedRaceTimeDeltaDec) {
			LocalTime plannedRaceTime = PaceUtils.DoubleToTime(raceTimeDec);
			
            // count through the fades
	        for (int fade = paceChartTO.getFirstFade(); fade <= paceChartTO.getLastFade(); fade ++) {
				
	        	log.info("Creating chart for time:" + PaceUtils.formatTime(plannedRaceTime) + ", fade: " +  fade);
		        paceChartInstances.add(createPaceChart(paceChartTO,plannedRaceTime,fade));
	        }
		}
		// TODO Get/SET
		paceChartTO.paceChartInstances = paceChartInstances;
		return paceChartTO;
	}
    
	
	public PaceChartInstanceTO createPaceChart(PaceChartTO paceChartTO, LocalTime plannedRaceTime, double fade) {
		
		// calculate the average pace from the planned race time and the start delay
		double totalWeightedTimeDec = 0;
		double timeOverrunFactor;
		double finalElapsedTimeDec = 0;
		ArrayList<SplitTO> raceSplits;
		
		PaceChartInstanceTO paceChartInstanceTO = new PaceChartInstanceTO();
		paceChartInstanceTO.setFade(fade);
		paceChartInstanceTO.setPlannedRaceTime(plannedRaceTime);
		
		raceSplits = new ArrayList<SplitTO>();
		
		paceChartInstanceTO.setAverageMovingPace(PaceUtils.DoubleToTime((PaceUtils.TimeToDouble(plannedRaceTime) - PaceUtils.TimeToDouble(paceChartTO.getStartDelay())) / paceChartTO.getDistance()));
		paceChartInstanceTO.setAverageEndToEndPace(PaceUtils.DoubleToTime((PaceUtils.TimeToDouble(plannedRaceTime)) / paceChartTO.getDistance()));
		
		// calculate what we can without totals
		for (int counter = 1;counter < Math.ceil(paceChartTO.getDistance()) + 1; counter ++)
		{
			SplitTO raceSplit = new SplitTO();
			
	        // the last lap may be a different (shorter) distance
			raceSplit.setSplitNumber(counter);
			//System.out.println("Split #: " + counter);
	        if (counter == Math.ceil(paceChartTO.getDistance()) && paceChartTO.getDistance() < Math.ceil(paceChartTO.getDistance()))   // last split of an (eg) 21.1 race
	        	raceSplit.setDistance(((double)Math.round((paceChartTO.getDistance() - Math.floor(paceChartTO.getDistance()))*100))/100);
	        else
	        	raceSplit.setDistance(1);
	        
			raceSplit.setElevation(paceChartTO.getElevation()[counter]);

	       //raceSplit.manualWeighting = manualWeighting[counter];
	        raceSplit.setManualWeighting(100); //manualWeighting[counter];
	        
			// calculate the split time
	        if (counter == 1)
			{
				raceSplit.setNominalTime(PaceUtils.DoubleToTime((PaceUtils.TimeToDouble(paceChartInstanceTO.getAverageMovingPace()) + PaceUtils.TimeToDouble(paceChartTO.getStartDelay())) * raceSplit.getDistance()));
			}
			else
				raceSplit.setNominalTime(PaceUtils.DoubleToTime(PaceUtils.TimeToDouble(paceChartInstanceTO.getAverageMovingPace()) * raceSplit.getDistance()));

			// cater for the fade 
			// Todo: change to be linear per split
			
			if (counter < 1+ paceChartTO.getDistance()/2) 
			{
				raceSplit.setFadeFactor( 1 - fade/100);
				//raceSplit.fadeFactor = 1 + (raceSplit.splitNumber-1) * (fade/100/distance)*2;
			}
			else
			{
				raceSplit.setFadeFactor(1 + fade/100);
				//raceSplit.fadeFactor = 1 + (raceSplit.splitNumber-1) * (fade/100/distance)*2;
			}

	        raceSplit.calculatePacePerSplit();
	        totalWeightedTimeDec += raceSplit.getWeightedTimeDec();
			raceSplits.add((raceSplit));	 
		
		}
		// calculate totals how much out our total weighted time is
		timeOverrunFactor = totalWeightedTimeDec / PaceUtils.TimeToDouble(plannedRaceTime);

		// calculate final times
		for (SplitTO raceSplit : raceSplits)
		{
			raceSplit.setFinalTimeDec(raceSplit.getWeightedTimeDec() / timeOverrunFactor);
			raceSplit.setFinalTime(PaceUtils.DoubleToTime(raceSplit.getFinalTimeDec()));
			raceSplit.setFinalPace(PaceUtils.DoubleToTime(raceSplit.getFinalTimeDec() / raceSplit.getDistance())); 
			finalElapsedTimeDec += raceSplit.getFinalTimeDec();
			raceSplit.setFinalElapsedTime(PaceUtils.DoubleToTime(finalElapsedTimeDec));
		}
		
		paceChartInstanceTO.raceSplits = raceSplits;
		return paceChartInstanceTO;
		
	}
	
}
