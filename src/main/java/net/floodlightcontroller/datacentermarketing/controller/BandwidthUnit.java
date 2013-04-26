package net.floodlightcontroller.datacentermarketing.controller;

public enum BandwidthUnit {
    Megabyte(1), Gigabyte(1000), Terabyte(1000000);

    private final int MBFactor;

    private BandwidthUnit(int unit) {
	int uint;
	this.MBFactor = unit;
    }

    public int MBFactor() {
	return this.MBFactor;
    }

}
