package net.floodlightcontroller.datacentermarketing.logic;

import java.util.Collection;
import java.util.Hashtable;

public interface AuctioneerStrategy {
	
	public abstract String toString();
	
	//the allocation process returns the collection of BidResults
	public Collection<BidResult> processAllocation(Hashtable<String, BidRequest> requests);
	
	
}

