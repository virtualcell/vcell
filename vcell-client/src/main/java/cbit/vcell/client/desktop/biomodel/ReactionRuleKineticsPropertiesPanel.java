package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.TreePath;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Entity;
import org.vcell.relationship.RelationshipObject;
import org.vcell.util.Compare;
import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.model.RbmKineticLaw.RateLawType;
import cbit.vcell.model.RbmKineticLaw.RbmKineticLawParameterType;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.RbmKineticLaw;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;


@SuppressWarnings("serial")

public class ReactionRuleKineticsPropertiesPanel extends DocumentEditorSubPanel {
	
	private BioModel bioModel = null;
	private ReactionRule reactionRule = null;
	private JCheckBox isReversibleCheckBox;
	private JComboBox<RateLawType> kineticsTypeComboBox = null;
	private JButton jToggleButton = null;
	private JLabel titleLabel = null;

	private ReactionRulePropertiesTableModel tableModel = null;
	private ScrollTable table = null;
	private JTextField nameTextField = null;
	private JTextArea annotationTextArea;

	private JScrollPane linkedPOScrollPane;

	private InternalEventHandler eventHandler = new InternalEventHandler();

//	private class BioModelNodeEditableTree extends JTree {
//		@Override
//		public boolean isPathEditable(TreePath path) {
//			Object object = path.getLastPathComponent();
//			return object instanceof BioModelNode;
//		}
//	}
	
	private final static RateLawType[] RateLawTypes = {
		RateLawType.MassAction,
		RateLawType.MichaelisMenten,
//		RateLawType.Saturable,
	};
	
