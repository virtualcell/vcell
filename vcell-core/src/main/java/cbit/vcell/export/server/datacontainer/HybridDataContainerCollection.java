package cbit.vcell.export.server.datacontainer;

import javax.annotation.Nonnull;
import java.util.*;

public class HybridDataContainerCollection implements ResultDataContainerCollection<ResultDataContainer> {
    private final AggregateDataSize usedMemoryTracker;
    private Map<String, ResultDataContainer> containerMap;
    private final Queue<String> memoryQueue; // Used to determine which containers to move to disk when maxing memory.
    private final Set<String> onDiskDataContainers;

    /**
     * Builds a new HybridDataContainerCollection
     */
    public HybridDataContainerCollection(){
        this.usedMemoryTracker = new AggregateDataSize();
        this.containerMap = new HashMap<>();
        this.memoryQueue = new PriorityQueue<>(new HybridDataContainerCollection.DataContainerComparator());
        this.onDiskDataContainers = new HashSet<>();
    }

    /**
     * Builds a new HybridDataContainerCollection, and initializes it with the provided collection
     * @param c the Data Containers to add right at creation time
     */
    public HybridDataContainerCollection(Collection<? extends ResultDataContainer> c){
        this();
        this.addAll(c);
    }


    private void prepareMemory(int bytes){
        while (this.usedMemoryTracker.hasNoMoreRoomFor(bytes)){
            this.moveOneContainerToDisk();
        }
    }

    private void moveOneContainerToDisk(){
        String containerKey = this.memoryQueue.remove();
        ResultDataContainer movedContainer = this.containerMap.remove(containerKey); // TODO: Call the converter
        this.usedMemoryTracker.decreaseDataAmount(movedContainer.getDataSize());
        this.onDiskDataContainers.add(containerKey);
        this.containerMap.put(containerKey, movedContainer);
    }


    /*  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *\
    * The following methods are for the purposes of implementing java's `Collection<T>` interface    *
    \*  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  */

    /**
     * Determines the number of DataContainers referenced in this collection.
     * @return the number of DataContainers.
     */
    @Override
    public int size() {
        return this.containerMap.size();
    }

    /**
     * Checks whether there are any DataContainers referenced in this collection.
     * @return true if the collection is empty.
     */
    @Override
    public boolean isEmpty() {
        return this.containerMap.isEmpty();
    }

    /**
     * Checks whether a provided container (or other object) is contained within the collection.
     * @param o element whose presence in this collection is to be tested.
     * @return true if the provided element was located within the collection.
     */
    @Override
    public boolean contains(Object o) {
        if (o == null) return false;
        if (!(o instanceof ResultDataContainer container)) return false;
        return this.containerMap.containsValue(container); // TODO: Change to key based for better runtime
    }

    /**
     * Generates an iterator to look through the items in the collection.
     * @return an iterator of the elements in the collection.
     */
    @Override
    @Nonnull
    public Iterator<ResultDataContainer> iterator() {
        return this.containerMap.values().iterator();
    }

    /**
     * Converts this collection to an abstract array
     * @return an Object array containing the elements in this collection.
     */
    @Override
    @Nonnull
    public Object[] toArray() {
        return this.containerMap.values().toArray();
    }

    /**
     *
     * @param a the array into which the elements of this collection are to be
     *        stored, if it is big enough; otherwise, a new array of the same
     *        runtime type is allocated for this purpose.
     * @return a stronger-typed array containing the elements in this collection.
     * @param <T> the type of the array to try and apply the items into. You probably want `ResultDataContainer`.
     */
    @Override
    @Nonnull
    @SuppressWarnings("NullableProblems")
    public <T> T[] toArray(T[] a) {
        return this.containerMap.values().toArray(a);
    }

    /**
     * Adds a Data Container to this collection.
     * @param dataContainer element whose presence in this collection is to be ensured.
     * @return true if the collection was modified.
     */
    @Override
    public boolean add(ResultDataContainer dataContainer) {
        String dataKey = ""; // TODO: Determine how to generate keys
        if (this.containerMap.containsKey(dataKey)) return false;
        this.prepareMemory(dataContainer.getDataSize());
        this.containerMap.put(dataKey, dataContainer);
        // TODO: Replace below with enhanced pattern matching switch in Java21+
        if (dataContainer instanceof InMemoryDataContainer) return this.memoryQueue.add(dataKey);
        if (dataContainer instanceof FileDataContainer) return this.onDiskDataContainers.add(dataKey);
        throw new RuntimeException("Unexpected type of dataContainer was added!");
    }

    /**
     * Removes a Data Container from this collection.
     * @param o element to be removed from this collection, if present.
     * @return true if the collection was modified.
     */
    @Override
    public boolean remove(Object o) {
        String dataKey = ""; // TODO: Determine how to generate keys
        if (!this.containerMap.containsKey(dataKey)) return false;
        ResultDataContainer container = this.containerMap.get(dataKey);
        this.usedMemoryTracker.decreaseDataAmount(container.getDataSize());
        this.containerMap.remove(dataKey);
        // '|' does NOT short-circuit, we *want* to NOT short circuit here, we want the side effects, DON'T use '||'.
        return this.memoryQueue.remove(dataKey) | this.onDiskDataContainers.remove(dataKey);
    }

