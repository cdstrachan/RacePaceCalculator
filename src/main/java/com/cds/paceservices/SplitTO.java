package com.cds.paceservices;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SplitTO {

	private int splitNumber;
	private double splitDistance;
	private double totalDistance;
	@JsonIgnore
	private LocalTime nominalTime;
	private double elevation;
	@JsonIgnore
	private double fadeFactor;
	private double manualWeighting;
	private LocalTime finalTime;
	private LocalTime finalElapsedTime;
	private LocalTime finalPace;

	
	// calculated fields
	@JsonIgnore
	private double gradient;
	@JsonIgnore
	private double paceImpact;
	@JsonIgnore
	private double timeDelta;
	@JsonIgnore
	private double nominalTimeDec;
	@JsonIgnore
	private double timeWithGradientDec;
	@JsonIgnore
	private double weightedTimeDec;
	@JsonIgnore
	private double finalTimeDec;
	
	
	
	public int getSplitNumber() {
		return splitNumber;
	}


	public void setSplitNumber(int splitNumber) {
		this.splitNumber = splitNumber;
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
		gradient = elevation / splitDistance / 10;
		paceImpact = PaceUtils.calcPaceImpact(elevation);  // some of this can be combined
		timeDelta = paceImpact * gradient; // seconds
		nominalTimeDec = PaceUtils.TimeToDouble(nominalTime);  // decimal time eg 8:30 is 8.5		
		timeWithGradientDec = 60 / ((60 / nominalTimeDec) - timeDelta);  // convert to KM/H; add the gradient impact and convert back to min/km
		setWeightedTimeDec(timeWithGradientDec * manualWeighting * fadeFactor / 100);
	}


	public double getWeightedTimeDec() {
		return weightedTimeDec;
	}


	public void setWeightedTimeDec(double weightedTimeDec) {
		this.weightedTimeDec = weightedTimeDec;
	}


	public double getFinalTimeDec() {
		return finalTimeDec;
	}


	public void setFinalTimeDec(double finalTimeDec) {
		this.finalTimeDec = finalTimeDec;
	}


	public double getSplitDistance() {
		return splitDistance;
	}


	public void setSplitDistance(double splitDistance) {
		this.splitDistance = splitDistance;
	}


	public double getTotalDistance() {
		return totalDistance;
	}


	public void setTotalDistance(double totalDistance) {
		this.totalDistance = totalDistance;
	}
	
	
	
	
}
