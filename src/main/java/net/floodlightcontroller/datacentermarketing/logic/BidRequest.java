package net.floodlightcontroller.datacentermarketing.logic;

import java.io.Serializable;
import java.util.HashMap;

public class BidRequest implements Serializable{
	
	private static final long serialVersionUID = 1L;

	//Referenced Andrew's paper about marketing
	String bidderID;
	
	//The total value user uses to bid
	float bidValue;
	
	//the end host IP addresses for the requested bided flow
	long sourceID;
	long destID;
	
	//a key-value pair list for resource and required amount
	HashMap<Resource, Float> requiredResources;
	
	public BidRequest(){
		this.requiredResources = new HashMap<Resource, Float>();
	}
	
	public BidRequest(String _bidderID, long _sourceID, long _destID, float _bidValue, HashMap<Resource, Float> _requiredResources){
		this.bidderID = _bidderID;
		this.bidValue = _bidValue;
		this.requiredResources = _requiredResources;
		this.sourceID = _sourceID;
		this.destID = _destID;
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
	
	
}
