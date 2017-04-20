package cbit.vcell.mapping.gui;

import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vcell.util.VCAssert;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.mapping.MembraneContext;
import cbit.vcell.mapping.MembraneSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.gui.SpeciesContextSpecsTableModel.TableUtil;
import cbit.vcell.model.Membrane;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * model for {@link MembraneConditionsPanel}
 * @author GWeatherby
 *
 */
@SuppressWarnings("serial")
public class MembraneConditionTableModel extends VCellSortTableModel<MembraneSpec> {
	private static Logger lg = Logger.getLogger(MembraneConditionTableModel.class);
	
	private SimulationContext simContext;
	
	private enum ColumnType {
		COLUMN_MEMBRANE("Membrane"),
		COLUMN_CELERITY_X("Velocity X"),
		COLUMN_CELERITY_Y("Velocity Y");
		public final String label;
		ColumnType(String lbl) {
			label = lbl;
		}
		@Override
		public String toString() {
			return label; 
		}
	}
	
	public MembraneConditionTableModel(ScrollTable table) {
		super(table);
		
		// get names from enums 
		ColumnType[] eValues = ColumnType.values();
		String[] labels = new String[eValues.length];
		for (int i = 0; i < eValues.length; i++ ) {
			labels[i] = eValues[i].toString();
		}
		setColumns(labels);
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (ColumnType.values( )[columnIndex]) {
		case COLUMN_MEMBRANE:
			return Membrane.class;
		case COLUMN_CELERITY_X:
		case COLUMN_CELERITY_Y:
		 return ScopedExpression.class;
		
		}
		if (lg.isEnabledFor(Level.WARN)) {
			lg.warn("getColumnClass asking for column " + columnIndex);
		}
		return Object.class; 
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		switch (ColumnType.values( )[columnIndex]) {
		case COLUMN_MEMBRANE:
			return false; 
		case COLUMN_CELERITY_X:
		case COLUMN_CELERITY_Y:
		 return true; 
		}
		if (lg.isEnabledFor(Level.WARN)) {
			lg.warn("isCellEditable asking for column " + columnIndex);
		}
		return false;
	}
	
	/**
	 * create scoped expression for editing
	 * @param exp input, not null
	 * @return new {@link ScopedExpression}
	 */
	private ScopedExpression scope(Expression exp) {
		ScopedExpression se = new ScopedExpression(exp, simContext.getNameScope(),true, true, simContext.getAutoCompleteSymbolFilter()); 
		return se;
	}

	@Override
	public Object getValueAt(int rowIndex, int col) {
		MembraneSpec ms = getValueAt(rowIndex);
		switch (ColumnType.values( )[col]) {
			case COLUMN_MEMBRANE:
				return ms.getName();
			case COLUMN_CELERITY_X:
				return scope( ms.getCelerityX() );
			case COLUMN_CELERITY_Y:
				return scope( ms.getCelerityY() );
		}
		
		IllegalArgumentException iae = new IllegalArgumentException("no column " + col);
		if (lg.isEnabledFor(Level.WARN)) {
			lg.warn("getValueAt", iae);
		}
		throw iae;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		try {
		MembraneSpec ms = getValueAt(rowIndex);
		switch (ColumnType.values( )[columnIndex]) {
			case COLUMN_CELERITY_X:
			{
				Expression e = new Expression(aValue.toString());
				ms.setCelerityX(e);
				break;
			}
			case COLUMN_CELERITY_Y:
			{
				Expression e = new Expression(aValue.toString());
				ms.setCelerityY(e);
				break;
			}
			default:
		}
		} catch (ExpressionException ee) {
			if (lg.isEnabledFor(Level.WARN)) {
				lg.warn("setValueAt " + rowIndex + ", " + columnIndex + " with " + aValue, ee);
			}
			PopupGenerator.showErrorDialog(super.ownerTable, ee.getMessage());
		}
	}

	@Override
	protected Comparator<MembraneSpec> getComparator(int col, boolean ascending) {
		switch (ColumnType.values( )[col]) {
			case COLUMN_MEMBRANE:
				if (ascending) {
					return new NCompare();
				}
				else {
					return new NCompareR();
				}
			case COLUMN_CELERITY_X:
				return new XCompare(ascending);
			case COLUMN_CELERITY_Y:
				return new YCompare(ascending);
		}
		IllegalArgumentException iae = new IllegalArgumentException("no column " + col);
		if (lg.isEnabledFor(Level.WARN)) {
			lg.warn("getComparator", iae);
		}
		throw iae;
	}
	
	/**
	 * name ascending
	 */
	private static class NCompare implements Comparator<MembraneSpec> {
		@Override
		public int compare(MembraneSpec o1, MembraneSpec o2) {
			return o1.getName().compareToIgnoreCase(o2.getName() );
		}
	}
	
	/**
	 * name descending
	 */
	private static class NCompareR implements Comparator<MembraneSpec> {
		@Override
		public int compare(MembraneSpec o1, MembraneSpec o2) {
			return o2.getName().compareToIgnoreCase(o1.getName() );
		}
	}
	
	/**
	 * celerityX, both directions
	 */
	private static class XCompare implements Comparator<MembraneSpec> {
		final boolean ascending;
		
		/**
		 * @param ascending true if ascending
		 */
		public XCompare(boolean ascending) {
			this.ascending = ascending;
		}

		@Override
		public int compare(MembraneSpec o1, MembraneSpec o2) {
			return TableUtil.expressionCompare(o1.getCelerityX(), o2.getCelerityX(),ascending); 
		}
	}
	
	/**
	 * celerityY, both directions
	 */
	private static class YCompare implements Comparator<MembraneSpec> {
		final boolean ascending;
		
		/**
		 * @param ascending true if ascending
		 */
		public YCompare(boolean ascending) {
			this.ascending = ascending;
		}

		@Override
		public int compare(MembraneSpec o1, MembraneSpec o2) {
			return TableUtil.expressionCompare(o1.getCelerityY(), o2.getCelerityY(),ascending); 
		}
	}
	
	public void setSimulationContext(SimulationContext fieldSimulationContext) {
		simContext = fieldSimulationContext;
		MembraneContext mc = fieldSimulationContext.getMembraneContext();
		List<MembraneSpec> lst = mc.getMembraneSpecs();
		VCAssert.assertTrue(lst != null, "null return val");
		setData(lst);
		GuiUtils.flexResizeTableColumns(ownerTable);
	}
}
