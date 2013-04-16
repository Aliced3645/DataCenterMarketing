package net.floodlightcontroller.datacentermarketing.logic;

import java.util.HashMap;

import net.floodlightcontroller.datacentermarketing.Scheduling.Scheduler;
import net.floodlightcontroller.datacentermarketing.Scheduling.Switch;

public class PriceAdjuster {
	private PriceAdjuster instance = null;

	private PriceAdjuster() {
	}

	public PriceAdjuster instance() {
		if (instance == null)
			instance = new PriceAdjuster();

		return instance;

	}

	public void adjustPrice() {
		return;
	}
}
