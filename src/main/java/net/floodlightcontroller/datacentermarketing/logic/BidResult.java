package net.floodlightcontroller.datacentermarketing.logic;

import net.floodlightcontroller.datacentermarketing.messagepasser.BidResultJSONSerializer;

import org.codehaus.jackson.map.annotate.JsonSerialize;

//The class is generated at the end of the auction 
//sent by the auctioneer

@JsonSerialize(using=BidResultJSONSerializer.class)
public class BidResult {

	//round counter (which round generates the result? )
	int round;
	//bidder ID
	String bidderID;
	
	
	
	
	
	//the allocation upon his requests
	//For testing JSON
	String allocationResultInString;

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public String getBidderID() {
		return bidderID;
	}

	public void setBidderID(String bidderID) {
		this.bidderID = bidderID;
	}

	public String getAllocationResultInString() {
		return allocationResultInString;
	}

	public void setAllocationResultInString(String allocationResultInString) {
		this.allocationResultInString = allocationResultInString;
	}
	
	
}
