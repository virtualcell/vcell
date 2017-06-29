package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.SpeciesContext;

public class RbmTreeCellEditor extends DefaultTreeCellEditor {

	protected Dimension componentPatternPreferredSizeWithWords = new Dimension(650, 50);
	protected Dimension componentPatternPreferredSizeWithoutWords = new Dimension(400, 50);

	public static class MolecularComponentPatternCellEditor extends AbstractCellEditor implements TreeCellEditor {
		public static final int other = 0;
		public static final int molecularTypes = 1;
		public static final int species = 2;
		public static final int observable = 3;
		public static final int reaction = 4;
		public int owner = other;

		private MolecularTypePattern molecularTypePattern;
		private MolecularComponent molecularComponent;
		private SpeciesPattern speciesPattern;
		
		private JLabel componentLabel = new JLabel();
		private JComboBox stateComboBox = new JComboBox();
		private JComboBox bondComboBox = new JComboBox();
		private JPanel panel = new JPanel();
		private JButton okButton; 
//		private Dimension preferredSize = new Dimension(480, 50);
		private Dimension preferredSize = new Dimension(400, 50);	// x, y  - the size of the edit combo boxes
		private JLabel stateLabel;
		
		public MolecularComponentPatternCellEditor() {
			initialize();
		}
		
		public Object getCellEditorValue() {			
			Object state = stateComboBox.getSelectedItem();
			Object bond = bondComboBox.getSelectedItem();
			
			MolecularComponentPattern mcp = new MolecularComponentPattern(molecularComponent);
			ComponentStatePattern csp = null;
			if(state instanceof String && state.equals(ComponentStatePattern.strAny)) {
				csp = new ComponentStatePattern();
				mcp.setComponentStatePattern(csp);
			} else if(state == null) {
				mcp.setComponentStatePattern(null);
			} else if(state instanceof ComponentStateDefinition) {
				csp = new ComponentStatePattern((ComponentStateDefinition)state);
				molecularComponent.addComponentStateDefinition((ComponentStateDefinition)state);
				mcp.setComponentStatePattern(csp);
			} else {
				throw new RuntimeException("Bad ComponentStateDefinition: " + state);
			}
			
			if (bond instanceof BondType) {
				mcp.setBondType((BondType)bond);
			} else if (bond instanceof Bond){
				Bond bondPartner = (Bond)bond;				
				mcp.setBond(bondPartner);
				
				MolecularComponentPattern existingCp = molecularTypePattern.getMolecularComponentPattern(molecularComponent);
				// that means the bond didn't change
				// because you can't change one specified bond
				// to another specified
				// you have to change to other type first
				// set the original partner there
				if (existingCp.getBondType() == BondType.Specified) {
				} else {
					int bondId = speciesPattern.nextBondId();
					mcp.setBondId(bondId);
				}
			}
			stateComboBox.removeAllItems();
			bondComboBox.removeAllItems();
			return mcp;
		}
		
		public Component getTreeCellEditorComponent(JTree tree,
				Object value, boolean isSelected, boolean expanded,
				boolean leaf, int row) {
			if (value instanceof BioModelNode) {
				Object userObject = ((BioModelNode) value).getUserObject();
				if (userObject instanceof MolecularComponent) {
					molecularComponent = (MolecularComponent) userObject;
					updateInterface();
				}
			}
			return panel;
		}
		
