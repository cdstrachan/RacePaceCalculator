package com.cds.paceservices.TO;

import java.time.LocalTime;
import java.util.ArrayList;

public class PaceChartTO {
	// inputs
	private double distance;
	// @JsonIgnore
	ArrayList<SplitInputTO> splitInputs;
	// @JsonIgnore
	private String raceName;
	private String raceTemplateName;
	private LocalTime plannedRaceTimeFirst;
	private LocalTime plannedRaceTimeLast;
	private LocalTime plannedRaceTimeDelta;
	private LocalTime startDelay;
	private int firstFade;
	private int lastFade;

	private ArrayList<ErrorMessageTO> ValidationErrorMessages;

	ArrayList<PaceChartInstanceTO> paceChartInstances;

	public ArrayList<PaceChartInstanceTO> getPaceChartInstances() {
		return paceChartInstances;
	}

	public void setPaceChartInstances(ArrayList<PaceChartInstanceTO> paceChartInstances) {
		this.paceChartInstances = paceChartInstances;
	}

	public ArrayList<SplitInputTO> getSplitInputs() {
		return splitInputs;
	}

	public void setSplitInputs(ArrayList<SplitInputTO> splitInputs) {
		this.splitInputs = splitInputs;
	}

	public String getRaceName() {
		return raceName;
	}

	public void setRaceName(String raceName) {
		this.raceName = raceName;
	}

	public LocalTime getPlannedRaceTimeFirst() {
		return plannedRaceTimeFirst;
	}

	public void setPlannedRaceTimeFirst(LocalTime plannedRaceTimeFirst) {
		this.plannedRaceTimeFirst = plannedRaceTimeFirst;
	}

	public LocalTime getPlannedRaceTimeLast() {
		return plannedRaceTimeLast;
	}

	public void setPlannedRaceTimeLast(LocalTime plannedRaceTimeLast) {
		this.plannedRaceTimeLast = plannedRaceTimeLast;
	}

	public LocalTime getPlannedRaceTimeDelta() {
		return plannedRaceTimeDelta;
	}

	public void setPlannedRaceTimeDelta(LocalTime plannedRaceTimeDelta) {
		this.plannedRaceTimeDelta = plannedRaceTimeDelta;
	}

	public LocalTime getStartDelay() {
		return startDelay;
	}

	public void setStartDelay(LocalTime startDelay) {
		this.startDelay = startDelay;
	}

	public int getFirstFade() {
		return firstFade;
	}

	public void setFirstFade(int firstFade) {
		this.firstFade = firstFade;
	}

	public int getLastFade() {
		return lastFade;
	}

	public void setLastFade(int lastFade) {
		this.lastFade = lastFade;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public ArrayList<ErrorMessageTO> getValidationErrorMessages() {
		return ValidationErrorMessages;
	}

	public void setValidationErrorMessages(ArrayList<ErrorMessageTO> validationErrorMessages) {
		ValidationErrorMessages = validationErrorMessages;
	}

	public String getRaceTemplateName() {
		return raceTemplateName;
	}

	public void setRaceTemplateName(String raceTemplateName) {
		this.raceTemplateName = raceTemplateName;
	}

}
