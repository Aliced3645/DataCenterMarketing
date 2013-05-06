package net.floodlightcontroller.datacentermarketing.logic;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.topology.NodePortTuple;
/**
 * 
 * @author shu
 * Important notion:
 * IN order to simplify the calculation,
 * we only consider one route for each bid request.
 */

public class MaxIncomeStrategy implements AuctioneerStrategy {

	public String toString() {
		return "Maximize income strategy.";
	}

	public class bidRequestStartTimeComparator implements
			Comparator<BidRequest> {

		@Override
		public int compare(BidRequest arg0, BidRequest arg1) {
			// TODO Auto-generated method stub
			if (arg0.getStartTime() > arg1.getStartTime()) {
				return 1;
			} else if (arg0.getStartTime() < arg0.getStartTime()) {
				return -1;
			}
			return 0;
		}
	}
	
	public class OverlappedTimeReqeusts{
		//intersected time period
		private long maxStartTime;
		private long minEndTime;
		
		//the request lists
		private LinkedList<BidRequest> requests;

		public void addRequest(BidRequest bidRequest){
			requests.add(bidRequest);
		}
		
		public OverlappedTimeReqeusts(){
			requests = new LinkedList<BidRequest>();
		}
		
		public synchronized long getMaxStartTime() {
			return maxStartTime;
		}

		public synchronized void setMaxStartTime(long maxStartTime) {
			this.maxStartTime = maxStartTime;
		}

		public synchronized long getMinEndTime() {
			return minEndTime;
		}

		public synchronized void setMinEndTime(long minEndTime) {
			this.minEndTime = minEndTime;
		}

		public synchronized LinkedList<BidRequest> getRequests() {
			return requests;
		}

		public synchronized void setRequests(LinkedList<BidRequest> requests) {
			this.requests = requests;
		}
		
		
	}

	public class OverlappedSwitchPorts{
		OverlappedTimeReqeusts overlappedTimeRequests;
		Set<NodePortTuple> switchPorts;
		
	}
	
	
	@Override
	public LinkedHashMap<String, BidResult> processAllocation(
			LinkedHashMap<String, BidRequest> requests) throws IOException,
			InterruptedException, ExecutionException {

		//every round select the 
		LinkedHashMap<String, BidResult> toReturn = new LinkedHashMap<String, BidResult>();
		Set<Entry<String, BidRequest>> requestSet = requests.entrySet();
		
		//rank the bidrequest according to request start time
		PriorityQueue<BidRequest> startRank = new PriorityQueue<BidRequest>(10, new bidRequestStartTimeComparator());
		
		for (Entry<String, BidRequest> requestEntry : requestSet) {
			BidRequest bidRequest = requestEntry.getValue();
			startRank.offer(bidRequest);
		}
		
		//get intersections of bidRequests with overlapped time period
		BidRequest[] startRankArray = (BidRequest[]) startRank.toArray();
		HashSet<OverlappedTimeReqeusts> overlappedTimes = new HashSet<OverlappedTimeReqeusts>();
		
		for(int i = 0; i < startRankArray.length; i ++){
			LinkedList<BidRequest> overlappedTime = new LinkedList<BidRequest>();
			overlappedTime.add(startRankArray[i]);
			long maxStartTime = startRankArray[i].getStartTime();
			long minEndTime = startRankArray[i].getEndTime();
			
			for(int j = i + 1; j < startRankArray.length; j ++){
				BidRequest next = startRankArray[j];
				if(next.getStartTime() < startRankArray[i].getStartTime()){
					overlappedTime.add(next);
					maxStartTime = next.getStartTime();
					if(next.getEndTime() < minEndTime)
						minEndTime = next.getEndTime();
				}
				
			}
			
			OverlappedTimeReqeusts overlappedTimeRequests = new OverlappedTimeReqeusts();
			overlappedTimeRequests.setRequests(overlappedTime);
			overlappedTimeRequests.setMaxStartTime(maxStartTime);
			overlappedTimeRequests.setMinEndTime(minEndTime);
			overlappedTimes.add(overlappedTimeRequests);
		}
		
		/* for each overlapped time set,
		 * find the overlapped switchports
		 */
		//Set<OverlappedSwitchPort> overlappedSwitchPorts = new HashSet<OverlappedSwitchPort>();
		LinkedList<OverlappedSwitchPorts> overlappedSwitchPortsList = new LinkedList<OverlappedSwitchPorts>();
		Set<HashMap<NodePortTuple, OverlappedTimeReqeusts>> allTimePortRequests 
			= new HashSet<HashMap<NodePortTuple, OverlappedTimeReqeusts>>();
		
		for(OverlappedTimeReqeusts overlappedTimeRequests : overlappedTimes){
			//group them by switch ports
			HashMap<NodePortTuple, OverlappedTimeReqeusts> portOverlappedTimeRequestMap =
					new HashMap<NodePortTuple, OverlappedTimeReqeusts>();
			for(BidRequest bidRequest : overlappedTimeRequests.getRequests()){
				Route rt = (Route) (bidRequest.getPossibleRoutes().toArray())[0];
				//for all nodeTuples..
				for(NodePortTuple nodePort : rt.getPath()){
					if(portOverlappedTimeRequestMap.containsKey(nodePort)){
						portOverlappedTimeRequestMap.get(nodePort).addRequest(bidRequest);
					}
					else{
						OverlappedTimeReqeusts newOverlappedTimeRequests
						 	= new OverlappedTimeReqeusts();
						newOverlappedTimeRequests.addRequest(bidRequest);
						newOverlappedTimeRequests.setMaxStartTime(overlappedTimeRequests.maxStartTime);
						newOverlappedTimeRequests.setMinEndTime(overlappedTimeRequests.minEndTime);
					}
					
				}
				
			}
			allTimePortRequests.add(portOverlappedTimeRequestMap);
		}
		
		
		
		
		return null;
	}
}
