package net.floodlightcontroller.datacentermarketing.logic;

import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Map.Entry;

public class MaxUtilizationStrategy implements AuctioneerStrategy {

	public String toString() {
		return "Max Utilization for all resources";
	}

	@Override
	public LinkedHashMap<String, BidResult> processAllocation(
			LinkedHashMap<String, BidRequest> requests) {
		// Utilization best-fit strategy
		LinkedHashMap<String, BidResult> toReturn = new LinkedHashMap<String, BidResult>();
		Set<Entry<String, BidRequest>> requestSet = requests.entrySet();

		for (Entry<String, BidRequest> requestEntry : requestSet) {
			BidRequest bidRequest = requestEntry.getValue();
			// valueRank.offer()
			
		}
		return null;
	}

}
