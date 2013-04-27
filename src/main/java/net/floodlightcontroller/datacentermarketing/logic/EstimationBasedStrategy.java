package net.floodlightcontroller.datacentermarketing.logic;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

public class EstimationBasedStrategy implements AuctioneerStrategy{

	@Override
	public LinkedHashMap<String, BidResult> processAllocation(
			LinkedHashMap<String, BidRequest> requests) throws IOException,
			InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		LinkedHashMap<String, BidResult> toReturn = new LinkedHashMap<String, BidResult>();
		Set<Entry<String, BidRequest>> requestSet = requests.entrySet();
		
		for(Entry<String, BidRequest> requestEntry : requestSet){
			
		}
		
		return toReturn;
	}
	
}
