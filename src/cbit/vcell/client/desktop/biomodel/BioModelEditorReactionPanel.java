package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.JDesktopPaneEnhanced;
import org.vcell.util.gui.JInternalFrameEnhanced;

import cbit.gui.graph.GraphModel;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.graph.ReactionCartoonEditorPanel;
import cbit.vcell.graph.structures.AllStructureSuite;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;

@SuppressWarnings("serial")
public class BioModelEditorReactionPanel extends BioModelEditorRightSidePanel<ReactionStep> {
	private InternalEventHandler eventHandler = new InternalEventHandler();
	private ReactionCartoonEditorPanel reactionCartoonEditorPanel = null;
	private ReactionPropertiesPanel reactionStepPropertiesPanel = null;
	private JTabbedPane tabbedPane = null;
	private JDesktopPaneEnhanced desktopPane = null;
	private JInternalFrameEnhanced diagramViewInternalFrame = null;
	
	private class InternalEventHandler implements java.beans.PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelEditorReactionPanel.this && evt.getPropertyName().equals(PROPERTY_NAME_BIO_MODEL)) {
				if(bioModel != null) {
					reactionCartoonEditorPanel.setModel(bioModel.getModel());
					reactionCartoonEditorPanel.setStructureSuite(
							new AllStructureSuite(BioModelEditorReactionPanel.this));
				}
			} else if (evt.getSource() == reactionCartoonEditorPanel && evt.getPropertyName().equals(ReactionCartoonEditorPanel.PROPERTY_NAME_FLOATING)) {
				showDiagramView((Boolean) evt.getNewValue());
			} else if (evt.getSource() == reactionCartoonEditorPanel.getReactionCartoon() && evt.getPropertyName().equals(GraphModel.PROPERTY_NAME_SELECTED)) {
				Object[] selectedObject = (Object[]) evt.getNewValue();
				if (selectedObject != null && selectedObject.length == 1 && selectedObject[0] instanceof ReactionStep) {
					select((ReactionStep) selectedObject[0]);
				} else {
					select(null);
				}
			}
		}
	}
	public BioModelEditorReactionPanel() {
		super();
		addPropertyChangeListener(eventHandler);
		initialize();
	}
        
	private void initialize() {
		reactionCartoonEditorPanel = new ReactionCartoonEditorPanel();
		reactionCartoonEditorPanel.setName("Diagram View");
		reactionCartoonEditorPanel.addPropertyChangeListener(eventHandler);
		reactionCartoonEditorPanel.getReactionCartoon().addPropertyChangeListener(eventHandler);
		reactionStepPropertiesPanel = new ReactionPropertiesPanel();
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		topPanel.add(new JLabel("Search "), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		topPanel.add(textFieldSearch, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,100,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		newButton.setPreferredSize(deleteButton.getPreferredSize());
		topPanel.add(newButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.insets = new Insets(4,4,4,20);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		topPanel.add(deleteButton, gbc);
		
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Diagram View", reactionCartoonEditorPanel);
		tabbedPane.addTab("Table View", table.getEnclosingScrollPane());
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 5;
		gbc.fill = GridBagConstraints.BOTH;
		topPanel.add(tabbedPane, gbc);		
						
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(topPanel);
		splitPane.setBottomComponent(reactionStepPropertiesPanel);

		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
		splitPane.setDividerLocation(450);
		splitPane.setResizeWeight(0.7);
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
	
	private void showDiagramView(boolean bFloating) {
		if (desktopPane == null) {
			desktopPane = (JDesktopPaneEnhanced)JOptionPane.getDesktopPaneForComponent(this);
		}
		if (desktopPane == null) {
			return;
		}
		if (bFloating) {
			diagramViewInternalFrame = new JInternalFrameEnhanced("Reaction Diagram View");
			tabbedPane.remove(reactionCartoonEditorPanel);
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.add(p, BorderLayout.NORTH);
			panel.add(reactionCartoonEditorPanel, BorderLayout.CENTER);
			diagramViewInternalFrame.setResizable(true);
			diagramViewInternalFrame.setMaximizable(true);
			diagramViewInternalFrame.setIconifiable(true);
			diagramViewInternalFrame.add(panel);
			diagramViewInternalFrame.pack();
			BeanUtils.centerOnComponent(diagramViewInternalFrame, this);
			DocumentWindowManager.showFrame(diagramViewInternalFrame, desktopPane);
		} else {	
			DocumentWindowManager.close(diagramViewInternalFrame, desktopPane);			
			tabbedPane.add(reactionCartoonEditorPanel, 0);
			tabbedPane.setSelectedComponent(reactionCartoonEditorPanel);
		}
	}

	protected void newButtonPressed() {
		SimpleReaction reactionStep = bioModel.getModel().createSimpleReaction(bioModel.getModel().getStructures()[0]);
		select(reactionStep);
	}

	protected void deleteButtonPressed() {
		int[] rows = table.getSelectedRows();
		if (rows == null || rows.length == 0) {
			return;
		}
		String confirm = PopupGenerator.showOKCancelWarningDialog(this, "Are you sure you want to delete selected reaction(s)?");
		if (confirm.equals(UserMessage.OPTION_CANCEL)) {
			return;
		}
		ArrayList<ReactionStep> deleteList = new ArrayList<ReactionStep>();
		for (int r : rows) {
			if (r < tableModel.getDataSize()) {
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
	}

	@Override
	protected void bioModelChange() {
		super.bioModelChange();
		reactionCartoonEditorPanel.setModel(bioModel.getModel());
	}

	@Override
	protected void tableSelectionChanged() {
		super.tableSelectionChanged();
		int[] rows = table.getSelectedRows();
		Object[] objects = new Object[rows.length];
		int i = 0;
		for (int row : rows) {
			objects[i ++] = tableModel.getValueAt(row);
		}
		reactionCartoonEditorPanel.getReactionCartoon().setSelectedObjects(objects);
		
		if (rows != null && rows.length == 1 && rows[0] < tableModel.getDataSize()) {					
			ReactionStep reactionStep = tableModel.getValueAt(rows[0]);
			reactionStepPropertiesPanel.setReactionStep(reactionStep);
		} else {
			reactionStepPropertiesPanel.setReactionStep(null);
		}
	}

	@Override
	protected BioModelEditorRightSideTableModel<ReactionStep> createTableModel() {
		return new BioModelEditorReactionTableModel(table);
	}	
}
