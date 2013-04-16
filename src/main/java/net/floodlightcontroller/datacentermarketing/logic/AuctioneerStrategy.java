package net.floodlightcontroller.datacentermarketing.logic;

import java.util.Collection;
import java.util.Hashtable;

public interface AuctioneerStrategy {
	
	public abstract String toString();
	
	//the allocation process returns the collection of BidResults
	public Hashtable<String,BidResult> processAllocation(Hashtable<String, BidRequest> requests);
	
}

