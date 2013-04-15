package net.floodlightcontroller.datacentermarketing.messagepasser;

import java.util.List;
import net.floodlightcontroller.datacentermarketing.logic.Auctioneer;
import net.floodlightcontroller.datacentermarketing.logic.BidResult;
import net.floodlightcontroller.datacentermarketing.logic.Bidder;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class BidResultResource extends ServerResource{
	//Result could only be fetched not pushed
	
	//Return the latest bidding result for a particular user
	@Get("json")
	public BidResult retriveResults(){
		//System.out.println(this.getAttribute());
		RESTQuerier querier = RESTQuerier.getInstance();
		String URI = querier.getBidderURIForRequest(this.getReference());
		Bidder bidder = querier.getBidder(URI);
		if(bidder == null)
			return null;
		System.out.println(bidder.getLastRequest());
		return bidder.getLatestResult();
		
	}
	
}
