package org.vcell.rest.common;

import cbit.vcell.model.Parameter;
import cbit.vcell.parser.SymbolTableEntry;

public class ParameterRepresentation {
	public final String name;
	public final double defaultValue;
	public final String modelSymbolContext;
	public final String modelSymbolType;
	public final String modelSymbolName;
	public final String modelSymbolDesc;
	public final String modelSymbolUnit;
	
	public ParameterRepresentation(String name, double defaultValue, SymbolTableEntry biologicalSymbol) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.modelSymbolContext = getModelSymbolContext(biologicalSymbol);
		this.modelSymbolType = getModelSymbolType(biologicalSymbol);
		this.modelSymbolName = getModelSymbolName(biologicalSymbol);
		this.modelSymbolDesc = getModelSymbolDescription(biologicalSymbol);
		this.modelSymbolUnit = getModelSymbolUnit(biologicalSymbol);
	}
	
	private String getModelSymbolUnit(SymbolTableEntry biologicalSymbol) {
		if (biologicalSymbol!=null){
			return biologicalSymbol.getUnitDefinition().getSymbol();
		}
		return null;
	}

	private String getModelSymbolName(SymbolTableEntry biologicalSymbol) {
		if (biologicalSymbol!=null){
			return biologicalSymbol.getName();
		}
		return null;
	}

	private static String getModelSymbolType(SymbolTableEntry biologicalSymbol){
		if (biologicalSymbol!=null){
			return biologicalSymbol.getClass().getSimpleName();
		}
		return null;
	}

	private static String getModelSymbolDescription(SymbolTableEntry biologicalSymbol){
		if (biologicalSymbol instanceof Parameter){
			Parameter parameter = (Parameter)biologicalSymbol;
			return parameter.getDescription();
		}else if (biologicalSymbol != null){
			return biologicalSymbol.getClass().getSimpleName();
		}else{
			return null;
		}
	}

	private static String getModelSymbolContext(SymbolTableEntry biologicalSymbol){
		if (biologicalSymbol == null || biologicalSymbol.getNameScope() == null){
			return null;
		}
		return biologicalSymbol.getNameScope().getPathDescription();
	}

	public String getName() {
		return name;
	}

	public double getDefaultValue() {
		return defaultValue;
	}

	public String getModelSymbolContext() {
		return modelSymbolContext;
	}

	public String getModelSymbolType() {
		return modelSymbolType;
	}

	public String getModelSymbolName() {
		return modelSymbolName;
	}

	public String getModelSymbolDesc() {
		return modelSymbolDesc;
	}

	public String getModelSymbolUnit() {
		return modelSymbolUnit;
	}	
	
}
