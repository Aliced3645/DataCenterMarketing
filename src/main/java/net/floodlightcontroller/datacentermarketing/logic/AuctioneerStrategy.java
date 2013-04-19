package net.floodlightcontroller.datacentermarketing.logic;

import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

public interface AuctioneerStrategy {
	
	public abstract String toString();
	
	//the allocation process returns the collection of BidResults
	public LinkedHashMap<String,BidResult> processAllocation(LinkedHashMap<String, BidRequest> requests) throws IOException, InterruptedException, ExecutionException;
	
}

