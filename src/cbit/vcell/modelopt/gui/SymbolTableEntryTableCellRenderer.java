package cbit.vcell.modelopt.gui;
import cbit.vcell.parser.SymbolTableEntry;
/**
 * Insert the type's description here.
 * Creation date: (11/30/2005 2:11:10 PM)
 * @author: Jim Schaff
 */
public class SymbolTableEntryTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
/**
 * SymbolTableEntryTableCellRenderer constructor comment.
 */
public SymbolTableEntryTableCellRenderer() {
	super();
}


		public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value == null) {
				setText("unmapped");
				return this;
			}
			SymbolTableEntry ste = (SymbolTableEntry)value;
			if (ste instanceof cbit.vcell.model.ReservedSymbol){
				setText(ste.getName());
			}else if (ste instanceof cbit.vcell.model.SpeciesContext){
				setText("["+ste.getName()+"]");
			}else if (ste instanceof cbit.vcell.model.Kinetics.KineticsParameter){
				setText(ste.getNameScope().getName()+":"+ste.getName());
			}else{
				setText(ste.getNameScope().getAbsoluteScopePrefix()+ste.getName());
			}
			//if (ste instanceof cbit.vcell.model.Kinetics.KineticsParameter){
				//setToolTipText("Kinetic parameter \""+ste.getName()+"\" in reaction "+);
			return this;
		}
}