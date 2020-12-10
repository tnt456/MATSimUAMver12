package net.bhl.matsim.uam.qsim;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.bhl.matsim.uam.data.UAMFleetData;
import net.bhl.matsim.uam.dispatcher.UAMClosestRangedPreferPooledDispatcher;
import net.bhl.matsim.uam.dispatcher.UAMDispatcher;
import net.bhl.matsim.uam.dispatcher.UAMDispatcherListener;
import net.bhl.matsim.uam.dispatcher.UAMManager;
import net.bhl.matsim.uam.infrastructure.UAMVehicle;
import net.bhl.matsim.uam.infrastructure.readers.UAMXMLReader;
import net.bhl.matsim.uam.passenger.UAMRequestCreator;
import net.bhl.matsim.uam.schedule.UAMOptimizer;
import net.bhl.matsim.uam.schedule.UAMSingleRideAppender;
import net.bhl.matsim.uam.schedule.UAMStayTask;
import net.bhl.matsim.uam.vrpagent.UAMActionCreator;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.fleet.DvrpVehicleSpecification;
import org.matsim.contrib.dvrp.fleet.Fleet;
import org.matsim.contrib.dvrp.optimizer.VrpOptimizer;
import org.matsim.contrib.dvrp.passenger.PassengerEngineQSimModule;
import org.matsim.contrib.dvrp.passenger.PassengerRequestCreator;
import org.matsim.contrib.dvrp.passenger.PassengerRequestEventToPassengerEngineForwarder;
import org.matsim.contrib.dvrp.run.AbstractDvrpModeQSimModule;
import org.matsim.contrib.dvrp.run.DvrpMode;
import org.matsim.contrib.dvrp.run.DvrpModes;
import org.matsim.contrib.dvrp.tracker.OnlineTrackerListener;
import org.matsim.contrib.dvrp.vrpagent.VrpAgentLogic.DynActionCreator;
import org.matsim.contrib.dvrp.vrpagent.VrpAgentSourceQSimModule;
import org.matsim.contrib.dvrp.vrpagent.VrpLeg;
import org.matsim.contrib.dvrp.vrpagent.VrpLegFactory;
import org.matsim.core.mobsim.framework.MobsimTimer;
import org.matsim.core.mobsim.qsim.PreplanningEngine;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.mobsim.qsim.components.QSimComponentsConfig;
import org.matsim.core.mobsim.qsim.interfaces.DepartureHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.bhl.matsim.uam.run.UAMConstants.uam;

/**
 * A MATSim Abstract Module for classes used by Qsim for UAM simulation.
 *
 * @author balacmi (Milos Balac), RRothfeld (Raoul Rothfeld)
 */
public class UAMQsimModule extends AbstractDvrpModeQSimModule {
	public final static String COMPONENT_NAME = uam.toUpperCase() + "Extension";
	private UAMManager uamManager;
	private UAMXMLReader uamReader;

	public UAMQsimModule(UAMXMLReader uamReader, UAMManager uamManager) {
		super(uam);
		this.uamReader = uamReader;
		this.uamManager = uamManager;
	}

	public static void configureComponents(QSimComponentsConfig components) {
		components.addComponent(DvrpModes.mode(uam));
	}

	@Override
	protected void configureQSim() {
		provideData();
		bind(MobsimTimer.class);
		bind(PassengerRequestEventToPassengerEngineForwarder.class);
		bind(PreplanningEngine.class);


		this.install(new VrpAgentSourceQSimModule(this.getMode()));
		this.install(new PassengerEngineQSimModule(this.getMode()));

		this.bindModal(PassengerRequestCreator.class).to(UAMRequestCreator.class);
		this.bindModal(DynActionCreator.class).to(UAMActionCreator.class);
		this.bindModal(VrpOptimizer.class).to(UAMOptimizer.class);

		this.bind(UAMOptimizer.class);
		this.bind(UAMDispatcherListener.class);

		this.bindModal(UAMDispatcherListener.class).to(UAMDispatcherListener.class);
		this.bindModal(Fleet.class).to(UAMFleetData.class);

		this.bindModal(UAMSingleRideAppender.class).to(UAMSingleRideAppender.class);
		this.bind(UAMSingleRideAppender.class);
		this.bind(UAMDepartureHandler.class);

		this.bindModal(DepartureHandler.class).to(UAMDepartureHandler.class);
		this.addModalQSimComponentBinding().to(UAMDispatcherListener.class);
		this.addModalQSimComponentBinding().to(UAMOptimizer.class);
		this.addModalQSimComponentBinding().to(UAMDepartureHandler.class);
	}

	@Provides
	@Singleton
	VrpLegFactory provideLegCreator(@DvrpMode(uam) final VrpOptimizer optimizer,final QSim qSim) {
		return new VrpLegFactory() {
			@Override
			public VrpLeg create(DvrpVehicle vehicle) {
				return VrpLegFactory.createWithOnlineTracker(TransportMode.car, vehicle,
						(OnlineTrackerListener) optimizer, qSim.getSimTimer());
			}
		};
	}

	@Provides
	@Singleton
	List<UAMDispatcher> provideDispatchers(UAMSingleRideAppender appender, UAMManager uamManager,
										   @Named(uam) Network network, @DvrpMode(uam) Fleet data) {

		UAMDispatcher dispatcher = new UAMClosestRangedPreferPooledDispatcher(appender, uamManager, network, data);

		List<UAMDispatcher> dispatchers = new ArrayList<>();
		dispatchers.add(dispatcher);
		return dispatchers;
	}

	@Provides
	@Singleton
	public UAMFleetData provideData() {
		Map<Id<DvrpVehicle>, UAMVehicle> returnVehicles = new HashMap<>();

		for (DvrpVehicleSpecification specification : uamReader.getFleetSpecification().getVehicleSpecifications()
				.values()) {

			returnVehicles.put(specification.getId(),
					new UAMVehicle(specification, uamReader.getVehicles().get(specification.getId()).getStartLink(),
							uamReader.getVehicles().get(specification.getId()).getInitialStationId(),
							uamReader.getVehicles().get(specification.getId()).getVehicleType()));
		}

		for (DvrpVehicle veh : returnVehicles.values()) {
			// create a new Fleet every new iteration
			veh.getSchedule()
					.addTask(new UAMStayTask(veh.getServiceBeginTime(), Double.POSITIVE_INFINITY, veh.getStartLink()));
			returnVehicles.put(veh.getId(), (UAMVehicle) veh);
		}

		// populate manager here
		uamManager.setVehicles(returnVehicles);
		return new UAMFleetData(returnVehicles);
	}

}
