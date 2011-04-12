package cbit.vcell.client.desktop.biomodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.GuiUtils;

import cbit.gui.AutoCompleteSymbolFilter;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.parser.ASTFuncNode;
import cbit.vcell.parser.ASTFuncNode.PredefinedSymbolTableFunctionEntry;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;

@SuppressWarnings("serial")
public class PredefinedSymbolsTableModel extends BioModelEditorRightSideTableModel<SymbolTableEntry> {
	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_DESCRIPTION = 1;
	public final static int COLUMN_EXPRESSION = 2;
	public final static int COLUMN_UNIT = 3;
	
	private static String[] columnNames = new String[] {"Name", "Description", "Expression", "Unit"};
	
	public PredefinedSymbolsTableModel(EditorScrollTable table) {
		super(table);
		setColumns(columnNames);
		setData(computeData());		
	}
	
	private String getDescription(SymbolTableEntry symbolTableEntry) {
		if (symbolTableEntry instanceof ReservedSymbol) {
			return ((ReservedSymbol) symbolTableEntry).getDescription();
		} 
		if (symbolTableEntry instanceof PredefinedSymbolTableFunctionEntry) {
			return ((PredefinedSymbolTableFunctionEntry) symbolTableEntry).getDescription();
		}
		return null;
	}
	private String getExpression(SymbolTableEntry symbolTableEntry) {
		if (symbolTableEntry instanceof ReservedSymbol) {
			Expression expression = ((ReservedSymbol) symbolTableEntry).getExpression();
			if (expression != null) {
				return expression.infix();
			}
		} 
		return null;
	}
	private String getName(SymbolTableEntry symbolTableEntry) {
		if (symbolTableEntry instanceof ReservedSymbol) {
			return ((ReservedSymbol) symbolTableEntry).getName();
		} 
		if (symbolTableEntry instanceof PredefinedSymbolTableFunctionEntry) {
			return ((PredefinedSymbolTableFunctionEntry) symbolTableEntry).getFunctionDeclaration();
		}
		return null;
	}
	@Override
	protected List<SymbolTableEntry> computeData() {
		List<SymbolTableEntry> predefinedSymbolList = new ArrayList<SymbolTableEntry>();
		predefinedSymbolList.addAll(Arrays.asList(ReservedSymbol.ALL_RESERVED_SYMBOLS));
		predefinedSymbolList.addAll(Arrays.asList(ASTFuncNode.predefinedSymbolTableFunctionEntries));
		
		List<SymbolTableEntry> searchList = null;
		if (searchText == null || searchText.length() == 0) {
			searchList = predefinedSymbolList;
		} else {
			String lowerCaseSearchText = searchText.toLowerCase();
			searchList = new ArrayList<SymbolTableEntry>();
			for (SymbolTableEntry ste: predefinedSymbolList) {					
				String expression = getExpression(ste);
				if (getName(ste).toLowerCase().contains(lowerCaseSearchText)
					|| getDescription(ste).toLowerCase().contains(lowerCaseSearchText)
					|| expression != null && expression.toLowerCase().contains(lowerCaseSearchText)
					|| ste.getUnitDefinition() != null && ste.getUnitDefinition().getSymbol().toLowerCase().contains(lowerCaseSearchText)) {					
					searchList.add(ste);
				}				
			}
		}
		return searchList;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		SymbolTableEntry ste = getValueAt(rowIndex);
		switch(columnIndex) {
		case COLUMN_NAME:
			return getName(ste);
		case COLUMN_DESCRIPTION:
			return getDescription(ste);
		case COLUMN_EXPRESSION:
			return getExpression(ste);
		case COLUMN_UNIT:
			if (ste.getUnitDefinition() != null) {
				return ste.getUnitDefinition().getSymbol();
			}
			return null;
		}
		return null;
	}

	@Override
	protected Comparator<SymbolTableEntry> getComparator(final int col, final boolean ascending) {
		return new Comparator<SymbolTableEntry>() {

			public int compare(SymbolTableEntry o1, SymbolTableEntry o2) {
				int scale = ascending ? 1 : -1;
				switch (col){
				case COLUMN_NAME:
					return scale * getName(o1).compareToIgnoreCase(getName(o2));
				case COLUMN_DESCRIPTION:
					return scale * getDescription(o1).compareToIgnoreCase(getDescription(o2));
				case COLUMN_EXPRESSION:
					String exp1 = getExpression(o1);
					String exp2 = getExpression(o2);
					if (exp1 == null) {
						exp1 = "";
					}
					if (exp2 == null) {
						exp2 = "";
					}
					return scale * exp1.compareToIgnoreCase(exp2);
				case COLUMN_UNIT:
					String u1 = o1.getUnitDefinition() == null ? "" : o1.getUnitDefinition().getSymbol();
					String u2 = o2.getUnitDefinition() == null ? "" : o2.getUnitDefinition().getSymbol();
					return scale * u1.compareToIgnoreCase(u2);
				}
				return 0;
			}
		};
	}

	public String checkInputValue(String inputValue, int row, int column) {
		return null;
	}

	public SymbolTable getSymbolTable(int row, int column) {
		return null;
	}

	public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(int row, int column) {
		return null;
	}

	public Set<String> getAutoCompletionWords(int row, int column) {
		return null;
	}
}
