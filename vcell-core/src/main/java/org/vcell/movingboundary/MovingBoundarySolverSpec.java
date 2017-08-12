package org.vcell.movingboundary;

import java.io.Serializable;

import org.vcell.util.BeanUtils;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.Matchable;

import cbit.vcell.math.VCML;


@SuppressWarnings("serial")
public class MovingBoundarySolverSpec implements Matchable, Serializable {
	private static final String TEXT_REPORT = "TextReport";

	private boolean textReport;
	
	public MovingBoundarySolverSpec( ) {
		textReport = false;
	}
	
	public MovingBoundarySolverSpec(CommentStringTokenizer tokenizer) throws DataAccessException {
		readVCML(tokenizer);
	}	
	
	public MovingBoundarySolverSpec(MovingBoundarySolverSpec mbss) {
		textReport = mbss.textReport;
	}
	
	public boolean isTextReport() {
		return textReport;
	}

	public void setTextReport(boolean textReport) {
		this.textReport = textReport;
	}


	
	
	public boolean compareEqual(Matchable object) {
		if (this == object) {
			return (true);
		}
		MovingBoundarySolverSpec other = BeanUtils.downcast(MovingBoundarySolverSpec.class, object);
		if (other == null) {
			return false;
		}
	
		return textReport == other.textReport; 
	}
	
	public String getVCML() {
		StringBuilder buffer = new StringBuilder();
		final char tab = '\t';
		final char space = ' ';
		final char newline = '\n';
		
		buffer.append(VCML.MovingBoundarySpec + space + VCML.BeginBlock + newline);
		buffer.append(tab + TEXT_REPORT + space + textReport + newline);
		buffer.append(VCML.EndBlock+"\n");
		return buffer.toString();
	}

	public void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
	
		String token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.MovingBoundarySpec)) {
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
			if (token.equalsIgnoreCase(TEXT_REPORT)) {
				textReport = Boolean.parseBoolean(tokens.nextToken());
			}
			else
			{
				throw new DataAccessException("unexpected token " + token);
			}
		}
	}
}
