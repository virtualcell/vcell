package cbit.vcell.util;

/**
 * EventRateLimiter -- Class provides for keeping track of how much time has
 * transpired since a given processes with associated messaging events has begun
 * (e.g. an export or time series) so that messaging events can be throttled to occur
 * only so often, and optionally at a decreasing frequency as a function of time 
 * for long running processes.  (Because frequent progress updates are desirable for
 * short running exports and time plots, but high frequency of updates is less important 
 * as time wears on).
 * 
 * Default constructor provides preset event intervals.  
 * A second constructor takes specified intervals in the form a two dimensional array mapping.
 * 
 * Assumed elapsed time of process begins at construction.
 * 
 * Only public method, okayToFireEventNow(), returns a boolean decision whether enough 
 * has elapsed since the last time an event was approved, also taking into account
 * how much total process time has transpired. 
 * 
 * 
 * 
 * @author Ed Boyce
 * 
 * @version June 24, 2014
 * 
 **/

public class EventRateLimiter {

private final long startTime = System.currentTimeMillis();
private long timeOfLastEvent = startTime;
private long[][] intervals = null;

private static final int TIME_INTERVAL_REGIME = 0;
private static final int TIME_INTERVAL_PER_REGIME = 1;

/**
 * 
 * @param specifiedIntervals: 
 *    <code>long[m][n]</code> representing a map comprised of of interval 
 *    definition entries which indicate: until total elapsed time of m milliseconds, 
 *    allow events to fire only as often as every n milliseconds.
 *    
 **/

public EventRateLimiter(long[][] specifiedIntervals){
	intervals = specifiedIntervals;
}

/**
 * 
 * @param None.  Use default intervals from <code>getDefaultIntervals()</code>
 * 
 **/

public EventRateLimiter(){
	this(getDefaultIntervals());
	}

/**
 * 
 * Default Intervals:
 * 	  For first 10 seconds, at most once every half second
 *    For 11 seconds through first minute, once per second
 *    For second through tenth minute, once per 5 seconds
 *    For 11th minute through 1 hour, once per 15 seconds
 *    After 1 hour, twice per minute
 *    
 *    @return <code>long[m][n] </code> representing a map comprised of of interval 
 *    definition entries which indicate: until total elapsed time of m milliseconds, 
 *    allow events to fire only as often as every n milliseconds.
 *    
 **/

private static long[][] getDefaultIntervals(){
	long[][] staticLookupTable = {
			{10000,500},
			{100000,1000},
			{600000,5000},
			{3600000,15000},
			{3600001,30000}
			};
	return staticLookupTable;
}

private long timeRightNow(){
	return System.currentTimeMillis();
}


private long timeSinceLastApprovedEvent(){
	return (timeRightNow() - timeOfLastEvent);
}

/**
 * 
 * @return <code>boolean</code>:
 * returns a decision whether enough time has elapsed since the last time an event 
 * was approved, also taking into account how much total process time has transpired. 
 *
 **/

public boolean isOkayToFireEventNow(){
	long totalElapsedTimeNow = timeRightNow()-startTime;
	int currentTimeRegimeIndex = 0;
	
	/*
	 *  First decide what frequency interval is current given 
	 *  how much total time has elapsed so far.
	 */
	
	for (int i=0; i<intervals.length; i++) {
		currentTimeRegimeIndex = i;
		if (totalElapsedTimeNow < intervals[i][TIME_INTERVAL_REGIME]){
			break;
		}
	}
	
	/*  Then decide whether enough time has elapsed since last approved
	 *  event according to the current frequency interval.  If not, 
	 *  return false.  If so, note the current time being that of the
	 *  last approved event and return true.
	 */
	
	if (timeSinceLastApprovedEvent() < intervals[currentTimeRegimeIndex][TIME_INTERVAL_PER_REGIME]){
		return false;
	} else {
		timeOfLastEvent = timeRightNow();
		return true;	
	}
}

/*
 * JUnit Test resides at cbit.vcell.util.EventRateLimiterTest
 */

public static void main(String[] args) {
	EventRateLimiter eventRateLimiter = new EventRateLimiter();
	System.out.println("Starting at "+eventRateLimiter.timeRightNow());
	while(true){
		if (eventRateLimiter.isOkayToFireEventNow()) {
			System.out.println("Now it is "+eventRateLimiter.timeRightNow());
		}		
	}
}
}