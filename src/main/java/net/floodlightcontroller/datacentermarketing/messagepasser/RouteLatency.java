/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.messagepasser;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import net.floodlightcontroller.routing.Route;

/**
 * @author mininet
 * 
 */
@JsonSerialize(using = RouteLatencyJSONSerializer.class)
public class RouteLatency extends Object
{
    long latency = 0;

    Route route = null;

    public RouteLatency()
    {
	super();
    }

    public RouteLatency(long latency, Route route)
    {
	super();
	this.latency = latency;
	this.route = route;
    }

    public long getLatency()
    {
	return latency;
    }

    public void setLatency(long latency)
    {
	this.latency = latency;
    }

    public Route getRoute()
    {
	return route;
    }

    public void setRoute(Route route)
    {
	this.route = route;
    }

    public String toString()
    {
	return (route == null ? "NULL" : route.toString())
		+ ((Long) latency).toString();
    }
}
