package org.uniko.camundaSimulator.engine.agent;

public enum WorkerStatus {

    // available but has no task to execute available
    IDLE,
    // executing a task
    EXECUTING_TASK,
    // at work but in a break
    ON_BREAK,
    // regular absence from work
    OFF_DUTY,
    // absence due to holiday
    ON_HOLIDAY,
    // unscheduled absence from work
    ILLNESS
}
