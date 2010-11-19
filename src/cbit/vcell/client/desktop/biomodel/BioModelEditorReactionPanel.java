package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DownArrowIcon;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.JDesktopPaneEnhanced;
import org.vcell.util.gui.JInternalFrameEnhanced;
import org.vcell.util.gui.ScrollTable;

import cbit.gui.LabelButton;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.graph.ReactionSlicesCartoonEditorPanel;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.gui.KineticsTypeTemplatePanel;

@SuppressWarnings("serial")
public class BioModelEditorReactionPanel extends JPanel {
	private static final int DIAGRAM_VIEW_TAB_INDEX = 1;

	private static final int TABLE_VIEW_TAB_INDEX = 0;

	private static final String PROPERTY_NAME_BIO_MODEL = "bioModel";
	
	private JButton addButton = null;
	private JButton deleteButton = null;
	private ScrollTable table;
	private BioModel bioModel;
	private BioModelEditorReactionTableModel tableModel = null;
	private JTextField textFieldSearch = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	private ReactionSlicesCartoonEditorPanel reactionCartoonEditorPanel = null;
	private KineticsTypeTemplatePanel kineticsTypeTemplatePanel = null;
	private JTabbedPane tabbedPane = null;
	private JButton floatingTabButton = null;
	private JPopupMenu floatingPopupMenu = null;

	private JMenuItem floatingMenuItem;
	private JButton dockButton;
	private JDesktopPaneEnhanced desktopPane = null;
	private JInternalFrameEnhanced diagramViewInternalFrame = null;

	private JPanel diagramViewTabComponent;
	
	private class InternalEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, 
		ListSelectionListener, DocumentListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelEditorReactionPanel.this && evt.getPropertyName().equals(PROPERTY_NAME_BIO_MODEL)) {
				tableModel.setModel(bioModel.getModel());
				reactionCartoonEditorPanel.setModel(bioModel.getModel());
			}			
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == addButton) {
				try {
					ReactionStep reactionStep = new SimpleReaction(bioModel.getModel().getStructures()[0], bioModel.getModel().getFreeReactionStepName());
					bioModel.getModel().addReactionStep(reactionStep);
				} catch (PropertyVetoException ex) {
					ex.printStackTrace();
					DialogUtils.showErrorDialog(BioModelEditorReactionPanel.this, ex.getMessage());
				}
			} else if (e.getSource() == deleteButton) {
				int[] rows = table.getSelectedRows();
				ArrayList<ReactionStep> deleteList = new ArrayList<ReactionStep>();
				for (int r : rows) {
					if (r < bioModel.getModel().getNumSpeciesContexts()) {
						deleteList.add(tableModel.getValueAt(r));
					}
				}
				try {
					for (ReactionStep sc : deleteList) {
						bioModel.getModel().removeReactionStep(sc);
					}
				} catch (PropertyVetoException ex) {
					ex.printStackTrace();
					DialogUtils.showErrorDialog(BioModelEditorReactionPanel.this, ex.getMessage());
				}
			} else if (e.getSource() == floatingTabButton) {
				floatingPopupMenu.show(floatingTabButton, 0, (int)(floatingTabButton.getSize().getHeight()));
			} else if (e.getSource() == floatingMenuItem) {
				showDiagramView(true);
			} else if (e.getSource() == dockButton) {
				showDiagramView(false);
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
	public BioModelEditorReactionPanel() {
		super();
		initialize();
	}
        
	private void initialize() {
		addButton = new JButton("New");
		deleteButton = new JButton("Delete");
		table = new EditorScrollTable();
		textFieldSearch = new JTextField(10);
		reactionCartoonEditorPanel = new ReactionSlicesCartoonEditorPanel(null);
		kineticsTypeTemplatePanel = new KineticsTypeTemplatePanel();
		
		setLayout(new GridBagLayout());
		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		add(new JLabel("Search "), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		add(textFieldSearch, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,100,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		addButton.setPreferredSize(deleteButton.getPreferredSize());
		add(addButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.insets = new Insets(4,4,4,20);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(deleteButton, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(new JSeparator(), gbc);
		
		tabbedPane = new JTabbedPane();
		tabbedPane.insertTab("Table View", null, table.getEnclosingScrollPane(), "", TABLE_VIEW_TAB_INDEX);
		tabbedPane.insertTab("Diagram View", null, reactionCartoonEditorPanel, "", DIAGRAM_VIEW_TAB_INDEX);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(4,4,0,4);
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 5;
		gbc.fill = GridBagConstraints.BOTH;
		add(tabbedPane, gbc);		
		
		diagramViewTabComponent = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		diagramViewTabComponent.setBackground(Color.WHITE);
		diagramViewTabComponent.add(new JLabel("Diagram View"));
		floatingTabButton = new LabelButton(new DownArrowIcon());
		diagramViewTabComponent.add(floatingTabButton);
		diagramViewTabComponent.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		tabbedPane.setTabComponentAt(1, diagramViewTabComponent);
				
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(0,4,4,4);
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 5;
		gbc.fill = GridBagConstraints.BOTH;
		add(kineticsTypeTemplatePanel, gbc);
			
		floatingPopupMenu = new JPopupMenu();
		floatingMenuItem = new JMenuItem("\u21b1 Floating");
		floatingPopupMenu.add(floatingMenuItem);
		dockButton = new LabelButton("\u21b5 Dock");

		addPropertyChangeListener(eventHandler);
		tableModel = new BioModelEditorReactionTableModel(table);
		table.setModel(tableModel);	
		table.getSelectionModel().addListSelectionListener(eventHandler);
		
		addButton.addActionListener(eventHandler);
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(eventHandler);
		textFieldSearch.getDocument().addDocumentListener(eventHandler);
		
		floatingTabButton.addActionListener(eventHandler);
		floatingMenuItem.addActionListener(eventHandler);
		dockButton.addActionListener(eventHandler);
	}
	
	public static void main(java.lang.String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
			JFrame frame = new javax.swing.JFrame();
			BioModelEditorReactionPanel panel = new BioModelEditorReactionPanel();
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
	
	private void showDiagramView(boolean bFloating) {
		if (desktopPane == null) {
			desktopPane = (JDesktopPaneEnhanced)JOptionPane.getDesktopPaneForComponent(this);
		}
		if (desktopPane == null) {
			return;
		}
		if (bFloating) {
			diagramViewInternalFrame = new JInternalFrameEnhanced("Diagram View");
			tabbedPane.remove(1);
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			p.add(dockButton);
			panel.add(p, BorderLayout.NORTH);
			panel.add(reactionCartoonEditorPanel, BorderLayout.CENTER);
			diagramViewInternalFrame.setResizable(true);
			diagramViewInternalFrame.setMaximizable(true);
			diagramViewInternalFrame.setIconifiable(true);
			diagramViewInternalFrame.add(panel);
			diagramViewInternalFrame.pack();
			BeanUtils.centerOnComponent(diagramViewInternalFrame, table);
			DocumentWindowManager.showFrame(diagramViewInternalFrame, desktopPane);
		} else {	
			DocumentWindowManager.close(diagramViewInternalFrame, desktopPane);			
			tabbedPane.insertTab("Diagram View", null, reactionCartoonEditorPanel, "", DIAGRAM_VIEW_TAB_INDEX);
			tabbedPane.setTabComponentAt(1, diagramViewTabComponent);
			tabbedPane.setSelectedIndex(DIAGRAM_VIEW_TAB_INDEX);
		}
	}
}
