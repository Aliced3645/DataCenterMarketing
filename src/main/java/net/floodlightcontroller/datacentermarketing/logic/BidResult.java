package net.floodlightcontroller.datacentermarketing.logic;

import java.util.LinkedList;

import net.floodlightcontroller.datacentermarketing.Scheduling.Allocation;
import net.floodlightcontroller.datacentermarketing.messagepasser.BidResultJSONSerializer;
import net.floodlightcontroller.routing.Route;

import org.codehaus.jackson.map.annotate.JsonSerialize;

//The class is generated at the end of the auction 
//sent by the auctioneer

@JsonSerialize(using=BidResultJSONSerializer.class)
public class BidResult {

	//round counter (which round generates the result? )
	int round;
	Bidder bidder;
	public float value;
	
	public synchronized float getValue() {
		return value;
	}

	public synchronized void setValue(float value) {
		this.value = value;
	}


	Route route; //which route to go?
	
	public synchronized Route getRoute() {
		return route;
	}

	public synchronized void setRoute(Route route) {
		this.route = route;
	}

	
	BidRequest bidRequest; // the corresbodding request
	
	public synchronized BidRequest getBidRequest() {
		return bidRequest;
	}

	public synchronized void setBidRequest(BidRequest bidRequest) {
		this.bidRequest = bidRequest;
	}


	private long hostID;
	
	public synchronized long getHostID() {
		return hostID;
	}

	public synchronized void setHostID(long hostID) {
		this.hostID = hostID;
	}

	
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
