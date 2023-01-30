package org.vcell.model.springsalad.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import org.apache.commons.lang3.StringEscapeUtils;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.ScrollTable;
import org.vcell.util.gui.VCellIcons;
import org.vcell.util.gui.ScrollTable.ScrollTableBooleanCellRenderer;
import org.vcell.util.gui.sorttable.JSortTable;
import org.vcell.util.gui.sorttable.SortTableModel;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.desktop.biomodel.ApplicationSpecificationsPanel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.graph.SmallShapeManager;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.graph.SpeciesPatternSmallShape;
import cbit.vcell.graph.gui.LargeShapePanel;
import cbit.vcell.mapping.AssignmentRule;
import cbit.vcell.mapping.MolecularInternalLinkSpec;
import cbit.vcell.mapping.RateRule;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SiteAttributesSpec;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.TaskCallbackMessage;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;
import cbit.vcell.mapping.gui.MolecularTypeSpecsTableModel;
import cbit.vcell.mapping.gui.SpeciesContextSpecsTableModel;
import cbit.vcell.mapping.gui.StructureMappingTableModel;
import cbit.vcell.mapping.gui.SpeciesContextSpecsTableModel.ColumnType;
import cbit.vcell.mapping.gui.StructureMappingTableRenderer.TextIcon;
import cbit.vcell.model.Model;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.units.VCUnitDefinition;

// we should use WindowBuilder Plugin (add it to Eclipse IDE) to speed up panel design
// can choose absolute layout and place everything exactly as we see fit
@SuppressWarnings("serial")
public class MolecularStructuresPanel extends DocumentEditorSubPanel implements ApplicationSpecificationsPanel.Specifier {

	private EventHandler eventHandler = new EventHandler();
	private SimulationContext fieldSimulationContext;
	private SpeciesContextSpec fieldSpeciesContextSpec;
	private MolecularComponentPattern fieldMolecularComponentPattern;
	
	private IssueManager fieldIssueManager;
	private SelectionManager fieldSelectionManager;

	private EditorScrollTable speciesContextSpecsTable = null;
	private SpeciesContextSpecsTableModel speciesContextSpecsTableModel = null;
	private SmallShapeManager shapeManager = new SmallShapeManager(false, false, false, false);

	private EditorScrollTable molecularTypeSpecsTable = null;
	private MolecularTypeSpecsTableModel molecularTypeSpecsTableModel = null;
	
	private JComboBox<String> siteColorComboBox = null;
	private JComboBox<String> siteLocationComboBox = null;
	private JTextField siteXField = null;
	private JTextField siteYField = null;
	private JTextField siteZField = null;
	
	private JTextField linkLengthField = null;
	private JButton addLinkButton = null;

	private JList<MolecularInternalLinkSpec> siteLinksList = null;
	private DefaultListModel<MolecularInternalLinkSpec> siteLinksListModel = new DefaultListModel<>();
	private ListCellRenderer<Object> siteLinksCellRenderer = new DefaultListCellRenderer(){
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof MolecularInternalLinkSpec && component instanceof JLabel){
				MolecularInternalLinkSpec sc = (MolecularInternalLinkSpec)value;
				((JLabel)component).setText(" " + "ala bala");
			}
			return component;
		}
	};

	
	private class EventHandler implements FocusListener, ActionListener, PropertyChangeListener, ListSelectionListener {

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == siteXField || source == siteYField || source == siteZField) {
				changePosition();
			} else if(source == linkLengthField) {
				changeLinkLength();
			} else if(source == addLinkButton) {
				addLinkActionPerformed();
				refreshSiteLinksList();
			}
		}
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
			Object source = e.getSource();
			if (source == siteXField || source == siteYField || source == siteZField) {
				changePosition();
			} else if(source == linkLengthField) {
				changeLinkLength();
			}
		}
		public void propertyChange(java.beans.PropertyChangeEvent e) {
			if(e.getSource() instanceof Model && e.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_MOLECULAR_TYPE_LIST)) {
				updateInterface();
			}
			// TODO: I think this is not needed
