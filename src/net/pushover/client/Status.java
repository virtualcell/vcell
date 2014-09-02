package net.pushover.client;

/**
 * API response object
 */
public class Status {

    private final Integer status;

    private String requestId;

    public Status(Integer status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return String.format("status: %s, requestId: %s", status != null ? String.valueOf(status) : "??", requestId);
    }
}
