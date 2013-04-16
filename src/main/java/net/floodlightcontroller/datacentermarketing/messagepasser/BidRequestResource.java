package net.floodlightcontroller.datacentermarketing.messagepasser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import net.floodlightcontroller.datacentermarketing.logic.Auctioneer;
import net.floodlightcontroller.datacentermarketing.logic.BidRequest;
import net.floodlightcontroller.datacentermarketing.logic.Bidder;
import net.floodlightcontroller.datacentermarketing.logic.Resource;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class BidRequestResource extends ServerResource {

	//The JSON parser
		/*
		 * String Example: 
		 * {"Bidder":"Shu", "Value":100, "SID":1, "DID":3, "MinRate":50, "MaxRate":100, "Data":100}
		 */
		private BidRequest requestJsonStringToBidRequest(String bidRequestString) throws IOException{
			BidRequest bidRequest = new BidRequest();
	        MappingJsonFactory f = new MappingJsonFactory();
	        JsonParser jp;
	        
	        //these six fields must be filled in the JSON request
	        boolean bBidder = false, bValue = false , bSID = false, bDID = false , 
	        		bMinRate = false, bData = false;
	        
	        try {
	            jp = f.createJsonParser(bidRequestString);
	        } catch (JsonParseException e) {
	            throw new IOException(e);
	        }
	        
	        jp.nextToken();
	        if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
	            throw new IOException("Expected START_OBJECT");
	        }
	        
	        while (jp.nextToken() != JsonToken.END_OBJECT){
	        	
	        	if (jp.getCurrentToken() != JsonToken.FIELD_NAME) {
	                throw new IOException("Expected FIELD_NAME");
	        		//System.out.println(jp.getText());
	        	}
	            
	            String name = jp.getCurrentName();
	            if(name == "Bidder"){
	            	jp.nextToken();
	            	Bidder bidder = new Bidder();
	            	String bidderID = jp.getText();
	            	bidder.setBidderID(bidderID);
	            	bidRequest.setBidder(bidder);
	            	bidder.setLastRequest(bidRequest);
	            	bBidder = true;
	            }
	            
	            else if(name == "Value"){
	            	jp.nextToken();
	            	bidRequest.setBidValue(jp.getFloatValue());
	            	bValue = true;
	            }
	            else if(name == "SID"){
	            	jp.nextToken();
	            	bidRequest.setSourceID(jp.getLongValue());
	            	bSID = true;
	            }
	            else if(name == "DID"){
	            	jp.nextToken();
	            	bidRequest.setDestID(jp.getLongValue());
	            	bDID = true;
	            }
	            else if(name == "MinRate"){
	            	jp.nextToken();
	            	bidRequest.addRequestField(Resource.MIN_RATE, jp.getFloatValue());
	            	bMinRate = true;
	            }
	            else if(name == "MaxRate"){
	            	jp.nextToken();
	            	bidRequest.addRequestField(Resource.MAX_RATE, jp.getFloatValue());
	            }
	            else if(name == "Data"){
	            	jp.nextToken();
	            	bidRequest.addRequestField(Resource.DATA, jp.getFloatValue());
	            	bData = true;
	            }
	            else {
	            	jp.nextToken();
	            }
	        }     
	        
	        //check if the bidRequest is value by seeing whether minimum set of fields are filled
	        if(bBidder && bValue && bMinRate && bSID && bDID && bData)
	        	return bidRequest;
	        else
	        	//not a valid bid request
	        	return null;
		}
		
		@Post
		@Put
		//public void postBidRequest(BidRequest bidRequest){
		public void postBidRequest(String bidRequestString) throws IOException{
			//System.out.println(bidRequestString);
			BidRequest bidRequest = this.requestJsonStringToBidRequest(bidRequestString);
			if(bidRequest == null)
				return;
			//generate the URL Hash for this User/BidRequest
			bidRequest.getBidder().setLastRequest(bidRequest);
			RESTQuerier querier = RESTQuerier.getInstance();
			String URI = querier.getBidderURIForRequest(this.getReference());
			querier.addBidder(URI, bidRequest.getBidder());
			//push to the auctioneer
			Auctioneer.getInstance().pushRequest(bidRequest);
			
			//for debugging
			/*
			HashMap<String,BidRequest> requests = Auctioneer.getInstance().getBidRequestForThisRound();
			Set<Entry<String,BidRequest>> reqset = requests.entrySet();
			for(Entry<String, BidRequest> req : reqset){
				System.out.println(req.getValue());
			}
			*/
			return;
		}
		
		@Get("json")
		public BidRequest retrive(){
			//System.out.println(this.getReference());
			RESTQuerier querier = RESTQuerier.getInstance();
			String URI = querier.getBidderURIForRequest(this.getReference());
			//query the bidder object
			Bidder bidder = querier.getBidder(URI);
			if(bidder == null)
				return null;
			//we are done
			//System.out.println(bidder.getLastRequest());
			return bidder.getLastRequest();
		}
		
		
}
