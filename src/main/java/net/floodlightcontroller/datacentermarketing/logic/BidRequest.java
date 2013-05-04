package net.floodlightcontroller.datacentermarketing.logic;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import net.floodlightcontroller.datacentermarketing.MarketManager;
import net.floodlightcontroller.datacentermarketing.messagepasser.BidRequestJSONSerializer;
import net.floodlightcontroller.datacentermarketing.messagepasser.BidResultJSONSerializer;
import net.floodlightcontroller.routing.Route;

import org.codehaus.jackson.map.annotate.JsonSerialize;
@JsonSerialize(using=BidRequestJSONSerializer.class)
public class BidRequest implements Serializable{
	
	private static final long serialVersionUID = 1L;

	//Referenced Andrew's paper about marketing
	private Bidder bidder;
	
	//The total value user uses to bid
	private float bidValue;
	
	//the end host IP addresses for the requested bided flow
	private long sourceID;
	private long destID;
	
	public boolean valid = false;
	
	//a key-value pair list for resource and required amount
	private HashMap<Resource, Long> requiredResources;
	
	/**
	 * the collection of feasible routes 
	 * which passed the latency check
	 */
	
	private Collection<Route> verifiedRoutes;
	public Collection<Route> getPossibleRoutes(){
		return verifiedRoutes;
	}
	
	//Set by the latency verification process
	private long probedLatency;
	
	public synchronized long getProbedLatency() {
		return probedLatency;
	}

	public synchronized void setProbedLatency(long probedLatency) {
		this.probedLatency = probedLatency;
	}

	/**
	 * Must be called when got the request
	 * @throws Exception 
	 */
	public void verifyPossibleRoutesByLatency() throws Exception{
		String specialMatchingString = "1.2.3." + sourceID;
		ArrayList<Route> possibleRoutes = MarketManager.getInstance().getNonLoopPaths(sourceID, destID);
		if(possibleRoutes == null || possibleRoutes.isEmpty() )
			return;
		
		Auctioneer.getInstance().setCurrentRequestWaitingLatencyVerification(this);
		//For each route, ping for them.
		for(Route rt : possibleRoutes){
			//probe one time now
			boolean res = MarketManager.getInstance().getLowLevelController().probeLatency(specialMatchingString, specialMatchingString, rt, true);
			if(res){
				synchronized(this){
					this.wait(); // block until being waken up
				}
			}
			else{
				this.verifiedRoutes.clear();
				return;
			}
			/**
			 * waken up, now the probed latency has been set
			 */
			float requiredLatency = this.requiredResources.get(Resource.LATENCY);
			System.out.println("\n\n\nWAKEN UP!!!!! " +this.probedLatency + " Required" + requiredLatency);
			// compare the latency
			if(requiredLatency > this.probedLatency){
				//pass
				System.out.println("HEYHEYHEY\n\n\n\n\n");
				verifiedRoutes.add(rt);
			}
		}
		return;
	}
	
	public BidRequest(){
		this.requiredResources = new HashMap<Resource, Long>();
		this.verifiedRoutes = new LinkedList<Route>();
	}
	
	public BidRequest(Bidder _bidder, long _sourceID, long _destID, float _bidValue, HashMap<Resource, Long> _requiredResources){
		this.bidder = _bidder;
		this.bidValue = _bidValue;
		this.requiredResources = _requiredResources;
		this.sourceID = _sourceID;
		this.destID = _destID;
	}
	
	public String toString(){
		return "BidderID: " + bidder.getBidderID() + " Value: " + bidValue + " Source ID: " + sourceID + " Dest ID: " + destID + " " + requiredResources ;
	}
	
	public Bidder getBidder() {
		return bidder;
	}

	public void setBidder(Bidder bidder) {
		this.bidder = bidder;
	}

	public float getBidValue() {
		return bidValue;
	}

	public void setBidValue(float bidValue) {
		this.bidValue = bidValue;
	}

	public long getSourceID() {
		return sourceID;
	}

	public void setSourceID(long sourceID) {
		this.sourceID = sourceID;
	}

	public long getDestID() {
		return destID;
	}

	public void setDestID(long destID) {
		this.destID = destID;
	}

	public HashMap<Resource, Long> getRequiredResources() {
		return requiredResources;
	}

	public void setRequiredResources(HashMap<Resource, Long> requiredResources) {
		this.requiredResources = requiredResources;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void addRequestField(Resource resource, long amount){
		if(requiredResources.containsKey(resource)){
			requiredResources.put(resource, requiredResources.get(resource) + amount);
		}
		else{
			requiredResources.put(resource, amount);
		}
	}
	
	public boolean deleteRequestField(Resource resource){
		if(requiredResources.containsKey(resource)){
			requiredResources.remove(resource);
			return true;
		}
		return false;
	}
	
	public long getStartTime(){
		return requiredResources.get(Resource.START_TIME);
	}
	
	public long getEndTime(){
		return requiredResources.get(Resource.END_TIME);
	}
	
	public float getMinBandwidth(){
		return requiredResources.get(Resource.MIN_RATE);
	}
	
	public long getMaxBandwidth(){
		return requiredResources.get(Resource.MAX_RATE);
	}
	
	public long getData(){
		return requiredResources.get(Resource.DATA);
	}

	
}
