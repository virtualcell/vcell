package cbit.rmi.event;

/**
 * Insert the type's description here.
 * Creation date: (2/10/2004 1:27:09 PM)
 * @author: Fei Gao
 */
public interface SimulationJobStatusListener extends java.util.EventListener {
/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 1:28:55 PM)
 * @param newJobStatus cbit.vcell.messaging.db.SimulationJobStatus
 * @param progress java.lang.Double
 * @param timePoint java.lang.Double
 */
void simulationJobStatusChanged(SimulationJobStatusEvent simJobStatusEvent);
}
