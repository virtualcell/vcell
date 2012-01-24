package cbit.vcell.math;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;

public final class PostProcessingBlock implements SymbolTable, Serializable {
	/**
	 * 
	 */
	private final MathDescription mathDescription;
	private ArrayList<DataGenerator> dataGeneratorList = new ArrayList<DataGenerator>();
	private HashMap<String, DataGenerator> dataGeneratorHashMap = new HashMap<String, DataGenerator>();

	public PostProcessingBlock(MathDescription mathDescription){
		this.mathDescription = mathDescription;
		
	}
	public MathDescription getMathDescription(){
		return this.mathDescription;
	}
	public Variable getDataGenerator(String name){
		return dataGeneratorHashMap.get(name);
	}
	public void addDataGenerator(DataGenerator dataGenerator) throws MathException{
		if (getDataGenerator(dataGenerator.getName()) != null || this.mathDescription.getVariable(dataGenerator.getName()) != null){
			throw new MathException("dataGenerator or variable '"+dataGenerator.getName()+"' already exists");
		}
		dataGeneratorList.add(dataGenerator);
		dataGeneratorHashMap.put(dataGenerator.getName(), dataGenerator);
	}

	public SymbolTableEntry getEntry(String id) throws ExpressionBindingException {
		SymbolTableEntry entry = null;
		
		entry = ReservedMathSymbolEntries.getEntry(id,true);
		if (entry != null){
			return entry;
		}

		entry = getMathDescription().getEntry(id);
		if (entry != null){
			return entry;
		}
		
		entry = dataGeneratorHashMap.get(id);
		if (entry != null){
			return entry;
		}
		
		return null;
	}
	
	public void getEntries(Map<String, SymbolTableEntry> entryMap) {
		ReservedMathSymbolEntries.getAll(entryMap,true);
		getMathDescription().getEntries(entryMap);
		for (DataGenerator dataGenerator : dataGeneratorList) {
			entryMap.put(dataGenerator.getName(), dataGenerator);
		}
	}
	
	public void rebind() throws ExpressionBindingException {
		for (DataGenerator dataGenerator : dataGeneratorList){
			dataGenerator.bind(this);
		}
	}
	
}