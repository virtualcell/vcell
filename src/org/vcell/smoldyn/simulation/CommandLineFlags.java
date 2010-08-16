package org.vcell.smoldyn.simulation;



/**
 * @author mfenwick
 *
 */
public class CommandLineFlags{

	private boolean o;
	private boolean p;
	private boolean q;
	private boolean t;
	private boolean V;
	private boolean v;
	private boolean w;
	
	/**
	 * 
	 */
	public CommandLineFlags() {
		
	}
	/*
	 * --> -o
	 * --> -p
	 * --> -q
	 * --> -t
	 * --> -V
	 * --> -v
	 * --> -w
	 */

	public boolean isO() {
		return o;
	}

	public boolean isP() {
		return p;
	}

	public boolean isQ() {
		return q;
	}

	public boolean isT() {
		return t;
	}

	public boolean isV() {
		return V;
	}

	public boolean isv() {
		return v;
	}

	public boolean isW() {
		return w;
	}

}

