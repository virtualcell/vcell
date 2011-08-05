/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.visit;

public class GetSystemProperties {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("os.arch: "+System.getProperty("os.arch"));
		System.out.println("os.name: "+System.getProperty("os.name"));
		System.out.println("os.version: "+System.getProperty("os.version"));
		System.out.println("java.version: "+System.getProperty("java.version"));
		System.out.println(System.getProperty("sun.arch.data.model"));
		//System.out.println(System.getProperty("os.arch"));

	}

}
