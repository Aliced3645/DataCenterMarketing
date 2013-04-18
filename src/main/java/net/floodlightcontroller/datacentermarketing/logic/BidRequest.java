package net.floodlightcontroller.datacentermarketing.logic;

import java.io.Serializable;
import java.util.HashMap;

import net.floodlightcontroller.datacentermarketing.messagepasser.BidRequestJSONSerializer;
import net.floodlightcontroller.datacentermarketing.messagepasser.BidResultJSONSerializer;

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
	
	//a key-value pair list for resource and required amount
	private HashMap<Resource, Float> requiredResources;
	
	public BidRequest(){
		this.requiredResources = new HashMap<Resource, Float>();
	}
	
	public BidRequest(Bidder _bidder, long _sourceID, long _destID, float _bidValue, HashMap<Resource, Float> _requiredResources){
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

	public HashMap<Resource, Float> getRequiredResources() {
		return requiredResources;
	}

	public void setRequiredResources(HashMap<Resource, Float> requiredResources) {
		this.requiredResources = requiredResources;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void addRequestField(Resource resource, float amount){
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
	
	public float getStartTime(){
		return requiredResources.get(Resource.START_TIME);
	}
	
	public float getEndTime(){
		return requiredResources.get(Resource.END_TIME);
	}
	
	public float getMinBandwidth(){
		return requiredResources.get(Resource.MIN_RATE);
	}
	
	public float getMaxBandwidth(){
		return requiredResources.get(Resource.MAX_RATE);
	}
	
	public float getData(){
		return requiredResources.get(Resource.DATA);
	}
	
}
