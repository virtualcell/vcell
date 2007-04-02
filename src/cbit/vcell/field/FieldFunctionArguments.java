package cbit.vcell.field;

import java.util.StringTokenizer;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

public class FieldFunctionArguments {

	private String fieldName;
	private String variableName;
	private Expression time;
	public FieldFunctionArguments(String fieldName, String variableName, Expression time) {
		super();
		this.fieldName = fieldName;
		this.variableName = variableName;
		this.time = time;
	}
	
	public static FieldFunctionArguments fromTokens(StringTokenizer st) throws ExpressionException{
		return
			new FieldFunctionArguments(
					st.nextToken(),
					st.nextToken(),
					new Expression(st.nextToken()));
	}
	public String toCSVString(){
		return fieldName+","+variableName+","+time.infix();
	}
	public String getFieldName() {
		return fieldName;
	}
	public Expression getTime() {
		return time;
	}
	public String getVariableName() {
		return variableName;
	}
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = PRIME * result + ((time == null) ? 0 : time.hashCode());
		result = PRIME * result + ((variableName == null) ? 0 : variableName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FieldFunctionArguments other = (FieldFunctionArguments) obj;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (variableName == null) {
			if (other.variableName != null)
				return false;
		} else if (!variableName.equals(other.variableName))
			return false;
		return true;
	}
}
