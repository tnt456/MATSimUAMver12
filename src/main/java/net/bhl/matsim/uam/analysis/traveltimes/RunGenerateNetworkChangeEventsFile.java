package net.bhl.matsim.uam.analysis.traveltimes;

import java.util.ArrayList;
import java.util.List;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.TravelTimeCalculatorConfigGroup;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.NetworkChangeEvent.ChangeType;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkChangeEventsWriter;
import org.matsim.core.trafficmonitoring.TravelTimeCalculator;

/**
 * This script generates a NetworkChangeEvents file containing changes in the
 * network throughout the day. Necessary inputs are in the following order:
 * -Network file; -Events file; -output;
 *
 * @author Aitanm (Aitan Militao), RRothfeld (Raoul Rothfeld)
 */

public class RunGenerateNetworkChangeEventsFile {
	private double timeStep = 15 * 60; // Time step set to 15 minutes
	double minFreeSpeed = 3;
	double endTime = 30 * 60 * 60; // end time set to 30 hours

	public RunGenerateNetworkChangeEventsFile() {
	}

	public static void main(String[] args) throws Exception {
		// ARGS
		int j = 0;
		String networkInput = args[j++];
		String eventsFileInput = args[j++];
		String networkEventsChangeFile = args[j++];

		Config config = ConfigUtils.createConfig();
		config.network().setInputFile(networkInput);

		RunGenerateNetworkChangeEventsFile fileGenerator = new RunGenerateNetworkChangeEventsFile();
		fileGenerator.generateNetworkChangeEventsFile(networkInput, eventsFileInput, networkEventsChangeFile, config);

	}

	public void generateNetworkChangeEventsFile(String networkInput, String eventsFileInput,
			String networkEventsChangeFile, Config config) {
		// Generate networkChangeEvents file for the Time-Dependent Network
		Network networkForReader = NetworkUtils.createNetwork();
		new MatsimNetworkReader(networkForReader).readFile(networkInput);
		TravelTimeCalculator tcc = readEventsIntoTravelTimeCalculator(networkForReader, eventsFileInput,
				config.travelTimeCalculator());
		config.qsim().setEndTime(endTime);
		List<NetworkChangeEvent> networkChangeEvents = createNetworkChangeEvents(networkForReader, tcc,
				config.qsim().getEndTime(), timeStep, minFreeSpeed);
		new NetworkChangeEventsWriter().write(networkEventsChangeFile, networkChangeEvents);
	}

	private TravelTimeCalculator readEventsIntoTravelTimeCalculator(Network network, String eventsFile,
			TravelTimeCalculatorConfigGroup group) {
		EventsManager manager = EventsUtils.createEventsManager();
		TravelTimeCalculator tcc = TravelTimeCalculator.create(network, group);
		manager.addHandler(tcc);
		new MatsimEventsReader(manager).readFile(eventsFile);
		return tcc;
	}

	private List<NetworkChangeEvent> createNetworkChangeEvents(Network network, TravelTimeCalculator tcc,
			Double endTime, Double timeStep, Double MinFreeSpeed) {
		List<NetworkChangeEvent> networkChangeEvents = new ArrayList<>();
		for (Link l : network.getLinks().values()) {

			//if (l.getId().toString().startsWith("pt")) continue;

			double length = l.getLength();
			double previousTravelTime = l.getLength() / l.getFreespeed();

			for (double time = 0; time < endTime; time = time + timeStep) {

				double newTravelTime = tcc.getLinkTravelTimes().getLinkTravelTime(l, time, null, null);

				if (newTravelTime != previousTravelTime) {
					// log.warn("Linkd ID: "+ l.getId()+" previousTravelTime: "+previousTravelTime+"
					// NewTravelTime: "+ newTravelTime);
					NetworkChangeEvent nce = new NetworkChangeEvent(time);
					nce.addLink(l);
					double newFreespeed = length / newTravelTime;
					if (newFreespeed < MinFreeSpeed)
						newFreespeed = MinFreeSpeed;
					NetworkChangeEvent.ChangeValue freespeedChange = new NetworkChangeEvent.ChangeValue(
							ChangeType.ABSOLUTE_IN_SI_UNITS, newFreespeed);
					nce.setFreespeedChange(freespeedChange);

					networkChangeEvents.add(nce);
					previousTravelTime = newTravelTime;
				}
			}
		}
		return networkChangeEvents;
	}

}