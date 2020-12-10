package net.bhl.matsim.uam.schedule;

import net.bhl.matsim.uam.passenger.UAMRequest;
import net.bhl.matsim.uam.vrpagent.UAMActionCreator;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.schedule.StayTask;

import java.util.Collection;

/**
 * During this task the UAM Vehicle is idle.
 *
 * @author balacmi (Milos Balac), RRothfeld (Raoul Rothfeld)
 */
public class UAMStayTask extends StayTask implements UAMTask {
	public UAMStayTask(double beginTime, double endTime, Link link, String name) {
		super(UAMTaskType.STAY, beginTime, endTime, link);
	}

	public UAMStayTask(double beginTime, double endTime, Link link) {
		this(beginTime, endTime, link, UAMActionCreator.STAY_ACTIVITY_TYPE);
	}


	@Override
	public UAMTaskType getUAMTaskType() {
		return UAMTaskType.STAY;
	}

	@Override
	public Collection<UAMRequest> getRequests() {
		return null;
	}

	@Override
	public void addRequest(UAMRequest request) {

	}

	public String getName() {
		return "";
	}
}
