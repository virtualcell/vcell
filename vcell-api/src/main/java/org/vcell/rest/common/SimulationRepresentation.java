package org.vcell.rest.common;

import java.util.ArrayList;
import java.util.Enumeration;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.Constant;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathSymbolTable;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.Model.ReservedSymbol;
import cbit.vcell.model.ModelException;
import cbit.vcell.modeldb.BioModelRep;
import cbit.vcell.modeldb.SimContextRep;
import cbit.vcell.modeldb.SimulationRep;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.server.BioModelLink;
import cbit.vcell.server.MathModelLink;
import cbit.vcell.server.SimulationDocumentLink;


public class SimulationRepresentation {
	
	private final String key;
	private final String branchId;
	private final String name;
	private final String ownerName;
	private final String ownerKey;
	private final String mathKey;
	private final String solverName;
	private final int scanCount;
	private final MathModelLink mathModelLink;
	private final BioModelLink bioModelLink;
	private final OverrideRepresentation[] overrides;
	private final ParameterRepresentation[] parameters;
	

	private SimulationRepresentation(SimulationRep simulationRep, ParameterRepresentation[] parameters, OverrideRepresentation[] overrides, SimulationDocumentLink simulationDocumentLink) throws ExpressionException {
		this.parameters = parameters;
		this.key = simulationRep.getKey().toString();
		this.branchId = simulationRep.getBranchID().toString();
		this.name = simulationRep.getName();
		this.ownerKey = simulationRep.getOwner().getID().toString();
		this.ownerName = simulationRep.getOwner().getName();
		this.mathKey = simulationRep.getMathKey().toString();
		if (simulationRep.getSolverTaskDescription()!=null){
			this.solverName = simulationRep.getSolverTaskDescription().getSolverDescription().getDisplayLabel();
		}else{
			this.solverName = "unknown";
		}
		this.scanCount = simulationRep.getScanCount();
		if (simulationDocumentLink instanceof MathModelLink){
			this.mathModelLink = (MathModelLink)simulationDocumentLink;
			this.bioModelLink = null;
		}else if (simulationDocumentLink instanceof BioModelLink){
			this.mathModelLink = null;
			this.bioModelLink = (BioModelLink)simulationDocumentLink;
		}else{
			this.mathModelLink = null;
			this.bioModelLink = null;
		}
		this.overrides = overrides;
	}

	public SimulationRepresentation(SimulationRep simulationRep, BioModelRep bioModelRep) throws ExpressionException {
		this(simulationRep,null,getOverrideRepresentations(simulationRep),getBioModelLink(bioModelRep,simulationRep));
	}
	
	public SimulationRepresentation(SimulationRep simulationRep, SimulationDocumentLink simulationDocumentLink) throws ExpressionException {
		this(simulationRep,null,getOverrideRepresentations(simulationRep),simulationDocumentLink);
	}
	
	public SimulationRepresentation(SimulationRep simulationRep, BioModel bioModel) throws ExpressionException, MappingException, MathException, MatrixException, ModelException {
		this(simulationRep,getParameters(bioModel,simulationRep),getOverrideRepresentations(simulationRep),getBioModelLink(bioModel,simulationRep));
	}
	
	private static OverrideRepresentation[] getOverrideRepresentations(SimulationRep simulationRep) throws ExpressionException{
		OverrideRepresentation[] overrides = new OverrideRepresentation[simulationRep.getMathOverrideElements().length];
		for (int i=0;i<simulationRep.getMathOverrideElements().length;i++){
			overrides[i] = new OverrideRepresentation(simulationRep.getMathOverrideElements()[i]);
		}
		return overrides;
	}
	
	private static BioModelLink getBioModelLink(BioModelRep bioModelRep, SimulationRep simulationRep){
		SimContextRep simContextRep = bioModelRep.getSimContextRepFromMathKey(simulationRep.getMathKey());
		BioModelLink bioModelLink = new BioModelLink(
				bioModelRep.getBmKey().toString(), 
				bioModelRep.getBranchID().toString(), 
				bioModelRep.getName(),  
				(simContextRep!=null)?(simContextRep.getScKey().toString()):null, 
				(simContextRep!=null)?(simContextRep.getBranchID().toString()):null,
				(simContextRep!=null)?(simContextRep.getName()):null);
		return bioModelLink;
	}

