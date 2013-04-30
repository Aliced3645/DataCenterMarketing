package net.floodlightcontroller.datacentermarketing.logic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.Timer;

import net.floodlightcontroller.datacentermarketing.messagepasser.ClockJSONSerializer;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(using = ClockJSONSerializer.class)
public class BiddingClock implements Runnable {
	// performs as a thread to take care of time
	// send hint to auctioneer when the auction cycle begins/ends

	ActionListener ticker = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			synchronized (this) {
				currentTime++;
			}
			if (currentTime % roundTime == 0) {
				
				tickTimer.stop();
				Auctioneer.getInstance().setBusy();
				
				try {
					Auctioneer.getInstance().computeAllocation();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// broadcast result to all participants
				try {
					Auctioneer.getInstance().pushResults();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Auctioneer.getInstance().setNotBusy();
				PriceAdjuster.getInstance().adjustPrice();
				Auctioneer.getInstance().clearRound();
				// a new round
				Auctioneer.getInstance().round++;
				tickTimer.start();
			}
		}

	};

	private BiddingClock() {
		currentTime = 0;
		// biddingTimer = new Timer(roundTime, roundCleaner);
		// biddingTimer.start();
		tickTimer = new Timer(tick, ticker);
		tickTimer.start();

	}

	private static BiddingClock _instance = null;

	public static BiddingClock getInstance() {
		if (_instance == null)
			_instance = new BiddingClock();
		return _instance;
	}

	// in milliseconds
	int roundTime = 100;
	int tick = 100;
	private Timer biddingTimer;
	private Timer tickTimer;

	long currentTime;

	public long getCurrentTime() {
		synchronized (this) {
			return currentTime;
		}
	}

	@Override
	// send the auctioneer signals
	// when a bidding round begins/ends ???
	public void run() {
		// TODO Auto-generated method stub
	}

}
