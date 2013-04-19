package net.floodlightcontroller.datacentermarketing.messagepasser;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.floodlightcontroller.datacentermarketing.logic.Auctioneer;
import net.floodlightcontroller.datacentermarketing.logic.BidResult;
import net.floodlightcontroller.datacentermarketing.logic.Bidder;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class BidResultResource extends ServerResource{
	//Result could only be fetched not pushed
	
	//Return the latest bidding result for a particular user
	@Get("json")
	public LinkedList<BidResult> retriveResults(){
		RESTQuerier querier = RESTQuerier.getInstance();
		String URI = querier.getBidderURIForResult(this.getReference());
		LinkedList<BidResult> toReturn = new LinkedList<BidResult>();
		System.out.println("\n\nURI:" + URI + "\n\n");
		if(URI.length() == 0){
			//which means return all results
			LinkedHashMap<String, BidResult> resultsMap = Auctioneer.getInstance().getResultsForThisRound();
			Set<Entry<String, BidResult>> resultSet = resultsMap.entrySet();
			for(Entry<String, BidResult> e : resultSet){
				toReturn.add(e.getValue());
			}
			return toReturn;
		}
		
		Bidder bidder = querier.getBidder(URI);
		if(bidder == null)
			return null;
		//System.out.println(bidder.getLastRequest());
		toReturn.add(bidder.getLatestResult());
		return toReturn;
		
	}
	
}
