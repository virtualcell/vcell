package cbit.vcell.messaging.server;
/**
 * Insert the type's description here.
 * Creation date: (2/20/2004 3:20:06 PM)
 * @author: Fei Gao
 */
public interface Worker extends ServiceProvider, cbit.vcell.messaging.ControlTopicListener, cbit.vcell.solver.SolverListener {
	public String getJobSelector();


	public boolean isRunning();
}