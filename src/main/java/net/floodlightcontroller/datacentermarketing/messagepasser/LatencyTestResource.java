package net.floodlightcontroller.datacentermarketing.messagepasser;

import java.io.IOException;
import java.util.LinkedList;

import net.floodlightcontroller.datacentermarketing.MarketManager;
import net.floodlightcontroller.datacentermarketing.logic.Auctioneer;
import net.floodlightcontroller.datacentermarketing.logic.BidRequest;
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

public class LatencyTestResource extends ServerResource {
	// get the latency between hosts

	@Get("json")
	public LinkedList<RouteLatency> retriveResults() {

		System.out.println("wuhhhaa\n\n\n\n\n\n\n\n");

		LinkedList<RouteLatency> toReturn = new LinkedList<RouteLatency>();

		toReturn.add(new RouteLatency(100, null));
		toReturn.add(new RouteLatency(200, null));
		return toReturn;

	}

	@Post
	@Put
	public void postPingRequest(String pingRequest) throws Exception {

		System.out.println(pingRequest);

		MappingJsonFactory f = new MappingJsonFactory();
		JsonParser jp;

		// these 2 fields must be filled in the JSON request
		boolean bSID = false, bDID = false;
		long start = -1, end = -1;
		try {
			jp = f.createJsonParser(pingRequest);
		} catch (JsonParseException e) {
			throw new IOException(e);
		}

		jp.nextToken();
		if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
			throw new IOException("Expected START_OBJECT");
		}

		while (jp.nextToken() != JsonToken.END_OBJECT) {

			if (jp.getCurrentToken() != JsonToken.FIELD_NAME) {
				throw new IOException("Expected FIELD_NAME");
				// System.out.println(jp.getText());
			}

			String name = jp.getCurrentName();

			if (name == "SID") {
				jp.nextToken();
				start = jp.getLongValue();
				bSID = true;
			} else if (name == "DID") {
				jp.nextToken();
				end = jp.getLongValue();
				bDID = true;
			}

			else {
				jp.nextToken();
			}
		}

		System.out.println("gathered route ping request for " + start + " "
				+ end);

		MarketManager.getInstance().getLowLevelController().ping(start, end);

	/*
	 * // TODO TOREMOVE move to other router
	 * net.floodlightcontroller.datacentermarketing.Scheduling.Scheduler
	 * .getInstance().refreshTopo();
	 */

		return;
	}

}
