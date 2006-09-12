package cbit.rmi.event;
/**
 * Insert the type's description here.
 * Creation date: (2/10/2004 2:00:22 PM)
 * @author: Fei Gao
 */
public interface SimulationJobStatusSender {
public abstract void addSimulationJobStatusListener(SimulationJobStatusListener listener);


public abstract void removeSimulationJobStatusListener(SimulationJobStatusListener listener);
}