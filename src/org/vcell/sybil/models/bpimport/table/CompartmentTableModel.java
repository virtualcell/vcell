package org.vcell.sybil.models.bpimport.table;

/*   ComponentTable  --- by Oliver Ruebenacker, UCHC --- August 2008 to November 2009
 *   The table to edit properties of compartments (hierachy and dims)
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.Location;
import org.vcell.sybil.util.exception.CatchUtil;

public class CompartmentTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 4048931832981867882L;

	public class Row {
		public Row(String locNew, int dimsNew, String outLocNew) {
			loc = locNew; dims = dimsNew; outLoc = outLocNew;
		}
		public String loc;
		public Integer dims;
		public String outLoc;
	}
	
	protected Map<String, SBBox.MutableLocation> locDirectory = new HashMap<String, SBBox.MutableLocation>();
	protected Vector<Row> rows = new Vector<Row>();
	protected Set<SBBox.MutableLocation> locs = new HashSet<SBBox.MutableLocation>();
	
	public CompartmentTableModel(SBBox box) {
		locs.addAll(box.factories().location().openAll());
		locDirectory.put("[none]", null);
		for(SBBox.MutableLocation loc : locs) {
			String label = loc.label();
			locDirectory.put(label, loc);
			Location locOut = loc.locationSurrounding();
			String labelSurrounding = locOut != null ? locOut.label() : "";
			rows.add(new Row(loc.label(), loc.dims(), labelSurrounding));
		}
	}
	
	public int getColumnCount() { return 3; }
	public int getRowCount() { return rows.size(); }

	public void setValueAt(Object valueNew, int rowInd, int colInd) {
		try {
			switch(colInd) {
			case 0: rows.elementAt(rowInd).loc = (String) valueNew; break;
			case 1: rows.elementAt(rowInd).dims = (Integer) valueNew; break;
			case 2: rows.elementAt(rowInd).outLoc = (String) valueNew; break;
			}
		}
		catch (Exception e) {
			CatchUtil.handle(e);
		}
		fireTableCellUpdated(rowInd, colInd);
	}
	
	public Object getValueAt(int rowInd, int colInd) {
		try {
			switch(colInd) {
			case 0: return rows.elementAt(rowInd).loc;
			case 1: return rows.elementAt(rowInd).dims;
			case 2: return rows.elementAt(rowInd).outLoc;
			}
		}
		catch (NullPointerException e) {}
		return null;
	}
	
	public String getColumnName(int colInd) {
		switch(colInd) {
			case 0: return "<html><b>Compartment name (SBML)/<br>derived from location in BioPAX</html>";
			case 1: return "<html><b>Specify spatial<br>dimension</html>";
			case 2: return "<html><b>Specify surrounding <br>compartment</html>";
		}
		return null;
	}

	public Class<?> getColumnClass(int colInd) {
		switch(colInd) {
			case 0: return String.class;
			case 1: return Integer.class;
			case 2: return String.class;
		}
		return null;
	}

	public boolean isCellEditable(int rowInd, int colInd) { return colInd > 0; }
	public Map<String, SBBox.MutableLocation> locDir() { return locDirectory; }
	
	public Set<SBBox.Location> locations() { 
		Set<SBBox.Location> locations = new HashSet<SBBox.Location>();
		for(Row row : rows) {
			SBBox.MutableLocation loc = locDirectory.get(row.loc);
			int dims = row.dims.intValue();
			SBBox.Location outLoc = locDirectory.get(row.outLoc);
			if(loc != null) {
				loc.setDims(dims);
				if(outLoc != null) { loc.setSurrounding(outLoc); }
			}
			locations.add(loc);
		}
		return locations;
	}

}
