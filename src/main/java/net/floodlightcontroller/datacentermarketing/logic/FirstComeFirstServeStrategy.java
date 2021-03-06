package net.floodlightcontroller.datacentermarketing.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import net.floodlightcontroller.datacentermarketing.MarketManager;
import net.floodlightcontroller.datacentermarketing.Scheduling.Allocation;
import net.floodlightcontroller.datacentermarketing.Scheduling.Scheduler;
import net.floodlightcontroller.routing.Route;

public class FirstComeFirstServeStrategy implements AuctioneerStrategy{
	
	
	
	public String toString(){
		return "First come first serve strategy.";
	}
	
	@Override
	public LinkedHashMap<String, BidResult> processAllocation(
			LinkedHashMap<String, BidRequest> requests) throws IOException, InterruptedException, ExecutionException {
		//iterate through all lists and allocate as much as possible 
		//allocation order is the order bids arrive
		LinkedHashMap<String, BidResult> toReturn = new LinkedHashMap<String, BidResult>();
		Set<Entry<String, BidRequest>> requestSet = requests.entrySet();
		for(Entry<String, BidRequest> requestEntry : requestSet){
			BidResult result = new BidResult();
			result.setResult(false);
			BidRequest bidRequest = requestEntry.getValue();
			//get all possible routes
			Collection<Route> possibleRoutes = bidRequest.getPossibleRoutes();
			if(possibleRoutes == null){
				//failed to establish
				//toReturn.clear();
				//return toReturn;
				System.out.println("\n\n\n no possible routes \n\n\n");
			}
			for(Route route : possibleRoutes){
				Allocation alloc = new Allocation();
				alloc.setBandwidth(bidRequest.getMinBandwidth());
				alloc.setFrom((long)bidRequest.getStartTime());
				alloc.setTo((long)bidRequest.getEndTime());
				if(Scheduler.getInstance().validateAndReserveRoute(route, alloc,true)){
					//successfully allocated
					result.setResult(true);
					result.setRoute(route);
					break;
				}
			}
			result.setValue(bidRequest.getBidValue());
			result.setBidRequest(bidRequest);
			result.setHostID(bidRequest.getSourceID());
			result.setBidder(bidRequest.getBidder());
			result.setRound(Auctioneer.round);
			result.getBidder().setLatestResult(result);
			toReturn.put(result.getBidder().getBidderID(), result);
		}	
		return toReturn;
	}
	
}
