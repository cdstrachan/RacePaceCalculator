package com.cds.paceservices;

import java.time.LocalTime;

public class SplitTo {

	private int splitNumber;
	private double distance;
	private LocalTime nominalTime;
	private double elevation;
	private double fadeFactor;
	private double manualWeighting;
	private LocalTime finalTime;
	private LocalTime finalElapsedTime;
	private LocalTime finalPace;

	
	// calculated fields
	private double gradient;
	private double paceImpact;
	private double timeDelta;
	private double nominalTimeDec;
	private double timeWithGradientDec;
	private double weightedTimeDec;
	private double finalTimeDec;
	
	
	
	public int getSplitNumber() {
		return splitNumber;
	}


	public void setSplitNumber(int splitNumber) {
		this.splitNumber = splitNumber;
	}


	public double getDistance() {
		return distance;
	}


	public void setDistance(double distance) {
		this.distance = distance;
	}


	public LocalTime getNominalTime() {
		return nominalTime;
	}


	public void setNominalTime(LocalTime nominalTime) {
		this.nominalTime = nominalTime;
	}


	public double getElevation() {
		return elevation;
	}


	public void setElevation(double elevation) {
		this.elevation = elevation;
	}


	public double getFadeFactor() {
		return fadeFactor;
	}


	public void setFadeFactor(double fadeFactor) {
		this.fadeFactor = fadeFactor;
	}


	public double getManualWeighting() {
		return manualWeighting;
	}


	public void setManualWeighting(double manualWeighting) {
		this.manualWeighting = manualWeighting;
	}


	public LocalTime getFinalTime() {
		return finalTime;
	}


	public void setFinalTime(LocalTime finalTime) {
		this.finalTime = finalTime;
	}


	public LocalTime getFinalElapsedTime() {
		return finalElapsedTime;
	}


	public void setFinalElapsedTime(LocalTime finalElapsedTime) {
		this.finalElapsedTime = finalElapsedTime;
	}


	public LocalTime getFinalPace() {
		return finalPace;
	}


	public void setFinalPace(LocalTime finalPace) {
		this.finalPace = finalPace;
	}


	
	public void calculatePacePerSplit(){
		gradient = elevation / distance / 10;
		paceImpact = PaceUtils.calcPaceImpact(elevation);  // some of this can be combined
		timeDelta = paceImpact * gradient; // seconds
		nominalTimeDec = PaceUtils.TimeToDouble(nominalTime);  // decimal time eg 8:30 is 8.5		
		timeWithGradientDec = 60 / ((60 / nominalTimeDec) - timeDelta);  // convert to KM/H; add the gradient impact and convert back to min/km
		weightedTimeDec = timeWithGradientDec * manualWeighting * fadeFactor / 100;
	}
	
	
	
	
}
