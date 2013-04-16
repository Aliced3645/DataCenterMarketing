package net.floodlightcontroller.datacentermarketing.logic;

import java.util.Hashtable;

public class PriceAdjuster {
	
	
	//per unit price..
	Hashtable<Resource, Integer> currentPrices;
	
	private static PriceAdjuster instance = null;

	private PriceAdjuster() {
	}

	public static PriceAdjuster getInstance() {
		if (instance == null)
			instance = new PriceAdjuster();
		return instance;
	}

	public void adjustPrice() {
		return;
	}
	
	
	
}
