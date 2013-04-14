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
public class BiddingMessageResource extends ServerResource{
	
	@Get("json")
	public List<BidResult> retriveResults(){
		List<BidResult> results = Auctioneer.getInstance().getResultsForThisRound();
		return results;
	}
	
	
	//The JSON parser
	/*
	 * String Example: 
	 * {"Bidder":"Shu", "Value":100, "SID":1, "DID":3, "MinRate":50, "MaxRate":100, "Data":100}
	 */
	private BidRequest requestJsonStringToBidRequest(String bidRequestString) throws IOException{
		BidRequest bidRequest = new BidRequest();
        MappingJsonFactory f = new MappingJsonFactory();
        JsonParser jp;
        
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
            	
            }
            else if(name == "Value"){
            	jp.nextToken();
            	bidRequest.setBidValue(jp.getFloatValue());
            }
            else if(name == "SID"){
            	jp.nextToken();
            	bidRequest.setSourceID(jp.getLongValue());
            }
            else if(name == "DID"){
            	jp.nextToken();
            	bidRequest.setDestID(jp.getLongValue());
            }
            else if(name == "MinRate"){
            	jp.nextToken();
            	bidRequest.addRequestField(Resource.MIN_RATE, jp.getFloatValue());
            }
            else if(name == "MaxRate"){
            	jp.nextToken();
            	bidRequest.addRequestField(Resource.MAX_RATE, jp.getFloatValue());
            }
            else if(name == "Data"){
            	jp.nextToken();
            	bidRequest.addRequestField(Resource.DATA, jp.getFloatValue());
            }
            else {
            	jp.nextToken();
            }
            
        }     
		return bidRequest;
	}
	
	@Post
	@Put
	//public void postBidRequest(BidRequest bidRequest){
	public void postBidRequest(String bidRequestString) throws IOException{
		//System.out.println(bidRequestString);
		BidRequest bidRequest = this.requestJsonStringToBidRequest(bidRequestString);
		
		//generate the URL for this User/BidRequest
		
		return;
	}
	
}
