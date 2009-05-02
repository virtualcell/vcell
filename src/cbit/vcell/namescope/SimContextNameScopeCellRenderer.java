package cbit.vcell.namescope;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.model.Parameter;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.mapping.SimulationContext;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Model;
import cbit.vcell.desktop.BioModelNode;
import javax.swing.JTree;

import org.vcell.util.gui.JTableFixed;
/**
 * Insert the type's description here.
 * Creation date: (5/6/2004 3:33:12 PM)
 * @author: Anuradha Lakshminarayana
 */
public class SimContextNameScopeCellRenderer extends cbit.vcell.desktop.VCellBasicCellRenderer implements ActionListener {
	private NameScopeParametersPanel tablePanel = null;
	private java.util.Hashtable tableHash = null;
/**
 * SimContextNameScopeCellRenderer constructor comment.
 */
public SimContextNameScopeCellRenderer() {
	super();
}
	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {
}
/**
 * Gets the tableHash property (java.util.Hashtable) value.
 * @return The tableHash property value.
 * @see #setTableHash
 */
private String getParameterTypeString(StructureMapping.StructureMappingParameter[] paramArray) {
	//
	// The param array should either contain geometry mapping parameters or membrane potential options,
	// hence checking only the first element's ROLE to find out what the table header for the param 
	// array should be.
	//
	if (paramArray != null && paramArray.length > 0) {
		if ( (paramArray[0].getRole() == (StructureMapping.ROLE_SurfaceToVolumeRatio)) ||
			 (paramArray[0].getRole() == (StructureMapping.ROLE_VolumeFraction)) ) {
			return "Geometry Mappings Parameters";
		} else {
			return "Membrane potential Parameters";
		}
	} else {
		return "";
	}
}
/**
 * Gets the tableHash property (java.util.Hashtable) value.
 * @return The tableHash property value.
 * @see #setTableHash
 */
public java.util.Hashtable getTableHash() {
	return tableHash;
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2000 6:41:57 PM)
 * @return java.awt.Component
 */
public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

	tree.setRowHeight(0);
	tablePanel = new NameScopeParametersPanel();
	
	if (!leaf && expanded) {
		setIcon(fieldFolderOpenIcon);
	}else if (!leaf && !expanded) {
		setIcon(fieldFolderClosedIcon);
	}

	try {
		if (value instanceof BioModelNode) {
			BioModelNode node = (BioModelNode) value;
			boolean bLoaded = false;

			if (node.getUserObject() instanceof Model) {
				Model model = (Model)node.getUserObject();
				this.setText(model.getName() /*+" : "+model.getVersion().getDate()*/);
				return this;
			} else if (node.getUserObject() instanceof String) {
				this.setText((String)node.getUserObject());
				return this;
			} else if (node.getUserObject() instanceof Model.ModelParameter[]) {
				// Create ModelParameterTable, set tablePanel, render that as node.
				tablePanel.getTableHeaderLabel().setText("Model Parameters");
				JTableFixed aJTable = (JTableFixed)tableHash.get(node.getUserObject());
				if (aJTable != null) {
					tablePanel.setAJTable(aJTable);
				} else {
					return this;
				}
			} else if (node.getUserObject() instanceof Structure) {
				Structure struct = (Structure)node.getUserObject();
				this.setText(struct.getName());
				return this;
			} else if (node.getUserObject() instanceof ReactionStep) {
				this.setText(((ReactionStep)node.getUserObject()).getName());
				return this;
			} else if (node.getUserObject() instanceof Kinetics) {
				// Create KineticsParameterTable, set tablePanel, render that as node.				
				Kinetics kinetics = (Kinetics)node.getUserObject();
				tablePanel.getTableHeaderLabel().setText("Reaction Parameters (Kinetics and Unresolved)");
				ReactionStep reactStep = ((Kinetics)node.getUserObject()).getReactionStep();
				JTableFixed aJTable = (JTableFixed)tableHash.get(reactStep);
				if (aJTable != null) {
					tablePanel.setAJTable(aJTable);
				} else {
					return this;
				}
			} else if (node.getUserObject() instanceof StructureMapping) {
				StructureMapping structMapping = (StructureMapping)node.getUserObject();
				this.setText(structMapping.getNameScope().getName());
				return this;				
			} else if (node.getUserObject() instanceof StructureMapping.StructureMappingParameter[]) {
				// Create StructureMappingParameterTable, set tablePanel, render that as node.
				StructureMapping.StructureMappingParameter[] paramArray = (StructureMapping.StructureMappingParameter[])node.getUserObject();
				String paramTypeString = getParameterTypeString(paramArray);
				tablePanel.getTableHeaderLabel().setText(paramTypeString);
				JTableFixed aJTable = (JTableFixed)tableHash.get(node.getUserObject());
				if (aJTable != null) {
					tablePanel.setAJTable(aJTable);
				} else {
					return this;
				}
			} else if (node.getUserObject() instanceof Boolean) {
				// Create a check box and return.
				Boolean calcVoltage = (Boolean)node.getUserObject();
				final JCheckBox calcVoltCheckBox = new JCheckBox("Calculate Voltage", calcVoltage.booleanValue());
				// add a new actionListener to this checkbox, it should set the calculateVoltage field of the 
				// membraneMapping parent of this node to the value of checkBox.isSelected. 
				if (((BioModelNode)node.getParent()).getUserObject() instanceof MembraneMapping) {
					final MembraneMapping membMapping = (MembraneMapping)((BioModelNode)node.getParent()).getUserObject();
					calcVoltCheckBox.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								membMapping.setCalculateVoltage(calcVoltCheckBox.isSelected());
								// System.out.println("\n\t\t****CheckBox o/p : "+calcVoltCheckBox.isSelected()+"\n\t\t****MembraneMappingCalcVolt : "+membMapping.getCalculateVoltage());
							}
						}
					);
				}
					
				return calcVoltCheckBox;
			} else if (node.getUserObject() instanceof SimulationContext) {
				SimulationContext simContext = (SimulationContext)node.getUserObject();
				this.setText(simContext.getNameScope().getName());
				setIcon(fieldSimulationContextIcon);
				return this;				
			} else if (node.getUserObject() instanceof SpeciesContextSpec) {
				SpeciesContextSpec speciesContextSpec = (SpeciesContextSpec)node.getUserObject();
				this.setText(speciesContextSpec.getNameScope().getName());
				return this;				
			} else if (node.getUserObject() instanceof Parameter[]) {
				// Create SpeciesContextSpecParameterTable, set tablePanel, render that as node.				
				Parameter[] paramArray = (Parameter[])node.getUserObject();
				String paramTypeString = "";
				if (((BioModelNode)node.getParent()).getUserObject() instanceof SpeciesContextSpec) {
					SpeciesContextSpec scSpec = (SpeciesContextSpec)((BioModelNode)node.getParent()).getUserObject();
					paramTypeString = scSpec.getNameScope().getName()+" Parameters";
				}
				tablePanel.getTableHeaderLabel().setText(paramTypeString);
				JTableFixed aJTable = (JTableFixed)tableHash.get(node.getUserObject());
				if (aJTable != null) {
					tablePanel.setAJTable(aJTable);
				} else {
					return this;
				}
			} 
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}

	return tablePanel;
}
/**
 * Sets the tableHash property (java.util.Hashtable) value.
 * @param tableHash The new value for the property.
 * @see #getTableHash
 */
public void setTableHash(java.util.Hashtable newTableHash) {
	java.util.Hashtable oldValue = tableHash;
	tableHash = newTableHash;
	firePropertyChange("tableHash", oldValue, newTableHash);
}
}
