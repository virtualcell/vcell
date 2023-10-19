package org.vcell.model.springsalad.gui;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.desktop.biomodel.ApplicationSpecificationsPanel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.graph.SmallShapeManager;
import cbit.vcell.graph.SpeciesPatternSmallShape;
import cbit.vcell.mapping.*;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.gui.MolecularTypeSpecsTableModel;
import cbit.vcell.mapping.gui.SpeciesContextSpecsTableModel;
import cbit.vcell.mapping.gui.StructureMappingTableRenderer.TextIcon;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.units.VCUnitDefinition;
import org.apache.commons.lang3.StringEscapeUtils;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Coordinate;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.ScrollTable.ScrollTableBooleanCellRenderer;
import org.vcell.util.gui.VCellIcons;
import org.vcell.util.gui.sorttable.SortTableModel;
import org.vcell.util.springsalad.Colors;
import org.vcell.util.springsalad.NamedColor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;

// we should use WindowBuilder Plugin (add it to Eclipse IDE) to speed up panel design
// can choose absolute layout and place everything exactly as we see fit
@SuppressWarnings("serial")
public class MolecularStructuresPanel extends DocumentEditorSubPanel implements ApplicationSpecificationsPanel.Specifier {

	private EventHandler eventHandler = new EventHandler();
	private SimulationContext fieldSimulationContext;
	private SpeciesContextSpec fieldSpeciesContextSpec;
	private MolecularComponentPattern fieldMolecularComponentPattern;
	
	private EditorScrollTable speciesContextSpecsTable = null;
	private SpeciesContextSpecsTableModel speciesContextSpecsTableModel = null;
	private SmallShapeManager shapeManager = new SmallShapeManager(false, false, false, false);

	private EditorScrollTable molecularTypeSpecsTable = null;
	private MolecularTypeSpecsTableModel molecularTypeSpecsTableModel = null;
	
	private JComboBox<String> siteColorComboBox = null;
	private JTextField siteXField = null;
	private JTextField siteYField = null;
	private JTextField siteZField = null;
	
	private JTextField linkLengthField = null;
	private JButton addLinkButton = null;

	//
	// TODO: make it possible to use right click menu to delete links
	//
	private JList<MolecularInternalLinkSpec> siteLinksList = null;
	private DefaultListModel<MolecularInternalLinkSpec> siteLinksListModel = new DefaultListModel<>();
	private ListCellRenderer<Object> siteLinksCellRenderer = new DefaultListCellRenderer(){
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof MolecularInternalLinkSpec && component instanceof JLabel){
				MolecularInternalLinkSpec mils = (MolecularInternalLinkSpec)value;
				MolecularComponentPattern firstMcp = mils.getMolecularComponentPatternOne();
				MolecularComponentPattern secondtMcp = mils.getMolecularComponentPatternTwo();
				((JLabel)component).setText(firstMcp.getMolecularComponent().getName() + " :: " + secondtMcp.getMolecularComponent().getName());
			}
			return component;
		}
	};

	// TODO: this is for popup menus in the table (instantiated in getMolecularTypeSpecsTable() - uncomment there too)
//	private class InternalScrollTableActionManager extends DefaultScrollTableActionManager {
//		InternalScrollTableActionManager(JTable table) {
//			super(table);
//			ApplicationSpecificationsPanel asp;
//		}
//		@Override
//		protected void constructPopupMenu() {
//			if (popupMenu == null) {
//				super.constructPopupMenu();
//				int pos = 0;
//				DocumentEditorSubPanel.addFieldDataMenuItem(getOwnerTable(), popupMenu, pos++);
//				popupMenu.insert(new JSeparator(), pos++);
//			}
//			Object obj = VCellTransferable.getFromClipboard(VCellTransferable.OBJECT_FLAVOR);	
//			boolean bPastable = obj instanceof VCellTransferable.ResolvedValuesSelection;
//			boolean bSomethingSelected = getSpeciesContextSpecsTable().getSelectedRows() != null && getSpeciesContextSpecsTable().getSelectedRows().length > 0;
//		}
//	}

	private class EventHandler implements FocusListener, ActionListener, PropertyChangeListener, ListSelectionListener {

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == siteXField || source == siteYField || source == siteZField) {
				changePosition((JTextField)source);
			} else if(source == linkLengthField) {
				changeLinkLength();
			} else if(source == addLinkButton) {
				addLinkActionPerformed();
				refreshSiteLinksList();
			} else if(source == getSiteColorComboBox()) {
				updateSiteColor();
			}
		}
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
			Object source = e.getSource();
			if (source == siteXField || source == siteYField || source == siteZField) {
				changePosition((JTextField)source);
			} else if(source == linkLengthField) {
				// TODO: do NOT call here changeLinkLength(), it will modified the newly selected link instead the old one
				// changeLinkLength();
			}
		}
		public void propertyChange(java.beans.PropertyChangeEvent e) {
			if(e.getSource() instanceof Model && e.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_MOLECULAR_TYPE_LIST)) {
				updateInterface();
			}
			// TODO: I think this is not needed
