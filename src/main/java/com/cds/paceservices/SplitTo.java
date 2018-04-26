package com.cds.paceservices;

import java.time.LocalTime;

public class SplitTo {

	int splitNumber;
	double distance;
	LocalTime nominalTime;
	
	double elevation;
	double fadeFactor;
	double manualWeighting;
	
	// calculated fields
	private double gradient;
	private double paceImpact;
	private double timeDelta;
	private double nominalTimeDec;
	private double timeWithGradientDec;
	double weightedTimeDec;
	LocalTime finalTime;
	double finalTimeDec;
	LocalTime finalElapsedTime;
	LocalTime finalPace;
	
	
	public void calculatePacePerSplit(){
		gradient = elevation / distance / 10;
		paceImpact = calcPaceImpact(elevation);  // some of this can be combined
		timeDelta = paceImpact * gradient; // seconds
		nominalTimeDec = PaceUtils.TimeToDouble(nominalTime);  // decimal time eg 8:30 is 8.5		
		timeWithGradientDec = 60 / ((60 / nominalTimeDec) - timeDelta);  // convert to KM/H; add the gradient impact and convert back to min/km
		weightedTimeDec = timeWithGradientDec * manualWeighting * fadeFactor / 100;
	}
	
	// TODO: make static variables
	private double calcPaceImpact(double elevation) {
		if (elevation > 25) return 0.4;
		if (elevation > 0) return 0.5;
		if (elevation < -20) return 0.15;
		if (elevation < 0) return 0.3;
		return 0;
		
	}
	
	
}
