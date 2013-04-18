package net.floodlightcontroller.datacentermarketing.logic;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

public class FirstComeFirstServeStrategy implements AuctioneerStrategy{
	
	
	
	public String toString(){
		return "First come first serve strategy.";
	}
	
	
	@Override
	public LinkedHashMap<String, BidResult> processAllocation(
			LinkedHashMap<String, BidRequest> requests) {
		//iterate through all lists and allocate as much as possible 
		//allocation order is the order bids arrive
		Set<Entry<String, BidRequest>> requestSet = requests.entrySet();
		for(Entry<String, BidRequest> requestEntry : requestSet){
			BidResult result = new BidResult();
			
		}	
		return null;
	}
	
}
