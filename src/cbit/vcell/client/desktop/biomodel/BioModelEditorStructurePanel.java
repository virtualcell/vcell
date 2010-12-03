package cbit.vcell.client.desktop.biomodel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.graph.CartoonEditorPanelFixed;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public class BioModelEditorStructurePanel extends BioModelEditorRightSidePanel<Structure> {	
	private CartoonEditorPanelFixed ivjCartoonEditorPanel = null;
	
	public BioModelEditorStructurePanel() {
		super();
		initialize();
	}
	
	private void initialize() {		
		newButton.setText("New " + Structure.TYPE_NAME_FEATURE);
		ivjCartoonEditorPanel  = new CartoonEditorPanelFixed();
		
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
		gbc.insets = new Insets(4,10,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(newButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.insets = new Insets(4,4,4,10);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(deleteButton, gbc);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Table View", table.getEnclosingScrollPane());
		tabbedPane.addTab("Diagram View", ivjCartoonEditorPanel);
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 5;
		gbc.fill = GridBagConstraints.BOTH;
		add(tabbedPane, gbc);
	}
	
	public static void main(java.lang.String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
			JFrame frame = new javax.swing.JFrame();
			BioModelEditorStructurePanel panel = new BioModelEditorStructurePanel();
			frame.add(panel);
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.pack();
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}
	
	public void setDocumentManager(DocumentManager documentManager) {
		ivjCartoonEditorPanel.setDocumentManager(documentManager);		
	}

	protected void newButtonPressed() {
		Feature parentFeature = null;
		for (int i = bioModel.getModel().getNumStructures() - 1; i >= 0; i --) {
			if (bioModel.getModel().getStructures()[i] instanceof Feature) {
				parentFeature = (Feature)bioModel.getModel().getStructures()[i];
				break;
			}
		}
		try {
			bioModel.getModel().addFeature(bioModel.getModel().getFreeFeatureName(), parentFeature, bioModel.getModel().getFreeMembraneName());
		} catch (Exception e) {
			e.printStackTrace();
			DialogUtils.showErrorDialog(this, e.getMessage(), e);
		}
	}
	
	protected void deleteButtonPressed() {
		int[] rows = table.getSelectedRows();
		if (rows == null || rows.length == 0) {
			return;
		}
		String confirm = PopupGenerator.showOKCancelWarningDialog(this, "Are you sure you want to delete selected structure(s)?");
		if (confirm.equals(UserMessage.OPTION_CANCEL)) {
			return;
		}
		ArrayList<Feature> deleteList = new ArrayList<Feature>();
		for (int r : rows) {
			if (r < tableModel.getDataSize()) {
				Structure rowValue = tableModel.getValueAt(r);
				if (rowValue instanceof Feature) {
					deleteList.add((Feature) rowValue);
				}
			}
		}
		try {
			for (Feature f : deleteList) {
				bioModel.getModel().removeFeature(f);
			}
		} catch (PropertyVetoException ex) {
			ex.printStackTrace();
			DialogUtils.showErrorDialog(BioModelEditorStructurePanel.this, ex.getMessage());
		}
	}

	@Override
	protected void bioModelChanged() {
		super.bioModelChanged();
		ivjCartoonEditorPanel.setBioModel(bioModel);
	}

	@Override
	protected BioModelEditorRightSideTableModel<Structure> createTableModel() {
		return new BioModelEditorStructureTableModel(table);
	}
}
