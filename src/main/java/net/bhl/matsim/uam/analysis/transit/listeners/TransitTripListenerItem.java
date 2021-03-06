package net.bhl.matsim.uam.analysis.transit.listeners;

import net.bhl.matsim.uam.analysis.transit.TransitTripItem;
import org.matsim.api.core.v01.Coord;

/**
 * This class stores information about a transit trip that is being read by the
 * {@link TransitTripListener}.
 *
 * @author balacmi (Milos Balac), RRothfeld (Raoul Rothfeld)
 */
public class TransitTripListenerItem extends TransitTripItem {
	public String mode = "unknown";
	public Coord intermediateOrigin;
	public double legDepartureTime;
	public boolean waitingForFirstTransitEvent = true;
}
