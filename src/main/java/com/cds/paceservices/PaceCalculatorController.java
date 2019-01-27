package com.cds.paceservices;

import java.time.LocalTime;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.common.net.HttpHeaders;

import java.io.FileOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
// todo:remove unused imports

@RestController
public class PaceCalculatorController {

	private static final Logger log = LoggerFactory.getLogger(PaceCalculatorController.class);
	private static final int MAX_CHART_COUNT = 100;
	
	// create the template splits to full in
	@RequestMapping(value = "/pacecharttemplate", method = RequestMethod.POST)
	public PaceChartTO createPaceChartTemplate(@RequestBody PaceChartTO paceChartTO) {
		log.info("pacecharttemplate: start - received test operation for distance: " + paceChartTO.getDistance());

		ArrayList<SplitInputTO> splitInputs;

		// setup elevations & manual weighting
		splitInputs = new ArrayList<SplitInputTO>();
		for (int counter = 0;counter < Math.ceil(paceChartTO.getDistance()); counter ++)
		{
			SplitInputTO splitInput = new SplitInputTO();
			splitInput.setSplitNumber(counter+1);
			splitInput.setElevation(0);
			splitInput.setManualWeight(100);
			if (counter + 1 > paceChartTO.getDistance())
				splitInput.setSplitDistance(paceChartTO.getDistance());
			else
				splitInput.setSplitDistance(counter+1);
			splitInputs.add((splitInput));	 

		}
		paceChartTO.setSplitInputs(splitInputs);

		paceChartTO.setRaceName("My pace chart");
		log.info("pacecharttemplate: Finished");
		return paceChartTO;
	}
	
		// used to create the initial blank pacechart
		// TODO: create different charts for template races
		@RequestMapping(value = "/pacechartbootstrap", method = RequestMethod.GET)
		public PaceChartTO createPaceChartBootstrap(@RequestParam("distance") double distance) {
			log.info("pacechartbootstrap: start - received test operation for distance: " + distance);

			ArrayList<SplitInputTO> splitInputs;

			log.info("pacechartbootstrap: creating input");
			PaceChartTO  paceChartTO = new PaceChartTO();

			// setup race distance
			paceChartTO.setDistance(distance);        

			// setup elevations & manual weighting
			splitInputs = new ArrayList<SplitInputTO>();
			for (int counter = 0;counter < Math.ceil(paceChartTO.getDistance()); counter ++)
			{
				SplitInputTO splitInput = new SplitInputTO();
				splitInput.setSplitNumber(counter+1);
				splitInput.setElevation(0);
				splitInput.setManualWeight(100);
				if (counter + 1 > paceChartTO.getDistance())
					splitInput.setSplitDistance(paceChartTO.getDistance());
				else
					splitInput.setSplitDistance(counter+1);
				splitInputs.add((splitInput));	 

			}
			paceChartTO.setSplitInputs(splitInputs);

			paceChartTO.setRaceName("My pace chart");
			paceChartTO.setPlannedRaceTimeFirst(LocalTime.of(0,59,00));
			paceChartTO.setPlannedRaceTimeLast(LocalTime.of(1,29,00));
			paceChartTO.setPlannedRaceTimeDelta(LocalTime.of(0,10,00));
			paceChartTO.setStartDelay(LocalTime.of(0,0,30));
			paceChartTO.setFirstFade(0);
			paceChartTO.setLastFade(2);

			log.info("pacechartbootstrap: Finished");
			return paceChartTO;
		}


