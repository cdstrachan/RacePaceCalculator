package com.cds.paceservices;

import java.time.LocalTime;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@CrossOrigin
@RestController
public class PaceCalculatorController {
	
	private static final Logger log = LoggerFactory.getLogger(PaceCalculatorController.class);

	@RequestMapping(value = "/pacecharttemplate", method = RequestMethod.GET)
	public PaceChartTO createPaceChartTemplate(@RequestParam("distance") double distance) {
    	log.info("pacecharttemplate: start - received test operation for distance: " + distance);
    	
    	ArrayList<ElevationTO> elevations;
    	ArrayList<ManualWeightingTO> manualWeightings;
		
		log.info("pacecharttemplate: creating input");
		PaceChartTO  paceChartTO = new PaceChartTO();
	    
        // setup race distance
		paceChartTO.setDistance(distance);        
        
		// setup elevations weighting
		elevations = new ArrayList<ElevationTO>();
		for (int counter = 0;counter < Math.ceil(paceChartTO.getDistance()); counter ++)
		{
			ElevationTO elevation = new ElevationTO();
			elevation.setSplitNumber(counter+1);
			elevation.setElevation(0);
			elevations.add((elevation));	 
		
		}
		paceChartTO.setElevations(elevations);
		
		// setup manual weighting
		manualWeightings = new ArrayList<ManualWeightingTO>();
		for (int counter = 0;counter < Math.ceil(paceChartTO.getDistance()); counter ++)
		{
			ManualWeightingTO manualWeighting = new ManualWeightingTO();
			manualWeighting.setSplitNumber(counter+1);
			manualWeighting.setManualWeight(100);
			manualWeightings.add((manualWeighting));	 
		
		}
		paceChartTO.setManualWeightings(manualWeightings);		
		paceChartTO.setRaceName("My pace chart");
		paceChartTO.setPlannedRaceTimeFirst(LocalTime.of(1,00,00));
		paceChartTO.setPlannedRaceTimeLast(LocalTime.of(1,30,00));
		paceChartTO.setPlannedRaceTimeDelta(LocalTime.of(0,15,00));
		paceChartTO.setStartDelay(LocalTime.of(0,0,30));
		paceChartTO.setFirstFade(0);
		paceChartTO.setLastFade(2);
		
		log.info("pacecharttemplate: Finished");
		return paceChartTO;
    }
	
	
    @RequestMapping(value = "/pacecharttest", method = RequestMethod.GET)
	public PaceChartTO createPaceChartTest() {
    	log.info("pacecharttest: start - received test operation");
    	
    	double distance = 10;
    	
    	ArrayList<ElevationTO> elevations;
    	ArrayList<ManualWeightingTO> manualWeightings;
		
		log.info("pacecharttest: creating test input");
		PaceChartTO  paceChartTO = new PaceChartTO();
	    
        // setup race distance
		paceChartTO.setDistance(distance);        
        
		// setup elevations weighting
		elevations = new ArrayList<ElevationTO>();
		for (int counter = 0;counter < Math.ceil(paceChartTO.getDistance()); counter ++)
		{
			ElevationTO elevation = new ElevationTO();
			elevation.setSplitNumber(counter);
			elevation.setElevation(100);
			elevations.add((elevation));	 
		
		}
		paceChartTO.setElevations(elevations);
		
		// setup manual weighting
		manualWeightings = new ArrayList<ManualWeightingTO>();
		for (int counter = 0;counter < Math.ceil(paceChartTO.getDistance()); counter ++)
		{
			ManualWeightingTO manualWeighting = new ManualWeightingTO();
			manualWeighting.setSplitNumber(counter);
			manualWeighting.setManualWeight(100);
			manualWeightings.add((manualWeighting));	 
		
		}
		paceChartTO.setManualWeightings(manualWeightings);
		
		paceChartTO.setRaceName("Testing3");
		paceChartTO.setPlannedRaceTimeFirst(LocalTime.of(1,00,00));
		paceChartTO.setPlannedRaceTimeLast(LocalTime.of(1,30,00));
		paceChartTO.setPlannedRaceTimeDelta(LocalTime.of(0,15,00));
		paceChartTO.setStartDelay(LocalTime.of(0,0,30));
		paceChartTO.setFirstFade(2);
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
	        	raceSplit.setSplitDistance(((double)Math.round((paceChartTO.getDistance() - Math.floor(paceChartTO.getDistance()))*100))/100);
	        else
	        	raceSplit.setSplitDistance(1);
	        
	        

	        raceSplit.setElevation(paceChartTO.getElevations().get(counter).getElevation());
	        raceSplit.setManualWeighting(paceChartTO.getManualWeightings().get(counter).getManualWeight());
	        //raceSplit.setManualWeighting(100);
	        
			// calculate the split time
	        if (counter == 0)
			{
				raceSplit.setNominalTime(PaceUtils.DoubleToTime((PaceUtils.TimeToDouble(paceChartInstanceTO.getAverageMovingPace()) + PaceUtils.TimeToDouble(paceChartTO.getStartDelay())) * raceSplit.getSplitDistance()));
			}
			else
				raceSplit.setNominalTime(PaceUtils.DoubleToTime(PaceUtils.TimeToDouble(paceChartInstanceTO.getAverageMovingPace()) * raceSplit.getSplitDistance()));

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
			raceSplit.setTotalDistance(raceSplit.getSplitDistance() + counter);
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
			raceSplit.setFinalPace(PaceUtils.DoubleToTime(raceSplit.getFinalTimeDec() / raceSplit.getSplitDistance())); 
			finalElapsedTimeDec += raceSplit.getFinalTimeDec();
			raceSplit.setFinalElapsedTime(PaceUtils.DoubleToTime(finalElapsedTimeDec));
		}
		
		paceChartInstanceTO.raceSplits = raceSplits;
		log.info("createpacechart - complete");
		return paceChartInstanceTO;
		
	}
	
}
