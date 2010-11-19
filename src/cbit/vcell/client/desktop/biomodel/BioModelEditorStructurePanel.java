package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.graph.CartoonEditorPanelFixed;
import cbit.vcell.model.Feature;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public class BioModelEditorStructurePanel extends JPanel {
	private static final String PROPERTY_NAME_BIO_MODEL = "bioModel";
	
	private JButton addFeatureButton = null;
	private JButton deleteButton = null;
	private EditorScrollTable table;
	private BioModel bioModel;
	private BioModelEditorStructureTableModel tableModel = null;
	private JTextField textFieldSearch = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	private CartoonEditorPanelFixed ivjCartoonEditorPanel1 = null;
	private JLabel showDiagramLabel = null;
	
	private class InternalEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, ListSelectionListener, DocumentListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelEditorStructurePanel.this && evt.getPropertyName().equals(PROPERTY_NAME_BIO_MODEL)) {
				tableModel.setModel(bioModel.getModel());
				ivjCartoonEditorPanel1.setBioModel(bioModel);
			}			
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == addFeatureButton) {
					// TODO
//				try {
//					bioModel.getModel().addFeature(bioModel.getModel().getFreeFeatureName());
//				} catch (PropertyVetoException ex) {
//					ex.printStackTrace();
//					DialogUtils.showErrorDialog(BioModelEditorStructurePanel.this, ex.getMessage());
//				} catch (ModelException ex) {
//					ex.printStackTrace();
//					DialogUtils.showErrorDialog(BioModelEditorStructurePanel.this, ex.getMessage());
//				}
			} else if (e.getSource() == deleteButton) {
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

		public void valueChanged(ListSelectionEvent e) {
			if (bioModel == null || e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == table.getSelectionModel()) {
				int[] rows = table.getSelectedRows();
				deleteButton.setEnabled(rows != null && rows.length > 0 && (rows.length > 1 || rows[0] < bioModel.getModel().getNumStructures()));
			}
			
		}

		public void insertUpdate(DocumentEvent e) {
			searchTable();
		}

		public void removeUpdate(DocumentEvent e) {
			searchTable();
		}

		public void changedUpdate(DocumentEvent e) {
			searchTable();
		}	
		
	}
	public BioModelEditorStructurePanel() {
		super();
		initialize();
	}
	
	private void initialize() {
		addFeatureButton = new JButton("New " + Structure.TYPE_NAME_FEATURE);
		deleteButton = new JButton("Delete");
		table = new EditorScrollTable();
		textFieldSearch = new JTextField(10);
		ivjCartoonEditorPanel1  = new CartoonEditorPanelFixed();
		showDiagramLabel = new JLabel("<html><u>Hide Diagram &lt;&lt;</u></html>");
		showDiagramLabel.setForeground(Color.blue);
		
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
		add(addFeatureButton, gbc);
		
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
		gbc.insets = new Insets(10,4,5,4);
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 5;
		gbc.anchor = GridBagConstraints.LINE_START;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(showDiagramLabel, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 5;
		gbc.fill = GridBagConstraints.BOTH;
		add(ivjCartoonEditorPanel1, gbc);
		
		addPropertyChangeListener(eventHandler);
		tableModel = new BioModelEditorStructureTableModel(table);
		table.setModel(tableModel);	
		table.getSelectionModel().addListSelectionListener(eventHandler);
		
		addFeatureButton.addActionListener(eventHandler);
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(eventHandler);
		textFieldSearch.getDocument().addDocumentListener(eventHandler);
		
		showDiagramLabel.addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void mouseClicked(MouseEvent e) {
				if (ivjCartoonEditorPanel1.isVisible()) {
					ivjCartoonEditorPanel1.setVisible(false);
					showDiagramLabel.setText("<html><u>Show Diagram &gt;&gt;</u></html>");
				} else {
					ivjCartoonEditorPanel1.setVisible(true);
					showDiagramLabel.setText("<html><u>Hide Diagram &lt;&lt;</u></html>");				
				}
				
			}
		});
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
	
	public void setBioModel(BioModel newValue) {
		BioModel oldValue = bioModel;
		bioModel = newValue;
		
//		if (oldValue != null) {			
//			oldValue.removePropertyChangeListener(this);
//			if (oldValue.getBioEvents() != null) {		
//				for (BioEvent be : oldValue.getBioEvents()) {
//					be.removePropertyChangeListener(this);
//				}
//			}
//		}
//			
//		if (argSimContext != null) {
//			argSimContext.addPropertyChangeListener(this);
//			if (argSimContext.getBioEvents() != null) {		
//				for (BioEvent be : argSimContext.getBioEvents()) {
//					be.addPropertyChangeListener(this);
//				}
//			}
//		}
		firePropertyChange(PROPERTY_NAME_BIO_MODEL, oldValue, newValue);
	}
	
	public void searchTable() {
		String text = textFieldSearch.getText();
		tableModel.setSearchText(text);
	}

	public void setDocumentManager(DocumentManager documentManager) {
		ivjCartoonEditorPanel1.setDocumentManager(documentManager);		
	}
}
