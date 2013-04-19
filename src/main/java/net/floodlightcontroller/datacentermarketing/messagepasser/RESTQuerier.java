package net.floodlightcontroller.datacentermarketing.messagepasser;

import java.util.HashMap;
import java.util.HashSet;

import org.restlet.data.Reference;

import net.floodlightcontroller.datacentermarketing.logic.Bidder;


//This class maintains the URL relationship between a REST URI with a bidder ID 
//with the request/result object related to it
//Maintain hashmap: <REST URI, Bidder Object> 

public class RESTQuerier {
	
	//Singleton
	private RESTQuerier(){}
	private static RESTQuerier _instance = null;
	public static RESTQuerier getInstance(){
		if(_instance == null)
			_instance = new RESTQuerier();
		return _instance;
	}
	
	private static String requestTemplate = "http://localhost:8080/marketing/request/";
	private static String resultTemplate =  "http://localhost:8080/marketing/result/";
	
	HashMap<String, Bidder> biddersMap = new HashMap<String, Bidder>();
	
	public void addBidder(String bidderURI, Bidder bidder){
		this.biddersMap.put(bidderURI, bidder);
	}
	
	public String getBidderURIForRequest(Reference ref){
		String totalURI  = ref.toString();
		String bidderURI = totalURI.substring(requestTemplate.length());
		return bidderURI;
	}
	
	public String getBidderURIForResult(Reference ref){
		String totalURI = ref.toString();
		String bidderURI = totalURI.substring(resultTemplate.length());
		return bidderURI;
	}
	
	public Bidder getBidder(String bidderURI){
		return biddersMap.get(bidderURI);
	}
	
	public void setBidder(String bidderURI, Bidder bidder){
		if(bidder != null)
		this.biddersMap.put(bidderURI, bidder);
	}
}
