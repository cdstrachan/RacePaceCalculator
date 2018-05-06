package com.cds.paceservices;

import java.time.LocalTime;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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

		try {
			boolean isValid = true;
			ArrayList<ErrorMessageTO> validationErrorMessages = new ArrayList<ErrorMessageTO>();
			log.info("pacechart: received a REST POST request");

			// distance >0 and <100
			if (paceChartTO.getDistance() <1 || paceChartTO.getDistance()>201) {
				validationErrorMessages.add(createValidationMessage(1,"Distance must be between 1 and 200"));
				isValid=false;
			}

			// first & second fades 0 or more
			if (paceChartTO.getFirstFade() <0 || paceChartTO.getFirstFade()>99) {
				validationErrorMessages.add(createValidationMessage(1,"First fade must be between 0 and 99"));
				isValid=false;
			}
			if (paceChartTO.getLastFade() <0 || paceChartTO.getLastFade()>99) {
				validationErrorMessages.add(createValidationMessage(1,"Last fade must be between 0 and 99"));
				isValid=false;
			}
						
			// first fade must be <= then second fade
			if (paceChartTO.getFirstFade() > paceChartTO.getLastFade()) {
				validationErrorMessages.add(createValidationMessage(1,"First fade may not be larger than last fade"));
				isValid=false;
			}

			// last start time >= first start time
			if (paceChartTO.getPlannedRaceTimeFirst().isAfter(paceChartTO.getPlannedRaceTimeLast())) {
				validationErrorMessages.add(createValidationMessage(1,"First start time may not be larger than last start time"));
				isValid=false;
				
			}
				
			// delta delay positive
			if (paceChartTO.getPlannedRaceTimeDelta().isBefore(LocalTime.of(0, 0,0))) {
				validationErrorMessages.add(createValidationMessage(1,"Time increment cannot be negative"));
				isValid=false;
				
			}
			
			// start delay positive
			if (paceChartTO.getStartDelay().isBefore(LocalTime.of(0, 0,0))) {
				validationErrorMessages.add(createValidationMessage(1,"Start delay cannot be negative"));
				isValid=false;
				
			}

			// no more than 100 records returned
			//Additional is valid check here because bad data already trapped could break the counter function
			if (isValid) {
				int chartCount = CountDryRun(paceChartTO);
				if (chartCount>100) {
					validationErrorMessages.add(createValidationMessage(1,"You are trying to create " + chartCount +" pace charts. That is too many pace charts. Please narrow your input. The max returned is 100 charts. Reduce the increments, last start time or fades."));
					isValid=false;
				}
			}	

			// calculate results
			log.info("pacechart: about to calculate");
			
			if (isValid) paceChartTO = createPaceCharts(paceChartTO);
			else paceChartTO.setValidationErrorMessages(validationErrorMessages);
			
			
			log.info("pacechart: calculation complete");
			log.info("pacechart: end - ready to send JSON response");
			return paceChartTO;

		} catch (Exception e) {
			log.error(e.getMessage());
			return paceChartTO;
		}
	}
	
	private ErrorMessageTO createValidationMessage(int messageID, String inputMessage) {
		ErrorMessageTO validationMessage = new ErrorMessageTO();
		validationMessage.setMessageCode(messageID);
		validationMessage.setMessageDescription(inputMessage);
		return validationMessage;
	}

	private int CountDryRun(PaceChartTO paceChartTO) {
		log.info("counting pace charts - start");
		double plannedRaceTimeFirstDec = PaceUtils.TimeToDouble(paceChartTO.getPlannedRaceTimeFirst()); 
		double plannedRaceTimeLastDec = PaceUtils.TimeToDouble(paceChartTO.getPlannedRaceTimeLast());
		double plannedRaceTimeDeltaDec = PaceUtils.TimeToDouble(paceChartTO.getPlannedRaceTimeDelta());

		int counter = 0;
		// count through the different race times
		for (double raceTimeDec = plannedRaceTimeFirstDec; raceTimeDec <= plannedRaceTimeLastDec; raceTimeDec += plannedRaceTimeDeltaDec) {
			for (int fade = paceChartTO.getFirstFade(); fade <= paceChartTO.getLastFade(); fade ++) {
				counter ++;
			}
		}
		log.info("counting pace charts - end. total=" + counter);
		return counter;
	
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
		paceChartTO.setPaceChartInstances(paceChartInstances);
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

		paceChartInstanceTO.setRaceSplits(raceSplits);
		log.info("createpacechart - complete");
		return paceChartInstanceTO;

	}

}
