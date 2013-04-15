package net.floodlightcontroller.datacentermarketing.messagepasser;

import java.util.List;
import net.floodlightcontroller.datacentermarketing.logic.Auctioneer;
import net.floodlightcontroller.datacentermarketing.logic.BidResult;
import org.restlet.resource.Get;
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
