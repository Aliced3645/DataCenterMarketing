package net.floodlightcontroller.datacentermarketing.logic;

import java.util.HashMap;


//each bidder is a thread
//temporarily we emulate the network behavior on a single machine
//so using threads which stands for bidders are reasonable

public class Bidder extends Thread{
	
	String bidderID;
	
	//the most recent bidding result
	private BidResult latestResult;
	
	public String getBidderID() {
		return bidderID;
	}


	public void setBidderID(String bidderID) {
		this.bidderID = bidderID;
	}


	public void setLatestResult(BidResult latestResult) {
		this.latestResult = latestResult;
	}


	//performs as a factory for BidRequest
	private BidRequest generateBidRequest(HashMap<Resource, Float> requestResources, long sourceID, long destID, float value){
		
		//clear the result for the last time
		BidRequest request = new BidRequest(this, sourceID, destID, value, requestResources);
		return request;
		
	}
	
	
	public void pushResult(BidResult result){
		this.latestResult = result;
	}
	
	public BidResult getLatestResult(){
		return this.latestResult;
	}
	
	
	//send bid request to Auctioneer
	//blocked until the auctioneer received the request
	//true if received
	//false if timeout (auctionner's ack is not received in the period of time)
	
	public boolean sendBidRequestAndWaitForResult(long sourceID, long destID, HashMap<Resource, Float> requestResources, float value) throws InterruptedException{
		
		latestResult = null;
		BidRequest request = this.generateBidRequest(requestResources, sourceID, destID, value);
		//get the auctioneer instance1
		Auctioneer auctioneer = Auctioneer.getInstance();
		auctioneer.pushRequest(request);
		//block self and wait for result
		this.wait();
		
		//it is woken up, try to get the result
		//If everything is OK, then the result should have been "pushed" to the bidder
		if(this.latestResult == null)
			return false;
		else 
			return true;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
