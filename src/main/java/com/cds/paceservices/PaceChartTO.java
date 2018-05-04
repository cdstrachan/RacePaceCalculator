package com.cds.paceservices;

import java.time.LocalTime;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class PaceChartTO {
	// inputs
	private double distance;
	//@JsonIgnore
	ArrayList<ElevationTO> elevations;
	//@JsonIgnore
	ArrayList<ManualWeightingTO> manualWeightings;
	private String raceName;
	private LocalTime plannedRaceTimeFirst;
	private LocalTime plannedRaceTimeLast;
	private LocalTime plannedRaceTimeDelta;
	private LocalTime startDelay;
	private int firstFade;
	private int lastFade;
	ArrayList<PaceChartInstanceTO> paceChartInstances;
	

	public ArrayList<PaceChartInstanceTO> getPaceChartInstances() {
		return paceChartInstances;
	}

	public void setPaceChartInstances(ArrayList<PaceChartInstanceTO> paceChartInstances) {
		this.paceChartInstances =  paceChartInstances;
	}
	
	public ArrayList<ElevationTO> getElevations() {
		return elevations;
	}

	public void setElevations(ArrayList<ElevationTO> elevations) {
		this.elevations = elevations;
	}

	public ArrayList<ManualWeightingTO> getManualWeightings() {
		return manualWeightings;
	}

	public void setManualWeightings(ArrayList<ManualWeightingTO> manualWeightings) {
		this.manualWeightings = manualWeightings;
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
	
}




	