package net.floodlightcontroller.datacentermarketing.messagepasser;

import java.io.IOException;
import java.util.List;

import net.floodlightcontroller.datacentermarketing.logic.Auctioneer;
import net.floodlightcontroller.datacentermarketing.logic.BidRequest;
import net.floodlightcontroller.datacentermarketing.logic.BidResult;
import net.floodlightcontroller.datacentermarketing.logic.Bidder;
import net.floodlightcontroller.datacentermarketing.logic.Resource;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

public class BidResultResource extends ServerResource{
	
	
	
	@Get("json")
	public List<BidResult> retriveResults(){
		//System.out.println(this.getAttribute());
		System.out.println(this.getReference());
		System.out.println(this.getReferrerRef());
		List<BidResult> results = Auctioneer.getInstance().getResultsForThisRound();
		return results;
	}
	
}
