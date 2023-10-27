package cbit.vcell.export.server.datacontainer;

import java.util.Map;
import java.util.Queue;

public class HybridDataContainerPool implements ResultDataContainerPool {
    private AggregateDataSize usedMemoryTracker;
    private Map<String, ResultDataContainer> containerMap;
    private Queue<String> memoryQueue; // Used to determine which containers to move to disk when maxing memory.


}
