package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

import org.vcell.util.gui.DialogUtils;

import cbit.gui.LabelButton;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.graph.CartoonEditorPanelFixed;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public class BioModelEditorStructurePanel extends BioModelEditorRightSidePanel<Structure> {	
	private InternalEventHandler eventHandler = new InternalEventHandler();
	private CartoonEditorPanelFixed ivjCartoonEditorPanel1 = null;
	private JButton showDiagramButton = null;
	
	private class InternalEventHandler implements ActionListener, java.beans.PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelEditorStructurePanel.this && evt.getPropertyName().equals(PROPERTY_NAME_BIO_MODEL)) {
				ivjCartoonEditorPanel1.setBioModel(bioModel);
			}			
		}
		
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == showDiagramButton) {
				if (ivjCartoonEditorPanel1.isVisible()) {
					ivjCartoonEditorPanel1.setVisible(false);
					showDiagramButton.setText("<html><u>Show Diagram &gt;&gt;</u></html>");
				} else {
					ivjCartoonEditorPanel1.setVisible(true);
					showDiagramButton.setText("<html><u>Hide Diagram &lt;&lt;</u></html>");				
				}
			}			
		}
		
	}
	public BioModelEditorStructurePanel() {
		super();
		initialize();
	}
	
	private void initialize() {
		addPropertyChangeListener(eventHandler);
		tableModel = new BioModelEditorStructureTableModel(table);
		table.setModel(tableModel);	
		
		addButton.setText("New " + Structure.TYPE_NAME_FEATURE);
		ivjCartoonEditorPanel1  = new CartoonEditorPanelFixed();
		showDiagramButton = new LabelButton("<html><u>Hide Diagram &lt;&lt;</u></html>");
		showDiagramButton.setForeground(Color.blue);
		showDiagramButton.addActionListener(eventHandler);
		
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
		add(addButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.insets = new Insets(4,4,4,10);
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
		gbc.gridwidth = 5;
		gbc.fill = GridBagConstraints.BOTH;
		add(table.getEnclosingScrollPane(), gbc);

		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(5,4,5,4);
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 5;
		gbc.ipadx = 50;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(showDiagramButton, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(0,4,4,4);
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 5;
		gbc.fill = GridBagConstraints.BOTH;
		add(ivjCartoonEditorPanel1, gbc);
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
		ivjCartoonEditorPanel1.setDocumentManager(documentManager);		
	}

	protected void newButtonPressed() {		
	}
	
	protected void deleteButtonPressed() {
		int[] rows = table.getSelectedRows();
		ArrayList<Feature> deleteList = new ArrayList<Feature>();
		for (int r : rows) {
			if (r < bioModel.getModel().getNumStructures()) {
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
}
