package net.floodlightcontroller.datacentermarketing.logic;

import java.util.Collection;

public interface AuctioneerStrategy {
	
	public abstract String toString();
	
	//the allocation process returns the collection of BidResults
	public Collection<BidResult> processAllocation(Collection<BidRequest> requests);
	
	
}

