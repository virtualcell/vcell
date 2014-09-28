package net.pushover.client;

/**
 * Enumeration to represent the priority of a message
 */
public enum MessagePriority {

    QUIET(-1), NORMAL(0), HIGH(1);

    MessagePriority(int priority) {
        this.priority = priority;
    }

    private final int priority;

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return String.valueOf(this.priority);
    }
}
