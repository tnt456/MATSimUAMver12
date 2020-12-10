package net.bhl.matsim.uam.passenger;

import com.google.inject.Inject;
import net.bhl.matsim.uam.data.UAMStationConnectionGraph;
import net.bhl.matsim.uam.dispatcher.UAMDispatcher;
import net.bhl.matsim.uam.dispatcher.UAMManager;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Route;
import org.matsim.contrib.dvrp.optimizer.Request;
import org.matsim.contrib.dvrp.passenger.PassengerRequest;
import org.matsim.contrib.dvrp.passenger.PassengerRequestCreator;

import java.util.List;

/**
 * This class creates a UAM request.
 *
 * @author balacmi (Milos Balac), RRothfeld (Raoul Rothfeld)
 */
public class UAMRequestCreator implements PassengerRequestCreator {

    @Inject
    List<UAMDispatcher> dispatchers;

    @Inject
    private UAMStationConnectionGraph stationConnectionutilities;

    @Inject
    private UAMManager uamManager;


    @Override
    public PassengerRequest createRequest(Id<Request> id, Id<Person> id1, Route route, Link link, Link link1, double v, double v1) {
        double distance = stationConnectionutilities.getFlightLeg(
                uamManager.getStations().getNearestUAMStation(link).getId(),
                uamManager.getStations().getNearestUAMStation(link1).getId()).distance;
        return new UAMRequest(id, id1, link, link1, v, v1, dispatchers.get(0), distance);

    }
}
