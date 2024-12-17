package org.vcell.model.springsalad.gui;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.desktop.biomodel.ApplicationSpecificationsPanel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.graph.GraphConstants;
import cbit.vcell.graph.SmallShapeManager;
import cbit.vcell.graph.SpeciesPatternSmallShape;
import cbit.vcell.mapping.*;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.gui.LinkSpecsTableModel;
import cbit.vcell.mapping.gui.MolecularTypeSpecsTableModel;
import cbit.vcell.mapping.gui.SpeciesContextSpecsTableModel;
import cbit.vcell.mapping.gui.StructureMappingTableRenderer.TextIcon;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.units.VCUnitDefinition;
import org.apache.commons.lang3.StringEscapeUtils;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Coordinate;
import org.vcell.util.gui.*;
import org.vcell.util.gui.ScrollTable.ScrollTableBooleanCellRenderer;
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
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Map;


@SuppressWarnings("serial")
public class MolecularStructuresPanel extends DocumentEditorSubPanel implements ApplicationSpecificationsPanel.Specifier {

	private EventHandler eventHandler = new EventHandler();
	private SimulationContext fieldSimulationContext;
	private SpeciesContextSpec fieldSpeciesContextSpec;
	private MolecularComponentPattern fieldMolecularComponentPattern;
	private MolecularInternalLinkSpec fieldMolecularInternalLinkSpec;

	private EditorScrollTable speciesContextSpecsTable = null;
	private SpeciesContextSpecsTableModel speciesContextSpecsTableModel = null;
	private SmallShapeManager shapeManager = new SmallShapeManager(false, false, false, false);

	private EditorScrollTable molecularTypeSpecsTable = null;
	private MolecularTypeSpecsTableModel molecularTypeSpecsTableModel = null;

	private EditorScrollTable linkSpecsTable = null;
	private LinkSpecsTableModel linkSpecsTableModel = null;

	private JButton addLinkButton = null;
	private JButton deleteLinkButton = null;
	//
	// TODO: make it possible to use right click menu to delete links
	//

	// keep the code below, just in case we need to override a ListCellRenderer
//	private JList<MolecularInternalLinkSpec> siteLinksList = null;
//	private DefaultListModel<MolecularInternalLinkSpec> siteLinksListModel = new DefaultListModel<>();
//	private ListCellRenderer<Object> siteLinksCellRenderer = new DefaultListCellRenderer(){
//		@Override
//		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//			Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//			if (value instanceof MolecularInternalLinkSpec && component instanceof JLabel){
//				MolecularInternalLinkSpec mils = (MolecularInternalLinkSpec)value;
//				MolecularComponentPattern firstMcp = mils.getMolecularComponentPatternOne();
//				MolecularComponentPattern secondtMcp = mils.getMolecularComponentPatternTwo();
//				((JLabel)component).setText(firstMcp.getMolecularComponent().getName() + " :: " + secondtMcp.getMolecularComponent().getName());
//			}
//			return component;
//		}
//	};

