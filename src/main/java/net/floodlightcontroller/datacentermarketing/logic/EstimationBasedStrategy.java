package net.floodlightcontroller.datacentermarketing.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import net.floodlightcontroller.datacentermarketing.MarketManager;
import net.floodlightcontroller.datacentermarketing.Scheduling.Allocation;
import net.floodlightcontroller.datacentermarketing.Scheduling.Scheduler;
import net.floodlightcontroller.routing.Route;

class BidRequestVersusEstimation implements Comparable {
	// the ratio of bidding value versus estimated class
	// Competitiveness is a term to describe the possibility the
	// request could win the bid. The class is ranked basing on competitiveness
	// in down cascade rank

	float competitiveness;
	BidRequest bidRequest;
	Route route;

	public synchronized float getCompetitiveness() {
		return competitiveness;
	}

	public synchronized void setCompetitiveness(float competitiveness) {
		this.competitiveness = competitiveness;
	}

	public synchronized BidRequest getBidRequest() {
		return bidRequest;
	}

	public synchronized void setBidRequest(BidRequest bidRequest) {
		this.bidRequest = bidRequest;
	}

	public BidRequestVersusEstimation(float _competitiveness,
			BidRequest _bidRequest, Route _route) {
		this.competitiveness = _competitiveness;
		this.bidRequest = _bidRequest;
		this.route = _route;
	}

	public synchronized Route getRoute() {
		return route;
	}

	public synchronized void setRoute(Route route) {
		this.route = route;
	}

	@Override
	public int compareTo(Object right) {
		// used for priority queue
		BidRequestVersusEstimation another = (BidRequestVersusEstimation) right;
		if (this.competitiveness > another.getCompetitiveness())
			return -1;
		if (this.competitiveness == another.getCompetitiveness())
			return 0;
		else
			return 1;
	}
}

public class EstimationBasedStrategy implements AuctioneerStrategy {

	@Override
	public LinkedHashMap<String, BidResult> processAllocation(
			LinkedHashMap<String, BidRequest> requests) throws IOException,
			InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		LinkedHashMap<String, BidResult> toReturn = new LinkedHashMap<String, BidResult>();
		Set<Entry<String, BidRequest>> requestSet = requests.entrySet();

		PriorityQueue<BidRequestVersusEstimation> competitivenessRank = new PriorityQueue<BidRequestVersusEstimation>();

		/**
		 * first round processing For each bid, first validate its feasibility
		 * if feasible, compute the estimated price then put into the
		 * competitivenessRank queue
		 */
		for (Entry<String, BidRequest> requestEntry : requestSet) {
			BidResult result = new BidResult();
			result.setResult(false);
			BidRequest bidRequest = requestEntry.getValue();
			Collection<Route> possibleRoutes = bidRequest.getPossibleRoutes();
			/**
			 * First check feasibility for all routes For each possible route,
			 * select one with highest competitiveness
			 */
			if (possibleRoutes == null) {
				// failed to establish
				toReturn.clear();
				return toReturn;
			}

			float highestCompetitiveness = Integer.MIN_VALUE;
			BidRequestVersusEstimation bestOne = null;
			for (Route route : possibleRoutes) {
				Allocation alloc = new Allocation();
				alloc.setBandwidth(bidRequest.getMinBandwidth());
				alloc.setFrom((long) bidRequest.getStartTime());
				alloc.setTo((long) bidRequest.getEndTime());
				if (Scheduler.getInstance().validateAndReserveRoute(route,
						alloc, false)) {
					/**
					 * The route is feasible, compute the competitiveness
					 */
					float estimatedValue = Scheduler.getInstance()
							.estimatePrice(route, alloc);
					float competitiveness = bidRequest.getBidValue()
							/ estimatedValue;
					if (competitiveness > highestCompetitiveness) {
						highestCompetitiveness = competitiveness;
						bestOne = new BidRequestVersusEstimation(
								competitiveness, bidRequest, route);
					}
					break;
				}
			}
			/**
			 * If no route is installable....
			 */
			if (highestCompetitiveness == Integer.MIN_VALUE) {
				/**
				 * Result is set false to this bid
				 */
				result.setValue(bidRequest.getBidValue());
				result.setBidRequest(bidRequest);
				result.setHostID(bidRequest.getSourceID());
				result.setResult(false);
				result.setBidder(bidRequest.getBidder());
				result.setRound(Auctioneer.round);
				result.getBidder().setLatestResult(result);
				toReturn.put(result.getBidder().getBidderID(), result);
			} else {
				/**
				 * get one feasible route, create the ranking class
				 */
				competitivenessRank.offer(bestOne);
			}
		}

		/**
		 * All possible requests have been ranked Now allocate from them
		 * according to the rank
		 */
		while (!competitivenessRank.isEmpty()) {
			BidRequestVersusEstimation competitiveBid = competitivenessRank
					.poll();
			// whether it is still feasible?
			// If yes, then reserve the route and bandwidth.
			BidRequest bidRequest = competitiveBid.getBidRequest();
			Allocation alloc = new Allocation();
			alloc.setBandwidth(bidRequest.getMinBandwidth());
			alloc.setFrom((long) bidRequest.getStartTime());
			alloc.setTo((long) bidRequest.getEndTime());

			BidResult result = new BidResult();

			if (Scheduler.getInstance().validateAndReserveRoute(
					competitiveBid.getRoute(), alloc, true)) {
				/**
				 * Allocation succeed! set the result as success
				 */
				result.setRoute(competitiveBid.getRoute());
				result.setResult(true);
			} else {
				/**
				 * Allocation failed set the result as failure
				 */
				result.setResult(false);
			}
			result.setValue(bidRequest.getBidValue());
			result.setBidRequest(bidRequest);
			result.setBidder(bidRequest.getBidder());
			result.setRound(Auctioneer.round);
			result.getBidder().setLatestResult(result);
			result.setHostID(bidRequest.getSourceID());
			toReturn.put(result.getBidder().getBidderID(), result);
		}

		return toReturn;
	}

}
