package net.floodlightcontroller.datacentermarketing.controller;

//According to the ofp_port_features
//In Openflow Specification

public class MaxBandwidth {
	
	int value;
	BandwidthUnit unit;
	//true for full duplex 
	//false for half duplex
	boolean fullDuplex;
	
	public MaxBandwidth(int value, BandwidthUnit unit, boolean fullDuplex){
		this.value = value;
		this.unit = unit;
		this.fullDuplex = fullDuplex;
	}
	
	public String toString(){
		String unitString = null;
		if(unit == BandwidthUnit.Gigabyte)
			unitString = "GB";
		else if(unit == BandwidthUnit.Megabyte)
			unitString = "MB";
		else if(unit == BandwidthUnit.Terabyte)
			unitString = "TB";
		
		String duplexString;
		if(fullDuplex == false)
			duplexString = "Half-duplex";
		else
			duplexString = "Full-duplex";
		return value + unitString + " " + duplexString;
	}
	
	public int hashCode(){
		return super.hashCode();
	}
	
}
