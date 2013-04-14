package net.floodlightcontroller.datacentermarketing.messagepasser;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.restlet.routing.Template;

import net.floodlightcontroller.restserver.RestletRoutable;

public class BiddingMessageRouter implements RestletRoutable{

	@Override
	public Restlet getRestlet(Context context) {
		// TODO Auto-generated method stub
		Router router = new Router(context);
		router.attach("/results/json/", BidResultResource.class).setMatchingMode(Template.MODE_STARTS_WITH);
		//router.attach("/request/json/", BidRequestResource.class).setMatchingMode(Template.MODE_STARTS_WITH);
		router.attach("/request/json/{BidderID}", BidRequestResource.class);
		
		//router.at
		return router;
	}

	
	@Override
	public String basePath() {
		// TODO Auto-generated method stub
		return "/marketing";
	}
	
	
	
}
