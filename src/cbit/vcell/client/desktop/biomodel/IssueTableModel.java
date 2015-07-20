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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.vcell.util.Issue;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.VCDocument;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.biomodel.IssueManager.IssueEvent;
import cbit.vcell.client.desktop.biomodel.IssueManager.IssueEventListener;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.GeometryContext.UnmappedGeometryClass;
import cbit.vcell.mapping.MicroscopeMeasurement;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.ReactionSpec.ReactionCombo;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mapping.StructureMapping.StructureMappingNameScope;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.ReactionStep.ReactionNameScope;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.modelopt.ModelOptimizationSpec;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.OutputFunctionContext.OutputFunctionIssueSource;
import cbit.vcell.solver.Simulation;

@SuppressWarnings("serial")
public class IssueTableModel extends VCellSortTableModel<Issue> implements IssueEventListener {

	static final int COLUMN_DESCRIPTION = 0;
	static final int COLUMN_URL = 1;
	static final int COLUMN_SOURCE = 2;
	static final int COLUMN_PATH = 3;
	private static final String[] labels = {"Description", "Url", "Source", "Defined In:"};
	private boolean bShowWarning = true;
	
	public IssueTableModel(ScrollTable table) {
		super(table, labels);
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		VCDocument vcDocument = (issueManager!=null)?(issueManager.getVCDocument()):null;
		Issue issue = getValueAt(rowIndex);
		switch (columnIndex) {
		case COLUMN_DESCRIPTION:
			return issue;
		case COLUMN_URL:
		{
			String url = issue.getHyperlink();
			if (url == null) {
				return "";
			}
			return "<html><a href=\"" + url + "\">More</a></html>";
		}
		case COLUMN_SOURCE:
			return getSourceObjectDescription(vcDocument,issue);
		case COLUMN_PATH:
			return getSourceObjectPathDescription(vcDocument,issue);
		}
		return null;
	}

