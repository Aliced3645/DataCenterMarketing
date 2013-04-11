package net.floodlightcontroller.datacentermarketing.logic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;



//Auctioneer is a singleton class
public class Auctioneer {
	//the auctioneer maintains a pool of existing bidders
	
	private static Auctioneer _instance = null;
	HashSet<String> bidderIDs;
	
	//a list of requests received 
	
	
	private Auctioneer(){
		super();
		bidderIDs = null;
	}
	
	
	public static Auctioneer getInstance(){
		if(_instance == null){
			_instance = new Auctioneer();
		}
		
		return _instance;
	}
	
	
	
	
}
