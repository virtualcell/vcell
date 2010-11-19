package cbit.vcell.client.desktop.biomodel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import javax.swing.JButton;
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
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;

@SuppressWarnings("serial")
public class BioModelEditorSpeciesPanel extends JPanel {
	private static final String PROPERTY_NAME_BIO_MODEL = "bioModel";
	
	private JButton addButton = null;
	private JButton deleteButton = null;
	private ScrollTable table;
	private BioModel bioModel;
	private BioModelEditorSpeciesTableModel tableModel = null;
	private JTextField textFieldSearch = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	
	private class InternalEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, ListSelectionListener, DocumentListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelEditorSpeciesPanel.this && evt.getPropertyName().equals(PROPERTY_NAME_BIO_MODEL)) {
				tableModel.setModel(bioModel.getModel());
			}			
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == addButton) {
				SpeciesContext speciesContext = new SpeciesContext(new Species(bioModel.getModel().getFreeSpeciesName(), null), bioModel.getModel().getStructures()[0]);
				try {
					bioModel.getModel().addSpecies(speciesContext.getSpecies());
					bioModel.getModel().addSpeciesContext(speciesContext);
				} catch (PropertyVetoException ex) {
					ex.printStackTrace();
					DialogUtils.showErrorDialog(BioModelEditorSpeciesPanel.this, ex.getMessage());
				}
			} else if (e.getSource() == deleteButton) {
				int[] rows = table.getSelectedRows();
				ArrayList<SpeciesContext> deleteList = new ArrayList<SpeciesContext>();
				for (int r : rows) {
					if (r < bioModel.getModel().getNumSpeciesContexts()) {
						deleteList.add(tableModel.getValueAt(r));
					}
				}
				try {
					for (SpeciesContext sc : deleteList) {
						bioModel.getModel().removeSpeciesContext(sc);
					}
				} catch (PropertyVetoException ex) {
					ex.printStackTrace();
					DialogUtils.showErrorDialog(BioModelEditorSpeciesPanel.this, ex.getMessage());
				}
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			if (bioModel == null || e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == table.getSelectionModel()) {
				int[] rows = table.getSelectedRows();
				deleteButton.setEnabled(rows != null && rows.length > 0 && (rows.length > 1 || rows[0] < bioModel.getModel().getNumSpeciesContexts()));
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
	public BioModelEditorSpeciesPanel() {
		super();
		initialize();
	}

	private void initialize() {
		addButton = new JButton("New");
		deleteButton = new JButton("Delete");
		table = new EditorScrollTable();
		textFieldSearch = new JTextField(10);
		
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
		addButton.setPreferredSize(deleteButton.getPreferredSize());
		add(addButton, gbc);
		
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
		
		addPropertyChangeListener(eventHandler);
		tableModel = new BioModelEditorSpeciesTableModel(table);
		table.setModel(tableModel);	
		table.getSelectionModel().addListSelectionListener(eventHandler);
//		table.setDefaultRenderer(SpeciesContext.class, new DefaultScrollTableCellRenderer() {
//
//			@Override
//			public Component getTableCellRendererComponent(JTable table,
//					Object value, boolean isSelected, boolean hasFocus,
//					int row, int column) {
//				super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
//						row, column);
//				if (value instanceof SpeciesContext) {
//					setText(((SpeciesContext)value).getName());
//				}
//				return this;
//			}
//			
//		});
		
		addButton.addActionListener(eventHandler);
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(eventHandler);
		textFieldSearch.getDocument().addDocumentListener(eventHandler);
	}
	
	public static void main(java.lang.String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
			JFrame frame = new javax.swing.JFrame();
			BioModelEditorSpeciesPanel panel = new BioModelEditorSpeciesPanel();
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
}
