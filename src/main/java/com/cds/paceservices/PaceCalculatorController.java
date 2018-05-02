package com.cds.paceservices;

import java.time.LocalTime;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class PaceCalculatorController {
	
	private static final Logger log = LoggerFactory.getLogger(PaceCalculatorController.class);

    @RequestMapping(value = "/pacecharttest", method = RequestMethod.GET)
	public PaceChartTO createPaceChartTest() {
    	log.info("pacecharttest: start - received test operation");
    	double[] elevation;
		double [] manualWeighting;    	
		
		log.info("pacecharttest: creating test input");
		PaceChartTO  paceChartTO = new PaceChartTO();
	    
		double distance = 10;
        
        // setup race distance
		paceChartTO.setDistance(distance);        
        
        // setup elevation profiles
		elevation = new double[(int) Math.ceil(distance) ];  // create elevation profiles
		elevation[0] = 1;
		elevation[1] = 2;
		elevation[2] = 3;
		elevation[3] = 4;
		elevation[4] = 5;
		elevation[5] = 4;
		elevation[6] = 3;
		elevation[7] = 2;
		elevation[8] = 1;
		elevation[9] = 0;
		
		paceChartTO.setElevation(elevation);
		
		int baseWeightDelta = 0; //tweaking the hills
		
		// setup manual weighting
		
		// TODO check if the elevation is  >1, and then allow for a base weighting delta of hills
		manualWeighting = new double[(int) Math.ceil(distance) ];  // create elevation profiles
		manualWeighting [0] = 100;
		manualWeighting [1] = 100;
		manualWeighting [2] = 100;
		manualWeighting [3] = 100 + baseWeightDelta;
		manualWeighting	[4] = 100 + baseWeightDelta;
		manualWeighting [5] = 100 + baseWeightDelta;
		manualWeighting [6] = 100;
		manualWeighting [7] = 100;
		manualWeighting [8] = 100;
		manualWeighting [9] = 100 + baseWeightDelta;
		paceChartTO.setManualWeighting(manualWeighting);
		paceChartTO.setRaceName("Testing3");
		paceChartTO.setPlannedRaceTimeFirst(LocalTime.of(1,00,00));
		paceChartTO.setPlannedRaceTimeLast(LocalTime.of(1,30,00));
		paceChartTO.setPlannedRaceTimeDelta(LocalTime.of(0,55,00));
		paceChartTO.setStartDelay(LocalTime.of(0,0,30));
		paceChartTO.setFirstFade(3);
		paceChartTO.setLastFade(3);
		
		String uri = "http://localhost:8080/pacechart";
		RestTemplate paceTemplate = new RestTemplate();
		log.info("pacecharttest: REST call to pacechart");
		paceChartTO = paceTemplate.postForObject(uri, paceChartTO, PaceChartTO.class);
		log.info("pacecharttest: REST response received");
		return paceChartTO;
    }
	
    @RequestMapping(value = "/pacechart", method = RequestMethod.POST)
	public PaceChartTO createPaceChart(@RequestBody PaceChartTO paceChartTO) {
    	
    	log.info("pacechart: received a REST POST request");
    	// todo: validate inputs
    	
		// calculate results
		try {
			log.info("pacechart: about to calculate");
			paceChartTO = createPaceCharts(paceChartTO);
			log.info("pacechart: calculation complete");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.info("pacechart: end - ready to send JSON response");	
        return paceChartTO;
        
	}
    
    private PaceChartTO createPaceCharts(PaceChartTO paceChartTO) {
    	
    	log.info("createpacecharts - start");
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
				
	        	log.info("createpacecharts: creating chart for time:" + PaceUtils.formatTime(plannedRaceTime) + ", fade: " +  fade);
		        paceChartInstances.add(createPaceChart(paceChartTO,plannedRaceTime,fade));
		        log.info("createpacecharts: creating chart complete");
	        }
		}
		// TODO Get/SET
		paceChartTO.paceChartInstances = paceChartInstances;
		log.info("createpacecharts - end");
		return paceChartTO;
	}
    
	
	public PaceChartInstanceTO createPaceChart(PaceChartTO paceChartTO, LocalTime plannedRaceTime, double fade) {
		
		log.info("createpacechart - start");
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
		log.info("createpacechart - creating splits");
		for (int counter = 0;counter < Math.ceil(paceChartTO.getDistance()); counter ++)
		{
			log.info("createpacechart - creating split # " + (counter +1));
			SplitTO raceSplit = new SplitTO();
			
	        // the last lap may be a different (shorter) distance
			raceSplit.setSplitNumber(counter+1);
			//System.out.println("Split #: " + counter);
	        if (counter+1 == Math.ceil(paceChartTO.getDistance()) && paceChartTO.getDistance() < Math.ceil(paceChartTO.getDistance()))   // last split of an (eg) 21.1 race
	        	raceSplit.setDistance(((double)Math.round((paceChartTO.getDistance() - Math.floor(paceChartTO.getDistance()))*100))/100);
	        else
	        	raceSplit.setDistance(1);
	        
			raceSplit.setElevation(paceChartTO.getElevation()[counter]);

	        raceSplit.setManualWeighting(paceChartTO.getManualWeighting()[counter]);
	        //raceSplit.setManualWeighting(100);
	        
			// calculate the split time
	        if (counter == 0)
			{
				raceSplit.setNominalTime(PaceUtils.DoubleToTime((PaceUtils.TimeToDouble(paceChartInstanceTO.getAverageMovingPace()) + PaceUtils.TimeToDouble(paceChartTO.getStartDelay())) * raceSplit.getDistance()));
			}
			else
				raceSplit.setNominalTime(PaceUtils.DoubleToTime(PaceUtils.TimeToDouble(paceChartInstanceTO.getAverageMovingPace()) * raceSplit.getDistance()));

			// cater for the fade 
			// Todo: change to be linear per split
			if (counter <  paceChartTO.getDistance()/2) 
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
		log.info("createpacechart - calculating final split data");
		for (SplitTO raceSplit : raceSplits)
		{
			raceSplit.setFinalTimeDec(raceSplit.getWeightedTimeDec() / timeOverrunFactor);
			raceSplit.setFinalTime(PaceUtils.DoubleToTime(raceSplit.getFinalTimeDec()));
			raceSplit.setFinalPace(PaceUtils.DoubleToTime(raceSplit.getFinalTimeDec() / raceSplit.getDistance())); 
			finalElapsedTimeDec += raceSplit.getFinalTimeDec();
			raceSplit.setFinalElapsedTime(PaceUtils.DoubleToTime(finalElapsedTimeDec));
		}
		
		paceChartInstanceTO.raceSplits = raceSplits;
		log.info("createpacechart - complete");
		return paceChartInstanceTO;
		
	}
	
}
