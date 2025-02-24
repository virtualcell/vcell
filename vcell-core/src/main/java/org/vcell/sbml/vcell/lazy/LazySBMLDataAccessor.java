package org.vcell.sbml.vcell.lazy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.sbml.vcell.SBMLDataRecord;
import org.vcell.util.Pair;

import java.util.List;
import java.util.concurrent.Callable;

public abstract class LazySBMLDataAccessor {
    private static final Logger lg = LogManager.getLogger(LazySBMLDataAccessor.class);

    private final boolean shouldUseCaching;
    // First term is the flat data array, second is the dimensions of the data; data can not be jagged at this time!
    private SBMLDataRecord cachedData;
    // We want to lazy get the data, to avoid as much in memory usage as possible
    private final Callable<SBMLDataRecord> lazyMethod;
    private int flatSize;

    /**
     * Creates a lazy accessor from runs of SBML-originated models
     * @param lazyMethod the callable method that will access the data lazily; by design, it is highly recommended
     *                   the data being accessed is not stored in RAM: that would defeat the purpose of lazy access.
     * @param flatSize the size of the (expected) data.
     */
    protected LazySBMLDataAccessor(Callable<SBMLDataRecord> lazyMethod, int flatSize) {
        this(lazyMethod, flatSize, true);
    }

    /**
     *
     * @param lazyMethod the callable method that will access the data lazily; by design, it is highly recommended
     *                   the data being accessed is not stored in RAM: that would defeat the purpose of lazy access.
     * @param flatSize the size of the (expected) data.
     * @param shouldUseCaching whether to cache the data collected by the lazyMethod or not.
     */
    protected LazySBMLDataAccessor(Callable<SBMLDataRecord> lazyMethod, int flatSize, boolean shouldUseCaching) {
        if (lazyMethod == null) throw new IllegalArgumentException("lazyMethod cannot be null");
        this.shouldUseCaching = shouldUseCaching;
        this.lazyMethod = lazyMethod;
        this.cachedData = null;
        this.flatSize = flatSize;
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
        if (this.flatSize != product) lg.warn("Flat size mismatch: final calculated flat-size ({}) != set value ({})", product, this.flatSize);
        if (this.shouldUseCaching) this.cachedData = result;
        return result;
    }

    /**
     * Returns the flat size of the data to be lazily accessed.
     * @return the flat size of the data
     */
    public int getFlatSize() { return this.flatSize; }

    /**
     * Sets the flat size of the data to be lazily accessed.
     * @param flatSize the value to set the size to
     */
    public void setFlatSize(int flatSize) { this.flatSize = flatSize; }

    /**
     * Determines whether data provided by the method is flat, but has caveats:
     * 1) The use of type `Boolean` is intentional; if the data is not cached, we return `null`
     * 2) If we have cached the data, we return `true` if the data is considered "flat", otherwise `false`
     * @return `true`, `false`, or `null` depending on both the initialization and current state of the object.
     */
    public Boolean dataIsFlat(){
        if (this.cachedData == null) return null;
        List<Integer> dimensions = this.cachedData.dimensions();
        return dimensions == null || dimensions.isEmpty() || (dimensions.size() == 2 && dimensions.get(1) == 1);
    }
}