    /**
     * Checks to see if everything in the provided collection exists in this collection.
     * @param c collection to be checked for containment in this collection.
     * @return true if all provided objects exist in this collection.
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) if (!this.contains(o)) return false;
        return true;
    }

    /**
     * Adds all the provided Data Containers, assuming they don't already exist, in the collection.
     * @param c collection containing elements to be added to this collection.
     * @return true if the collection was modified.
     */
    @Override
    public boolean addAll(Collection<? extends ResultDataContainer> c) {
        boolean didChange = false;
        for (ResultDataContainer container: c) didChange |= this.add(container);
        return didChange;
    }

    /**
     * Removes all the provided Data Containers, assuming they exist, in the collection.
     * @param c collection containing elements to be removed from this collection.
     * @return true if the collection was modified.
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean didChange = false;
        for (Object o: c) didChange |= this.remove(o);
        return didChange;
    }

    /**
     * Keep all the provided Data Containers, assuming they exist, in the collection, and discarding the rest.
     * @param c collection containing elements to be retained in this collection
     * @return true if the collection was modified.
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        Map<String, ResultDataContainer> newContainerMap = new HashMap<>();
        long sizeSum = 0;
        for (Object o : c){
            if (!this.contains(o)) continue;
            String dataKey = ""; // TODO: Determine how to generate keys
            ResultDataContainer container = (ResultDataContainer)o;
            newContainerMap.put(dataKey, container);
            sizeSum += container.getDataSize();
        }
        if (newContainerMap.size() == this.containerMap.size())
            return false;
        this.usedMemoryTracker.resetTo(sizeSum);
        this.onDiskDataContainers.retainAll(newContainerMap.keySet());
        this.memoryQueue.retainAll(newContainerMap.keySet());
        this.containerMap = newContainerMap;
        return true;
    }

    /**
     * Clears the collection, discarding all Data Containers
     */
    @Override
    public void clear() {
        this.usedMemoryTracker.reset();
        this.containerMap.clear();
        this.memoryQueue.clear();
        this.onDiskDataContainers.clear();
    }

    /*  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *\
    * The Comparator defined below this comment is for the purposes of creating a better PriorityQueue<>   *
    \*  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  */
    private class DataContainerComparator implements Comparator<String>{

        /**
         * Compares its two arguments for order.  Returns a negative integer,
         * zero, or a positive integer as the first argument is less than, equal
         * to, or greater than the second.<p>
         * <p>
         * The implementor must ensure that {@link Integer#signum
         * signum}{@code (compare(x, y)) == -signum(compare(y, x))} for
         * all {@code x} and {@code y}.  (This implies that {@code
         * compare(x, y)} must throw an exception if and only if {@code
         * compare(y, x)} throws an exception.)<p>
         * <p>
         * The implementor must also ensure that the relation is transitive:
         * {@code ((compare(x, y)>0) && (compare(y, z)>0))} implies
         * {@code compare(x, z)>0}.<p>
         * <p>
         * Finally, the implementor must ensure that {@code compare(x,
         * y)==0} implies that
         * {@code signum(compare(x, z))==signum(compare(y, z))} for all {@code z}.
         *
         * @param o1 the first object to be compared.
         * @param o2 the second object to be compared.
         * @return a negative integer, zero, or a positive integer as the
         * first argument is less than, equal to, or greater than the
         * second.
         * @throws NullPointerException if an argument is null and this
         *                              comparator does not permit null arguments
         * @throws ClassCastException   if the arguments' types prevent them from
         *                              being compared by this comparator.
         * @apiNote It is generally the case, but <i>not</i> strictly required that
         * {@code (compare(x, y)==0) == (x.equals(y))}.  Generally speaking,
         * any comparator that violates this condition should clearly indicate
         * this fact.  The recommended language is "Note: this comparator
         * imposes orderings that are inconsistent with equals."
         */
        @Override
        public int compare(String o1, String o2) {
            // It would take a decent amount of work to modify ResultDataContainers to have time stamps,
            // let alone access frequencies, so rather than an LRU/LFU caching algorithm,
            // we're going with largest-priority. That said, TODO: change to LRU/LFU caching
            if (containerMap.containsKey(o1)) throw new IllegalArgumentException("`" + o1 + "` is not a valid key.");
            if (containerMap.containsKey(o2)) throw new IllegalArgumentException("`" + o2 + "` is not a valid key.");
            ResultDataContainer container1 = containerMap.get(o1);
            ResultDataContainer container2 = containerMap.get(o2);
            // We want larger sizes to mean higher priority. A PriorityQueue prioritizes "smaller" elements
            return Long.signum(container2.getDataSize() - container1.getDataSize());
        }
    }
}
