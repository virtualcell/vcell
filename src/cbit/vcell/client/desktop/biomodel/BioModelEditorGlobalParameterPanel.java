package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTable;

import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;

import com.hp.hpl.jena.sparql.function.library.namespace;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.gui.ModelParameterTableModel;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.NameScope;
import cbit.vcell.units.VCUnitDefinition;

/**
 * Insert the type's description here.
 * Creation date: (9/23/2003 12:23:30 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class BioModelEditorGlobalParameterPanel extends BioModelEditorRightSidePanel<Parameter> {

	public BioModelEditorGlobalParameterPanel() {
		super();
		initialize();
	}
		
/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 9:10:39 PM)
 */
public void cleanupOnClose() {
	setBioModel(null);
	tableModel.setBioModel(null);
}

/**
 * Initialize the class.
 */
private void initialize() {
	newButton.setText("New Global Parameter");
	setLayout(new GridBagLayout());
	
	int gridy = 0;
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.gridx = 0;
	gbc.gridy = gridy;
	gbc.insets = new Insets(4,4,4,4);
	add(new JLabel("Search "), gbc);
	
	gbc = new GridBagConstraints();
	gbc.gridx = 1;
	gbc.gridy = gridy;
	gbc.weightx = 1.0;
	gbc.anchor = GridBagConstraints.LINE_START;
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.insets = new Insets(4,4,4,4);
	add(textFieldSearch, gbc);
			
	gbc = new GridBagConstraints();
	gbc.gridx = 2;
	gbc.gridy = gridy;
	gbc.insets = new Insets(4,100,4,4);
	gbc.anchor = GridBagConstraints.LINE_END;
	add(newButton, gbc);
	
	gbc = new GridBagConstraints();
	gbc.gridx = 3;
	gbc.insets = new Insets(4,4,4,20);
	gbc.gridy = gridy;
	gbc.anchor = GridBagConstraints.LINE_END;
	add(deleteButton, gbc);
	
	gridy ++;
	gbc = new GridBagConstraints();
	gbc.gridx = 0;
	gbc.insets = new Insets(4,4,4,4);
	gbc.gridy = gridy;
	gbc.weighty = 1.0;
	gbc.weightx = 1.0;
	gbc.gridwidth = 4;
	gbc.fill = GridBagConstraints.BOTH;
	add(table.getEnclosingScrollPane(), gbc);
	
	table.setDefaultRenderer(NameScope.class, new DefaultScrollTableCellRenderer(){

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
					row, column);
			if (value instanceof NameScope) {
				NameScope nameScope = (NameScope)value;
				String text = nameScope.getName();
				if (nameScope instanceof Model.ModelNameScope) {
					text = "Global";
				}
				setText(text);
			}
			return this;
		}
		
	});
}

public void setScrollPaneTableCurrentRow(ModelParameter selection) {
	if (selection == null) {
		table.clearSelection();
		return;
	}

	int numRows = table.getRowCount();
	for(int i=0; i<numRows; i++) {
		ModelParameter valueAt = (ModelParameter) tableModel.getValueAt(i, ModelParameterTableModel.COLUMN_NAME);
		if(valueAt.equals(selection)) {
			table.changeSelection(i, 0, false, false);
			return;
		}
	}
}

protected void newButtonPressed() {
	ModelParameter modelParameter = bioModel.getModel().new ModelParameter(bioModel.getModel().getFreeModelParamName(), new Expression(0), Model.ROLE_UserDefined, VCUnitDefinition.UNIT_TBD);
	try {
		bioModel.getModel().addModelParameter(modelParameter);
		select(modelParameter);
	} catch (PropertyVetoException e) {
		e.printStackTrace(System.out);
		DialogUtils.showErrorDialog(this, e.getMessage());
	}
}

protected void deleteButtonPressed() {
	int[] rows = table.getSelectedRows();
	if (rows == null || rows.length == 0) {
		return;
	}
	String confirm = PopupGenerator.showOKCancelWarningDialog(this, "Are you sure you want to delete selected global parameter(s)?");
	if (confirm.equals(UserMessage.OPTION_CANCEL)) {
		return;
	}
	ArrayList<ModelParameter> deleteList = new ArrayList<ModelParameter>();
	for (int r : rows) {
		if (r < tableModel.getDataSize()) {			
			Parameter parameter = tableModel.getValueAt(r);
			if (parameter instanceof ModelParameter) {
				deleteList.add((ModelParameter)parameter);
			}
		}
	}	
	try {
		for (ModelParameter param : deleteList) {
			bioModel.getModel().removeModelParameter(param);
		}
	} catch (PropertyVetoException e) {
		PopupGenerator.showErrorDialog(this, e.getMessage());
	}	
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		BioModelEditorGlobalParameterPanel aModelParameterPanel;
		aModelParameterPanel = new BioModelEditorGlobalParameterPanel();
		frame.setContentPane(aModelParameterPanel);
		frame.setSize(aModelParameterPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}

@Override
protected BioModelEditorRightSideTableModel<Parameter> createTableModel() {
	return new BioModelEditorGlobalParameterTableModel(table, false);
}
}