package org.vcell.smoldyn.simulationsettings;


/**
 * Smoldyn uses a simple system for time specification.  The user must specify the start time, stop time, and step time.  Since there are
 * no units in Smoldyn, it's completely up to the user to figure out what those parameters mean.
 * Example:  if start time is 45, stop time is 100, and step time is 5, then Smoldyn will start at t = 45 (45 has no other meaning except
 * in relation to stop time; it should not be interpreted to mean that the user is seeing the simulation after 45 time units have already
 * elapsed), then go through its stuff at t = 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100.
 * 
 * Invariants:
 * 		start must be less than or equal to stop
 * 		step must be greater than or equal to 0
 * 
 * @author mfenwick
 *
 */
public class SmoldynTime {

	private double starttime;
	private double stoptime;
	private double steptime;
//	private Float time_now;
	
	/**
	 * Instantiates a new SmoldynTime object. // TODO see reference manual to figure out what exactly these mean
	 * @param start
	 * @param stop
	 * @param step
	 * @throws RuntimeException if the stop time is less than the start time, or if the
	 * step value is negative or zero.
	 */
	public SmoldynTime(double start, double stop, double step) {
		if (stop < start) {
			throw new RuntimeException("Simulation stop time must be greater than start time");
		}
		if (step <= 0) {
			throw new RuntimeException("Simulation time step must be positive");
		}
		starttime = start;
		stoptime = stop;
		steptime = step;
	}

	public double getStarttime() {
		return starttime;
	}

	public double getStoptime() {
		return stoptime;
	}
	
	public double getSteptime() {
		return steptime;
	}
}
