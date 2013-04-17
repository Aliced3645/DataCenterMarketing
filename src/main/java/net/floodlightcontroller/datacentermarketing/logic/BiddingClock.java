package net.floodlightcontroller.datacentermarketing.logic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import net.floodlightcontroller.datacentermarketing.messagepasser.ClockJSONSerializer;

import org.codehaus.jackson.map.annotate.JsonSerialize;
@JsonSerialize(using=ClockJSONSerializer.class)
public class BiddingClock implements Runnable{
	//performs as a thread to take care of time
	//send hint to auctioneer when the auction cycle begins/ends
	
	//Singleton
	ActionListener roundCleaner = new ActionListener() {
	     public void actionPerformed(ActionEvent evt) {
	    	 //pause the ticker when computing the allocations
	    	 tickTimer.stop();
	    	 //time is up
	    	 Auctioneer.getInstance().setBusy();
	    	 Auctioneer.getInstance().computeAllocation();
	    	 Auctioneer.getInstance().setNotBusy();
	    	
	    	 PriceAdjuster.getInstance().adjustPrice(); 	 
	    	 Auctioneer.getInstance().clearRound();
	    	 //a new round
	    	 Auctioneer.getInstance().round ++ ;
	    	 tickTimer.start();
	     }
	 };
	 
	 ActionListener ticker = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			synchronized(this){
				currentTime ++;
			}
		}
		
	 };
	  
	private BiddingClock(){
		currentTime = 0;
		biddingTimer = new Timer(roundTime, roundCleaner);
		biddingTimer.start();
		tickTimer = new Timer(tick, ticker);
		tickTimer.start();
		
	}
	
	private static BiddingClock _instance = null;
	public static BiddingClock getInstance(){
		if(_instance == null)
			_instance = new BiddingClock();
		return _instance;
	}

	//in milliseconds
	int roundTime= 3000;
	int tick = 100;
	private Timer biddingTimer;
	private Timer tickTimer;
	
	long currentTime;
	
	public long getCurrentTime(){
		synchronized (this){
			return currentTime;
		}
	}
	
	@Override
	//send the auctioneer signals
	//when a bidding round begins/ends ???
	public void run() {
		// TODO Auto-generated method stub
	}
	
}
