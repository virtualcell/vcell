package cbit.vcell.export.server.datacontainer;

import cbit.vcell.resource.PropertyLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that tracks how much memory has been consumed, while observing limits.
 */
public class AggregateDataSize {
    private final static Logger lg = LogManager.getLogger(AggregateDataSize.class);

    private static final long ONE_HUNDRED_MB = 1024*1024*100;
    private static final long JAVA_MAXIMUM = 1024L *1024*1024*2; // 2 gigabytes
    private static final long MEMORY_LIMIT;

    static {
        long val = PropertyLoader.getLongProperty(
                PropertyLoader.exportMaxInMemoryLimit,
                AggregateDataSize.ONE_HUNDRED_MB); // Default if not defined
        MEMORY_LIMIT = Math.min(val, JAVA_MAXIMUM);
    }
    private long amountOfData;

    public AggregateDataSize(){
        this(0);
    }
    public AggregateDataSize(int initialSize){
        this.amountOfData = initialSize;
    }

    public boolean hasNoMoreRoomFor(int amount){
        return (this.amountOfData + amount) > MEMORY_LIMIT;
    }

    /**
     * Increases the size of allocation;
     * @param increase the amount of bytes you're trying to increase by.
     * @throws RuntimeException if the change would violate the maximum memory limit; use `hasEnoughRoomFor(long)`
     * prior to calling this method
     */
    public void increaseDataAmount(int increase){
            if (this.hasNoMoreRoomFor(increase)) throw new RuntimeException("Unavailable memory requested");
            this.amountOfData += increase;
    }

    /**
     * Reduces the size of allocation;
     * @param decrease the amount of bytes you're trying to decrease by.
     * @throws RuntimeException if the change would violate the minimum memory limit (of '0' bytes).
     */
    public void decreaseDataAmount(int decrease){
        if (this.hasNoMoreRoomFor(decrease)) throw new RuntimeException("Change would create \"negative\" memory");
        this.amountOfData += decrease;
    }

    @Deprecated
    public boolean isMaxedOut(){
        return this.amountOfData > AggregateDataSize.MEMORY_LIMIT;
    }

    public void reset(){
        this.resetTo(0);
    }

    public void resetTo(long value){
        if (value < 0) throw new IllegalArgumentException("Value can not be negative.");
        this.amountOfData = value;
    }
}
