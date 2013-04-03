package net.floodlightcontroller.datacentermarketing.logic;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;



//Auctioneer is a singleton class
public class Auctioneer {
	//the auctioneer maintains a pool of existing bidders
	
	private static Auctioneer _instance = null;
	ConcurrentHashMap<String, Bidder> bidders;
	
	//a list of requests received 
	
	
	
	private Auctioneer(){
		super();
		bidders = null;
	}
	
	private Auctioneer(ConcurrentHashMap<String, Bidder> bidders) {
		super();
		this.bidders = bidders;
	}
	
	public static Auctioneer getInstance(){
		if(_instance == null){
			_instance = new Auctioneer();
		}
		
		return _instance;
	}
	
	
	
}
