package net.floodlightcontroller.datacentermarketing.logic;

import java.util.LinkedList;

import net.floodlightcontroller.datacentermarketing.Scheduling.Allocation;
import net.floodlightcontroller.datacentermarketing.messagepasser.BidResultJSONSerializer;

import org.codehaus.jackson.map.annotate.JsonSerialize;

//The class is generated at the end of the auction 
//sent by the auctioneer

@JsonSerialize(using=BidResultJSONSerializer.class)
public class BidResult {

	//round counter (which round generates the result? )
	int round;
	Bidder bidder;
	//true for success
	//false for fail
	private boolean result;
	
	public boolean getResult(){
		return result;
	}
	
	public void setResult(boolean _result){
		result = _result;
	}
	
	private LinkedList<Allocation> allocations;
	
	public LinkedList<Allocation> getAllocations(){
		return allocations;
	}
	
	public void addAllocation(Allocation allocation){
		this.allocations.add(allocation);
	}
	
	//the allocation upon his requests
	//For testing JSON

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public Bidder getBidder() {
		return bidder;
	}

	public void setBidder(Bidder bidder) {
		this.bidder = bidder;
	}
	
}
