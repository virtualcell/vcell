package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.JTabbedPaneEnhanced;
import org.vcell.util.gui.ScrollTable;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.ReactionRuleParticipantLocal;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionRule.ReactionRuleParticipantType;
import cbit.vcell.model.common.VCellErrorMessages;


@SuppressWarnings("serial")

public class ReactionRulePropertiesPanel extends JTabbedPaneEnhanced {
	protected SelectionManager selectionManager = null;
	protected IssueManager issueManager = null;

	private BioModel bioModel = null;
	private ReactionRule reactionRule = null;
	private JLabel titleLabel = null;

	ReactionRuleKineticsPropertiesPanel kpp = null;
	ReactionRuleEditorPropertiesPanel epp = null;

	private InternalEventHandler eventHandler = new InternalEventHandler();

	private class InternalEventHandler implements PropertyChangeListener, ActionListener, MouseListener
	{
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == reactionRule) {
				if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_REACTANT_WARNING)) {
					Object warning = evt.getNewValue();
				} else if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_PRODUCT_WARNING)) {
					Object warning = evt.getNewValue();
				} else if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_NAME)) {
					updateTitleLabel();
				}
			}
		}
		public void actionPerformed(ActionEvent e) {
		}
		public void mouseClicked(MouseEvent e) {			
		}
		public void mousePressed(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}
		public void valueChanged(TreeSelectionEvent e) {
		}
	}
	private ChangeListener changeListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			final String prologue = "<html><b>";
			final String epilogue = "</html></b>";
			JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();	// it's the ReactionRulePropertiesPanel itself
			int numTabs = sourceTabbedPane.getTabCount();
			for(int i = 0; i < numTabs; i++) {
				String curTitle = sourceTabbedPane.getTitleAt(i);
				if(curTitle.startsWith(prologue)) {
					curTitle = curTitle.substring(prologue.length());
					curTitle = curTitle.substring(0, curTitle.indexOf(epilogue));
					setTitleAt(i, curTitle);
				}
			}
			int index = sourceTabbedPane.getSelectedIndex();
			System.out.println("Tab changed to: " + sourceTabbedPane.getTitleAt(index));
			String selectedTitle = sourceTabbedPane.getTitleAt(index);	// we know it's clean of prologue or epilogue
			selectedTitle = prologue + selectedTitle + epilogue;
			setTitleAt(index, selectedTitle);
		}
	};
		
	public ReactionRulePropertiesPanel() {
		super();
		kpp = new ReactionRuleKineticsPropertiesPanel();
		epp = new ReactionRuleEditorPropertiesPanel();

		setTabPlacement(TOP);
		addTab(" Kinetics ", VCellIcons.kineticsPropertiesIcon, kpp);
		addTab(" Editor ", VCellIcons.editorPropertiesIcon, epp);
		
		addChangeListener(changeListener);
		setSelectedComponent(epp);
	}
	
	private void handleException(java.lang.Throwable exception) {
		 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		 exception.printStackTrace(System.out);
	}

	public void setBioModel(BioModel newValue) {
		kpp.setBioModel(newValue);
		epp.setBioModel(newValue);
		if (bioModel == newValue) {
			return;
		}
		bioModel = newValue;
	}	
	
//	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		if (selectedObjects == null || selectedObjects.length != 1) {
			setReactionRule(null);
		} else if (selectedObjects[0] instanceof ReactionRule) {
			setReactionRule((ReactionRule) selectedObjects[0]);
		} else {
			setReactionRule(null);
		}
	}

	private void setReactionRule(ReactionRule newValue) {
		kpp.setReactionRule(reactionRule);
		epp.setReactionRule(reactionRule);
		if (reactionRule == newValue) {
			return;
		}
		ReactionRule oldValue = reactionRule;
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(eventHandler);
		}
		reactionRule = newValue;
		if (newValue != null) {
			newValue.addPropertyChangeListener(eventHandler);
		}
		refreshInterface();
	}
	
	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
		if (selectionManager != null) {
			kpp.setSelectionManager(selectionManager);
			epp.setSelectionManager(selectionManager);
		}
	}

	public void stateChanged(ChangeEvent e) {

	}


	protected void refreshInterface() {
		if (reactionRule == null){	// sanity check
			return;
		}
		updateTitleLabel();
	}
	
	private void updateTitleLabel() {
		if (reactionRule != null) {
			titleLabel.setText("Properties for Reaction Rule: " + reactionRule.getName());
		}
	}
}
