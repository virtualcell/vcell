package org.vcell.sbml.vcell.lazy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.sbml.vcell.SBMLDataRecord;

import java.util.List;
import java.util.concurrent.Callable;

public abstract class LazySBMLDataAccessor {
    private static final Logger lg = LogManager.getLogger(LazySBMLDataAccessor.class);

    private final boolean shouldUseCaching;
    // First term is the flat data array, second is the dimensions of the data; data can not be jagged at this time!
    private SBMLDataRecord cachedData;
    // We want to lazy get the data, to avoid as much in memory usage as possible
    private final Callable<SBMLDataRecord> lazyMethod;
    private final int flatSize;
    private final List<Integer> spatialDimensions;
    private final List<Double> desiredTimes;

    /**
     * Creates a lazy accessor from runs of SBML-originated models
     * @param lazyMethod the callable method that will access the data lazily; by design, it is highly recommended
     *                   the data being accessed is not stored in RAM: that would defeat the purpose of lazy access.
     * @param flatSize the size of the (expected) data.
     */
    protected LazySBMLDataAccessor(Callable<SBMLDataRecord> lazyMethod, int flatSize, List<Integer> spatialDimensions, List<Double> desiredTimes) {
        this(lazyMethod, flatSize, spatialDimensions, desiredTimes, true);
    }

    /**
     *
     * @param lazyMethod the callable method that will access the data lazily; by design, it is highly recommended
     *                   the data being accessed is not stored in RAM: that would defeat the purpose of lazy access.
     * @param flatSize the size of the (expected) data.
     * @param shouldUseCaching whether to cache the data collected by the lazyMethod or not.
     */
    protected LazySBMLDataAccessor(Callable<SBMLDataRecord> lazyMethod, int flatSize, List<Integer> spatialDimensions, List<Double> desiredTimes, boolean shouldUseCaching) {
        if (lazyMethod == null) throw new IllegalArgumentException("lazyMethod cannot be null");
        this.shouldUseCaching = shouldUseCaching;
        this.lazyMethod = lazyMethod;
        this.cachedData = null;
        this.flatSize = flatSize;
        this.spatialDimensions = spatialDimensions;
        this.desiredTimes = desiredTimes;
    }

    /**
     * Fetches the data pointed to by this object. Note: This getter is using lazy access, O(1) is not to be expected,
     * unless caching is implemented and the method has already been called previously.
     * @return the flat data and it's dimensions, as a VCell Pair
     * @throws Exception if the lazy method itself throws an exception.
     */
    public SBMLDataRecord getData() throws Exception {
        if (this.cachedData != null) return this.cachedData; // We only set `cachedData` to a not-null value if we `shouldUseCaching`
        SBMLDataRecord result = this.lazyMethod.call();
        int product = 1; for (int i : result.dimensions()) product *= i;
        //if (this.flatSize != product) lg.warn("Flat size mismatch: final calculated flat-size ({}) != set value ({})", product, this.flatSize);
        if (this.shouldUseCaching) this.cachedData = result;
        return result;
    }

    /**
     * Returns the flat size of the data to be lazily accessed.
     * @return the flat size of the data
     */
    public int getFlatSize() { return this.flatSize; }

    public List<Integer> getSpatialDimensions() { return this.spatialDimensions; }

    public List<Double> getDesiredTimes() { return this.desiredTimes; }
}
