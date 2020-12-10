package net.bhl.matsim.uam.schedule;

import net.bhl.matsim.uam.passenger.UAMRequest;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.schedule.StayTask;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This task mirrors <code>UAMStayTask</code> in order to make the vehicle
 * unavailable for other trips after landing.
 *
 * @author Aitanm (Aitan Militao), RRothfeld (Raoul Rothfeld)
 */
public class UAMTurnAroundTask extends StayTask implements UAMTask {
	private final Set<UAMRequest> requests = new HashSet<>(); // does it need to store the request list ?

	public UAMTurnAroundTask(double beginTime, double endTime, Link link) {
		super(UAMTaskType.TURNAROUND,beginTime, endTime, link);
	}

	public UAMTurnAroundTask(double beginTime, double endTime, Link link, Collection<UAMRequest> requests) {
		this(beginTime, endTime, link);

		this.requests.addAll(requests);
	}

	@Override
	public UAMTaskType getUAMTaskType() {
		return UAMTaskType.TURNAROUND;
	}

	@Override
	public Collection<UAMRequest> getRequests() {
		return this.requests;
	}

	@Override
	public void addRequest(UAMRequest request) {
		requests.add(request);
	}

}
