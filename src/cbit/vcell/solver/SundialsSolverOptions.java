package cbit.vcell.solver;

import java.io.Serializable;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.Matchable;

import cbit.vcell.math.VCML;

@SuppressWarnings("serial")
public class SundialsSolverOptions implements Matchable, Serializable {
	private int maxOrder = 5;
	
	public SundialsSolverOptions() {
		
	}
	public SundialsSolverOptions(int order) {
		maxOrder = order;
	}
	public SundialsSolverOptions(SundialsSolverOptions sundialsSolverOptions) {
		maxOrder = sundialsSolverOptions.maxOrder;
	}
	public SundialsSolverOptions(CommentStringTokenizer tokens) throws DataAccessException {
		this();
		readVCML(tokens);
	}
	
	public boolean compareEqual(Matchable obj) {
		if (!(obj instanceof SundialsSolverOptions)) {
			return false;
		}
		SundialsSolverOptions sundialsSolverOptions = (SundialsSolverOptions)obj;
		return maxOrder == sundialsSolverOptions.maxOrder;
	}
	
	public String getVCML() {		
		StringBuffer buffer = new StringBuffer();
		buffer.append("\t" + VCML.SundialsSolverOptions + " " + VCML.BeginBlock + "\n");
		buffer.append("\t\t" + VCML.SundialsSolverOptions_maxOrder + " " + maxOrder + "\n");
		buffer.append("\t" + VCML.EndBlock + "\n");
		
		return buffer.toString();
	}
	
	private void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
		String token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.SundialsSolverOptions)) {
			token = tokens.nextToken();
			if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
				throw new DataAccessException("unexpected token " + token + " expecting " + VCML.BeginBlock); 
			}
		}
		
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)) {
				break;
			}
			if (token.equalsIgnoreCase(VCML.SundialsSolverOptions_maxOrder)) {
				token = tokens.nextToken();
				maxOrder = Integer.parseInt(token);
			}  else { 
				throw new DataAccessException("unexpected identifier " + token);
			}
		}
	}
	public final int getMaxOrder() {
		return maxOrder;
	}
}
