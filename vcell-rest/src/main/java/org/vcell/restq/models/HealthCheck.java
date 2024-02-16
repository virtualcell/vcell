package org.vcell.restq.models;

public record HealthCheck(
        String statusName,
        int statusCode,
        String message,
        long elapsedTime_MS
        ) {
        public static enum HealthEventType {
                LOGIN_START,
                LOGIN_FAILED,
                LOGIN_SUCCESS,
                RUNSIM_START,
                RUNSIM_SUBMIT,
                RUNSIM_FAILED,
                RUNSIM_SUCCESS
        }

}

