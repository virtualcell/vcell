package cbit.vcell.export.server.datacontainer;

import com.sun.istack.NotNull;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;

public class HybridDataContainerPool implements ResultDataContainerPool<HybridDataContainer> {
    private final AggregateDataSize usedMemoryTracker;
    private final Map<String, HybridDataContainer> containerMap;
    private final Queue<String> memoryQueue; // Used to determine which containers to move to disk when maxing memory.

    public HybridDataContainerPool(){
        this.usedMemoryTracker = new AggregateDataSize();
        this.containerMap = new HashMap<>();
        this.memoryQueue = new PriorityQueue<>();
    }


    @Override
    public int size() {
        return this.containerMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.containerMap.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) return false;
        if (!(o instanceof HybridDataContainer container)) return false;
        return this.containerMap.containsValue(container);
    }

    @Override
    @Nonnull
    public Iterator<HybridDataContainer> iterator() {
        return this.containerMap.values().iterator();
    }

    @Override
    @Nonnull
    public Object[] toArray() {
        return this.containerMap.values().toArray();
    }

    @Override
    @Nonnull
    @SuppressWarnings("NullableProblems")
    public <T> T[] toArray(T[] a) {
        return this.containerMap.values().toArray(a);
    }

    @Override
    public boolean add(HybridDataContainer hybridDataContainer) {

        return this.memoryQueue.add();
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends HybridDataContainer> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    /*@Override
    public void forEach(Consumer<? super HybridDataContainer> action) {
        ResultDataContainerPool.super.forEach(action);
    } */
}
