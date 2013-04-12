package net.floodlightcontroller.datacentermarketing.controller;

import java.util.List;

import net.floodlightcontroller.datacentermarketing.logic.Auctioneer;
import net.floodlightcontroller.datacentermarketing.logic.BidResult;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
public class BiddingMessageResource extends ServerResource{
	
	@Get("json")
	public List<BidResult> retriveResults(){
		List<BidResult> results = Auctioneer.getInstance().getResultsForThisRound();
		return results;
	}
	
	
	
}
