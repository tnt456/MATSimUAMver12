package net.bhl.matsim.uam.schedule;

import net.bhl.matsim.uam.passenger.UAMRequest;
import org.matsim.contrib.dvrp.path.VrpPathWithTravelData;
import org.matsim.contrib.dvrp.schedule.DriveTask;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * During this task the UAM vehicle is flying.
 *
 * @author balacmi (Milos Balac), RRothfeld (Raoul Rothfeld)
 */
public class UAMFlyTask extends DriveTask implements UAMTask {
	private final Set<UAMRequest> requests = new HashSet<>();

	public UAMFlyTask(VrpPathWithTravelData path) {
		super(UAMTaskType.FLY,path);
	}

	public UAMFlyTask(VrpPathWithTravelData path, Collection<UAMRequest> requests) {
		this(path);
		this.requests.addAll(requests);
	}

	@Override
	public UAMTaskType getUAMTaskType() {
		return UAMTaskType.FLY;
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
