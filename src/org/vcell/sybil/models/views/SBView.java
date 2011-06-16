/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.views;

/*   SBView  --- by Oliver Ruebenacker, UCHC --- October 2009 to February 2010
 *   A way to store a view of an sbbox
 */

import java.util.List;
import java.util.Set;

import org.vcell.sybil.models.io.Exporter;
import org.vcell.sybil.models.sbbox.SBBox.Location;
import org.vcell.sybil.models.sbbox.SBBox.Process;
import org.vcell.sybil.models.sbbox.SBBox.Substance;
import org.vcell.sybil.models.sbbox.SBInferenceBox;
import org.vcell.sybil.util.keys.KeyOfTwo;

public interface SBView {
	
	public static class ExporterList extends KeyOfTwo<List<Exporter>, Exporter> {

		public ExporterList(List<Exporter> exporters, Exporter exporterDefault) {
			super(exporters, exporterDefault);
		}
		public List<Exporter> exporters() { return a(); }
		public Exporter exporterDefault() { return b(); }
	}
	
	public SBInferenceBox box();
	public Set<Process> processes();
	public Set<Location> locations();
	public Set<Substance> substances();
	
}