		private void initialize() {
			bondComboBox.setRenderer(new DefaultListCellRenderer() {
				
				public Component getListCellRendererComponent(JList list, Object value,
						int index, boolean isSelected, boolean cellHasFocus) {
					super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					if (value instanceof Bond) {
						Bond bp = (Bond) value;
						setText(RbmTreeCellRenderer.toHtml(bp));
					}
					return this;
				}
			});
			
			okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == okButton) {
						fireEditingStopped();
					}
				}
				
			});
			Border border = BorderFactory.createEmptyBorder(4, 4, 4, 4);
			okButton.setBorder(border);
						
			panel.setPreferredSize(preferredSize);
			panel.setBackground(Color.white);
			panel.setLayout(new GridBagLayout());
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.LINE_START;
			panel.add(componentLabel, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.insets = new Insets(0, 4, 0, 0);
			stateLabel = new JLabel("State(~): ");
			panel.add(stateLabel, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 0;
			gbc.weightx = 0.5;
			gbc.insets = new Insets(0, 4, 0, 0);
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			panel.add(stateComboBox, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 3;
			gbc.gridy = 0;
			gbc.insets = new Insets(0, 10, 0, 0);
			panel.add(new JLabel("Bond(?): "), gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 4;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.insets = new Insets(0, 4, 0, 0);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			panel.add(bondComboBox, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 5;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.insets = new Insets(0, 2, 0, 0);
			panel.add(okButton, gbc);
		}
		
		void updateInterface() {
			componentLabel.setText(toHtml(molecularComponent, owner));
			List<ComponentStateDefinition> componentStates = molecularComponent.getComponentStateDefinitions();
			boolean bShowStateComboBox = componentStates.size() > 0;
			stateLabel.setVisible(bShowStateComboBox);
			stateComboBox.setVisible(bShowStateComboBox);
			if (bShowStateComboBox) {
				stateComboBox.removeAllItems();
				if(owner == species) {
					if(componentStates.size() == 0) {
						stateComboBox.addItem(ComponentStatePattern.strAny);
					}
				} else {
					stateComboBox.addItem(ComponentStatePattern.strAny);
				}
				for (ComponentStateDefinition cs : componentStates) {
					stateComboBox.addItem(cs);
				}
			}
			bondComboBox.removeAllItems();
			bondComboBox.addItem(BondType.None);
			if(owner != species) {
				bondComboBox.addItem(BondType.Exists);
				bondComboBox.addItem(BondType.Possible);
			}
			List<Bond> bondPartnerChoices = speciesPattern.getAllBondPartnerChoices(molecularTypePattern, molecularComponent);
			
			MolecularComponentPattern mcp = molecularTypePattern.getMolecularComponentPattern(molecularComponent);
			if (mcp == null) {
				bondComboBox.addItem(BondType.Specified);
				bondComboBox.setSelectedItem(BondType.Possible);
				for (Bond choice : bondPartnerChoices) {
					bondComboBox.addItem(choice);
				}
			} else {
				bondComboBox.setSelectedItem(BondType.None);
				ComponentStatePattern csp = mcp.getComponentStatePattern();
				if ( csp!= null) {
					stateComboBox.setSelectedItem(csp);
				}
				if (mcp.getBondType() == BondType.Specified) {					
					Bond bondPartner = mcp.getBond();
					for (Bond choice : bondPartnerChoices) {
						if (bondPartner.equals(choice)) {
							bondComboBox.addItem(choice);
							bondComboBox.setSelectedItem(choice);
							break;
						}
					}
				} else {
					for (Bond bond : bondPartnerChoices) {
						bondComboBox.addItem(bond);
					}
					bondComboBox.setSelectedItem(mcp.getBondType());
				}
			}
		}
	}


static String toHtml(MolecularComponent mc, int owner) {
	String text = null;
	switch (owner) {
	case MolecularComponentPatternCellEditor.species:
	case MolecularComponentPatternCellEditor.observable:
		text = "<html> " + MolecularComponent.typeName + " <b>" + mc.getName() + "<sub>" + mc.getIndex() + "</sub></b></html>";
		break;
	case MolecularComponentPatternCellEditor.reaction:
		text = "<html> " + "" + " <b>" + mc.getDisplayName() + "<sub>" + mc.getIndex() + "</sub></b></html>";
		break;
	default:
		text = "<html> " + MolecularComponent.typeName + " <b>" + mc.getName() + "<sub>" + mc.getIndex() + "</sub></b></html>";
		break;
	}
	return text;
}
	
	private MolecularComponentPatternCellEditor mcpCellEditor = null;
	private DefaultCellEditor defaultCellEditor;
	
	public RbmTreeCellEditor(JTree tree) {
		super(tree, new DefaultTreeCellRenderer(), new DefaultCellEditor(new JTextField()));
		defaultCellEditor = (DefaultCellEditor) realEditor;
	}

	private MolecularComponentPatternCellEditor getMolecularComponentPatternCellEditor() {
		if (mcpCellEditor == null) {
			mcpCellEditor = new MolecularComponentPatternCellEditor();
			for (CellEditorListener listener : defaultCellEditor.getCellEditorListeners()) {
				mcpCellEditor.addCellEditorListener(listener);
			}
		}
		return mcpCellEditor;
	}
	
	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {	
		Component component = null;
		realEditor = defaultCellEditor;
		if (value instanceof BioModelNode) {
			BioModelNode node = (BioModelNode) value;
			Object userObject = node.getUserObject();
			String text = null;
			Icon icon = null;
			if (userObject instanceof MolecularType) {
				text = ((MolecularType) userObject).getName();
				icon = VCellIcons.rbmMolecularTypeIcon;
			} else 	if (userObject instanceof MolecularTypePattern) {
				text = ((MolecularTypePattern) userObject).getMolecularType().getName();
				icon = VCellIcons.rbmMolecularTypeIcon;
			} else if (userObject instanceof MolecularComponent) {
				BioModelNode parentNode = (BioModelNode) node.getParent();
				Object parentObject = parentNode == null ? null : parentNode.getUserObject();
				icon = VCellIcons.rbmComponentErrorIcon;		// TODO: look for the proper icon
				if (parentObject instanceof MolecularType) {
					text = ((MolecularComponent) userObject).getName();
				} else if (parentObject instanceof MolecularTypePattern) {					
					realEditor = getMolecularComponentPatternCellEditor();
					getMolecularComponentPatternCellEditor().molecularTypePattern = ((MolecularTypePattern) parentObject);
					// find SpeciesPattern
					while (true) {
						parentNode = (BioModelNode) parentNode.getParent();
						if (parentNode == null) {
							break;
						}
						if (parentNode.getUserObject() instanceof RbmObservable) {
							((MolecularComponentPatternCellEditor)realEditor).owner = MolecularComponentPatternCellEditor.observable;
							getMolecularComponentPatternCellEditor().speciesPattern = ((RbmObservable)parentNode.getUserObject()).getSpeciesPattern(0);
							break;
						}
						if (parentNode.getUserObject() instanceof ReactionRule) {
							((MolecularComponentPatternCellEditor)realEditor).owner = MolecularComponentPatternCellEditor.reaction;
							ReactionRulePropertiesTreeModel tm = (ReactionRulePropertiesTreeModel) tree.getModel();
							switch (tm.getParticipantType()) {
							case Reactant:
								getMolecularComponentPatternCellEditor().speciesPattern = ((ReactionRule)parentNode.getUserObject()).getReactantPattern(0).getSpeciesPattern();
								break;
							case Product:
								getMolecularComponentPatternCellEditor().speciesPattern = ((ReactionRule)parentNode.getUserObject()).getProductPattern(0).getSpeciesPattern();
								break;
							}
						}
						if(parentNode.getUserObject() instanceof SpeciesContext) {
							((MolecularComponentPatternCellEditor)realEditor).owner = MolecularComponentPatternCellEditor.species;
							getMolecularComponentPatternCellEditor().speciesPattern = ((SpeciesContext)parentNode.getUserObject()).getSpeciesPattern();
							break;
						}
					}
				}
			} else if (userObject instanceof ComponentStateDefinition) {
				text = ((ComponentStateDefinition) userObject).getName();
				icon = VCellIcons.rbmComponentStateIcon;
			} else if (userObject instanceof RbmObservable) {
				text = ((RbmObservable) userObject).getName();
				icon = VCellIcons.rbmObservableIcon;				
			}
			renderer.setOpenIcon(icon);
			renderer.setClosedIcon(icon);
			renderer.setLeafIcon(icon);
			component = super.getTreeCellEditorComponent(tree, value,
						isSelected, expanded, leaf, row);
			if (editingComponent instanceof JTextField) {
				JTextField textField = (JTextField) editingComponent;
				textField.setText(text);
			}
		}
		return component;
	}
}
