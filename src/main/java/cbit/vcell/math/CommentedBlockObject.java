package cbit.vcell.math;

import java.util.Objects;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Matchable;
import org.vcell.util.Token;
import org.vcell.util.VCAssert;

import cbit.vcell.parser.ExpressionException;

/**
 * Commented object whose VCML representation is of block format
 * @author gweatherby
 *
 */
@SuppressWarnings("serial")
public abstract class CommentedBlockObject extends CommentedObject {
	
	private final String name;
	
	protected CommentedBlockObject(String name) {
		super();
		this.name = name;
	}
	
	public final String getName(){
		return this.name;
	}
	
	/**
	 * semantic equality
	 */
	@Override
	public boolean compareEqual(Matchable obj) {
		if (super.compareEqual(obj)) {
			VCAssert.ofType(obj, CommentedBlockObject.class);
			CommentedBlockObject cbo = (CommentedBlockObject) obj;
			return Objects.equals(name, cbo.name);
		}
		return false;
	}

	/**
	 * partially construct -- verify tag and set name
	 * @param leadToken
	 * @param tokens token stream source; should be set to read name next
	 */
	protected CommentedBlockObject(Token leadToken, CommentStringTokenizer tokens) {
		String rst = startToken();
		if (!leadToken.getValue().equalsIgnoreCase(rst)) {
			throw new IllegalArgumentException("Invalid start token " + leadToken + " for " + getClass().getName() + ", " + rst + " required");
		}
		setBeforeComment(leadToken.getBeforeComment());
		name = tokens.nextToken();
	}
	
	/**
	 * parse out VCML block after name is set
	 * @param mathdesc parent
	 * @param tokens token stream source; should be set to read beginning of block 
	 * @throws MathException
	 * @throws ExpressionException
	 */
	protected void parseBlock(MathDescription mathdesc, CommentStringTokenizer tokens) throws MathException, ExpressionException {
		
		String beginBlockString= tokens.nextToken();		if (!beginBlockString.equalsIgnoreCase(VCML.BeginBlock)){			throw new MathFormatException("unexpected token "+beginBlockString+" expecting "+VCML.BeginBlock);		}					while (tokens.hasMoreTokens()){			Token token  = tokens.next();			String tokenString = token.getValue(); 			if (tokenString.equalsIgnoreCase(VCML.EndBlock)){				setAfterComment(token.getAfterComment());				return;			}			
			parse(mathdesc,tokenString, tokens);		}			}
	
	/**
	 * process one or more tokens
	 * @param mathdesc parent
	 * @param tokenString current token
	 * @param tokens stream of tokens
	 */
	protected abstract void parse(MathDescription mathdesc, String tokenString, CommentStringTokenizer tokens) throws MathException, ExpressionException;
	
	/**
	 * required start token
	 * @return
	 */
	protected abstract String startToken( );
}