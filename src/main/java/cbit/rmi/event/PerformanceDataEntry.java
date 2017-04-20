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
public class PerformanceDataEntry implements java.io.Serializable {
		// just a generic pair
		private String identifier;
		private String value;
		public PerformanceDataEntry(String identifier, String value) {
			this.identifier = identifier;
			this.value = value;
		}
		public String getIdentifier() {
			return identifier;
		}
		public String getValue() {
			return value;
		}
	}
