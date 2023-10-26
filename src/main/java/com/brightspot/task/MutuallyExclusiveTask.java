package com.brightspot.task;

/**
 * Interface that can be applied to {@link com.psddev.dari.util.Task} class. Implementing classes must all run under the
 * same {@link com.psddev.dari.util.TaskExecutor} and override the {@code shouldContinue} method to check if run is
 * blocked
 */
public interface MutuallyExclusiveTask {

    Boolean isBlocked();
}