	// TODO: keep this code
	//  this is for popup menus in the table (instantiated in getMolecularTypeSpecsTable() - uncomment there too)
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
			if(source == addLinkButton) {
				addLinkActionPerformed();
				refreshSiteLinksList();
			} else if(source == deleteLinkButton) {
				deleteLinkActionPerformed();
				refreshSiteLinksList();
			}
		}
		public void focusGained(FocusEvent e) {
			;
		}
		public void focusLost(FocusEvent e) {
			;
		}
		public void propertyChange(java.beans.PropertyChangeEvent e) {
			if(e.getSource() instanceof Model && e.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_MOLECULAR_TYPE_LIST)) {
				updateInterface();
			} else if(e.getSource() instanceof SpeciesContextSpec && e.getPropertyName().equals(SpeciesContextSpec.PROPERTY_NAME_SITE_SELECTED_IN_SHAPE)) {
				// notified that the user clicked in a site oval shape or link line shape
				// we need to update the selected row in the corresponding table
				for(int row=0 ; row < molecularTypeSpecsTableModel.getRowCount(); row++) {
					MolecularComponentPattern mcp = molecularTypeSpecsTableModel.getValueAt(row);
					if(e.getNewValue() instanceof MolecularComponentPattern mcpSelected) {
						if(mcpSelected == mcp) {
							// select the table row
							getMolecularTypeSpecsTable().setRowSelectionInterval(row, row);
							// bring the row into view if it's not visible
							Rectangle rect = getMolecularTypeSpecsTable().getCellRect(row, 0, true);
							getMolecularTypeSpecsTable().scrollRectToVisible(rect);
							break;
						}
					}
				}
			} else if(e.getSource() instanceof SpeciesContextSpec && e.getPropertyName().equals(SpeciesContextSpec.PROPERTY_NAME_LINK_SELECTED_IN_SHAPE)) {
				for(int row=0 ; row < linkSpecsTableModel.getRowCount(); row++) {
					MolecularInternalLinkSpec mils = linkSpecsTableModel.getValueAt(row);
					if(e.getNewValue() instanceof MolecularInternalLinkSpec milsSelected) {
						if(milsSelected == mils) {
							// select the table row
							getLinkSpecsTable().setRowSelectionInterval(row, row);
							// bring the row into view if it's not visible
							Rectangle rect = getLinkSpecsTable().getCellRect(row, 0, true);
							getLinkSpecsTable().scrollRectToVisible(rect);
							break;
						}
					}
				}
			}
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
				System.out.println("valueChanged: speciesContextSpecsTableModel");
				int row = getSpeciesContextSpecsTable().getSelectedRow();
				SpeciesContextSpec scsSelected = speciesContextSpecsTableModel.getValueAt(row);
				setSpeciesContextSpec(scsSelected);

				ArrayList<Object> selectedObjects = new ArrayList<Object>();
				selectedObjects.add(scsSelected);
				selectionManager.setSelectedObjects(selectedObjects.toArray());

			} else if (e.getSource() == getMolecularTypeSpecsTable().getSelectionModel()) {
				System.out.println("valueChanged: molecularTypeSpecsTableModel");
				int row = getMolecularTypeSpecsTable().getSelectedRow();
				MolecularComponentPattern mcmSelected = molecularTypeSpecsTableModel.getValueAt(row);
				setMolecularComponentPattern(mcmSelected);

			} else if(e.getSource() == getLinkSpecsTable().getSelectionModel()) {		// links table
				System.out.println("valueChanged: linkSpecsTableModel");
				int row = getLinkSpecsTable().getSelectedRow();
				MolecularInternalLinkSpec milsSelected = linkSpecsTableModel.getValueAt(row);
				setMolecularInternalLinkSpec(milsSelected);
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
		addLinkButton.addActionListener(eventHandler);
		deleteLinkButton.addActionListener(eventHandler);

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

		ListSelectionModel lsm3 = getLinkSpecsTable().getSelectionModel();
		if(lsm3 instanceof DefaultListSelectionModel) {
			DefaultListSelectionModel dlsm = (DefaultListSelectionModel)lsm3;
			dlsm.addListSelectionListener(eventHandler);
		}

	}
	
	private void initialize() {
		try {
			addLinkButton = new JButton("Add Link");
			deleteLinkButton = new JButton("Delete Link");

			addLinkButton.setEnabled(true);
			deleteLinkButton.setEnabled(false);
		
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

		top.setMinimumSize(new Dimension(0, 100));
		
		thePanel.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 0.4;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(3, 0, 0, 0);	//  top, left, bottom, right 
		thePanel.add(top, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.6;
//		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(1, 0, 0, 0);
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

		// ---------------------------------------------------------------------------------------------
		
		bottom.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.9;
		gbc.weighty = 1.0;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(3, 2, 2, 3);	//  top, left, bottom, right 
		bottom.add(sitesPanel, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.weightx = 0.1;
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
		sitesPanel.add(pb, gbc);		// MolecularTypeSpecsTable

		// The NamedColor combobox cell renderer in the MolecularTypeSpecsTable
		DefaultScrollTableCellRenderer namedColorTableCellRenderer = new DefaultScrollTableCellRenderer() {
			final Color lightBlueBackground = new Color(214, 234, 248);
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
														   int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (table.getModel() instanceof MolecularTypeSpecsTableModel) {
					if (value instanceof NamedColor) {
						NamedColor namedColor = (NamedColor)value;
						setText(namedColor.getName());
//						setText("");		// we may want to just display the icon and no text
						Icon icon = new ColorIcon(10,10,namedColor.getColor(), true);	// small square icon with subdomain color
						setHorizontalTextPosition(SwingConstants.RIGHT);
						setIcon(icon);
					}
				}
				return this;
			}
		};
		// The Expression cell renderer  in the MolecularTypeSpecsTable
		DefaultScrollTableCellRenderer expressionTableCellRenderer = new DefaultScrollTableCellRenderer() {
			String darkRed = NamedColor.getHex(GraphConstants.darkred);	// "#8B0000"
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
														   int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (table.getModel() instanceof MolecularTypeSpecsTableModel) {
					MolecularTypeSpecsTableModel model = (MolecularTypeSpecsTableModel)table.getModel();
					if (value instanceof Double dvalue) {
						Integer intValue = dvalue.intValue();
						String formattedValue = Integer.toString(intValue);		// we initialize with the int value
						if(dvalue % 1 != 0) {									// if it has a non-zero fractional part,
							formattedValue = String.format("%.1f", dvalue);		// we truncate the double to the first decimal (angstrom)
						}
						MolecularTypeSpecsTableModel.ColumnType columnType = MolecularTypeSpecsTableModel.ColumnType.values()[column];
						int cellWidth = table.getColumnModel().getColumn(column).getWidth();
						switch(columnType) {
							case COLUMN_X:
							case COLUMN_Y:
							case COLUMN_Z:
								if(cellWidth > 70) {			// we show units only if there's enough space
									if(!isSelected) {
										String text = "<html>" + formattedValue + "<span style='color:" + darkRed + ";'> [nm]</span></html>";
										setText(text);
									} else {
										setText(formattedValue + " [nm]");
									}
								} else {
									setText(formattedValue + "");		// if it's too busy, just show the numbers
								}
								setToolTipText(formattedValue + " [nm]");	// we always show the units in the tooltip
								break;
							case COLUMN_RADIUS:					// for Diffusion and Radius we show what the user explicitely entered, no truncation
								if(cellWidth > 70) {			// we show units only if there's enough space
									if(!isSelected) {
										String text = "<html>" + value + "<span style='color:" + darkRed + ";'> [nm]</span></html>";
										setText(text);
									} else {
										setText(value + " [nm]");
									}
								} else {
									setText(value + "");		// if it's too busy, just show the numbers
								}
								setToolTipText(value + " [nm]");	// we always show the units in the tooltip
								break;
							case COLUMN_DIFFUSION:
								if(cellWidth > 70) {
									if(!isSelected) {		// <sup>2</sup> is html for superscript 2  (x^2 for example)
										String text = "<html>" + value + "<span style='color:" + darkRed + ";'> [&mu;m<sup>2</sup>/s]</span></html>";
										setText(text);
									} else {
										setText(value + " [\u03BCm\u00B2/s]");		// \u03BC is unicode for greek mu, \u00B2 is unicode for superscript 2
									}
								} else {
									setText(value + "");
								}
								setToolTipText(value + " [\u03BCm\u00B2/s]");
								break;
							default:
								break;
						}
					}
				} else if(table.getModel() instanceof LinkSpecsTableModel) {
					LinkSpecsTableModel model = (LinkSpecsTableModel)table.getModel();
					if(value instanceof Double dvalue) {
						Integer intValue = dvalue.intValue();
						String formattedValue = Integer.toString(intValue);		// we initialize with the int value
						if(dvalue % 1 != 0) {									// if it has a non-zero fractional part,
							formattedValue = String.format("%.1f", dvalue);		// we truncate the double to the first decimal (angstrom)
						}
						LinkSpecsTableModel.ColumnType columnType = LinkSpecsTableModel.ColumnType.values()[column];
						int cellWidth = table.getColumnModel().getColumn(column).getWidth();
						switch(columnType) {
							case COLUMN_LENGTH:
								if(cellWidth > 70) {
									if(!isSelected) {
										String text = "<html>" + formattedValue + "<span style='color:" + darkRed + ";'> [nm]</span></html>";
										setText(text);
									} else {
										setText(formattedValue + " [nm]");
									}
								} else {
									setText(formattedValue + "");		// if it's too busy, just show the numbers
								}
								setToolTipText(formattedValue + " [nm]");	// we always show the units in the tooltip
								break;
							default:
								break;
						}
					}
				}
				return this;
			}
		};

		getMolecularTypeSpecsTable().setDefaultRenderer(String.class, new DefaultScrollTableCellRenderer());
		getMolecularTypeSpecsTable().setDefaultRenderer(Structure.class, structuresTableCellRenderer);	// The Structures combobox cell renderer
		getMolecularTypeSpecsTable().setDefaultRenderer(Expression.class, expressionTableCellRenderer);	// Expression field cell renderer
		getMolecularTypeSpecsTable().setDefaultRenderer(NamedColor.class, namedColorTableCellRenderer);	// NamedColor combobox cell renderer

//		// --- links -----------------------------------------------
		linksPanel.setLayout(new GridBagLayout());
		JScrollPane linksScrollPane = new JScrollPane(getLinkSpecsTable());
		linksScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);
		linksPanel.add(linksScrollPane, gbc);

			DefaultScrollTableCellRenderer linkSpecsTableCellRenderer = new DefaultScrollTableCellRenderer() {
				final Color lightBlueBackground = new Color(214, 234, 248);
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
															   int row, int column) {
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					if (table.getModel() instanceof LinkSpecsTableModel) {
						if (value instanceof MolecularInternalLinkSpec) {
							MolecularInternalLinkSpec mils = (MolecularInternalLinkSpec)value;
							MolecularComponentPattern firstMcp = mils.getMolecularComponentPatternOne();
							MolecularComponentPattern secondtMcp = mils.getMolecularComponentPatternTwo();
							setText(firstMcp.getMolecularComponent().getName() + " :: " + secondtMcp.getMolecularComponent().getName());
							SpeciesContextSpec scs = mils.getSpeciesContextSpec();
							if(fieldSpeciesContextSpec != scs) {
								throw new RuntimeException("SpeciesContextSpec inconsistent.");
							}
							Map<MolecularComponentPattern, SiteAttributesSpec> siteAttributesMap = getSpeciesContextSpec().getSiteAttributesMap();
							SiteAttributesSpec sasFirst = siteAttributesMap.get(firstMcp);
							SiteAttributesSpec sasSecond = siteAttributesMap.get(secondtMcp);
							NamedColor ncFirst = sasFirst.getColor();
							NamedColor ncSecond = sasSecond.getColor();
							Icon iconFirst = new ColorIcon(10,10,ncFirst.getColor(), true);
							Icon iconSecond = new ColorIcon(10,10,ncSecond.getColor(), true);
							Icon compositeIcon = new CompositeIcon(iconFirst, iconSecond);
//							setHorizontalTextPosition(SwingConstants.RIGHT);
							setIcon(compositeIcon);
						}
					}
					return this;
				}
			};
			getLinkSpecsTable().setDefaultRenderer(MolecularInternalLinkSpec.class, linkSpecsTableCellRenderer);	// MolecularInternalLinkSpec field cell renderer
			getLinkSpecsTable().setDefaultRenderer(Expression.class, expressionTableCellRenderer);	// Expression field cell renderer

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);
		linksPanel.add(addLinkButton, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 2, 2, 3);
		linksPanel.add(deleteLinkButton, gbc);

		initConnections();		// adding listeners
		
		} catch(Throwable e) {
			handleException(e);
		}
	}

	private EditorScrollTable getLinkSpecsTable() {
		if (linkSpecsTable == null) {
			try {
				linkSpecsTable = new EditorScrollTable();
				linkSpecsTable.setName("linkSpecsTable");
				linkSpecsTableModel = new LinkSpecsTableModel(linkSpecsTable);
				linkSpecsTable.setModel(linkSpecsTableModel);
//				molecularComponentSpecsTable.setScrollTableActionManager(new InternalScrollTableActionManager(table));
				linkSpecsTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return linkSpecsTable;
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
				speciesContextSpecsTableModel = new SpeciesContextSpecsTableModel(speciesContextSpecsTable, this);
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
		linkSpecsTableModel.setSimulationContext(simulationContext);
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
		linkSpecsTableModel.setSpeciesContextSpec(fieldSpeciesContextSpec);
		updateInterface();
	}
	public SpeciesContextSpec getSpeciesContextSpec() {
		return fieldSpeciesContextSpec;
	}
	void setMolecularComponentPattern(MolecularComponentPattern mcp) {
		fieldMolecularComponentPattern = mcp;
		if(fieldSpeciesContextSpec != null) {		// mcp may be null
			fieldSpeciesContextSpec.firePropertyChange(SpeciesContextSpec.PROPERTY_NAME_SITE_SELECTED_IN_TABLE, null, mcp);
		}
		updateInterface();
	}
	void setMolecularInternalLinkSpec(MolecularInternalLinkSpec mils) {
		fieldMolecularInternalLinkSpec = mils;
		if(fieldSpeciesContextSpec != null) {		// mils may be null
			fieldSpeciesContextSpec.firePropertyChange(SpeciesContextSpec.PROPERTY_NAME_LINK_SELECTED_IN_TABLE, null, mils);
		}
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

		MolecularTypePattern mtp = null;
		if(bNonNullMolecularTypePattern) {
			mtp = fieldSpeciesContextSpec.getSpeciesContext().getSpeciesPattern().getMolecularTypePatterns().get(0);
		}
		
		if(bNonNullMolecularTypePattern && mtp.getComponentPatternList().size() > 1) {		// a link requires 2 sites (components)
			addLinkButton.setEnabled(true);
		} else {
			addLinkButton.setEnabled(false);
		}
		if(linkSpecsTableModel.getRowCount() > 0 && fieldMolecularInternalLinkSpec != null) {	// there are links we can delete
			deleteLinkButton.setEnabled(true);
		} else {
			deleteLinkButton.setEnabled(false);
		}
	}

	/*
	 * Here we refresh the links table when we add or delete a link
	 */
	private void refreshSiteLinksList() {
		linkSpecsTableModel.refreshData();
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		setTableSelections(selectedObjects, speciesContextSpecsTable, speciesContextSpecsTableModel);
	}

	private void changeSpeciesContextSpec() {
		;
	}

	private void changePosition(JTextField source) {
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
//			System.out.println("addLinkActionPerformed");
			return;
		} else {
			return;
		}
	}
	private void deleteLinkActionPerformed() {

		int row = linkSpecsTable.getSelectedRow();
		MolecularInternalLinkSpec selectedValue = linkSpecsTableModel.getValueAt(row);
		if(selectedValue != null) {
			fieldSpeciesContextSpec.getInternalLinkSet().remove(selectedValue);
//			System.out.println("deleteLinkActionPerformed");
		}
		return;
	}

}
