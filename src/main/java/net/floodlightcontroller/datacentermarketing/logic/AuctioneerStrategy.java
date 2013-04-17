package net.floodlightcontroller.datacentermarketing.logic;

import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedHashMap;

public interface AuctioneerStrategy {
	
	public abstract String toString();
	
	//the allocation process returns the collection of BidResults
	public LinkedHashMap<String,BidResult> processAllocation(LinkedHashMap<String, BidRequest> requests);
	
}

