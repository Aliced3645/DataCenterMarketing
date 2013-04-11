package net.floodlightcontroller.datacentermarketing.logic;

import java.util.Collection;

public class MaxUtilizationStrategy implements AuctioneerStrategy{

	public String toString(){
		return "Max Utilization for all resources";
	}
	
	@Override
	public Collection<BidResult> processAllocation(
			Collection<BidRequest> requests) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
