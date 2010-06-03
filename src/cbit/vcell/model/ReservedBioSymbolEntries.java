package cbit.vcell.model;

import java.util.HashMap;
import java.util.Map;

import cbit.vcell.field.FieldFunctionDefinition;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry;

public class ReservedBioSymbolEntries {
	
	private static HashMap<String,ReservedSymbol> symbolTableEntries = null;
	private static HashMap<String,SymbolTableFunctionEntry> symbolTableFunctionEntries = null;
	public final static NameScope nameScope = new ReservedBioSymbolNameScope();
	public final static ReservedBioSymbolTable symbolTable = new ReservedBioSymbolTable();

	
	public static class ReservedBioSymbolTable implements ScopedSymbolTable {

		private ReservedBioSymbolTable() {
		}

		public SymbolTableEntry getEntry(String identifierString) throws ExpressionBindingException {
			ReservedSymbol reservedSymbol = ReservedBioSymbolEntries.getReservedSymbolEntry(identifierString);
			if (reservedSymbol != null) {
				return reservedSymbol;
			}
			SymbolTableFunctionEntry steFunction = ReservedBioSymbolEntries.getFunctionDefinitionEntry(identifierString);
			return steFunction;
		}

		public void getEntries(Map<String, SymbolTableEntry> entryMap) {
			ReservedBioSymbolEntries.getAll(entryMap);

		}

		public void getLocalEntries(Map<String, SymbolTableEntry> entryMap) {
			getEntries(entryMap);

		}

		public SymbolTableEntry getLocalEntry(String identifier)
				throws ExpressionBindingException {
			return getEntry(identifier);
		}

		public NameScope getNameScope() {
			return nameScope;
		}
	}

	public static class ReservedBioSymbolNameScope extends BioNameScope {
		private NameScope children[] = new NameScope[0];

		private ReservedBioSymbolNameScope() {
			super();
		}

		public NameScope[] getChildren() {
			return children;
		}

		public String getName() {
			return "ReservedSymbols";
		}

		public NameScope getParent() {
			// System.out.println("ModelNameScope.getParent() returning null ... no parent");
			return null;
		}

		public ScopedSymbolTable getScopedSymbolTable() {
			return new ReservedBioSymbolTable();
		}

		public boolean isPeer(NameScope nameScope) {
			if (nameScope instanceof BioNameScope) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static ReservedSymbol getReservedSymbolEntry(String symbolName) {
		ReservedSymbol ste = getSymbolTableEntries().get(symbolName);
		return ste;
	}
	
	public static SymbolTableFunctionEntry getFunctionDefinitionEntry(String symbolName) {
		SymbolTableFunctionEntry ste = getSymbolTableFunctionEntries().get(symbolName);
		return ste;
	}
	
	public static SymbolTableEntry getEntry(String symbolName) {
		SymbolTableEntry ste = getSymbolTableEntries().get(symbolName);
		if (ste!=null){
			return ste;
		}
		SymbolTableFunctionEntry steFunction = getSymbolTableFunctionEntries().get(symbolName);
		if (steFunction!=null){
			return steFunction;
		}
		return null;
	}

	public static void getAll(Map<String, SymbolTableEntry> entryMap) {
		entryMap.putAll(getSymbolTableEntries());
		entryMap.putAll(getSymbolTableFunctionEntries());
	}

	private static HashMap<String,ReservedSymbol> getSymbolTableEntries(){
		if (symbolTableEntries==null){
			symbolTableEntries = new HashMap<String, ReservedSymbol>();
			symbolTableEntries.put(ReservedSymbol.FARADAY_CONSTANT.getName(),ReservedSymbol.FARADAY_CONSTANT);
			symbolTableEntries.put(ReservedSymbol.FARADAY_CONSTANT_NMOLE.getName(),ReservedSymbol.FARADAY_CONSTANT_NMOLE);
			symbolTableEntries.put(ReservedSymbol.GAS_CONSTANT.getName(),ReservedSymbol.GAS_CONSTANT);
			symbolTableEntries.put(ReservedSymbol.K_GHK.getName(),ReservedSymbol.K_GHK);
			symbolTableEntries.put(ReservedSymbol.KMILLIVOLTS.getName(),ReservedSymbol.KMILLIVOLTS);
			symbolTableEntries.put(ReservedSymbol.KMOLE.getName(),ReservedSymbol.KMOLE);
			symbolTableEntries.put(ReservedSymbol.N_PMOLE.getName(),ReservedSymbol.N_PMOLE);
			symbolTableEntries.put(ReservedSymbol.TEMPERATURE.getName(),ReservedSymbol.TEMPERATURE);
			symbolTableEntries.put(ReservedSymbol.TIME.getName(),ReservedSymbol.TIME);
			symbolTableEntries.put(ReservedSymbol.X.getName(),ReservedSymbol.X);
			symbolTableEntries.put(ReservedSymbol.Y.getName(),ReservedSymbol.Y);
			symbolTableEntries.put(ReservedSymbol.Z.getName(),ReservedSymbol.Z);
		}
		return symbolTableEntries;
	}
	
	private static HashMap<String,SymbolTableFunctionEntry> getSymbolTableFunctionEntries(){
		if (symbolTableFunctionEntries==null){
			symbolTableFunctionEntries = new HashMap<String, SymbolTableFunctionEntry>();
			symbolTableFunctionEntries.put(FieldFunctionDefinition.fieldFunctionDefinition.getName(),FieldFunctionDefinition.fieldFunctionDefinition);
		}
		return symbolTableFunctionEntries;
	}
	
	

}
