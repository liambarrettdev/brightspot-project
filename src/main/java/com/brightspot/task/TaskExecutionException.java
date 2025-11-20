package com.brightspot.task;

import javax.servlet.ServletException;

public class TaskExecutionException extends ServletException {

    public TaskExecutionException(String message) {
        super(message);
    }
}
