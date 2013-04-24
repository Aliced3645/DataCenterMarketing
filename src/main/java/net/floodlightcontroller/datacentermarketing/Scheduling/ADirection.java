/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

/**
 * @author mininet
 * 
 */
public enum ADirection {
    IN("IN"), OUT("OUT");
    private final String name;

    private ADirection(String s) {
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
