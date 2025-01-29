package org.vcell.cli.commands;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;

public abstract class TimeLimitedCommand implements Callable<Integer> {
    private static final Logger logger = LogManager.getLogger(TimeLimitedCommand.class);

    private long timeout;
    private TimeUnit timeUnit;

    /**
     * All the processing done related to validating and storing the arguments provided at runtime.
     * @return true if the execution needs to proceed, otherwise false
     * <br/>
     * Note that returning false does NOT mean execution failed, but rather succeeded (think: show help / version)
     */
    protected abstract boolean executionShouldContinue();

    /**
     * Perform the desired command
     * @return return code of the command
     */
    protected abstract Integer executeCommand();

    protected long getTimeout() {return this.timeout;}

    protected TimeUnit getTimeUnit() {return this.timeUnit;}

    protected void setTimeout(long timeout, TimeUnit timeUnit) {
        this.timeout = timeout <= 0 ? 0 : timeout;
        this.timeUnit = timeUnit;
        if (this.timeout == 0) return;

        // Calculate equivalent times for human readability (only works for whole units)
        if (timeUnit == TimeUnit.MILLISECONDS && timeout % 1000 == 0){
            this.timeout /= 1000;
            this.timeUnit = TimeUnit.SECONDS;
        }

        if (timeUnit == TimeUnit.SECONDS && timeout % 60 == 0){
            this.timeout /= 60;
            this.timeUnit = TimeUnit.MINUTES;
        }

        if (timeUnit == TimeUnit.MINUTES && timeout % 60 == 0){
            this.timeout /= 60;
            this.timeUnit = TimeUnit.HOURS;
        }
    }


    @Override
    public Integer call() throws Exception {
        if (!this.executionShouldContinue()) return 0;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> futureResult = executor.submit(this::executeCommand);
        try {
            return this.timeout == 0 ? futureResult.get() : futureResult.get(this.timeout, this.timeUnit);
        } catch (TimeoutException e) {
            // In the event of timeout, we want to log like we're talking from the command in question
            // We'll leave an in-log reference to the actual class and method
            String debugSnipIt = logger.getLevel().isInRange(Level.TRACE, Level.DEBUG) ? "(TimeLimitedCommand::call) " : "";
            String msg = String.format("%sTask too too long, exceeding %s %s.", debugSnipIt, this.timeout, this.timeUnit.toString().toLowerCase());
            LogManager.getLogger(this.getClass()).error(msg);
            return -1;
        } catch (Exception e) {
            logger.fatal("Unexpected error during execution", e);
            return 1;
        } finally {
            executor.shutdown();
        }
    }
}