	private class InternalEventHandler implements PropertyChangeListener, ActionListener, FocusListener, MouseListener
	{
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == reactionRule) {
				if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_REACTANT_WARNING)) {

				} else if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_PRODUCT_WARNING)) {

				} else if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_NAME)) {
					nameChanged(evt);
				} else if(evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_REVERSIBLE)) {
					isReversibleCheckBox.setSelected((boolean)(evt.getNewValue()));
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

			isReversibleCheckBox = new JCheckBox("Reversible");
			isReversibleCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
			isReversibleCheckBox.addActionListener(eventHandler);
			isReversibleCheckBox.setEnabled(true);
			isReversibleCheckBox.setBackground(Color.white);
//			isReversibleCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);

			Border border = BorderFactory.createLineBorder(Color.gray);
			Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();

			TitledBorder annotationBorder = BorderFactory.createTitledBorder(loweredEtchedBorder, " Annotation and Pathway Links ");
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
			gridy ++;
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 0;
			gbc.insets = new Insets(0, 4, 4, 4);
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.NONE;
			add(p, gbc);
			
			p.setLayout(new GridBagLayout());
			p.setBackground(Color.white);
			int gridyy = 0;
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; 
			gbc.gridy = gridyy;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			p.add(isReversibleCheckBox, gbc);
			
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

			// 'true' means expanded
			CollapsiblePanel collapsiblePanel = new CollapsiblePanel(" Annotation and Pathway Links ", true);
			collapsiblePanel.getContentPanel().setLayout(new GridBagLayout());
						
			JPanel jp1 = new JPanel();
			jp1.setLayout(new GridBagLayout());
			
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.LINE_START;
			JLabel pathwayLink = new JLabel("Linked Pathway Object(s): ");
			jp1.add(pathwayLink, gbc);
			
			linkedPOScrollPane = new JScrollPane();
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			jp1.add(linkedPOScrollPane, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.LINE_START;
			collapsiblePanel.getContentPanel().add(jp1, gbc);

			annotationTextArea = new javax.swing.JTextArea("", 4, 30);
			annotationTextArea.setLineWrap(true);
			annotationTextArea.setWrapStyleWord(true);
			annotationTextArea.setFont(new Font("monospaced", Font.PLAIN, 11));
			annotationTextArea.setEditable(false);
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.weightx = 1.0;
			gbc.weighty = 0.5;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.anchor = GridBagConstraints.SOUTHWEST;
			JScrollPane jp2 = new JScrollPane(annotationTextArea);
			collapsiblePanel.getContentPanel().add(jp2, gbc);

			gridy ++;
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; 
			gbc.gridy = gridy;
			gbc.gridwidth = 4;
			gbc.weightx = 1.0;
			gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
			add(collapsiblePanel, gbc);

			getKineticsTypeComboBox().addActionListener(eventHandler);
			getKineticsTypeComboBox().setEnabled(false);
			initKineticChoices();
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
		updateInterface();
	}
	private void setReversible(boolean bReversible) {
		reactionRule.setReversible(bReversible);
		if(!bReversible && reactionRule != null) {
			LocalParameter lp = null;
			switch(reactionRule.getKineticLaw().getRateLawType()) {
			case MassAction:
				lp = reactionRule.getKineticLaw().getLocalParameter(RbmKineticLawParameterType.MassActionReverseRate);
				break;
			case MichaelisMenten:	// not reversible
			case Saturable:
				return;
			default:
				throw new RuntimeException("unsupported rule-based kinetic law "+reactionRule.getKineticLaw().getRateLawType());
			}
			try {
				lp.setExpression(new Expression(0.0d));
			} catch (ExpressionBindingException e) {
				e.printStackTrace();
			}
		}
		getReactionRulePropertiesTableModel().refreshData();
	}

	private RateLawType getRateLawType(RbmKineticLaw kineticLaw) {
		if (kineticLaw != null) {
			return kineticLaw.getRateLawType();
		}else{
			return null;
		}
	}
	
	private void initKineticChoices() {
		javax.swing.DefaultComboBoxModel<RateLawType> model = new DefaultComboBoxModel<RateLawType>();
		for(RateLawType rateLawType : RateLawTypes) {
			model.addElement(rateLawType);
		}
		getKineticsTypeComboBox().setModel(model);
		return;
	}
	private void updateKineticChoice() {
		RateLawType newRateLawType = (RateLawType)getKineticsTypeComboBox().getSelectedItem();
		// if same as current kinetics, don't create new one
		if (reactionRule == null || reactionRule.getKineticLaw().getRateLawType().equals(newRateLawType)) {
			return;
		}
		RbmKineticLaw newRbmKineticLaw = new RbmKineticLaw(reactionRule, newRateLawType);
		reactionRule.setKineticLaw(newRbmKineticLaw);
		if(newRateLawType != RateLawType.MassAction) {
			reactionRule.setReversible(false);
			isReversibleCheckBox.setSelected(reactionRule.isReversible());
			isReversibleCheckBox.setEnabled(false);
		} else {
			isReversibleCheckBox.setSelected(reactionRule.isReversible());
			isReversibleCheckBox.setEnabled(true);
		}
	}

	
	protected void updateInterface() {
		
		boolean bNonNullRule = reactionRule != null && bioModel != null;
		annotationTextArea.setEditable(bNonNullRule);
		//getKineticsTypeComboBox().setEnabled(bNonNullRule);	// TODO: here!!!
		getKineticsTypeComboBox().setEnabled(false);			// TODO: here!!!

		if (bNonNullRule) {
//			initKineticChoices();
			VCMetaData vcMetaData = bioModel.getModel().getVcMetaData();
			annotationTextArea.setText(vcMetaData.getFreeTextAnnotation(reactionRule));
			nameTextField.setEditable(true);
			nameTextField.setText(reactionRule.getName());
			getKineticsTypeComboBox().setSelectedItem(getRateLawType(reactionRule.getKineticLaw()));

			if(reactionRule.getKineticLaw().getRateLawType() == RateLawType.Saturable) {
				isReversibleCheckBox.setSelected(false);
				isReversibleCheckBox.setEnabled(false);
			} else if(reactionRule.getKineticLaw().getRateLawType() == RateLawType.MichaelisMenten) {
				isReversibleCheckBox.setSelected(false);
				isReversibleCheckBox.setEnabled(false);
			} else {	// MassAction
				isReversibleCheckBox.setSelected(reactionRule.isReversible());
				isReversibleCheckBox.setEnabled(true);
			}
		} else {
			annotationTextArea.setText(null);
			nameTextField.setEditable(false);
			nameTextField.setText(null);
			isReversibleCheckBox.setSelected(false);
			isReversibleCheckBox.setEnabled(true);
		}
		listLinkedPathwayObjects();
	}
	private String listLinkedPathwayObjects(){
		if (reactionRule == null) {
			return "no selected rule";
		}
		if(bioModel == null || bioModel.getModel() == null){
			return "no biomodel";
		}
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		String linkedPOlist = "";
		for(RelationshipObject relObject : bioModel.getRelationshipModel().getRelationshipObjects(reactionRule)){
			if(relObject == null) {
				continue;
			}
			final BioPaxObject bpObject = relObject.getBioPaxObject();
			if(bpObject == null) {
				continue;
			}
			if(bpObject instanceof Entity){
				String name = new String();
				if(((Entity)bpObject).getName().isEmpty()) {
					name = ((Entity)bpObject).getID();
				} else {
					name = ((Entity)bpObject).getName().get(0);
				}
				if(name.contains("#")) {
					name = name.substring(name.indexOf("#")+1);
				}
				JLabel label = new JLabel("<html><u>" + name + "</u></html>");
				label.setForeground(Color.blue);
				label.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() == 2) {
							selectionManager.followHyperlink(new ActiveView(null,DocumentEditorTreeFolderClass.PATHWAY_DIAGRAM_NODE, ActiveViewID.pathway_diagram),new Object[]{bpObject});
						}
					}
				});
				panel.add(label);
			}
		}
		Dimension dim = new Dimension(200, 20);
		panel.setMinimumSize(dim);
		panel.setPreferredSize(dim);
		linkedPOScrollPane.setViewportView(panel);
		return linkedPOlist;
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	private JComboBox<RateLawType> getKineticsTypeComboBox() {
		if (kineticsTypeComboBox == null) {
			kineticsTypeComboBox = new JComboBox();
			kineticsTypeComboBox.setName("JComboBox1");
			kineticsTypeComboBox.setRenderer(new DefaultListCellRenderer() {
				private final static String PI = "\u03A0";
				private final static String MU = "\u03BC";
				private final static String SQUARED = "\u00B2";
				private final static String PIs = "<strong>\u03A0</strong>";
				private final static String PIsb = "<strong><big>\u03A0</big></strong>";
				private final static String pi = "\u03C0";
				private final static String MULT = "\u22C5";
				private final static String MICROMOLAR = MU+"M";
				private final static String SQUAREMICRON = MU+"m"+SQUARED;

				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					java.awt.Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					if(value == RateLawType.Saturable) {
						String str = "<html>Saturable";
						str += "</html>";
						setText(str);
					} else if(value == RateLawType.MichaelisMenten) {
						String str = "<html>";
						if(reactionRule.getStructure() instanceof Feature) {
							str += "Henri-Michaelis-Menten (Irreversible) ["+MICROMOLAR+"/s]";
						} else if (reactionRule.getStructure() instanceof Membrane) {
							str += "Henri-Michaelis-Menten (Irreversible) [molecules/("+SQUAREMICRON+" s)]";
						}
						str += "</html>";
						setText(str);
					} else if(value == RateLawType.MassAction) {
						String str = "<html>Mass Action ( for each reaction:  ";
						str += "Kf" + MULT + PIs + " <small>reactants</small> - Kr" + MULT + PIs + " <small>products</small> )</html>";
						setText(str);
					}
					return component;
				}
			});
		}
		return kineticsTypeComboBox;
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
