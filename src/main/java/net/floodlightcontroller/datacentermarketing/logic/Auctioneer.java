package net.floodlightcontroller.datacentermarketing.logic;

import java.io.IOException;
import java.io.IOException;  
import java.util.logging.FileHandler;  
import java.util.logging.Level;  
import java.util.logging.Logger;  
import java.util.logging.SimpleFormatter;  
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.Hashtable;

import net.floodlightcontroller.datacentermarketing.MarketManager;
import net.floodlightcontroller.datacentermarketing.messagepasser.RESTQuerier;

//Auctioneer is a singleton class
public class Auctioneer {
	// the auctioneer maintains a pool of existing bidders

	private boolean busyFlag = false;
	public static int round = 0;
	private Logger auctionLogger = Logger.getLogger("Auction Logger");
	FileHandler fh;  

	public BidRequest currentRequestWaitingLatencyVerification;
	private float totalIncome = 0;
	
	public void addTotalIncome(float value){
		totalIncome += value;
	}
	
	public float getTotalIncome(){
		return totalIncome;
	}
	
	public synchronized BidRequest getCurrentRequestWaitingLatencyVerification() {
		return currentRequestWaitingLatencyVerification;
	}

	public synchronized void setCurrentRequestWaitingLatencyVerification(
			BidRequest currentRequestWaitingLatencyVerification) {
		this.currentRequestWaitingLatencyVerification = currentRequestWaitingLatencyVerification;
	}
	
	public synchronized void setBusy() {
		// busy if calculating the bidding for this round
		this.busyFlag = true;
	}

	public synchronized boolean isBusy() {
		return this.busyFlag;
	}

	public synchronized void setNotBusy() {
		this.busyFlag = false;
		// IF there are something in the NextRoundrequests
		// add them into thisRound request
		if (this.requestsForNextRound != null) {
			requestsForThisRound.putAll(requestsForNextRound);
		}
	}

	private static Auctioneer _instance = null;

	// current policy in resource allocaiton for this round
	private AuctioneerStrategy strategy;
	private LinkedHashMap<String, BidResult> resultsForThisRound;

	// collects the bidding requests
	LinkedHashMap<String, BidRequest> requestsForThisRound;
	// If the auctioneer is busy processing bidding for the round,
	// so bids for next round will be stored in this map
	// after doing the calculation, the bids will be moved to
	// requestsForThisRound
	LinkedHashMap<String, BidRequest> requestsForNextRound;

	private Auctioneer() {
		super();
		requestsForThisRound = new LinkedHashMap<String, BidRequest>();
		resultsForThisRound = new LinkedHashMap<String, BidResult>();
		requestsForNextRound = new LinkedHashMap<String, BidRequest>();
		
		//initialize logger
		try {
			fh = new FileHandler("/home/shu/git/DataCenterMarketing/BiddingLog.log");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        auctionLogger.addHandler(fh);  
        SimpleFormatter formatter = new SimpleFormatter();  
        fh.setFormatter(formatter);  
          
        // the following statement is used to log any messages  
        auctionLogger.info("Bidding info logger");  
		
		//default strategy..
		this.strategy = new FirstComeFirstServeStrategy();
		//this.strategy = new EstimationBasedStrategy();
	}

	public synchronized LinkedHashMap<String, BidRequest> getBidRequestForThisRound() {
		return this.requestsForThisRound;
	}

	public synchronized void computeAllocation() throws IOException, InterruptedException, ExecutionException {
		this.resultsForThisRound = strategy
				.processAllocation(requestsForThisRound);
		//update all values
		Set<Entry<String, BidResult>> resultSet = resultsForThisRound.entrySet();
		
		for(Entry<String, BidResult> resultEntry : resultSet){
			BidResult result = resultEntry.getValue();
			float originalIncome = totalIncome;
			if(result.getResult()){
				this.totalIncome += result.getValue();
			}
			float incomeThisRound = totalIncome - originalIncome;
			auctionLogger.info("Bidding Round " + round + ", thisRoundIncome " + incomeThisRound + ", " +
					"total income " + totalIncome + ".");
		}
		
	}
	
	public synchronized void setRouteInstallers(){
		Set<Entry<String, BidResult>> resultSet = resultsForThisRound.entrySet();
		
		for(Entry<String, BidResult> resultEntry : resultSet){
			BidResult result = resultEntry.getValue();

			if(result.getResult()){
				BidRequest bidRequest = result.getBidRequest();
				RouteInstallerFactory.getInstance().createRouteInstaller(
						(long)bidRequest.getStartTime(), (long)bidRequest.getSourceID(), (long)bidRequest.getDestID(), 
						result.getRoute(), (long)(bidRequest.getMinBandwidth()), 
						(short)((bidRequest.getEndTime() - bidRequest.getStartTime()) / 1000), (long)bidRequest.getData());
				
				System.out.println("automan" + bidRequest.getEndTime() + " " + bidRequest.getStartTime() + " " + 
						(short)((bidRequest.getEndTime() - bidRequest.getStartTime()) / 1000));
			}
		}
		
	}
	
	
	
	public synchronized void pushResults() throws IOException, InterruptedException, ExecutionException{
		Set<Entry<String, BidResult>> resultSet = resultsForThisRound.entrySet();
		if(resultSet.size() == 0){
			//It should not happen, the reason it happens is due to the unstable
			//property of floodlight
			//but if it happens, then push the string to all requests
			Set<Entry<String, BidRequest>> requestSet = requestsForThisRound.entrySet();
			for(Entry<String, BidRequest> requestEntry : requestSet){
				BidRequest bidRequest = requestEntry.getValue();
				MarketManager.getInstance().getLowLevelController().pushMessageToHost("1.2.3.4", bidRequest.getSourceID(), "Strange problem in Floodlight");
			}
			return ;
		}
		for(Entry<String, BidResult> resultEntry : resultSet){
			BidResult result = resultEntry.getValue();
			String replyContent;
			if(result.getResult()){
				replyContent = "Congratulations you won the bid";
			}
			else{
				replyContent = "Sorry, you lose your bid";
			}
			
			//send to the host machine
			MarketManager.getInstance().getLowLevelController().pushMessageToHost("1.2.3.4", result.getHostID(), replyContent);
			System.out.println("\nResult pushed\n");
		}
	}

	public static Auctioneer getInstance() {
		if (_instance == null) {
			_instance = new Auctioneer();
		}
		return _instance;
	}

	public synchronized void pushRequest(BidRequest bidRequest) {
		synchronized (this) {
			if (bidRequest != null) {
				if (!this.isBusy())
					requestsForThisRound.put(bidRequest.getBidder()
							.getBidderID(), bidRequest);

				if (this.isBusy())
					requestsForNextRound.put(bidRequest.getBidder()
							.getBidderID(), bidRequest);
			}
		}
	}

	public synchronized void setStrategy(AuctioneerStrategy _strategy) {
		this.strategy = _strategy;
	}

	public synchronized AuctioneerStrategy getStrategy() {
		return this.strategy;
	}

	// a bidding round has ended, clear the round
	public synchronized void clearRound() {
		//synchronized (this) {
			requestsForThisRound.clear();
			if(resultsForThisRound != null)
				resultsForThisRound.clear();
			// If there are bids for the next round, move them to this buffer
			if (!requestsForNextRound.isEmpty()) {
				requestsForThisRound = requestsForNextRound;
				requestsForNextRound = new LinkedHashMap<String, BidRequest>();
			}
			this.setNotBusy();
		//}
	}

	
	public synchronized LinkedHashMap<String, BidResult> getResultsForThisRound() {
		return this.resultsForThisRound;
	}
}