	// test operaton to remove
	@RequestMapping(value = "/pacecharttest", method = RequestMethod.GET)
	public PaceChartTO createPaceChartTest() {
		log.info("pacecharttest: start - received test operation");

		double distance = 10;

		ArrayList<SplitInputTO> splitInputs;

		log.info("pacecharttest: creating test input");
		PaceChartTO  paceChartTO = new PaceChartTO();

		// setup race distance
		paceChartTO.setDistance(distance);        

		// setup elevations weighting
		splitInputs = new ArrayList<SplitInputTO>();
		for (int counter = 0;counter < Math.ceil(paceChartTO.getDistance()); counter ++)
		{
			SplitInputTO splitInput = new SplitInputTO();
			splitInput.setSplitNumber(counter);
			splitInput.setManualWeight(100);
			splitInput.setElevation(100);
			splitInputs.add((splitInput));	 

		}
		paceChartTO.setSplitInputs(splitInputs);

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

	// create the actual pace chart
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
				if (!DryRunCountOK(paceChartTO)) {
					validationErrorMessages.add(createValidationMessage(1,"You are trying to create more than " + MAX_CHART_COUNT +" pace charts. That is too many! Please narrow your input. The max returned is 100. Reduce the increments, last start time or fades."));
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
	
	// create the actual pace chart
	@RequestMapping(value = "/pacechartexcel", method = RequestMethod.POST)
	public ResponseEntity<InputStreamResource> createPaceChartExcel(@RequestBody PaceChartTO paceChartTO) {

		try {
			boolean isValid = true;
			ArrayList<ErrorMessageTO> validationErrorMessages = new ArrayList<ErrorMessageTO>();
			log.info("pacechartexcel: received a REST POST request");

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
				if (!DryRunCountOK(paceChartTO)) {
					validationErrorMessages.add(createValidationMessage(1,"You are trying to create more than " + MAX_CHART_COUNT +" pace charts. That is too many! Please narrow your input. The max returned is 100. Reduce the increments, last start time or fades."));
					isValid=false;
				}
			}	

			// calculate results
			log.info("pacechartexcel: about to calculate");
			
			if (isValid) paceChartTO = createPaceCharts(paceChartTO);
			// TODO: fix error handling here
			else paceChartTO.setValidationErrorMessages(validationErrorMessages);
			
				// create the spreadsheet
				SpreadsheetUtils spreadsheet = new SpreadsheetUtils();
				String outputFileLocation = spreadsheet.CreateSpreadsheet(paceChartTO);				

				File file = ResourceUtils.getFile(outputFileLocation);
		        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		        
				log.info("pacechartexcel: calculation complete");
				log.info("pacechartexcel: end - ready to send JSON response");

		        return ResponseEntity.ok()              
		                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
		                .contentType(MediaType.TEXT_HTML)
		                .body(resource);


		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	private ErrorMessageTO createValidationMessage(int messageID, String inputMessage) {
		ErrorMessageTO validationMessage = new ErrorMessageTO();
		validationMessage.setMessageCode(messageID);
		validationMessage.setMessageDescription(inputMessage);
		return validationMessage;
	}

	private boolean DryRunCountOK(PaceChartTO paceChartTO) {
		log.info("counting pace charts - start");
		double plannedRaceTimeFirstDec = PaceUtils.TimeToDouble(paceChartTO.getPlannedRaceTimeFirst()); 
		double plannedRaceTimeLastDec = PaceUtils.TimeToDouble(paceChartTO.getPlannedRaceTimeLast());
		double plannedRaceTimeDeltaDec = PaceUtils.TimeToDouble(paceChartTO.getPlannedRaceTimeDelta());

		int counter = 0;
		// count through the different race times
		for (double raceTimeDec = plannedRaceTimeFirstDec; raceTimeDec <= plannedRaceTimeLastDec; raceTimeDec += plannedRaceTimeDeltaDec) {
			for (int fade = paceChartTO.getFirstFade(); fade <= paceChartTO.getLastFade(); fade ++) {
				counter ++;
				if (counter > MAX_CHART_COUNT) {
					log.info("counting pace charts - count not ok");
					return false;
				}
			}
		}
		log.info("counting pace charts - count ok");
		return true;
	
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

				log.info("createpacecharts: creating chart for time:" + plannedRaceTime + ", fade: " +  fade);
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



			raceSplit.setElevation(paceChartTO.getSplitInputs().get(counter).getElevation());
			raceSplit.setManualWeighting(paceChartTO.getSplitInputs().get(counter).getManualWeight());

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
