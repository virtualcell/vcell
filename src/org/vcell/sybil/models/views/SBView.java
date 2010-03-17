package org.vcell.sybil.models.views;

/*   SBView  --- by Oliver Ruebenacker, UCHC --- October 2009 to February 2010
 *   A way to store a view of an sbbox
 */

import java.util.List;
import java.util.Set;

import org.vcell.sybil.models.io.Exporter;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.Location;
import org.vcell.sybil.models.sbbox.SBBox.Process;
import org.vcell.sybil.models.sbbox.SBBox.Substance;
import org.vcell.sybil.util.keys.KeyOfTwo;

public interface SBView {
	
	public static class ExporterList extends KeyOfTwo<List<Exporter>, Exporter> {

		public ExporterList(List<Exporter> exporters, Exporter exporterDefault) {
			super(exporters, exporterDefault);
		}
		public List<Exporter> exporters() { return a(); }
		public Exporter exporterDefault() { return b(); }
	}
	
	public SBBox box();
	public Set<Process> processes();
	public Set<Location> locations();
	public Set<Substance> substances();
	
}
