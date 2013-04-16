package net.floodlightcontroller.datacentermarketing.logic;
	
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Hashtable;

//Auctioneer is a singleton class
public class Auctioneer {
	//the auctioneer maintains a pool of existing bidders
	
	private boolean busyFlag= false;
	public static int round = 0;
	
	public void setBusy(){
		//busy if calculating the bidding for this round
		this.busyFlag = true;
	}
	
	public boolean isBusy(){
		return this.busyFlag;
	}
	
	public void setNotBusy(){
		this.busyFlag = false;
		//IF there are something in the NextRoundrequests
		//add them into thisRound request
		if(this.requestsForNextRound != null){
			requestsForThisRound.putAll(requestsForNextRound);
		}
	}
	
	private static Auctioneer _instance = null;
	
	//current policy in resource allocaiton for this round
	private AuctioneerStrategy strategy;
	private Hashtable<String, BidResult> resultsForThisRound;
	
	//collects the bidding requests
	Hashtable<String, BidRequest> requestsForThisRound;
	//If the auctioneer is busy processing bidding for the round, 
	//so bids for next round will be stored in this map
	//after doing the calculation, the bids will be moved to requestsForThisRound
	Hashtable<String, BidRequest> requestsForNextRound;
	
	private Auctioneer(){
		super();
		requestsForThisRound = new Hashtable<String, BidRequest>();
		resultsForThisRound = new Hashtable<String, BidResult>();
		BidResult br = new BidResult();
		br.setAllocationResultInString("Congratulations");
		br.setBidderID("Shu Zhang");
		br.setRound(10);
		resultsForThisRound.put(br.getBidderID(), br);
	}
	
	public Hashtable<String, BidRequest>  getBidRequestForThisRound(){
		return this.requestsForThisRound;
	}
	
	public void computeAllocation(){
		this.resultsForThisRound = strategy.processAllocation(requestsForThisRound);
	}
	
	public static Auctioneer getInstance(){
		if(_instance == null){
			_instance = new Auctioneer();
		}
		
		return _instance;
	}
	
	public void pushRequest(BidRequest bidRequest){
		if(bidRequest != null){
			if(!this.isBusy())
				requestsForThisRound.put(bidRequest.getBidder().getBidderID(), bidRequest);
			
			if(this.isBusy())
				requestsForNextRound.put(bidRequest.getBidder().getBidderID(), bidRequest);
		}
	}
	
	public void setStrategy(AuctioneerStrategy _strategy){
		this.strategy = _strategy;
	}
	
	public AuctioneerStrategy getStrategy(){
		return this.strategy;
	}
	
	//a bidding round has ended, clear the round
	public void clearRound(){
		requestsForThisRound.clear();
		resultsForThisRound.clear();
		//If there are bids for the next round, move them to this buffer
		if(!requestsForNextRound.isEmpty()){
			requestsForThisRound = requestsForNextRound;
			requestsForNextRound = new Hashtable<String, BidRequest>();
		}
		this.setNotBusy();
	}
	
	public Hashtable<String, BidResult> getResultsForThisRound(){
		return this.resultsForThisRound;
	}
}
