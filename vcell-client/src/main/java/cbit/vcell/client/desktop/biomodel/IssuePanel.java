/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.NetworkConstraints;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.common.NetworkConstraintsEntity;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.Issue.Severity;
import org.vcell.util.IssueContext;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.VCellIcons;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.client.desktop.DecoratedIssueSource;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.GeometryContext.UnmappedGeometryClass;
import cbit.vcell.mapping.MicroscopeMeasurement;
import cbit.vcell.mapping.NetworkTransformer;
import cbit.vcell.mapping.ReactionSpec.ReactionCombo;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.SimulationContextNameScope;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mapping.StructureMapping.StructureMappingNameScope;
import cbit.vcell.mapping.gui.NetworkConstraintsTableModel;
import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.mapping.spatial.processes.SpatialProcess;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.resource.ErrorUtils;
import cbit.vcell.solver.OutputFunctionContext.OutputFunctionIssueSource;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationOwner;

@SuppressWarnings("serial")
public class IssuePanel extends DocumentEditorSubPanel {

	private JSortTable issueTable = null;
	private IssueTableModel issueTableModel = null;
	private JButton refreshButton = null;
	private JCheckBox showWarningCheckBox;
	
	public IssuePanel() {
		super();
		refreshButton = new JButton("Refresh");
		refreshButton.addActionListener(e -> {
				if (issueManager != null) {
					issueManager.updateIssues();
				}			
		});
		showWarningCheckBox = new JCheckBox("Show Warnings");
		showWarningCheckBox.setSelected(true);
		showWarningCheckBox.addActionListener(e-> {
				issueTableModel.setShowWarning(showWarningCheckBox.isSelected());
		});
		issueTable = new JSortTable();
		issueTableModel = new IssueTableModel(issueTable);
		issueTable.setModel(issueTableModel);
		issueTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				final int row = issueTable.getSelectedRow();
				final int column = issueTable.getSelectedColumn();
				if (row >= 0) {
					final Issue issue = issueTableModel.getValueAt(row);
					switch (e.getClickCount()) {
					case 1:
						if (column == IssueTableModel.COLUMN_URL) {
							String url = issue.getHyperlink();
							if (url != null) {
								try {
									URI uri = new URI(url);
									Desktop.getDesktop().browse(uri);
								} catch (URISyntaxException | IOException e1) {
									ErrorUtils.sendRemoteLogMessage(null, "Can't open IssuePanel URI "  + url + ' ' +e1.getMessage());
								}
							}
						}
						break;
					case 2:
						if (column == IssueTableModel.COLUMN_URL) {
							return;
						}
						invokeHyperlink(issue);
					}
				}
			}			
		});
		
		setLayout(new GridBagLayout());
		int gridy = 0;
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(0,10,0,0);
		add(showWarningCheckBox, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(0,0,0,10);
		add(refreshButton, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		add(issueTable.getEnclosingScrollPane(), gbc);
		
		DefaultTableCellRenderer tableRenderer = new DefaultScrollTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus,	row, column);
				setIcon(null);
				switch (column) {
				case IssueTableModel.COLUMN_DESCRIPTION: {
					Issue issue = (Issue)value;
					Severity severity = issue.getSeverity();
					Icon icon;
					switch (severity) {
					case INFO:
						icon = VCellIcons.getInfoIcon();
						break;
					case WARNING:
						icon = VCellIcons.getWarningIcon();
						break;					
					case ERROR:
						icon = VCellIcons.getErrorIcon();
						break;
					default:
						icon = null;
					}
					setIcon(icon);
					setText(issue.getMessage());
					String tt = issue.getHtmlTooltip();
//					if (!StringUtils.isBlank(tt)) {
						setToolTipText(tt);
//					}
					break;
				}								
				}
				return this;
			}			
		};
		issueTable.getColumnModel().getColumn(IssueTableModel.COLUMN_DESCRIPTION).setCellRenderer(tableRenderer);
	}
	
	@Override
	public void setIssueManager(IssueManager issueManager) {
		super.setIssueManager(issueManager);
		issueTableModel.setIssueManager(issueManager);
	}
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {	
	}	

	private void invokeHyperlink(Issue issue) {
		if (selectionManager != null) { //followHyperlink is no-op if selectionManger null, so no point in proceeding if it is
			IssueContext issueContext = issue.getIssueContext();
			IssueSource object = issue.getSource();

			if (object instanceof DecoratedIssueSource) {
				DecoratedIssueSource dis =(DecoratedIssueSource) object;
				dis.activateView(selectionManager);
			}
			else if (object instanceof Parameter) {
				followHyperlink(new ActiveView(null, DocumentEditorTreeFolderClass.BIOMODEL_PARAMETERS_NODE, ActiveViewID.parameters_functions),new Object[] {object});
			} else if (object instanceof StructureMapping) {
				StructureMapping structureMapping = (StructureMapping) object;
				StructureMappingNameScope structureMappingNameScope = (StructureMappingNameScope)structureMapping.getNameScope();
				SimulationContext simulationContext = ((SimulationContextNameScope)(structureMappingNameScope.getParent())).getSimulationContext();
				followHyperlink(new ActiveView(simulationContext, DocumentEditorTreeFolderClass.GEOMETRY_NODE, ActiveViewID.structure_mapping),new Object[] {object});
			} else if (object instanceof SpatialObject) {
				SpatialObject spatialObject = (SpatialObject) object;
				SimulationContext simulationContext = spatialObject.getSimulationContext();
				followHyperlink(new ActiveView(simulationContext, DocumentEditorTreeFolderClass.GEOMETRY_NODE, ActiveViewID.spatial_objects),new Object[] {object});
			} else if (object instanceof SpatialProcess) {
				SpatialProcess spatialProcess = (SpatialProcess) object;
				SimulationContext simulationContext = spatialProcess.getSimulationContext();
				followHyperlink(new ActiveView(simulationContext, DocumentEditorTreeFolderClass.GEOMETRY_NODE, ActiveViewID.spatial_processes),new Object[] {object});
			} else if (object instanceof GeometryContext.UnmappedGeometryClass) {
				UnmappedGeometryClass unmappedGeometryClass = (UnmappedGeometryClass) object;
				SimulationContext simulationContext = unmappedGeometryClass.getSimulationContext();
				followHyperlink(new ActiveView(simulationContext, DocumentEditorTreeFolderClass.GEOMETRY_NODE, ActiveViewID.structure_mapping),new Object[] {object});
			} else if (object instanceof MicroscopeMeasurement) {
				SimulationContext simulationContext = ((MicroscopeMeasurement) object).getSimulationContext();
				followHyperlink(new ActiveView(simulationContext, DocumentEditorTreeFolderClass.PROTOCOLS_NODE, ActiveViewID.microscope_measuremments),new Object[] {object});
			} else if (object instanceof BioEvent) {
				BioEvent be = (BioEvent)object;
				SimulationContext simulationContext = be.getSimulationContext();
				followHyperlink(new ActiveView(simulationContext, DocumentEditorTreeFolderClass.PROTOCOLS_NODE, ActiveViewID.events),new Object[] {object});
			} else if (object instanceof OutputFunctionIssueSource) {
				SimulationOwner simulationOwner = ((OutputFunctionIssueSource)object).getOutputFunctionContext().getSimulationOwner();
				if (simulationOwner instanceof SimulationContext) {
					SimulationContext simulationContext = (SimulationContext) simulationOwner;
					followHyperlink(new ActiveView(simulationContext, DocumentEditorTreeFolderClass.SIMULATIONS_NODE, ActiveViewID.output_functions),new Object[] {((OutputFunctionIssueSource)object).getAnnotatedFunction()});
				} else if (simulationOwner instanceof MathModel) {
					followHyperlink(new ActiveView(null, DocumentEditorTreeFolderClass.MATH_OUTPUT_FUNCTIONS_NODE, ActiveViewID.math_output_functions),new Object[] {((OutputFunctionIssueSource)object).getAnnotatedFunction()});
				}
			} else if (object instanceof Simulation) {
				Simulation simulation = (Simulation)object;
				SimulationOwner simulationOwner = simulation.getSimulationOwner();
				if (simulationOwner instanceof SimulationContext) {
					SimulationContext simulationContext = (SimulationContext) simulationOwner;
					followHyperlink(new ActiveView(simulationContext, DocumentEditorTreeFolderClass.SIMULATIONS_NODE, ActiveViewID.simulations),new Object[] {simulation});
				} else if (simulationOwner instanceof MathModel) {
					followHyperlink(new ActiveView(null, DocumentEditorTreeFolderClass.MATH_SIMULATIONS_NODE, ActiveViewID.math_simulations),new Object[] {simulation});
				}
			} else if (object instanceof GeometryContext) {
				setActiveView(new ActiveView(((GeometryContext)object).getSimulationContext(), DocumentEditorTreeFolderClass.GEOMETRY_NODE, ActiveViewID.geometry_definition));
			} else if (object instanceof Structure) {
				followHyperlink(new ActiveView(null, DocumentEditorTreeFolderClass.STRUCTURES_NODE, ActiveViewID.structures), new Object[] {object});
			} else if (object instanceof MolecularType) {
				followHyperlink(new ActiveView(null, DocumentEditorTreeFolderClass.MOLECULAR_TYPES_NODE, ActiveViewID.structures), new Object[] {object});
			} else if (object instanceof ReactionStep) {
				followHyperlink(new ActiveView(null, DocumentEditorTreeFolderClass.REACTIONS_NODE, ActiveViewID.reactions), new Object[] {object});
			} else if (object instanceof ReactionRule) {
				followHyperlink(new ActiveView(null, DocumentEditorTreeFolderClass.REACTIONS_NODE, ActiveViewID.reactions), new Object[] {object});
			} else if (object instanceof SpeciesContextSpec) {
				SpeciesContextSpec scs = (SpeciesContextSpec)object;
				ActiveView av = new ActiveView(scs.getSimulationContext(), DocumentEditorTreeFolderClass.SPECIFICATIONS_NODE, ActiveViewID.species_settings);
				followHyperlink(av, new Object[] {object});
			} else if (object instanceof ReactionCombo) {
				ReactionCombo rc = (ReactionCombo)object;
				followHyperlink(new ActiveView(rc.getReactionContext().getSimulationContext(), DocumentEditorTreeFolderClass.SPECIFICATIONS_NODE, ActiveViewID.reaction_setting),new Object[] {((ReactionCombo)object).getReactionSpec()});
			} else if (object instanceof SpeciesContext) {
				followHyperlink(new ActiveView(null, DocumentEditorTreeFolderClass.SPECIES_NODE, ActiveViewID.species), new Object[] {object});
			} else if (object instanceof RbmObservable) {
				followHyperlink(new ActiveView(null, DocumentEditorTreeFolderClass.OBSERVABLES_NODE, ActiveViewID.observables), new Object[] {object});
			} else if (object instanceof MathDescription) {
//				followHyperlink(new ActiveView(null, DocumentEditorTreeFolderClass.MATH_SIMULATIONS_NODE, ActiveViewID.generated_math), new Object[] {object});
				followHyperlink(new ActiveView(null, DocumentEditorTreeFolderClass.GEOMETRY_NODE, ActiveViewID.structure_mapping), new Object[] {object});
			} else if (object instanceof SpeciesPattern) {
				//			if (issue.getIssueContext().hasContextType(ContextType.SpeciesContext)){
				//				SpeciesContext thing = (SpeciesContext)issue.getIssueContext().getContextObject(ContextType.SpeciesContext);
				//				followHyperlink(new ActiveView(null, DocumentEditorTreeFolderClass.SPECIES_NODE, ActiveViewID.species), new Object[] {thing});
				//			}else if(issue.getIssueContext().hasContextType(ContextType.ReactionRule)) {
				//				ReactionRule thing = (ReactionRule)issue.getIssueContext().getContextObject(ContextType.ReactionRule);
				//				followHyperlink(new ActiveView(null, DocumentEditorTreeFolderClass.REACTIONS_NODE, ActiveViewID.reactions), new Object[] {thing});
				//			}else if(issue.getIssueContext().hasContextType(ContextType.RbmObservable)) {
				//				RbmObservable thing = (RbmObservable)issue.getIssueContext().getContextObject(ContextType.RbmObservable);
				//				followHyperlink(new ActiveView(null, DocumentEditorTreeFolderClass.OBSERVABLES_NODE, ActiveViewID.observables), new Object[] {thing});
				//			} else {
				System.err.println("SpeciesPattern object missing a proper issue context.");
				//			}
			} else if (object instanceof SimulationContext) {
				SimulationContext sc = (SimulationContext)object;
				IssueCategory ic = issue.getCategory();
				switch(ic) {
				case RbmNetworkConstraintsBad:
					NetworkConstraints nc = sc.getNetworkConstraints();
					if(issue.getMessage() == SimulationContext.IssueInsufficientMolecules) {
						NetworkConstraintsEntity nce = new NetworkConstraintsEntity(NetworkConstraintsTableModel.sMaxMoleculesName, nc.getMaxMoleculesPerSpecies()+"", NetworkTransformer.defaultMaxMoleculesPerSpecies+"");
						followHyperlink(new ActiveView(sc, DocumentEditorTreeFolderClass.SPECIFICATIONS_NODE, ActiveViewID.network_setting), new Object[] {nce});
					} else {
						NetworkConstraintsEntity nce = new NetworkConstraintsEntity(NetworkConstraintsTableModel.sMaxIterationName, nc.getMaxIteration()+"", NetworkTransformer.defaultMaxIteration+"");
						followHyperlink(new ActiveView(sc, DocumentEditorTreeFolderClass.SPECIFICATIONS_NODE, ActiveViewID.network_setting), new Object[] {nce});
					}
					break;
				default:
					followHyperlink(new ActiveView(sc, DocumentEditorTreeFolderClass.SPECIFICATIONS_NODE, ActiveViewID.network_setting), new Object[] {object});
					break;
				}
			} else if (object instanceof Geometry) {
				if (issueContext.hasContextType(ContextType.SimContext)){
					SimulationContext simContext = (SimulationContext)issueContext.getContextObject(ContextType.SimContext);
					followHyperlink(new ActiveView(simContext, DocumentEditorTreeFolderClass.GEOMETRY_NODE, ActiveViewID.geometry_definition), new Object[] {object});
				}else if (issueContext.hasContextType(ContextType.MathModel)){
					followHyperlink(new ActiveView(null, DocumentEditorTreeFolderClass.MATH_GEOMETRY_NODE, ActiveViewID.math_geometry), new Object[] {object});
				}else if (issueContext.hasContextType(ContextType.MathDescription)){
					followHyperlink(new ActiveView(null, DocumentEditorTreeFolderClass.GEOMETRY_NODE, ActiveViewID.geometry_definition), new Object[] {object});
				}
			} else {
				System.err.println("unknown object type in IssuePanel.invokeHyperlink(): " + object.getClass() + ", context type: " + issueContext.getContextType());
			}
		}
	}
}
