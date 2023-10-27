package cbit.vcell.export.server.datacontainer;

import cbit.vcell.resource.PropertyLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AggregateDataSize {
    private final static Logger lg = LogManager.getLogger(AggregateDataSize.class);

    private static final long ONE_HUNDRED_MB = 1024*1024*100;
    private static final long MEMORY_LIMIT;

    static {
        MEMORY_LIMIT = PropertyLoader.getLongProperty(
                PropertyLoader.exportMaxInMemoryLimit,
                AggregateDataSize.ONE_HUNDRED_MB); // Default if not defined
    }
    private long amountOfData;

    public AggregateDataSize(){
        this(0);
    }
    public AggregateDataSize(long initialSize){
        this.amountOfData = initialSize;
    }

    public boolean hasEnoughRoomFor(long amount){
        return (this.amountOfData + amount) > MEMORY_LIMIT;
    }


    /**
     *
     * @param increase the amount of bytes you're trying to increase by.
     * @throws RuntimeException if the change would violate the maximum memory limit; use `hasEnoughRoomFor(long)`
     * prior to calling this method
     */
    public void increaseDataAmount(long increase){
            if (!this.hasEnoughRoomFor(increase)) throw new RuntimeException("Unavailable memory requested");
            this.amountOfData += increase;
    }

    /**
     *
     * @param decrease the amount of bytes you're trying to decrease by.
     * @throws RuntimeException if the change would violate the minimum memory limit (of '0' bytes).
     */
    public void decreaseDataAmount(long decrease){
        if (!this.hasEnoughRoomFor(decrease)) throw new RuntimeException("Change would create \"negative\" memory");
        this.amountOfData += decrease;
    }

    public boolean isMaxedOut(){
        return this.amountOfData > AggregateDataSize.MEMORY_LIMIT;
    }
}
