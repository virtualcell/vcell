package cbit.util;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class TimeWrapper {
	private long timeLastTouched;
	private long size;
	private Object object;
	private Object key;
public TimeWrapper(Object object, long size, Object key) {
	this.object = object;
	this.size = size;
	this.key = key;
	updateAge();
}
public long getAge() {
	return (System.currentTimeMillis() - timeLastTouched);
}
/**
 * This method was created in VisualAge.
 * @return long
 */
public double getCost(double costPerMillisecond, double costPerByte) {
	return getAge()*costPerMillisecond + getSize()*costPerByte;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getKey() {
	return key;
}
public Object getObject() {
	updateAge();
	return (object);
}
/**
 * This method was created in VisualAge.
 * @return long
 */
public long getSize() {
	return size;
}
private void updateAge() {
	timeLastTouched = System.currentTimeMillis();
}
}
