package swingthreads;

public enum TaskEventKeys {
    //PROGRESS_POPUP("asynchProgressPopup"),
    //TASK_PROGRESS_INTERVAL("progressRange"),
    TASK_ABORTED_BY_ERROR("abort"),
    TASK_ABORTED_BY_USER("cancel"),
    TASKS_TO_BE_SKIPPED("conditionalSkip"),
    FINAL_WINDOW("finalWindowInterface"),
    STACK_TRACE_ARRAY("clientTaskDispatcherStackTraceArray"), // hash key to store stack trace if {@link #lg} enabled for INFO
    HASH_DATA_ERROR("hdeHdeHde"), // hash key to internally flag problem with hash table data
    ;

    private final String value;

    TaskEventKeys(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
