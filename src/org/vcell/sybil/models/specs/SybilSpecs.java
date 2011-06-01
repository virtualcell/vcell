/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.specs;

/*   SybilSpecs  --- by Oliver Ruebenacker, UCHC --- September 2008 to February 2010
 *   Version and Product Information for Sybil
 */

import org.vcell.sybil.util.version.SoftwareVersion;
import org.vcell.sybil.util.version.SoftwareVersion.Edition;
import org.vcell.sybil.util.version.SoftwareVersion.Product;

public class SybilSpecs {

	public static class SybilProduct extends Product {
		public boolean equals(Object object) { return object instanceof SybilProduct; }
		public static final String name = "Systems Biology Linker (SyBiL)";
		public static final String shortName = "Sybil";
		public int hashCode() { return name.hashCode(); }
		public String toString() { return name; }
	}

	public static final Product product = new SybilProduct();
	public static final SoftwareVersion version118 = new SoftwareVersion(product, 1, 0, Edition.Alpha, 118, "Ginger Snaps");		
	public static final SoftwareVersion version119 = new SoftwareVersion(product, 1, 0, Edition.Alpha, 119, "Shortbread");		
	public static final SoftwareVersion version120 = new SoftwareVersion(product, 1, 0, Edition.Beta, 1, "Sacher Cake");		
	public static final SoftwareVersion version = version120;	
	public static final String shortText = SybilProduct.shortName + " " + version;
	public static final String longText = "This is " + product + ", " + version.text();
	
}