//			if (e.getSource() == selectionManager) {
//				System.out.println(e.getPropertyName());
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
		siteLinksList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		linkLengthField.addActionListener(eventHandler);
		addLinkButton.addActionListener(eventHandler);
		
		getSiteColorComboBox().addActionListener(eventHandler);
		
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
			siteXField = new JTextField();
			siteYField = new JTextField();
			siteZField = new JTextField();
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
		gbc.weighty = 0.8;
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
		
		// renderer for the rules column (reaction rules / assignment rules)
		// TODO: rules not compatible with springsalad, must create issue if present
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
		
		// The Structures combobox cell renderer in the MolecularTypeSpecsTable
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
						if(value != null) {
							setText(value.toString());
						}
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
		getSpeciesContextSpecsTable().setDefaultRenderer(SpeciesContextSpecsTableModel.RulesProvenance.class, rulesTableCellRenderer);	// icons for assignment and rate rules

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
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.insets = new Insets(2, 2, 2, 2);
		sitesPanel.add(getSiteColorComboBox(), gbc);

//		// --- links -----------------------------------------------
		linksPanel.setLayout(new GridBagLayout());
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
		getMolecularTypeSpecsTable().setDefaultRenderer(Structure.class, structuresTableCellRenderer);	// The Structures combobox cell renderer
		
		
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
//				speciesContextSpecsTable.setScrollTableActionManager(new InternalScrollTableActionManager(speciesContextSpecsTable));
				speciesContextSpecsTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return speciesContextSpecsTable;
	}
	
	// siteColorComboBox
	private JComboBox<String> getSiteColorComboBox() {
		if (siteColorComboBox == null) {
			siteColorComboBox = new JComboBox<String>();
			siteColorComboBox.setName("JComboBox1");
			
			DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
			for(NamedColor namedColor : Colors.COLORARRAY) {
				model.addElement(namedColor.getName());
			}
			siteColorComboBox.setModel(model);
//			siteColorComboBox.setRenderer(new DefaultListCellRenderer() {
// see ReactionRuleKineticsPropertiesPanel.getKineticsTypeComboBox() for complex renderer
//			});
		}
		return siteColorComboBox;
	}
	private void updateSiteColor() {
		String colorName = (String)getSiteColorComboBox().getSelectedItem();
		if(colorName == null) {
			return;
		}
		NamedColor namedColor = Colors.getColorByName(colorName);
		SiteAttributesSpec sas = fieldSpeciesContextSpec.getSiteAttributesMap().get(fieldMolecularComponentPattern);
		if(namedColor != null && namedColor != sas.getColor()) {
			sas.setColor(namedColor);
		}
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
	public SpeciesContextSpec getSpeciesContextSpec() {
		return fieldSpeciesContextSpec;
	}
	void setMolecularComponentPattern(MolecularComponentPattern mcp) {
		fieldMolecularComponentPattern = mcp;
		//TODO: stuff
		updateInterface();
	}

	// ============================================================================================
	
	public void setIssueManager(IssueManager issueManager) {
		super.setIssueManager(issueManager);
		speciesContextSpecsTableModel.setIssueManager(issueManager);
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
			addLinkButton.setEnabled(true);
			refreshSiteLinksList();
		} else {
			linkLengthField.setEditable(false);
			linkLengthField.setText(null);
			addLinkButton.setEnabled(false);
			refreshSiteLinksList();
		}
		if(bNonNullMolecularComponentPattern) {
			SiteAttributesSpec sas = fieldSpeciesContextSpec.getSiteAttributesMap().get(fieldMolecularComponentPattern);
			siteXField.setEditable(true);
			siteYField.setEditable(true);
			siteZField.setEditable(true);
			siteXField.setText(sas.getCoordinate().getX()+"");
			siteYField.setText(sas.getCoordinate().getY()+"");
			siteZField.setText(sas.getCoordinate().getZ()+"");
			getSiteColorComboBox().setSelectedItem(sas.getColor().getName());
		} else {
			siteXField.setEditable(false);
			siteYField.setEditable(false);
			siteZField.setEditable(false);
			siteXField.setText(null);
			siteYField.setText(null);
			siteZField.setText(null);
			getSiteColorComboBox().setSelectedItem(null);
		}
	}
	
	private void refreshSiteLinksList() {
		siteLinksListModel.removeAllElements();
		if(fieldSpeciesContextSpec != null) {
			for (MolecularInternalLinkSpec mils : fieldSpeciesContextSpec.getInternalLinkSet()){
				siteLinksListModel.addElement(mils);
			}
		}
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		setTableSelections(selectedObjects, speciesContextSpecsTable, speciesContextSpecsTableModel);
	}

	/*
	 * TODO: commit changes to current (old) SpeciesContextStep 
	 * before the newly selected SpeciesContextStep becomes current
	 */
	private void changeSpeciesContextSpec() {
		
	}

	private void changePosition(JTextField source) {
		System.out.println("Site coordinates changed");
		SiteAttributesSpec sas = fieldSpeciesContextSpec.getSiteAttributesMap().get(fieldMolecularComponentPattern);
		String text = source.getText();
		if(sas == null || text == null) {
			return;
		}
		Coordinate c = sas.getCoordinate();
		double res = 0.0;
		try {
			res = Double.parseDouble(text);
		} catch(NumberFormatException e) {
			return;
		}

		if(siteXField == source && c.getX() != res) {
			c = new Coordinate(res, c.getY(), c.getZ());
		} else if(siteYField == source && c.getY() != res) {
			c = new Coordinate(c.getX(), res, c.getZ());
		} else if(siteZField == source && c.getZ() != res) {
			c = new Coordinate(c.getX(), c.getY(), res);
		}
		sas.setCoordinate(c);
		
		recalculateLinkLengths();
	}
	private void changeLinkLength() {
		throw new UnsupportedOperationException("At this time the LinkLength is an uneditable derived value.");
//		Double linkLength = Double.parseDouble(linkLengthField.getText());
//		MolecularInternalLinkSpec selectedValue = siteLinksList.getSelectedValue();
//		int selectedIndex = siteLinksList.getSelectedIndex();
//		System.out.println("changeLinkLength(): selected index '" + selectedIndex + "' being set to " + linkLength);
//		// TODO: set x,y,z for sites, link length will be always calculated from xyz
//		recalculatePositions();
	}
	
	// we only display the link length in the UI as a courtesy, this field doesn't exist in MolecularInternalLinkSpec
	// when we need it, we compute it
	private void recalculateLinkLengths() {
		
		MolecularInternalLinkSpec mils = siteLinksList.getSelectedValue();
		
		
		showLinkLength(mils);
	}
	private void recalculatePositions() {
	}

	private void addLinkActionPerformed() {
		AddLinkPanel panel = new AddLinkPanel(this);
		ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(this);
		ChildWindow childWindow = childWindowManager.addChildWindow(panel, panel, " Add Link ");
		Dimension dim = new Dimension(320, 330);
		childWindow.pack();
		panel.setChildWindow(childWindow);
		childWindow.setPreferredSize(dim);
		childWindow.showModal();

		if(panel.getButtonPushed() == AddLinkPanel.ActionButtons.Apply) {
			MolecularComponentPattern firstMcp = panel.getFirstSiteList().getSelectedValue();
			MolecularComponentPattern secondMcp = panel.getSecondSiteList().getSelectedValue();
			MolecularInternalLinkSpec mils = new MolecularInternalLinkSpec(fieldSpeciesContextSpec, firstMcp, secondMcp);
			fieldSpeciesContextSpec.getInternalLinkSet().add(mils);
			return;
		} else {
			
			return;
		}
	}
	
	private void showLinkLength(MolecularInternalLinkSpec selectedValue) {
		if(selectedValue == null) {
			System.out.println("showLinkLength: SelectedValue is null");
			linkLengthField.setEditable(false);
			linkLengthField.setText(null);
			return;		// nothing selected
		}
		System.out.println("showLinkLength(): Selected row is '" + siteLinksList.getSelectedIndex() + "'");
		linkLengthField.setEditable(false);		// make it editable here, for now it's a derived value only
		linkLengthField.setText(selectedValue.getLinkLength()+"");
	};

}

