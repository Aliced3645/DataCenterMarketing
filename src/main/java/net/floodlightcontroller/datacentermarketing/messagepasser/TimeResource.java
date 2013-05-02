package net.floodlightcontroller.datacentermarketing.messagepasser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import net.floodlightcontroller.datacentermarketing.MarketManager;
import net.floodlightcontroller.datacentermarketing.logic.BidRequest;
import net.floodlightcontroller.datacentermarketing.logic.BiddingClock;
import net.floodlightcontroller.routing.Route;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class TimeResource extends ServerResource{
	@Get("json")
	public BiddingClock retrive() throws Exception{
		
		//For test, install a route from host 0 to host 2
		ArrayList<Route> rts = MarketManager.getInstance().getNonLoopPaths(0, 2);
		MarketManager.getInstance().getLowLevelController().installRoute(0, 2, rts.get(0), 10000, Short.MAX_VALUE);
		return BiddingClock.getInstance();
		
	}
}
