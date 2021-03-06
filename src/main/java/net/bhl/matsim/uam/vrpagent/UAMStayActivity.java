package net.bhl.matsim.uam.vrpagent;

import net.bhl.matsim.uam.schedule.UAMStayTask;
import org.matsim.contrib.dynagent.DynActivity;

/**
 * An implementation of AbstractDynActivity for UAM DynAgents for the
 * UAMStayTask.
 *
 * @author balacmi (Milos Balac), RRothfeld (Raoul Rothfeld)
 */
public class UAMStayActivity implements DynActivity {
	final private UAMStayTask stayTask;
	private final String activityType;
	private double now;

	public UAMStayActivity(UAMStayTask stayTask) {
		activityType = stayTask.getName();
		this.stayTask = stayTask;
		this.now = stayTask.getBeginTime();
	}

	@Override
	public void doSimStep(double now) {
		this.now = now;
	}

	@Override
	public String getActivityType() {
		return activityType;
	}

	@Override
	public double getEndTime() {
		if (Double.isInfinite(stayTask.getEndTime())) {
			return now + 1;
		} else {
			return stayTask.getEndTime();
		}
	}

}
