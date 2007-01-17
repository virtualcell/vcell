package cbit.vcell.server;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public interface SessionLog {
/**
 * This method was created in VisualAge.
 * @param message java.lang.String
 */
public void alert(String message);
/**
 * This method was created in VisualAge.
 * @param e java.lang.Throwable
 */
public void exception(Throwable e);
/**
 * This method was created in VisualAge.
 * @param message java.lang.String
 */
public void print(String message);
}
