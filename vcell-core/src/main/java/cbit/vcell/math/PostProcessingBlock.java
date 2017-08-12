package cbit.vcell.math;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.math.Variable.Domain;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;

public final class PostProcessingBlock implements SymbolTable, Serializable, Matchable {
	/**
	 * 
	 */
	private final MathDescription mathDescription;
	private ArrayList<DataGenerator> dataGeneratorList = new ArrayList<DataGenerator>();
	private HashMap<String, DataGenerator> dataGeneratorHashMap = new HashMap<String, DataGenerator>();

	public PostProcessingBlock(MathDescription mathDescription){
		this.mathDescription = mathDescription;
	}
		
	public void read(CommentStringTokenizer tokens) throws ExpressionException, MathException {
		String token = null;
		token = tokens.nextToken();
		if (!token.equalsIgnoreCase(VCML.BeginBlock)){
			throw new MathFormatException("unexpected token "+token+" expecting "+VCML.BeginBlock);
		}			
		while (tokens.hasMoreTokens()){
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)){
				break;
			}
			if (token.equalsIgnoreCase(VCML.ExplicitDataGenerator)){
				token = tokens.nextToken();
				Domain domain = Variable.getDomainFromCombinedIdentifier(token);
				String name = Variable.getNameFromCombinedIdentifier(token);
				Expression exp = new Expression(tokens.readToSemicolon());
				DataGenerator dataGenerator = new ExplicitDataGenerator(name, domain, exp);
				addDataGenerator(dataGenerator);
				continue;
			}
			if (token.equalsIgnoreCase(VCML.ProjectionDataGenerator)){
				token = tokens.nextToken();
				Domain domain = Variable.getDomainFromCombinedIdentifier(token);
				String name = Variable.getNameFromCombinedIdentifier(token);
				DataGenerator dataGenerator = new ProjectionDataGenerator(name, domain, tokens);
				addDataGenerator(dataGenerator);
				continue;
			}
			if (token.equalsIgnoreCase(VCML.ConvolutionDataGenerator)){
				token = tokens.nextToken();
				DataGenerator dataGenerator = new ConvolutionDataGenerator(token, tokens);
				addDataGenerator(dataGenerator);
				continue;
			}
		}
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

	public SymbolTableEntry getEntry(String id) {
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

	public String getVCML() throws MathException {
		StringBuilder buffer = new StringBuilder();
		buffer.append(VCML.PostProcessingBlock+" " + VCML.BeginBlock + "\n");
		for (DataGenerator dataGenerator : dataGeneratorList) {
			buffer.append("\t" + dataGenerator.getVCML() + "");
		}
		buffer.append(VCML.EndBlock + "\n");
		return buffer.toString();

	}

	public boolean compareEqual(Matchable obj) {
		if (obj instanceof PostProcessingBlock) {
			return Compare.isEqualOrNull(dataGeneratorList.toArray(new DataGenerator[0]), ((PostProcessingBlock)obj).dataGeneratorList.toArray(new DataGenerator[0]));
		}
		return false;
	}

	public final List<DataGenerator> getDataGeneratorList() {
		return dataGeneratorList;
	}
	
	public final int getNumDataGenerators() {
		return dataGeneratorList.size();
	}
}