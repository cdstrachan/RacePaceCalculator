package com.cds.paceservices;

public class SplitInputTO {
	private int splitNumber;
	private int elevation;
	private double splitDistance;
	private int manualWeight;
	
	public int getManualWeight() {
		return manualWeight;
	}
	public void setManualWeight(int manualWeight) {
		this.manualWeight = manualWeight;
	}
		
	public int getSplitNumber() {
		return splitNumber;
	}
	public void setSplitNumber(int splitNumber) {
		this.splitNumber = splitNumber;
	}
	public int getElevation() {
		return elevation;
	}
	public void setElevation(int elevation) {
		this.elevation = elevation;
	}
	public double getSplitDistance() {
		return splitDistance;
	}
	public void setSplitDistance(double splitDistance) {
		this.splitDistance = splitDistance;
	}
}
