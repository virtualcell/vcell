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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
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
import org.vcell.util.Compare;
import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.ReactionRuleParticipantLocal;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.model.DistributedKinetics;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.RbmKineticLaw.RbmKineticLawParameterType;
import cbit.vcell.model.ReactionRule.ReactionRuleParticipantType;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.model.gui.ParameterTableModel;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;


@SuppressWarnings("serial")

public class ReactionRuleKineticsPropertiesPanel extends DocumentEditorSubPanel {
	
	private BioModel bioModel = null;
	private ReactionRule reactionRule = null;
	private JLabel titleLabel = null;
	private JCheckBox isReversibleCheckBox;
	private JComboBox kineticsTypeComboBox = null;
	private JButton jToggleButton = null;

	private ReactionRulePropertiesTableModel tableModel = null;
	private ScrollTable table = null;
	private JTextField nameTextField = null;
	private JTextArea annotationTextArea;

	private InternalEventHandler eventHandler = new InternalEventHandler();

	private class BioModelNodeEditableTree extends JTree {
		@Override
		public boolean isPathEditable(TreePath path) {
			Object object = path.getLastPathComponent();
			return object instanceof BioModelNode;
		}
	}
	private class InternalEventHandler implements PropertyChangeListener, ActionListener, FocusListener, MouseListener
	{
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == reactionRule) {
				if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_REACTANT_WARNING)) {

				} else if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_PRODUCT_WARNING)) {

				} else if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_NAME)) {
					nameChanged(evt);
				}
			}
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getKineticsTypeComboBox()) {
				updateKineticChoice();
			} else if (e.getSource() == isReversibleCheckBox) {
				setReversible(isReversibleCheckBox.isSelected());
			}
		}
		@Override
		public void mouseClicked(MouseEvent e) {			
		}
		@Override
		public void mousePressed(MouseEvent e) {
		}
		@Override
		public void mouseReleased(MouseEvent e) {
		}
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
			if(e.getSource() == annotationTextArea){
				changeFreeTextAnnotation();
			}
		}
		@Override
		public void focusGained(FocusEvent e) {
		}
		@Override
		public void focusLost(FocusEvent e) {
			if (e.getSource() == nameTextField) {
				changeName();
			} else if (e.getSource() == annotationTextArea) {
				changeFreeTextAnnotation();
			}
		}
	}
	
	public ReactionRuleKineticsPropertiesPanel() {
		super();
		initialize();
	}
	
	private void initialize() {
		try {
			setName("KineticsTypeTemplatePanel");
			setLayout(new GridBagLayout());
			setBackground(Color.white);

			isReversibleCheckBox = new JCheckBox("");
			isReversibleCheckBox.addActionListener(eventHandler);
			isReversibleCheckBox.setEnabled(true);
			isReversibleCheckBox.setBackground(Color.white);
//			isReversibleCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);

			Border border = BorderFactory.createLineBorder(Color.gray);
			Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();

			TitledBorder annotationBorder = BorderFactory.createTitledBorder(loweredEtchedBorder, " Annotation ");
			annotationBorder.setTitleJustification(TitledBorder.LEFT);
			annotationBorder.setTitlePosition(TitledBorder.TOP);
			annotationBorder.setTitleFont(getFont().deriveFont(Font.BOLD));
			
			nameTextField = new JTextField();
			nameTextField.setEditable(false);
			nameTextField.addFocusListener(eventHandler);
			nameTextField.addActionListener(eventHandler);
			

			GridBagConstraints gbc = new java.awt.GridBagConstraints();
			int gridy = 0;
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; 
			gbc.gridy = gridy;
			gbc.insets = new java.awt.Insets(2, 4, 4, 4);
			gbc.anchor = GridBagConstraints.LINE_END;
			add(new JLabel("Reaction Name"), gbc);
			
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 1; 
			gbc.gridy = gridy;
			gbc.insets = new java.awt.Insets(2, 4, 4, 4);
			gbc.weightx = 1.0;
			gbc.gridwidth = 3;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			add(nameTextField, gbc);

			// --------------------------------------------------------
			
			JPanel p = new JPanel();
			p.setLayout(new GridBagLayout());
			p.setBackground(Color.white);
			int gridyy = 0;
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; 
			gbc.gridy = gridyy;
			gbc.anchor = GridBagConstraints.LINE_END;
			p.add(new JLabel("Reversible"), gbc);
			
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 1; 
			gbc.gridy = gridyy;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			p.add(isReversibleCheckBox, gbc);
			
			gridy ++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 0;
			gbc.insets = new Insets(0, 4, 4, 4);
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.NONE;
			add(p, gbc);
			
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 1; 
			gbc.gridy = gridy;
			gbc.anchor = GridBagConstraints.LINE_END;
			gbc.insets = new java.awt.Insets(0, 4, 4, 4);
			add(new JLabel("Kinetic Type"), gbc);

			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 2; 
			gbc.gridy = gridy;
			gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new java.awt.Insets(0, 4, 4, 4);
			add(getKineticsTypeComboBox(), gbc);
			getKineticsTypeComboBox().setEnabled(false);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 3;
			gbc.gridy = gridy;
			gbc.insets = new java.awt.Insets(0, 4, 4, 4);
			add(getJToggleButton(), gbc);
			getJToggleButton().setEnabled(false);
//			getJToggleButton().setVisible(false);
			
			// --------------------------------------------------------------
			gridy ++;
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; 
			gbc.gridy = gridy;
			gbc.fill = java.awt.GridBagConstraints.BOTH;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.gridwidth = 4;
			add(getScrollPaneTable().getEnclosingScrollPane(), gbc);

			CollapsiblePanel collapsiblePanel = new CollapsiblePanel("Annotations", false);
			collapsiblePanel.getContentPanel().setLayout(new GridBagLayout());
			gridy ++;
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; 
			gbc.gridy = gridy;
			gbc.gridwidth = 4;
			gbc.weightx = 1.0;
			gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
			add(collapsiblePanel, gbc);

			annotationTextArea = new javax.swing.JTextArea("", 4, 30);
			annotationTextArea.setLineWrap(true);
			annotationTextArea.setWrapStyleWord(true);
			annotationTextArea.setFont(new Font("monospaced", Font.PLAIN, 11));
			annotationTextArea.setEditable(false);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.weighty = 0.5;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.anchor = GridBagConstraints.SOUTHWEST;
			JScrollPane jp2 = new JScrollPane(annotationTextArea);
			collapsiblePanel.getContentPanel().add(jp2, gbc);

			annotationTextArea.addFocusListener(eventHandler);
			annotationTextArea.addMouseListener(eventHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	private ScrollTable getScrollPaneTable() {
		if (table == null) {
			try {
				table = new ScrollTable();
				table.setModel(getReactionRulePropertiesTableModel());
				table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return table;
	}
	private ReactionRulePropertiesTableModel getReactionRulePropertiesTableModel() {
		if (tableModel == null) {
			try {
				tableModel = new ReactionRulePropertiesTableModel(getScrollPaneTable(), true);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return tableModel;
	}
	
	private void handleException(java.lang.Throwable exception) {
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		 exception.printStackTrace(System.out);
	}

	public void setBioModel(BioModel newValue) {
		if (bioModel == newValue) {
			return;
		}
		bioModel = newValue;
	}	
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		if (selectedObjects == null || selectedObjects.length != 1) {
			setReactionRule(null);
		} else if (selectedObjects[0] instanceof ReactionRule) {
			setReactionRule((ReactionRule) selectedObjects[0]);
		} else {
			setReactionRule(null);
		}
	}

	private void nameChanged(PropertyChangeEvent e) {
		if (reactionRule == null) {
			return;
		}
		nameTextField.setText(reactionRule.getName());
	}
	private void changeName() {
		if (reactionRule == null) {
			return;
		}
		String newName = nameTextField.getText();
		if (newName == null || newName.length() == 0) {
			nameTextField.setText(reactionRule.getName());
			return;
		}
		if (newName.equals(reactionRule.getName())) {
			return;
		}
		try {
			reactionRule.setName(newName);
		} catch (PropertyVetoException e1) {
			e1.printStackTrace();
			DialogUtils.showErrorDialog(this, e1.getMessage());
		}
	}
	
	public void setReactionRule(ReactionRule newValue) {
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
		getReactionRulePropertiesTableModel().setReactionRule(reactionRule);
		refreshInterface();
	}
	private void setReversible(boolean bReversible) {
		reactionRule.setReversible(bReversible);
		if(!bReversible && reactionRule != null) {
			// we know for sure it must be MassAction
			LocalParameter lp = reactionRule.getKineticLaw().getLocalParameter(RbmKineticLawParameterType.MassActionReverseRate);
			try {
				lp.setExpression(new Expression(0.0d));
			} catch (ExpressionBindingException e) {
				e.printStackTrace();
			}
		}
		getReactionRulePropertiesTableModel().refreshData();
	}

	protected void refreshInterface() {
		
		boolean bNonNullRule = reactionRule != null && bioModel != null;
		annotationTextArea.setEditable(bNonNullRule);
		if (bNonNullRule) {
			VCMetaData vcMetaData = bioModel.getModel().getVcMetaData();
			annotationTextArea.setText(vcMetaData.getFreeTextAnnotation(reactionRule));
			nameTextField.setEditable(true);
			nameTextField.setText(reactionRule.getName());
			isReversibleCheckBox.setSelected(reactionRule.isReversible());
		} else {
			annotationTextArea.setText(null);
			nameTextField.setEditable(false);
			nameTextField.setText(null);
			isReversibleCheckBox.setSelected(false);
		}
	}

	private JComboBox getKineticsTypeComboBox() {
		if (kineticsTypeComboBox == null) {
			kineticsTypeComboBox = new JComboBox();
			kineticsTypeComboBox.setName("JComboBox1");
			kineticsTypeComboBox.setRenderer(new DefaultListCellRenderer() {
				private final static String PI = "\u03A0";
				private final static String PIs = "<strong>\u03A0</strong>";
				private final static String PIsb = "<strong><big>\u03A0</big></strong>";
				private final static String pi = "\u03C0";
				private final static String MULT = "\u22C5";

				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					java.awt.Component component = super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
					String str = "<html>Mass Action ( for each reaction:  ";
					str += "Kf" + MULT + PIs + " <small>reactants</small> - Kr" + MULT + PIs + " <small>products</small> )</html>";
					setText(str);
					return component;
				}
			});
		}
		return kineticsTypeComboBox;
	}
	private void updateKineticChoice() {
	}

	private JButton getJToggleButton() {
		if (jToggleButton == null) {
			jToggleButton = new JButton("Convert units");
		}
		return jToggleButton;
	}
	private void updateToggleButtonLabel(){
		getJToggleButton().setText("Convert units");
		getJToggleButton().setToolTipText("Feature unavailable at this time.");
	}
	
	
	private void changeFreeTextAnnotation() {
		try{
			if (reactionRule == null) {
				return;
			}
			// set text from annotationTextField in free text annotation for species in vcMetaData (from model)
			if(bioModel.getModel() != null && bioModel.getModel().getVcMetaData() != null){
				VCMetaData vcMetaData = bioModel.getModel().getVcMetaData();
				String textAreaStr = (annotationTextArea.getText() == null || annotationTextArea.getText().length()==0?null:annotationTextArea.getText());
				if(!Compare.isEqualOrNull(vcMetaData.getFreeTextAnnotation(reactionRule),textAreaStr)){
					vcMetaData.setFreeTextAnnotation(reactionRule, textAreaStr);	
				}
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
			PopupGenerator.showErrorDialog(this,"Edit Reaction Rule Error\n"+e.getMessage(), e);
		}
	}

}
