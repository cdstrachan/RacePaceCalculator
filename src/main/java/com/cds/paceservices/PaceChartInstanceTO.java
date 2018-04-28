package com.cds.paceservices;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ArrayList;

public class PaceChartInstanceTO {
	// calculated
	private LocalTime averageMovingPace;
	private LocalTime averageEndToEndPace;
	private double fade;
	private LocalTime plannedRaceTime;
	ArrayList<SplitTO> raceSplits;	
	
	public LocalTime getPlannedRaceTime() {
		return plannedRaceTime;
	}

	public void setPlannedRaceTime(LocalTime plannedRaceTime) {
		this.plannedRaceTime = plannedRaceTime;
	}

	public double getFade() {
		return fade;
	}

	public void setFade(double fade) {
		this.fade = fade;
	}

	public LocalTime getAverageMovingPace() {
		return averageMovingPace;
	}

	public void setAverageMovingPace(LocalTime averageMovingPace) {
		this.averageMovingPace = averageMovingPace;
	}

	public LocalTime getAverageEndToEndPace() {
		return averageEndToEndPace;
	}

	public void setAverageEndToEndPace(LocalTime averageEndToEndPace) {
		this.averageEndToEndPace = averageEndToEndPace;
	}

	public ArrayList<SplitTO> getRaceSplits() {
		return raceSplits;
	}

	public void setRaceSplits(ArrayList<SplitTO> raceSplits) {
		this.raceSplits = raceSplits;
	}	

	
	
}
