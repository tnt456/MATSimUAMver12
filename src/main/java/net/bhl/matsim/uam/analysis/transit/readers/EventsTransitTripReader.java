package net.bhl.matsim.uam.analysis.transit.readers;

import ch.ethz.matsim.baseline_scenario.transit.events.PublicTransitEvent;
import ch.ethz.matsim.baseline_scenario.transit.events.PublicTransitEventMapper;
import net.bhl.matsim.uam.analysis.transit.TransitTripItem;
import net.bhl.matsim.uam.analysis.transit.listeners.TransitTripListener;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.GenericEvent;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsReaderXMLv1;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

import java.util.Collection;

/**
 * This class is used to retrieve the a collection of {@link TransitTripItem} by
 * using a simulation events file as input
 *
 * @author balacmi (Milos Balac), RRothfeld (Raoul Rothfeld)
 */
public class EventsTransitTripReader {
	final private TransitTripListener tripListener;

	public EventsTransitTripReader(TransitTripListener tripListener) {
		this.tripListener = tripListener;
	}

	/**
	 * @param eventsPath the events file path
	 * @return A collection of {@link TransitTripItem}.
	 */
	public Collection<TransitTripItem> readTrips(String eventsPath) {
		EventsManager eventsManager = EventsUtils.createEventsManager();
		eventsManager.addHandler(tripListener);

		EventsReaderXMLv1 reader = new EventsReaderXMLv1(eventsManager);
		reader.addCustomEventMapper(PublicTransitEvent.TYPE, new MatsimEventsReader.CustomEventMapper() {

			public Event apply(GenericEvent event) {
				double arrivalTime = event.getTime();
				Id<Person> personId = Id.create((String)event.getAttributes().get("person"), Person.class);
				Id<TransitLine> transitLineId = Id.create((String)event.getAttributes().get("line"), TransitLine.class);
				Id<TransitRoute> transitRouteId = Id.create((String)event.getAttributes().get("route"), TransitRoute.class);
				Id<TransitStopFacility> accessStopId = Id.create((String)event.getAttributes().get("accessStop"), TransitStopFacility.class);
				Id<TransitStopFacility> egressStopId = Id.create((String)event.getAttributes().get("egressStop"), TransitStopFacility.class);
				double vehicleDepartureTime = Double.parseDouble((String)event.getAttributes().get("vehicleDepartureTime"));
				double travelDistance = Double.parseDouble((String)event.getAttributes().get("travelDistance"));
				return new PublicTransitEvent(arrivalTime, personId, transitLineId, transitRouteId, accessStopId, egressStopId, vehicleDepartureTime, travelDistance);

			}
		});
		reader.readFile(eventsPath);

		return tripListener.getTransitTripItems();
	}
}
