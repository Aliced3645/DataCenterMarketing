package net.floodlightcontroller.datacentermarketing.messagepasser;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class BiddingMessageRouter implements RestletRoutable{

	@Override
	public Restlet getRestlet(Context context) {
		// TODO Auto-generated method stub
		Router router = new Router(context);
		router.attach("/results/json", BiddingMessageResource.class);
		return router;
	}

	
	@Override
	public String basePath() {
		// TODO Auto-generated method stub
		return "/marketing";
	}
	
	
	
}