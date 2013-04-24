package net.floodlightcontroller.datacentermarketing.Scheduling;

public enum Port_Type {
    HALF_DUPLEX("half-duplex"), FULL_DUPLEX("full-duplex");

    private final String name;

    private Port_Type(String s) {
	name = s;
    }

    /*
     * public boolean equalsName(String otherName){ return (otherName == null)?
     * false:name.equals(otherName); }
     */
    public String toString() {
	return name;
    }

}
