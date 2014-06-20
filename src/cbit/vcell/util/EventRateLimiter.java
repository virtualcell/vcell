package cbit.vcell.util;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * EventRateLimiter -- Class provides for keeping track of how much time has
 * transpired since a given processes with associated messaging events has begun
 * (e.g. an export or time series) so that messaging events can be throttled to occur
 * only so often, and optionally at a decreasing frequency as a function of time 
 * for long running processes.  (Because frequent progress updates are desirable for
 * short running exports and time plots but high frequency of updates is less important 
 * as time wears on).
 * 
 * Default constructor provides preset event intervals.  
 * A second constructor takes specified intervals in the form of a LinkedHashMap.
 * 
 * Assumed elapsed time of process begins at construction.
 * 
 * Only public method, okayToFireEventNow(), returns a boolean decision whether enough 
 * has elapsed since the last time an event was approved, also taking into account
 * how much total process time has transpired. 
 * 
 * @author Ed Boyce
 * 
 * @version June 20, 2014
 * 
 */

public class EventRateLimiter {

private final long startTime = new Date().getTime();
private long timeOfLastEvent = startTime;
private LinkedHashMap<Long, Long> intervals = new LinkedHashMap<Long, Long>();

/**
 * 
 * @param specifiedIntervals: 
 *    <code>LinkedHashMap Long m --> Long n</code> representing a map comprised of of interval 
 *    definition entries which indicate: until total elapsed time of m milliseconds, 
 *    allow events to fire only as often as every n milliseconds.
 *    
 */

public EventRateLimiter(LinkedHashMap<Long,Long> specifiedIntervals){
	intervals = specifiedIntervals;
}

/**
 * 
 * @param None.  Use default intervals from <code>getDefaultIntervals()</code>
 * 
 */

public EventRateLimiter(){
	intervals = getDefaultIntervals();
	}

/**
 * 
 * Default Intervals:
 * 	  For first 10 seconds, at most once every quarter second
 *    For 11 seconds through first minute, once per second
 *    For second through tenth minute, once per 5 seconds
 *    For 11th minute through 1 hour, once per 15 seconds
 *    After 1 hour, twice per minute
 *    
 *    @return <code>LinkedHashMap Long m --> Long n</code> representing a map comprised of of interval 
 *    definition entries which indicate: until total elapsed time of m milliseconds, 
 *    allow events to fire only as often as every n milliseconds.
 *    
 **/

private LinkedHashMap<Long, Long> getDefaultIntervals() {
	LinkedHashMap<Long, Long> defaultIntervals = new LinkedHashMap<Long, Long>();
	defaultIntervals.put(new Long(10000), new Long(250));
	defaultIntervals.put(new Long(100000), new Long(1000));
	defaultIntervals.put(new Long(600000), new Long(5000));
	defaultIntervals.put(new Long(3600000), new Long(15000));
	defaultIntervals.put(new Long(3600001), new Long(30000));

	return defaultIntervals;
}


private long timeRightNow(){
	return new Date().getTime();
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
 */
public boolean okayToFireEventNow(){
	Iterator<Entry<Long, Long>> intervalIterator = intervals.entrySet().iterator();
	Map.Entry<Long,Long> intervalMapEntry = (Map.Entry<Long,Long>)intervalIterator.next();
	if (timeSinceLastApprovedEvent()<(Long)intervalMapEntry.getValue()){
		return false;
	} else {
		/*
		 * Time to move on to the next era of time intervals?
		 *
		 * Check only while there are remaining intervalMapEntries beyond the current one.
		 * Note: using the Iterator.remove method on LinkedHashMap entries is supposedly
		 * safe against ConcurrentModificationException's. 
		 * 
		 */
		
		while (intervalIterator.hasNext()){
			if ((timeRightNow()-startTime)> intervalMapEntry.getKey()){
				intervalIterator.remove();
				intervalMapEntry = (Map.Entry<Long,Long>)intervalIterator.next();
			} else {
				break;
			}
		}
	}
	timeOfLastEvent = timeRightNow();
	return true;		
}


public static void main(String[] args) {
	EventRateLimiter eventRateLimiter = new EventRateLimiter();
	System.out.println("Starting at "+eventRateLimiter.timeRightNow());
	while(true){
		if (eventRateLimiter.okayToFireEventNow()) {
			System.out.println("Now it is "+eventRateLimiter.timeRightNow());
		}		
	}
}
}