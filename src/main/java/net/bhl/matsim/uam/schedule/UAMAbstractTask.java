package net.bhl.matsim.uam.schedule;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.matsim.contrib.dvrp.schedule.Task;
import org.matsim.contrib.dvrp.tracker.TaskTracker;

abstract class UAMAbstractTask implements Task {
    // ==== BEGIN: fields managed by ScheduleImpl
    int taskIdx;
    TaskStatus status;
    // ==== END: fields managed by ScheduleImpl

    private final TaskType taskType;

    private double beginTime;
    private double endTime;

    private TaskTracker taskTracker;

    UAMAbstractTask(TaskType taskType, double beginTime, double endTime) {
        Preconditions.checkArgument(beginTime <= endTime, "beginTime=%s; endTime=%s", beginTime, endTime);
        this.taskType = Preconditions.checkNotNull(taskType);
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    @Override
    public final TaskType getTaskType() {
        return taskType;
    }

    @Override
    public final TaskStatus getStatus() {
        return status;
    }

    @Override
    public final int getTaskIdx() {
        return taskIdx;
    }

    @Override
    public final double getBeginTime() {
        return beginTime;
    }

    @Override
    public final double getEndTime() {
        return endTime;
    }

    @Override
    public final void setBeginTime(double beginTime) {
        Preconditions.checkState(status != TaskStatus.STARTED && status != TaskStatus.PERFORMED,
                "It is too late to change the beginTime");
        this.beginTime = beginTime;
    }

    @Override
    public final void setEndTime(double endTime) {
        Preconditions.checkState(status != TaskStatus.PERFORMED, "It is too late to change the endTime");
        this.endTime = endTime;
    }

    @Override
    public final TaskTracker getTaskTracker() {
        Preconditions.checkState(status == TaskStatus.STARTED, "Allowed only for STARTED tasks");
        return taskTracker;
    }

    @Override
    public final void initTaskTracker(TaskTracker taskTracker) {
        Preconditions.checkState(this.taskTracker == null, "Tracking already initialized");
        Preconditions.checkState(status == TaskStatus.STARTED, "Allowed only for STARTED tasks");
        this.taskTracker = taskTracker;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("taskType", taskType)
                .add("taskIdx", taskIdx)
                .add("status", status)
                .add("beginTime", beginTime)
                .add("endTime", endTime)
                .toString();
    }
}
