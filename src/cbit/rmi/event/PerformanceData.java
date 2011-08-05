/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.rmi.event;
public class PerformanceData implements java.io.Serializable {
		private String methodName;
		private int dataType;
		PerformanceDataEntry[] entries;
		public PerformanceData(String methodName, int dataType, PerformanceDataEntry[] entries) {
			this.methodName = methodName;
			this.dataType = dataType;
			this.entries = entries;
		}
		public String getMethodName() {
			return methodName;
		}
		public int getDataType() {
			return dataType;
		}
		public PerformanceDataEntry[] getEntries() {
			return entries;
		}
	}
