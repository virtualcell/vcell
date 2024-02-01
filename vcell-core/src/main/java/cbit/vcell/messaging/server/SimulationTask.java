package cbit.vcell.messaging.server;

import cbit.vcell.solver.simulation.Simulation;
import cbit.vcell.solver.simulation.SimulationInfo;
import cbit.vcell.solver.simulation.SimulationJob;
import org.vcell.util.Matchable;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

public interface SimulationTask extends Matchable, java.io.Serializable {

    boolean isPowerUser();

    double getEstimatedMemorySizeMB();

    KeyValue getSimKey();

    SimulationInfo getSimulationInfo();

    SimulationJob getSimulationJob();

    String getSimulationJobID();

    int getTaskID();

    User getUser();

    String getUserName();

    String getComputeResource();

    boolean compareEqual(Matchable obj);

    Simulation getSimulation();
}
