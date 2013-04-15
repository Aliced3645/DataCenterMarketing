package net.floodlightcontroller.datacentermarketing.logic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


//Auctioneer is a singleton class
public class Auctioneer {
	//the auctioneer maintains a pool of existing bidders
	
	
	private static Auctioneer _instance = null;
	
	//current policy in resource allocaiton for this round
	private AuctioneerStrategy strategy;
	private List<BidResult> resultsForThisRound;
	
	//collects the bidding requests
	ConcurrentLinkedQueue<BidRequest> requests;
	
	private Auctioneer(){
		super();
		requests = new ConcurrentLinkedQueue<BidRequest>();
		resultsForThisRound = new LinkedList<BidResult>();
		BidResult br = new BidResult();
		br.setAllocationResultInString("Congratulations");
		br.setBidderID("Shu Zhang");
		br.setRound(10);
		resultsForThisRound.add(br);
	}
	
	
	public static Auctioneer getInstance(){
		if(_instance == null){
			_instance = new Auctioneer();
		}
		
		return _instance;
	}
	
	public void pushRequest(BidRequest bidRequest){
		if(bidRequest != null){
			requests.add(bidRequest);
		}
	}
	
	public void setStrategy(AuctioneerStrategy _strategy){
		this.strategy = _strategy;
	}
	
	//a bidding round has ended, clear the round
	public void clearRound(){
		requests.clear();
	}

	public List<BidResult> getResultsForThisRound(){
		return this.resultsForThisRound;
	}
	
}
