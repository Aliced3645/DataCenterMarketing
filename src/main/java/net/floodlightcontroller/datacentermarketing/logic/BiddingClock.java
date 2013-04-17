package net.floodlightcontroller.datacentermarketing.logic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class BiddingClock implements Runnable{
	//performs as a thread to take care of time
	//send hint to auctioneer when the auction cycle begins/ends
	
	//Singleton
	ActionListener roundCleaner = new ActionListener() {
	     public void actionPerformed(ActionEvent evt) {
	    	 //time is up
	    	 Auctioneer.getInstance().setBusy();
	    	 Auctioneer.getInstance().computeAllocation();
	    	 Auctioneer.getInstance().setNotBusy();
	    	
	    	 PriceAdjuster.getInstance().adjustPrice(); 	 
	    	 Auctioneer.getInstance().clearRound();
	    	 //a new round
	    	 Auctioneer.getInstance().round ++ ;
	     }
	 };
	  
	private BiddingClock(){
		timer = new Timer(delay, roundCleaner);
		timer.start();		
	}
	
	private static BiddingClock _instance = null;
	public static BiddingClock getInstance(){
		if(_instance == null)
			_instance = new BiddingClock();
		return _instance;
	}

	//in milliseconds
	int delay= 30000;
	private Timer timer;	
	
	long currentTime;
	@Override
	//send the auctioneer signals
	//when a bidding round begins/ends ???
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