//			if (e.getSource() == selectionManager) {
//				if (e.getPropertyName().equals(SelectionManager.PROPERTY_NAME_SELECTED_OBJECTS)) {
//					Object[] objects = selectionManager.getSelectedObjects();
//					onSelectedObjectsChange(objects);
//				} else if (e.getPropertyName().equals(SelectionManager.PROPERTY_NAME_ACTIVE_VIEW)) {
//					onActiveViewChange(selectionManager.getActiveView());
//				}
//			}
		}
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == getSpeciesContextSpecsTable().getSelectionModel()) {
				setSelectedObjectsFromTable(getSpeciesContextSpecsTable(), speciesContextSpecsTableModel);
				int row = getSpeciesContextSpecsTable().getSelectedRow();
				SpeciesContextSpec scsSelected = speciesContextSpecsTableModel.getValueAt(row);
				setSpeciesContextSpec(scsSelected);
			}
			if (e.getSource() == getMolecularTypeSpecsTable().getSelectionModel()) {
				int row = getMolecularTypeSpecsTable().getSelectedRow();
				MolecularComponentPattern mcmSelected = molecularTypeSpecsTableModel.getValueAt(row);
				setMolecularComponentPattern(mcmSelected);
			}
			if(e.getSource() == siteLinksList) {
				showLinkLength(siteLinksList.getSelectedValue());
			}
		}
		
	}

	public MolecularStructuresPanel() {
		super();
		initialize();
	}
	
	@Override
	public ActiveViewID getActiveView() {
		return ActiveViewID.molecular_structure_setting;
	}
	/**
	 * no-op 
	 */
	@Override
	public void setSearchText(String s) {
		
	}
	
	private void initConnections() throws java.lang.Exception {		// listeners here!
		siteXField.addFocusListener(eventHandler);
		siteYField.addFocusListener(eventHandler);
		siteZField.addFocusListener(eventHandler);
		linkLengthField.addFocusListener(eventHandler);
		siteXField.addActionListener(eventHandler);
		siteYField.addActionListener(eventHandler);
		siteZField.addActionListener(eventHandler);
		
		siteLinksList.addListSelectionListener(eventHandler);
		linkLengthField.addActionListener(eventHandler);
		addLinkButton.addActionListener(eventHandler);
		
		ListSelectionModel lsm = getSpeciesContextSpecsTable().getSelectionModel();
		if(lsm instanceof DefaultListSelectionModel) {
			DefaultListSelectionModel dlsm = (DefaultListSelectionModel)lsm;
			dlsm.addListSelectionListener(eventHandler);
		}
		
		ListSelectionModel lsm2 = getMolecularTypeSpecsTable().getSelectionModel();
		if(lsm2 instanceof DefaultListSelectionModel) {
			DefaultListSelectionModel dlsm = (DefaultListSelectionModel)lsm2;
			dlsm.addListSelectionListener(eventHandler);
		}


	}
	
	private void initialize() {
		try {
		// labels / button / combos / lists initialization
			siteXField = new JTextField("");
			siteYField = new JTextField("");
			siteZField = new JTextField("");
			siteColorComboBox = new JComboBox<>();	// TODO: arg here should be combobox model
			siteLinksList = new JList<MolecularInternalLinkSpec>(siteLinksListModel);
			siteLinksList.setCellRenderer(siteLinksCellRenderer);
			linkLengthField = new JTextField("");
			addLinkButton = new JButton("Add Link");
			siteXField.setEditable(false);
			siteYField.setEditable(false);
			siteZField.setEditable(false);
			linkLengthField.setEditable(false);

		
		// ------------------------------------------- The 2 group boxes --------------------------
		JPanel thePanel = new JPanel();

		Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();

		TitledBorder titleTop = BorderFactory.createTitledBorder(loweredEtchedBorder, " Species ");
		titleTop.setTitleJustification(TitledBorder.LEFT);
		titleTop.setTitlePosition(TitledBorder.TOP);

		TitledBorder titleSites = BorderFactory.createTitledBorder(loweredEtchedBorder, " Sites ");
		titleSites.setTitleJustification(TitledBorder.LEFT);
		titleSites.setTitlePosition(TitledBorder.TOP);

		TitledBorder titleLinks = BorderFactory.createTitledBorder(loweredEtchedBorder, " Links ");
		titleLinks.setTitleJustification(TitledBorder.LEFT);
		titleLinks.setTitlePosition(TitledBorder.TOP);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(1, 1, 1, 1);
		add(thePanel, gbc);

		// ------------------------------------------- Populating the top group box ---------------
		JPanel top = new JPanel();
		JPanel bottom = new JPanel();
		JPanel sitesPanel = new JPanel();
		JPanel linksPanel = new JPanel();
		
		top.setBorder(titleTop);
		sitesPanel.setBorder(titleSites);
		linksPanel.setBorder(titleLinks);
		
		thePanel.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 0.7;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(3, 0, 0, 0);	//  top, left, bottom, right 
		thePanel.add(top, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridheight = 2;
		gbc.insets = new Insets(1, 0, 0, 0);
		gbc.fill = GridBagConstraints.BOTH;
		thePanel.add(bottom, gbc);

		// we may want to use a scroll pane whose viewing area is the JTable to provide similar look with NetGen Console
		JScrollPane pt = new JScrollPane(getSpeciesContextSpecsTable());
		pt.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		top.setLayout(new GridBagLayout());		// --- table of top panel
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(2, 3, 3, 4);
		top.add(pt, gbc);

		DefaultTableCellRenderer renderer = new DefaultScrollTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				setIcon(null);
				defaultToolTipText = null;

				if (value instanceof Species) {
					setText(((Species)value).getCommonName());
					defaultToolTipText = getText();
					setToolTipText(defaultToolTipText);
				} else if (value instanceof SpeciesContext) {
					setText(((SpeciesContext)value).getName());
					defaultToolTipText = getText();
					setToolTipText(defaultToolTipText);
				} else if (value instanceof Structure) {
					setText(((Structure)value).getName());
					defaultToolTipText = getText();
					setToolTipText(defaultToolTipText);
				} else if (value instanceof ScopedExpression) {
					SpeciesContextSpec scSpec = speciesContextSpecsTableModel.getValueAt(row);
					VCUnitDefinition unit = null;
					if (table.getColumnName(column).equals(SpeciesContextSpecsTableModel.ColumnType.COLUMN_INITIAL.label)) {
						SpeciesContextSpecParameter initialConditionParameter = scSpec.getInitialConditionParameter();
						unit = initialConditionParameter.getUnitDefinition();
					} else if (table.getColumnName(column).equals(SpeciesContextSpecsTableModel.ColumnType.COLUMN_DIFFUSION.label)) {
						SpeciesContextSpecParameter diffusionParameter = scSpec.getDiffusionParameter();
						unit = diffusionParameter.getUnitDefinition();
					}
					if (unit != null) {
						setHorizontalTextPosition(JLabel.LEFT);
						setIcon(new TextIcon("[" + unit.getSymbolUnicode() + "]", DefaultScrollTableCellRenderer.uneditableForeground));
					}
					int rgb = 0x00ffffff & DefaultScrollTableCellRenderer.uneditableForeground.getRGB();
					defaultToolTipText = "<html>" + StringEscapeUtils.escapeHtml4(getText()) + " <font color=#" + Integer.toHexString(rgb) + "> [" + unit.getSymbolUnicode() + "] </font></html>";
					setToolTipText(defaultToolTipText);
					if(unit != null) {
						setText(defaultToolTipText);
					}
				}
				
				TableModel tableModel = table.getModel();
				if (tableModel instanceof SortTableModel) {
					DefaultScrollTableCellRenderer.issueRenderer(this, defaultToolTipText, table, row, column, (SortTableModel)tableModel);
					setHorizontalTextPosition(JLabel.TRAILING);
				}
				return this;
			}
		};
		DefaultTableCellRenderer rbmSpeciesShapeDepictionCellRenderer = new DefaultScrollTableCellRenderer() {
			SpeciesPatternSmallShape spss = null;
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, 
					boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (table.getModel() instanceof VCellSortTableModel<?>) {
					Object selectedObject = null;
					if (table.getModel() == speciesContextSpecsTableModel) {
						selectedObject = speciesContextSpecsTableModel.getValueAt(row);
					}
					if (selectedObject != null) {
						if(selectedObject instanceof SpeciesContextSpec) {
							SpeciesContextSpec scs = (SpeciesContextSpec)selectedObject;
							SpeciesContext sc = scs.getSpeciesContext();
							SpeciesPattern sp = sc.getSpeciesPattern();		// sp may be null for "plain" species contexts
							Graphics panelContext = table.getGraphics();
							spss = new SpeciesPatternSmallShape(4, 2, sp, shapeManager, panelContext, sc, isSelected, issueManager);
						}
					} else {
						spss = null;
					}
				}
				setText("");
				return this;
			}
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				if(spss != null) {
					spss.paintSelf(g);
				}
			}
		};
		
		DefaultScrollTableCellRenderer rulesTableCellRenderer = new DefaultScrollTableCellRenderer() {
			final Color lightBlueBackground = new Color(214, 234, 248);
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				
				if (table.getModel() instanceof SpeciesContextSpecsTableModel) {
					Icon icon = VCellIcons.issueGoodIcon;
					Object selectedObject = null;
					if (table.getModel() == speciesContextSpecsTableModel) {
						selectedObject = speciesContextSpecsTableModel.getValueAt(row);
					}
					if (selectedObject != null) {
						if(isSelected) {
							setBackground(lightBlueBackground);
						}
						if(selectedObject instanceof SpeciesContextSpec) {
							SpeciesContextSpec scs = (SpeciesContextSpec)selectedObject;
							SpeciesContext sc = scs.getSpeciesContext();

							boolean foundRuleMatch = false;
							if(fieldSimulationContext.getRateRules() != null && fieldSimulationContext.getRateRules().length > 0) {
								for(RateRule rr : fieldSimulationContext.getRateRules()) {
									if(rr.getRateRuleVar() == null) {
										continue;
									}
									if(sc.getName().equals(rr.getRateRuleVar().getName())) {
										foundRuleMatch = true;
										icon = VCellIcons.ruleRateIcon;
										break;
									}
								}
							}
							if(!foundRuleMatch && fieldSimulationContext.getAssignmentRules() != null && fieldSimulationContext.getAssignmentRules().length > 0) {
								for(AssignmentRule rr : fieldSimulationContext.getAssignmentRules()) {
									if(rr.getAssignmentRuleVar() == null) {
										continue;
									}
									if(sc.getName().equals(rr.getAssignmentRuleVar().getName())) {
										icon = VCellIcons.ruleAssignIcon;
										break;
									}
								}
							}
						}
					}
					setIcon(icon);
				}
				return this;
			}
		};
		
		DefaultScrollTableCellRenderer structuresTableCellRenderer = new DefaultScrollTableCellRenderer() {
			final Color lightBlueBackground = new Color(214, 234, 248);
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				
				if (table.getModel() instanceof MolecularTypeSpecsTableModel) {
					MolecularTypeSpecsTableModel molecularTypeSpecsTableModel = (MolecularTypeSpecsTableModel)table.getModel();
					if (value instanceof Structure) {
						Structure structure = (Structure)value;
						setText(structure.getName());
					} else {
						setText(value.toString());
					}
				}
				return this;
			}
		};

		getSpeciesContextSpecsTable().setDefaultRenderer(SpeciesContext.class, renderer);
		getSpeciesContextSpecsTable().setDefaultRenderer(Structure.class, renderer);
		getSpeciesContextSpecsTable().setDefaultRenderer(SpeciesPattern.class, rbmSpeciesShapeDepictionCellRenderer);	// depiction icons
		getSpeciesContextSpecsTable().setDefaultRenderer(Species.class, renderer);
		getSpeciesContextSpecsTable().setDefaultRenderer(ScopedExpression.class, renderer);
		getSpeciesContextSpecsTable().setDefaultRenderer(Boolean.class, new ScrollTableBooleanCellRenderer());
		getSpeciesContextSpecsTable().setDefaultRenderer(SpeciesContextSpecsTableModel.RulesProvenance.class, rulesTableCellRenderer);	// rules icons

		// ---------------------------------------------------------------------------------------------
		
		bottom.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(3, 2, 2, 3);	//  top, left, bottom, right 
		bottom.add(sitesPanel, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(3, 2, 2, 3);
		bottom.add(linksPanel, gbc);
		
		JScrollPane pb = new JScrollPane(getMolecularTypeSpecsTable());
		pb.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sitesPanel.setLayout(new GridBagLayout());		// --- table of bottom panel
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 8;
		gbc.gridheight = 5;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(2, 3, 3, 4);
		sitesPanel.add(pb, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.insets = new Insets(2, 2, 2, 2);
		sitesPanel.add(new JLabel(" X: "), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.insets = new Insets(2, 2, 2, 2);
		sitesPanel.add(siteXField, gbc);		// 

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 5;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.insets = new Insets(2, 2, 2, 2);
		sitesPanel.add(new JLabel(" Y: "), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 5;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.insets = new Insets(2, 2, 2, 2);
		sitesPanel.add(siteYField, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 5;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.insets = new Insets(2, 2, 2, 2);
		sitesPanel.add(new JLabel(" Z: "), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 5;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.insets = new Insets(2, 2, 2, 2);
		sitesPanel.add(siteZField, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 6;
		gbc.gridy = 5;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.insets = new Insets(2, 2, 2, 2);
		sitesPanel.add(new JLabel(" Color "), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 7;
		gbc.gridy = 5;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.insets = new Insets(2, 2, 2, 2);
		sitesPanel.add(siteColorComboBox, gbc);

//		// --- links -----------------------------------------------
//		private JTextField siteXField = null;
//		private JTextField siteYField = null;
//		private JTextField siteZField = null;
//		
//		private JList<String> siteLinksList = null;
//		private JTextField linkLengthField = null;

		
		linksPanel.setLayout(new GridBagLayout());
//		siteLinksList.setBorder(loweredEtchedBorder);
		
		JScrollPane scrollPane1 = new JScrollPane(siteLinksList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);
		linksPanel.add(scrollPane1, gbc);
//		linksPanel.add(siteLinksList, gbc);

		gbc = new GridBagConstraints();		// ----------------------
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(5, 2, 2, 3);
		linksPanel.add(new JLabel("Length (nm): "), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);	//  top, left, bottom, right 
		linksPanel.add(linkLengthField, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);
		linksPanel.add(addLinkButton, gbc);
		
		getMolecularTypeSpecsTable().setDefaultRenderer(String.class, new DefaultScrollTableCellRenderer());
		getMolecularTypeSpecsTable().setDefaultRenderer(Structure.class, structuresTableCellRenderer);
		
		
		
		
		initConnections();		// adding listeners
		
		} catch(Throwable e) {
			handleException(e);
		}
	}
	
	private EditorScrollTable getMolecularTypeSpecsTable() {
		if (molecularTypeSpecsTable == null) {
			try {
				molecularTypeSpecsTable = new EditorScrollTable();
				molecularTypeSpecsTable.setName("molecularComponentSpecsTable");
				molecularTypeSpecsTableModel = new MolecularTypeSpecsTableModel(molecularTypeSpecsTable);
				molecularTypeSpecsTable.setModel(molecularTypeSpecsTableModel);
//				molecularComponentSpecsTable.setScrollTableActionManager(new InternalScrollTableActionManager(table));
				molecularTypeSpecsTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return molecularTypeSpecsTable;
	}
	private EditorScrollTable getSpeciesContextSpecsTable() {
		if (speciesContextSpecsTable == null) {
			try {
				speciesContextSpecsTable = new EditorScrollTable();
				speciesContextSpecsTable.setName("spceciesContextSpecsTable");
				speciesContextSpecsTableModel = new SpeciesContextSpecsTableModel(speciesContextSpecsTable);
				speciesContextSpecsTableModel.setEditable(false);
				speciesContextSpecsTable.setModel(speciesContextSpecsTableModel);
//				scsTable.setScrollTableActionManager(new InternalScrollTableActionManager(table));
				speciesContextSpecsTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return speciesContextSpecsTable;
	}

	private void handleException(Throwable exception) {
		System.out.println("--------- UNCAUGHT EXCEPTION --------- in cbit.vcell.mapping.InitialConditionPanel");
		exception.printStackTrace(System.out);
	}
	
	public void setSimulationContext(SimulationContext simulationContext) {
		if(simulationContext == null) {
			return;
		}
		if(fieldSimulationContext != null) {
			fieldSimulationContext.removePropertyChangeListener(eventHandler);
		}
		fieldSimulationContext = simulationContext;
		fieldSimulationContext.addPropertyChangeListener(eventHandler);
		
		Model m = fieldSimulationContext.getModel();
		if(m != null) {
			m.removePropertyChangeListener(eventHandler);
			m.addPropertyChangeListener(eventHandler);
		}
		speciesContextSpecsTableModel.setSimulationContext(simulationContext);
		molecularTypeSpecsTableModel.setSimulationContext(simulationContext);
		updateInterface();
	}
	public SimulationContext getSimulationContext() {
		return fieldSimulationContext;
	}
	void setSpeciesContextSpec(SpeciesContextSpec newValue) {
		if (fieldSpeciesContextSpec == newValue) {
			return;
		}
		SpeciesContextSpec oldValue = fieldSpeciesContextSpec;
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(eventHandler);
		}
		// commit the changes before switch to another species
		changeSpeciesContextSpec();
		
		fieldSpeciesContextSpec = newValue;
		if (newValue != null) {
			newValue.addPropertyChangeListener(eventHandler);
		}
		molecularTypeSpecsTableModel.setSpeciesContextSpec(fieldSpeciesContextSpec);
		updateInterface();
	}
	void setMolecularComponentPattern(MolecularComponentPattern mcp) {
		fieldMolecularComponentPattern = mcp;
		//TODO: stuff
		updateInterface();
	}

	// ============================================================================================
	
	public void setSelectionManager(SelectionManager selectionManager) {
		fieldSelectionManager = selectionManager;
	}
	public IssueManager getIssueManager() {
		return fieldIssueManager;
	}
	public void setIssueManager(IssueManager issueManager) {
		fieldIssueManager = issueManager;
	}
	

	private void updateInterface() {
		boolean bNonNullSpeciesContextSpec = fieldSpeciesContextSpec != null && fieldSimulationContext != null;
		boolean bNonNullSpeciesPattern = bNonNullSpeciesContextSpec && fieldSpeciesContextSpec.getSpeciesContext().getSpeciesPattern() != null;
		boolean bNonNullMolecularTypePattern = bNonNullSpeciesPattern && fieldSpeciesContextSpec.getSpeciesContext().getSpeciesPattern().getMolecularTypePatterns().get(0) != null;
		boolean bNonNullMolecularComponentPattern = bNonNullMolecularTypePattern && fieldMolecularComponentPattern != null;
		
		MolecularTypePattern mtp = null;
		if(bNonNullMolecularTypePattern) {
			mtp = fieldSpeciesContextSpec.getSpeciesContext().getSpeciesPattern().getMolecularTypePatterns().get(0);
		}
		
		if(bNonNullMolecularTypePattern && mtp.getComponentPatternList().size() > 1) {		// a link requires 2 sites (components)
			linkLengthField.setEditable(true);
			addLinkButton.setEnabled(true);
			refreshSiteLinksList();
		} else {
			linkLengthField.setEditable(false);
			linkLengthField.setText(null);
			addLinkButton.setEnabled(false);
		}
		if(bNonNullMolecularComponentPattern) {
			SiteAttributesSpec sas = fieldSpeciesContextSpec.getSiteAttributesMap().get(fieldMolecularComponentPattern);
			siteXField.setEditable(true);
			siteYField.setEditable(true);
			siteZField.setEditable(true);
			siteXField.setText(sas.getCoordinate().getX()+"");
			siteYField.setText(sas.getCoordinate().getY()+"");
			siteZField.setText(sas.getCoordinate().getZ()+"");
		} else {
			siteXField.setEditable(false);
			siteYField.setEditable(false);
			siteZField.setEditable(false);
			siteXField.setText(null);
			siteYField.setText(null);
			siteZField.setText(null);
			
		}
	}
	
	private void refreshSiteLinksList() {
		siteLinksListModel.removeAllElements();
		for (MolecularInternalLinkSpec mils : fieldSpeciesContextSpec.getInternalLinkSet()){
			siteLinksListModel.addElement(mils);
		}
	}

	
	private void appendToConsole(String string) {
		TaskCallbackMessage newCallbackMessage = new TaskCallbackMessage(TaskCallbackStatus.Error, string);
		fieldSimulationContext.appendToConsole(newCallbackMessage);
	}
	private void appendToConsole(TaskCallbackMessage newCallbackMessage) {
		fieldSimulationContext.appendToConsole(newCallbackMessage);
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		setTableSelections(selectedObjects, speciesContextSpecsTable, speciesContextSpecsTableModel);
	}
	
//	private void initialize() {
//
//		JPanel thePanel = new JPanel();
//
//		Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
//		Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();
//
//		TitledBorder titleLeft = BorderFactory.createTitledBorder(loweredEtchedBorder, " Molecule Types ");
//		titleLeft.setTitleJustification(TitledBorder.LEFT);
//		titleLeft.setTitlePosition(TitledBorder.TOP);
//
//		TitledBorder titleCenter = BorderFactory.createTitledBorder(loweredEtchedBorder, " Sites ");
//		titleCenter.setTitleJustification(TitledBorder.LEFT);
//		titleCenter.setTitlePosition(TitledBorder.TOP);
//		
//		TitledBorder titleRight = BorderFactory.createTitledBorder(loweredEtchedBorder, " Links ");
//		titleRight.setTitleJustification(TitledBorder.LEFT);
//		titleRight.setTitlePosition(TitledBorder.TOP);
//
//		setLayout(new GridBagLayout());
//		GridBagConstraints gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.weightx = 1;
//		gbc.weighty = 1;
//		gbc.fill = GridBagConstraints.BOTH;
//		gbc.insets = new Insets(1, 1, 1, 1);
//		add(thePanel, gbc);
//		
//		// ------------------------------------------- the 3 main panels ---------------
//		JPanel left = new JPanel();
//		JPanel center = new JPanel();
//		JPanel right = new JPanel();
//		
//		left.setBorder(titleLeft);
//		center.setBorder(titleCenter);
//		right.setBorder(titleRight);
//		
//		thePanel.setLayout(new GridBagLayout());
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.weighty = 1.0;
//		gbc.fill = GridBagConstraints.BOTH;
//		gbc.insets = new Insets(5, 2, 2, 3);	//  top, left, bottom, right 
//		thePanel.add(left, gbc);
//		
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.weighty = 1.0;
//		gbc.fill = GridBagConstraints.BOTH;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		thePanel.add(center, gbc);
//
//		gbc = new GridBagConstraints();
//		gbc.gridx = 2;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.weighty = 1.0;
//		gbc.fill = GridBagConstraints.BOTH;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		thePanel.add(right, gbc);
//		
//		// --- left -------------------------------------------------
//		JPanel leftSubpanel1 = new JPanel();
//		JList<String> molecules = new JList<String> ();
//		molecules.setBorder(loweredEtchedBorder);
//		JPanel leftSubpanel2 = new JPanel();
//		JTextField radius = new JTextField();
//		JTextField diameter = new JTextField();
//		JTextField color = new JTextField();
//
//		leftSubpanel1.setLayout(new GridBagLayout());
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.weighty = 1.0;
//		gbc.fill = GridBagConstraints.BOTH;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		leftSubpanel1.add(molecules, gbc);
//
//		leftSubpanel2.setLayout(new GridBagLayout());
//		gbc = new GridBagConstraints();		// ----------------------
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.anchor = GridBagConstraints.EAST;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		leftSubpanel2.add(new JLabel("Radius (nm): "), gbc);
//
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(5, 2, 2, 3);	//  top, left, bottom, right 
//		leftSubpanel2.add(radius, gbc);
//		
//		gbc = new GridBagConstraints();		// ----------------------
//		gbc.gridx = 0;
//		gbc.gridy = 1;
//		gbc.anchor = GridBagConstraints.EAST;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		leftSubpanel2.add(new JLabel("D (um^2/s): "), gbc);
//
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = 1;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		leftSubpanel2.add(diameter, gbc);
//
//		gbc = new GridBagConstraints();		// ----------------------
//		gbc.gridx = 0;
//		gbc.gridy = 2;
//		gbc.anchor = GridBagConstraints.EAST;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		leftSubpanel2.add(new JLabel("Color: "), gbc);
//
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = 2;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		leftSubpanel2.add(color, gbc);
//		
//		gbc = new GridBagConstraints();		// ----------------------
//		gbc.gridx = 0;
//		gbc.gridy = 3;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		leftSubpanel2.add(new JLabel("States"), gbc);
//
//		JList<String> states = new JList<String> ();
//		states.setBorder(loweredEtchedBorder);
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 4;
//		gbc.weightx = 1.0;
//		gbc.weighty = 1.0;
//		gbc.gridwidth = 2;
//		gbc.fill = GridBagConstraints.BOTH;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		leftSubpanel2.add(states, gbc);
//
//		left.setLayout(new GridBagLayout());
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.weighty = 1.0;
//		gbc.fill = GridBagConstraints.BOTH;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		left.add(leftSubpanel1, gbc);
//		
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.weighty = 1.0;
//		gbc.fill = GridBagConstraints.BOTH;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		left.add(leftSubpanel2, gbc);
//
//		// --- center --------------------------------------------------
//		JPanel centerSubpanel1 = new JPanel();
//		JList<String> sites = new JList<String> ();
//		sites.setBorder(loweredEtchedBorder);
//		JPanel centerSubpanel2 = new JPanel();
//		JTextField radiusCenter = new JTextField();
//		JTextField diameterCenter = new JTextField();
//		JTextField locationCenter = new JTextField();
//
//		centerSubpanel1.setLayout(new GridBagLayout());
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.weighty = 1.0;
//		gbc.fill = GridBagConstraints.BOTH;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		centerSubpanel1.add(sites, gbc);
//
//		centerSubpanel2.setLayout(new GridBagLayout());
//		gbc = new GridBagConstraints();		// ----------------------
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.anchor = GridBagConstraints.EAST;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		centerSubpanel2.add(new JLabel("Radius (nm): "), gbc);
//
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(5, 2, 2, 3);	//  top, left, bottom, right 
//		centerSubpanel2.add(radiusCenter, gbc);
//		
//		gbc = new GridBagConstraints();		// ----------------------
//		gbc.gridx = 0;
//		gbc.gridy = 1;
//		gbc.anchor = GridBagConstraints.EAST;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		centerSubpanel2.add(new JLabel("D (um^2/s): "), gbc);
//
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = 1;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		centerSubpanel2.add(diameterCenter, gbc);
//
//		gbc = new GridBagConstraints();		// ----------------------
//		gbc.gridx = 0;
//		gbc.gridy = 2;
//		gbc.anchor = GridBagConstraints.EAST;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		centerSubpanel2.add(new JLabel("Location: "), gbc);
//
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = 2;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		centerSubpanel2.add(locationCenter, gbc);
//		
//		gbc = new GridBagConstraints();		// ----------------------
//		gbc.gridx = 0;
//		gbc.gridy = 3;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		centerSubpanel2.add(new JLabel("Position (nm) "), gbc);
//		
//		JPanel centerSubpanel3 = new JPanel();	// ===================
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 4;
//		gbc.gridwidth = 2;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(2, 2, 2, 3);
//		centerSubpanel2.add(centerSubpanel3, gbc);
//		
//		gbc = new GridBagConstraints();		// ----------------------
//		gbc.gridx = 0;
//		gbc.gridy = 5;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		centerSubpanel2.add(new JButton("Set Position"), gbc);
//
//		gbc = new GridBagConstraints();		// ghost
//		gbc.gridx = 0;
//		gbc.gridy = 6;
//		gbc.weighty = 1.0;
//		gbc.fill = GridBagConstraints.VERTICAL;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		centerSubpanel2.add(new JLabel(" "), gbc);
//
//
//		centerSubpanel3.setLayout(new GridBagLayout());
//		gbc = new GridBagConstraints();		// ----------------------
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.insets = new Insets(0, 2, 2, 0);		//  top, left, bottom, right 
//		centerSubpanel3.add(new JLabel("X: "), gbc);
//
//		JTextField x = new JTextField();
//		x.setEditable(false);
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(0, 1, 2, 3);
//		centerSubpanel3.add(x, gbc);
//
//		gbc = new GridBagConstraints();		// ----------------------
//		gbc.gridx = 2;
//		gbc.gridy = 0;
//		gbc.insets = new Insets(0, 7, 2, 0);
//		centerSubpanel3.add(new JLabel("Y: "), gbc);
//
//		JTextField y = new JTextField();
//		y.setEditable(false);
//		gbc = new GridBagConstraints();
//		gbc.gridx = 3;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(0, 1, 2, 3);
//		centerSubpanel3.add(y, gbc);
//
//		gbc = new GridBagConstraints();		// ----------------------
//		gbc.gridx = 4;
//		gbc.gridy = 0;
//		gbc.insets = new Insets(0, 7, 2, 0);
//		centerSubpanel3.add(new JLabel("Z: "), gbc);
//
//		JTextField z = new JTextField();
//		z.setEditable(false);
//		gbc = new GridBagConstraints();
//		gbc.gridx = 5;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(0, 1, 2, 0);
//		centerSubpanel3.add(z, gbc);
//
//		center.setLayout(new GridBagLayout());
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.weighty = 1.0;
//		gbc.fill = GridBagConstraints.BOTH;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		center.add(centerSubpanel1, gbc);
//		
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.weighty = 1.0;
//		gbc.fill = GridBagConstraints.BOTH;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		center.add(centerSubpanel2, gbc);
//		
//		// --- right -----------------------------------------------
//		JList<String> links = new JList<String> ();
//		links.setBorder(loweredEtchedBorder);
//		JTextField lengthRight = new JTextField();
//		lengthRight.setEditable(false);
//		JButton addLink = new JButton("Add Link");
//		JButton removeLink = new JButton("Remove Link");
//		JButton setLinkLength = new JButton("Set Link Length");
//		
//		right.setLayout(new GridBagLayout());
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.gridwidth = 2;
//		gbc.weightx = 1.0;
//		gbc.weighty = 1.0;
//		gbc.fill = GridBagConstraints.BOTH;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		right.add(links, gbc);
//
//		gbc = new GridBagConstraints();		// ----------------------
//		gbc.gridx = 0;
//		gbc.gridy = 1;
//		gbc.anchor = GridBagConstraints.EAST;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		right.add(new JLabel("Length (nm): "), gbc);
//
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = 1;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(5, 2, 2, 3);	//  top, left, bottom, right 
//		right.add(lengthRight, gbc);
//
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 2;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		right.add(addLink, gbc);
//
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 3;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		right.add(removeLink, gbc);
//
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 4;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(5, 2, 2, 3);
//		right.add(setLinkLength, gbc);
//	}

	/*
	 * commit changes to current (old) SpeciesContextStep 
	 * before the newly selected SpeciesContextStep becomes current
	 */
	private void changeSpeciesContextSpec() {
		
	}

	private void changePosition() {
		System.out.println("Site coordinates changed");
		recalculateLinkLengths();
	}
	private void changeLinkLength() {
		System.out.println("Link length changed");
		recalculatePositions();
	}
	
	private void recalculateLinkLengths() {
	}
	private void recalculatePositions() {
	}

	private void addLinkActionPerformed() {
		// TODO make a dialog and let the user select the 2 components of the link
		// for now just make a link
		SpeciesPattern sp = fieldSpeciesContextSpec.getSpeciesContext().getSpeciesPattern();
		MolecularTypePattern mtp = sp.getMolecularTypePatterns().get(0);
		MolecularInternalLinkSpec mils = new MolecularInternalLinkSpec(fieldSpeciesContextSpec, mtp.getComponentPatternList().get(0), mtp.getComponentPatternList().get(1));
		fieldSpeciesContextSpec.getInternalLinkSet().add(mils);
	}
	
	private void showLinkLength(MolecularInternalLinkSpec selectedValue) {
		linkLengthField.setText(selectedValue.getLinkLength()+"");
		
	};

}

