package net.floodlightcontroller.datacentermarketing.logic;

import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedHashMap;

public class MaxUtilizationStrategy implements AuctioneerStrategy{

	public String toString(){
		return "Max Utilization for all resources";
	}

	@Override
	public LinkedHashMap<String, BidResult> processAllocation(
			LinkedHashMap<String, BidRequest> requests) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