	private static BioModelLink getBioModelLink(BioModel bioModel, SimulationRep simulationRep){
		SimulationContext simContext = null;
		for (SimulationContext sc : bioModel.getSimulationContexts()){
			if (sc.getMathDescription().getKey().equals(simulationRep.getMathKey())){
				simContext = sc;
				break;
			}
		}
		BioModelLink bioModelLink = new BioModelLink(
				bioModel.getVersion().getVersionKey().toString(), 
				bioModel.getVersion().getBranchID().toString(), 
				bioModel.getVersion().getName(),  
				(simContext!=null)?(simContext.getVersion().getVersionKey().toString()):null, 
				(simContext!=null)?(simContext.getVersion().getBranchID().toString()):null,
				(simContext!=null)?(simContext.getVersion().getName()):null);
		return bioModelLink;
	}
	
	private static ParameterRepresentation[] getParameters(BioModel bioModel, SimulationRep simulationRep) {
		SimulationContext simContext = null;
		for (SimulationContext sc : bioModel.getSimulationContexts()){
			if (sc.getMathDescription().getKey().equals(simulationRep.getMathKey())){
				simContext = sc;
				break;
			}
		}
		if (simContext==null){
			return null;
		}
		MathDescription mathDesc = simContext.getMathDescription(); // initialize to old mathDescription in case error generating math
		MathSymbolMapping mathSymbolMapping = null;
		try {
			simContext.updateAll(true);
			mathDesc = simContext.getMathDescription();
			mathSymbolMapping = (MathSymbolMapping) simContext.getMathDescription().getSourceSymbolMapping();
		} catch (Exception e1) {
			System.err.println(e1.getMessage());
		}
		ArrayList<ParameterRepresentation> parameterReps = new ArrayList<ParameterRepresentation>();
		Enumeration<Constant> enumMath = mathDesc.getConstants();
		while (enumMath.hasMoreElements()){
			Constant constant = enumMath.nextElement();
			if (constant.getExpression().isNumeric()){
				SymbolTableEntry biologicalSymbolTableEntry = null;
				if (mathSymbolMapping!=null){
					SymbolTableEntry[] stes = mathSymbolMapping.getBiologicalSymbol(constant);
					if (stes != null && stes.length>=1){
						biologicalSymbolTableEntry = stes[0];
					}
				}
				if (biologicalSymbolTableEntry instanceof ReservedSymbol){
					continue;
				}
				try {
					parameterReps.add(new ParameterRepresentation(constant.getName(), constant.getExpression().evaluateConstant(),biologicalSymbolTableEntry));
				} catch (ExpressionException e) {
					// can't happen, because constant expression is numeric
					e.printStackTrace();
				}
			}
		}
		return parameterReps.toArray(new ParameterRepresentation[0]);
	}

	public String getKey() {
		return key;
	}

	public String getBranchId() {
		return branchId;
	}

	public String getName() {
		return name;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public String getOwnerKey() {
		return ownerKey;
	}

	public String getMathKey() {
		return mathKey;
	}
	
	public String getSolverName(){
		return solverName;
	}
	
	public int getScanCount(){
		return scanCount;
	}

	public MathModelLink getMathModelLink(){
		return mathModelLink;
	}

	public BioModelLink getBioModelLink(){
		return bioModelLink;
	}
	
	public OverrideRepresentation[] getOverrides(){
		return overrides;
	}
	
	public ParameterRepresentation[] getParameters(){
		return parameters;
	}
	
	public OverrideRepresentation getOverride(String parameterName){
		for (OverrideRepresentation override : overrides){
			if (override.name.equals(parameterName)){
				return override;
			}
		}
		return null;
	}

}