	@Override
	protected Comparator<Issue> getComparator(final int col, final boolean ascending) {
		return new Comparator<Issue>() {
			public int compare(Issue o1, Issue o2) {
				VCDocument vcDocument = (issueManager!=null)?(issueManager.getVCDocument()):null;
				int scale = ascending ? 1 : -1;
				switch (col) {
				case COLUMN_DESCRIPTION: {
					int s1 = o1.getSeverity();
					int s2 = o2.getSeverity();
					if (s1 == s2) {
						return scale * o1.getMessage().compareTo(o2.getMessage());
					} else {
						return scale * new Integer(s1).compareTo(new Integer(s2));
					}
				}
				case COLUMN_URL: 
				{
					String u1 = o1.getHyperlink();
					String u2 = o2.getHyperlink();
					return u1 != null ? u1.compareTo(u2) : -1;
				}
					
				case COLUMN_SOURCE:
					return scale * getSourceObjectDescription(vcDocument,o1).compareTo(getSourceObjectDescription(vcDocument,o2));
				case COLUMN_PATH:
					return scale * getSourceObjectPathDescription(vcDocument,o1).compareTo(getSourceObjectPathDescription(vcDocument,o2));
				}
				return 0;
			}		
		};
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case COLUMN_DESCRIPTION:
			return Issue.class;
		case COLUMN_URL:
		case COLUMN_SOURCE:
		case COLUMN_PATH:
			return String.class;
		}
		return Object.class;
	}

	void refreshData() {
		List<Issue> issueList = null;
		if (issueManager != null) {
			List<Issue> allIssueList = issueManager.getIssueList();
			issueList = new ArrayList<Issue>();
			for (Issue issue : allIssueList) {
				int severity = issue.getSeverity();
				if (severity == Issue.SEVERITY_ERROR) {
					issueList.add(issue);
				}
			}
			if (bShowWarning) {
				for (Issue issue : allIssueList) {
					int severity = issue.getSeverity();
					if (severity == Issue.SEVERITY_WARNING) {
						issueList.add(issue);
					}
				}
			}
		}
		setData(issueList);
		GuiUtils.flexResizeTableColumns(ownerTable);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	void issueManagerChange(IssueManager oldValue, IssueManager newValue) {
		if (oldValue != null) {
			oldValue.removeIssueEventListener(this);
		}
		if (newValue != null) {
			newValue.addIssueEventListener(this);		
		}
		refreshData();
	}

	public void issueChange(IssueEvent issueEvent) {
		refreshData();		
	}

	public final void setShowWarning(boolean bShowWarning) {
		if (this.bShowWarning == bShowWarning) {
			return;
		}
		this.bShowWarning = bShowWarning;
		issueManager.updateIssues();
		refreshData();
	}

	private String getSourceObjectPathDescription(VCDocument vcDocument, Issue issue) {
		if (vcDocument instanceof BioModel){
			BioModel bioModel = (BioModel)vcDocument;
			Object source = issue.getSource();
			String description = "";
			if (source instanceof SymbolTableEntry) {
				description = ((SymbolTableEntry) source).getNameScope().getPathDescription();
			} else if (source instanceof ReactionStep) {
				ReactionStep reactionStep = (ReactionStep) source;
				description = ((ReactionNameScope)reactionStep.getNameScope()).getPathDescription();
			} else if (source instanceof SpeciesContext) {
				description = "Species";
			} else if (source instanceof Structure) {
				Structure structure = (Structure)source;
				description = "Model / " + structure.getTypeName() + "(" + structure.getName() + ")";
			} else if (source instanceof StructureMapping) {
				StructureMapping structureMapping = (StructureMapping) source;
				description = ((StructureMappingNameScope)structureMapping.getNameScope()).getPathDescription();
			} else if (source instanceof OutputFunctionIssueSource) {
				SimulationContext simulationContext = (SimulationContext) ((OutputFunctionIssueSource)source).getOutputFunctionContext().getSimulationOwner();
				description = "App(" + simulationContext.getNameScope().getPathDescription() + ") / " 
					+ "Simulations" + " / " + "Output Functions";
			} else if (source instanceof Simulation) {
				Simulation simulation = (Simulation)source;
				try {
					SimulationContext simulationContext = bioModel.getSimulationContext(simulation);
					description = "App(" + simulationContext.getNameScope().getPathDescription() + ") / Simulations";
				} catch (ObjectNotFoundException e) {
					e.printStackTrace();
					description = "App(" + "unknown" + ") / Simulations";
				}
			} else if (source instanceof UnmappedGeometryClass) {
				UnmappedGeometryClass unmappedGC = (UnmappedGeometryClass) source;
				description = "App(" + unmappedGC.getSimulationContext().getNameScope().getPathDescription() + ") / Subdomain(" + unmappedGC.getGeometryClass().getName() + ")";
			} else if (source instanceof GeometryContext) {
				description = "App(" + ((GeometryContext)source).getSimulationContext().getNameScope().getPathDescription() + ")";
			} else if (source instanceof ModelOptimizationSpec) {
				description = "App(" + ((ModelOptimizationSpec)source).getSimulationContext().getNameScope().getPathDescription() + ") / Parameter Estimation";
			} else if (source instanceof MicroscopeMeasurement) {
				description = "App(" + ((MicroscopeMeasurement)source).getSimulationContext().getNameScope().getPathDescription() + ") / Microscope Measurements";
			} else if (source instanceof SpeciesContextSpec) {
				SpeciesContextSpec scs = (SpeciesContextSpec)source;
				description = "App(" + scs.getSimulationContext().getNameScope().getPathDescription() + ") / Specifications / Species";
			} else if (source instanceof ReactionCombo) {
				ReactionCombo rc = (ReactionCombo)source;
				description = "App(" + rc.getReactionContext().getSimulationContext().getNameScope().getPathDescription() + ") / Specifications / Reactions";
			}
			return description;
		}else if (vcDocument instanceof MathModel){
			Object object = issue.getSource();
			if (object instanceof Geometry) {
				return GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_MATH_GEOMETRY;
			} else if (object instanceof OutputFunctionIssueSource) {
				return GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_MATH_OUTPUTFUNCTIONS;
			} else if (object instanceof Simulation){
				return "Simulation("+((Simulation)object).getName()+")";
			} else {
				return GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_MATH_VCML;
			}
		}else{
			System.err.println("unknown document type in IssueTableModel.getSourceObjectPathDescription()");
			return "";
		}
	}


	private String getSourceObjectDescription(VCDocument vcDocument, Issue issue) {
		
		if (vcDocument instanceof BioModel){
			Object object = issue.getSource();
			String description = "";
			if (object instanceof SymbolTableEntry) {
				description = ((SymbolTableEntry)object).getName();
			} else if (object instanceof ReactionStep) {
				description = ((ReactionStep)object).getName();
			} else if (object instanceof SpeciesContext) {
				description = ((SpeciesContext)object).getName();
			} else if (object instanceof Structure) {
				description = ((Structure)object).getName();
			} else if (object instanceof Variable) {
				description = ((Variable)object).getName();
			} else if (object instanceof SubDomain) {
				description = ((SubDomain)object).getName();
			} else if (object instanceof Geometry) {
				description = ((Geometry)object).getName();
			} else if (object instanceof StructureMapping) {
				description = ((StructureMapping)object).getStructure().getName();
			} else if (object instanceof OutputFunctionIssueSource) {
				description = ((OutputFunctionIssueSource)object).getAnnotatedFunction().getName();
			} else if (object instanceof UnmappedGeometryClass) {
				description = ((UnmappedGeometryClass) object).getGeometryClass().getName();
			} else if (object instanceof MicroscopeMeasurement) {
				description = ((MicroscopeMeasurement) object).getName();
			}else if (object instanceof GeometryContext) {
				description = "Geometry";
			}else if (object instanceof ModelOptimizationSpec) {
				description = ((ModelOptimizationSpec) object).getParameterEstimationTask().getName();
			}else if (object instanceof Simulation) {
				description = ((Simulation) object).getName();
			} else if (object instanceof SpeciesContextSpec) {
				SpeciesContextSpec scs = (SpeciesContextSpec)object;
				description = scs.getSpeciesContext().getName();
			} else if (object instanceof ReactionCombo) {
				ReactionSpec rs = ((ReactionCombo)object).getReactionSpec();
				description = rs.getReactionStep().getName();
			}
			return description;
		}else if (vcDocument instanceof MathModel){
			Object object = issue.getSource();
			String description = "";
			if (object instanceof Variable) {
				description = ((Variable)object).getName();
			} else if (object instanceof SubDomain) {
				description = ((SubDomain)object).getName();
			} else if (object instanceof Geometry) {
				description = "Geometry";
			} else if (object instanceof OutputFunctionIssueSource) {
				description = ((OutputFunctionIssueSource)object).getAnnotatedFunction().getName();
			} else if (object instanceof MathDescription) {
				return "math";
			} else if (object instanceof Simulation) {
				return "Simulation "+((Simulation)object).getName()+"";
			}
			return description;		
		}else{
			System.err.println("unknown document type in IssueTableModel.getSourceObjectDescription()");
			return "";
		}
	}



}
