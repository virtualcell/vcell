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
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.JDesktopPaneEnhanced;
import org.vcell.util.gui.JInternalFrameEnhanced;

import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.graph.ReactionSlicesCartoonEditorPanel;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.gui.KineticsTypeTemplatePanel;

@SuppressWarnings("serial")
public class BioModelEditorReactionPanel extends BioModelEditorRightSidePanel<ReactionStep> {
	private InternalEventHandler eventHandler = new InternalEventHandler();
	private ReactionSlicesCartoonEditorPanel reactionCartoonEditorPanel = null;
	private KineticsTypeTemplatePanel kineticsTypeTemplatePanel = null;
	private JTabbedPane tabbedPane = null;
	private JDesktopPaneEnhanced desktopPane = null;
	private JInternalFrameEnhanced diagramViewInternalFrame = null;
	
	private class InternalEventHandler implements java.beans.PropertyChangeListener, ListSelectionListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelEditorReactionPanel.this && evt.getPropertyName().equals(PROPERTY_NAME_BIO_MODEL)) {
				reactionCartoonEditorPanel.setModel(bioModel.getModel());
			} else if (evt.getSource() == reactionCartoonEditorPanel && evt.getPropertyName().equals(ReactionSlicesCartoonEditorPanel.PROPERTY_NAME_FLOATING)) {
				showDiagramView((Boolean) evt.getNewValue());
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			if (bioModel == null || e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == table.getSelectionModel()) {
				int[] rows = table.getSelectedRows();
				if (rows != null && rows.length == 1 && rows[0] < bioModel.getModel().getNumReactions()) {					
					kineticsTypeTemplatePanel.setReactionStep(tableModel.getValueAt(rows[0]));
				} else {
					kineticsTypeTemplatePanel.setReactionStep(null);
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
		tableModel = new BioModelEditorReactionTableModel(table);
		table.setModel(tableModel);
		table.getSelectionModel().addListSelectionListener(eventHandler);
		reactionCartoonEditorPanel = new ReactionSlicesCartoonEditorPanel();
		reactionCartoonEditorPanel.addPropertyChangeListener(eventHandler);
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
		tabbedPane.addTab("Table View", table.getEnclosingScrollPane());
		tabbedPane.addTab("Diagram View", reactionCartoonEditorPanel);
		
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
			tabbedPane.addTab("Diagram View", reactionCartoonEditorPanel);
			tabbedPane.setSelectedComponent(reactionCartoonEditorPanel);
		}
	}

	protected void newButtonPressed() {
		try {
			ReactionStep reactionStep = new SimpleReaction(bioModel.getModel().getStructures()[0], bioModel.getModel().getFreeReactionStepName());
			bioModel.getModel().addReactionStep(reactionStep);
		} catch (PropertyVetoException ex) {
			ex.printStackTrace();
			DialogUtils.showErrorDialog(BioModelEditorReactionPanel.this, ex.getMessage());
		}
	}

	protected void deleteButtonPressed() {
		int[] rows = table.getSelectedRows();
		ArrayList<ReactionStep> deleteList = new ArrayList<ReactionStep>();
		for (int r : rows) {
			if (r < bioModel.getModel().getNumReactions()) {
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
}
