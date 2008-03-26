package cbit.vcell.matrix;
import java.util.Vector;
/**
 * Insert the type's description here.
 * Creation date: (3/27/2003 12:16:31 PM)
 * @author: Jim Schaff
 */
public class RationalExp implements java.io.Serializable {

	private Vector<Term> numTermList = new Vector<Term>();
	private Vector<Term> denTermList = new Vector<Term>();

	private static class Term implements java.io.Serializable {
		private long coefficient = 1;
		private Vector<String> symbolList = new Vector<String>();

		public Term(long coeff){
			coefficient = coeff;
		}
		public Term(long coeff, String symbols[]){
			coefficient = coeff;
			for (int i=0;symbols!=null && i<symbols.length;i++){
				symbolList.add(symbols[i]);
			} 
		}
		public Term(String symbol){
			coefficient = 1;
			symbolList.add(symbol);
		}
		public Term(Term term){
			coefficient = term.coefficient;
			symbolList = (Vector<String>)term.symbolList.clone();
		}
		public boolean hasSymbol(String symbol){
			return symbolList.contains(symbol);
		}
		public boolean isConstant(){
			return symbolList.size()==0;
		}
		public boolean removeSymbol(String symbol){
			return symbolList.remove(symbol);
		}
		public boolean isSummable(Term term){
			if (symbolList.size()!=term.symbolList.size()){
				return false;
			}
			//
			// both terms are just integers
			//
			if (symbolList.size()==0){
				return true;
			}
			//
			// same number of symbols, compare symbols by removing like elements from copy
			//
			Vector<String> newVector = (Vector<String>)symbolList.clone();
			for (int i=0;i<term.symbolList.size();i++){
				if (!newVector.remove(term.symbolList.elementAt(i))){
					// symbol not found in 'this'
					return false;
				}
			}
			if (newVector.size()>0){
				// symbol not found in 'term'
				return false;
			}
			return true;
		}
		public long getCoefficient(){
			return coefficient;
		}
		public String[] getSymbols(){
			if (symbolList==null || symbolList.size()==0){
				return new String[0];
			}else{
				String symbols[] = new String[symbolList.size()];
				for (int i = 0; i < symbols.length; i++){
					symbols[i] = (String)symbolList.elementAt(i);
				}
				return symbols;
			}
		}
		public Term mult(Term term1, Term term2){
			Term newTerm = new Term(1);
			newTerm.coefficient = term1.coefficient * term2.coefficient;
			for (int i=0;i<term1.symbolList.size();i++){
				newTerm.symbolList.add(term1.symbolList.elementAt(i));
			}
			for (int i=0;i<term2.symbolList.size();i++){
				newTerm.symbolList.add(term2.symbolList.elementAt(i));
			}
			return newTerm;
		}
		public String infixString(){
			if (symbolList.size()==0){
				return String.valueOf(coefficient);
			}else{
				StringBuffer buffer = new StringBuffer();
				for (int i=0;i<symbolList.size();i++){
					if (i>0){
						buffer.append("*");
					}
					buffer.append((String)symbolList.elementAt(i));
				}
				if (coefficient == 1){
					return buffer.toString();
				}else if (coefficient == -1){
					return "-"+buffer.toString();
				}else{
					return coefficient+"*"+buffer.toString();
				}
			}
		}
		public String toString() {
			return "Term@"+Integer.toHexString(hashCode())+" "+infixString();
		}
	}

/**
 * Insert the method's description here.
 * Creation date: (3/28/2003 5:43:11 PM)
 */
public RationalExp(long num) {
	numTermList.add(new Term(num));
	denTermList.add(new Term(1));
}


/**
 * Insert the method's description here.
 * Creation date: (3/28/2003 5:43:11 PM)
 */
public RationalExp(long num, long den) {
	numTermList.add(new Term(num));
	denTermList.add(new Term(den));
}


/**
 * Insert the method's description here.
 * Creation date: (3/28/2003 5:43:11 PM)
 */
public RationalExp(String symbol) {
	//if (!cbit.util.TokenMangler.fixTokenStrict(symbol,0).equals(symbol)){
		//throw new IllegalArgumentException("symbol '"+symbol+"' invalid");
	//}
	numTermList.add(new Term(symbol));
	denTermList.add(new Term(1));
}


/**
 * Insert the method's description here.
 * Creation date: (3/30/2003 1:49:18 PM)
 * @param numList java.util.Vector
 * @param denList java.util.Vector
 */
public RationalExp(RationalExp rationalExp){
	this.numTermList = new Vector<Term>(rationalExp.numTermList.size());
	for (int i = 0; i < rationalExp.numTermList.size(); i++) {
		numTermList.add(new RationalExp.Term(rationalExp.numTermList.get(i)));
	}
	this.denTermList = new Vector<Term>(rationalExp.denTermList.size());
	for (int i = 0; i < rationalExp.denTermList.size(); i++){
		denTermList.add(new RationalExp.Term(rationalExp.denTermList.get(i)));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/30/2003 1:49:18 PM)
 * @param numList java.util.Vector
 * @param denList java.util.Vector
 */
private RationalExp(Vector<Term> argNumTermList, Vector<Term> argDenTermList){
	if (argNumTermList==null || argNumTermList.size()<1){
		throw new IllegalArgumentException("must have at least 1 numerator term");
	}
	if (argDenTermList==null || argDenTermList.size()<1){
		throw new IllegalArgumentException("must have at least 1 denominator term");
	}
	this.numTermList = argNumTermList;
	this.denTermList = argDenTermList;
	refactor();
}


/**
 * Insert the method's description here.
 * Creation date: (3/27/2003 12:50:27 PM)
 * @return cbit.vcell.mapping.RationalNumber
 * @param num cbit.vcell.mapping.RationalNumber
 */
public RationalExp add(RationalExp rational) {
	if (isZero()){
		return rational;
	}else{
		if (rational.isZero()){
			return this;
		}else{
			//
			// get the common denominator by cross-multiply and add
			//
			Vector<Term> newNumTermList = addTerms(multiplyTerms(this.numTermList,rational.denTermList),multiplyTerms(this.denTermList,rational.numTermList));
			Vector<Term> newDenTermList = multiplyTerms(this.denTermList,rational.denTermList);

			RationalExp newRationalExp = new RationalExp(newNumTermList,newDenTermList);
			
			return newRationalExp;
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2003 10:15:41 AM)
 * @return java.util.Vector
 * @param vector1 java.util.Vector
 * @param vector2 java.util.Vector
 */
private Vector<Term> addTerms(Vector<Term> vector1, Vector<Term> vector2) {
	if (vector1 == null && vector2 == null){
		return null;
	}
	if (vector1 == null && vector2 != null){
		return (Vector<Term>)vector2.clone();
	}
	if (vector1 != null && vector2 == null){
		return (Vector<Term>)vector1.clone();
	}
	Vector<Term> newVector = (Vector<Term>)vector1.clone();
	newVector.addAll(vector2);
	collectTerms(newVector);

	return newVector;
}


/**
 * Insert the method's description here.
 * Creation date: (3/31/2003 10:32:20 AM)
 * @param vector java.util.Vector
 */
private void collectTerms(Vector<Term> vector) {
	//
	// collect terms
	//
	for (int i = 0; i < vector.size()-1; i++){
		int numEquivalentTerms = 1;
		Term term1 = (Term)vector.elementAt(i);
		long coeff = term1.getCoefficient();
		for (int j = i+1; j < vector.size(); j++){
			Term term2 = (Term)vector.elementAt(j);
			if (term1.isSummable(term2)){
				numEquivalentTerms++;
				coeff += term2.getCoefficient();
				vector.remove(term2);
				j--;
			}
		}
		if (numEquivalentTerms>1){
			if (coeff==0){
				vector.remove(term1);
				i--;
			}else{
				vector.set(i,new Term(coeff,term1.getSymbols()));
			}
		}
	}
	if (vector.size()==0){
		vector.add(new Term(0));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/27/2003 12:50:27 PM)
 * @return cbit.vcell.mapping.RationalNumber
 * @param num cbit.vcell.mapping.RationalNumber
 */
public RationalExp div(RationalExp rational) {
	if (rational.isZero()){
		throw new RuntimeException("divide by zero");
	}else if (isZero()){
		return new RationalExp(0);
	}else{
		Vector<Term> newNumTermList = multiplyTerms(this.numTermList,rational.denTermList);
		Vector<Term> newDenTermList = multiplyTerms(this.denTermList,rational.numTermList);

		RationalExp newRationalExp = new RationalExp(newNumTermList,newDenTermList);
		
		return newRationalExp;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/31/2003 2:57:06 PM)
 * @return boolean
 */
public RationalNumber getConstant() {
	if (numTermList.size()==1 && denTermList.size()==1){
		Term numTerm = (Term)numTermList.elementAt(0);
		Term denTerm = (Term)denTermList.elementAt(0);
		if (numTerm.isConstant() && denTerm.isConstant()){
			return new RationalNumber(numTerm.getCoefficient(),denTerm.getCoefficient());
		}
	}
	throw new RuntimeException("RationalExp.getConstant(): expression '"+infixString()+"' is not constant");
}


/**
 * Insert the method's description here.
 * Creation date: (3/28/2003 5:48:52 PM)
 * @return java.lang.String
 */
public String infixString() {
	String numStr = infixString(numTermList);
	String denStr = infixString(denTermList);
	if (denTermList.size()==1 && ((Term)denTermList.elementAt(0)).getCoefficient()==1 && ((Term)denTermList.elementAt(0)).getSymbols().length==0){
		// numerator only
		if (numTermList.size()>1){
			return '(' + numStr + ')';
		}else{
			return numStr;
		}
	}else{
		StringBuffer buffer = new StringBuffer();
		if (numTermList.size()>1){
			buffer.append('(');
			buffer.append(numStr);
			buffer.append(")/");
		}else{
			buffer.append(numStr);
			buffer.append('/');
		}
		
		if (denStr.indexOf("*") >= 0 || denTermList.size() > 1) {
			buffer.append('(');
			buffer.append(denStr);
			buffer.append(')');			
		} else {
			buffer.append(denStr);
		}
		return buffer.toString();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/28/2003 5:48:52 PM)
 * @return java.lang.String
 */
private String infixString(Vector<Term> termList) {
	StringBuffer buffer = new StringBuffer();
	for (int i = 0; i < termList.size(); i++){
		Term term = (Term)termList.elementAt(i);
		if (i>0 && term.getCoefficient()>=0){
			buffer.append('+');
		}
		buffer.append(term.infixString());
	}
	return buffer.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (3/30/2003 11:38:27 PM)
 * @return cbit.vcell.matrixtest.RationalExp
 */
public RationalExp inverse() {
	return new RationalExp(denTermList,numTermList);
}


/**
 * Insert the method's description here.
 * Creation date: (3/31/2003 2:57:06 PM)
 * @return boolean
 */
public boolean isConstant() {
	if (numTermList.size()==1 && denTermList.size()==1){
		Term numTerm = (Term)numTermList.elementAt(0);
		Term denTerm = (Term)denTermList.elementAt(0);
		if (numTerm.isConstant() && denTerm.isConstant()){
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (3/30/2003 11:42:59 PM)
 * @return boolean
 */
public boolean isZero() {
	return ((Term)numTermList.elementAt(0)).getCoefficient()==0;
}


/**
 * Insert the method's description here.
 * Creation date: (3/30/2003 11:52:52 PM)
 * @return cbit.vcell.matrixtest.RationalExp
 */
public RationalExp minus() {
	return new RationalExp(minusTerms(numTermList),denTermList);
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2003 10:15:41 AM)
 * @return java.util.Vector
 * @param vector1 java.util.Vector
 * @param vector2 java.util.Vector
 */
private Vector<Term> minusTerms(Vector<Term> vector) {
	Vector<Term> newVector = new Vector<Term>();
	
	for (int i = 0; i < vector.size(); i++){
		Term term = (Term)vector.elementAt(i);
		newVector.add(new Term(-term.getCoefficient(),term.getSymbols()));
	}
	return newVector;
}


private Term mult(Term term1, Term term2){
	Term newTerm = new Term(1);
	newTerm.coefficient = term1.coefficient * term2.coefficient;
	for (int i=0;i<term1.symbolList.size();i++){
		newTerm.symbolList.add(term1.symbolList.elementAt(i));
	}
	for (int i=0;i<term2.symbolList.size();i++){
		newTerm.symbolList.add(term2.symbolList.elementAt(i));
	}
	return newTerm;
}

/**
 * Insert the method's description here.
 * Creation date: (3/27/2003 12:50:27 PM)
 * @return cbit.vcell.mapping.RationalNumber
 * @param num cbit.vcell.mapping.RationalNumber
 */
public RationalExp mult(RationalExp rational) {
	if (isZero()){
		return new RationalExp(0);
	}else if (rational.isZero()){
		return new RationalExp(0);
	}else{
		Vector<Term> newNumTermList = multiplyTerms(this.numTermList,rational.numTermList);
		Vector<Term> newDenTermList = multiplyTerms(this.denTermList,rational.denTermList);

		RationalExp newRationalExp = new RationalExp(newNumTermList,newDenTermList);
		
		return newRationalExp;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2003 10:15:41 AM)
 * @return java.util.Vector
 * @param vector1 java.util.Vector
 * @param vector2 java.util.Vector
 */
private Vector<Term> multiplyTerms(Vector<Term> vector1, Vector<Term> vector2) {
	if (vector1 == null && vector2 == null){
		return null;
	}
	if (vector1 == null && vector2 != null){
		return (Vector<Term>)vector2.clone();
	}
	if (vector1 != null && vector2 == null){
		return (Vector<Term>)vector1.clone();
	}
	Vector<Term> newVector = new Vector<Term>();
	for (int i = 0; i < vector1.size(); i++){
		Term term1 = (Term)vector1.elementAt(i);
		for (int j = 0; j < vector2.size(); j++){
			Term term2 = (Term)vector2.elementAt(j);
			newVector.add(mult(term1,term2));
		}
	}
	collectTerms(newVector);
	return newVector;
}


/**
 * Insert the method's description here.
 * Creation date: (3/30/2003 1:52:50 PM)
 */
private void refactor() {
	//
	// for each symbol in the first term, see if it's in all terms, and remove it from all
	// collect all terms that are only integers
	// if only integers left, find greatest common factor and simplify
	//
	String symbols[] = ((Term)numTermList.elementAt(0)).getSymbols();
	for (int i = 0; i < symbols.length; i++){
		boolean bFoundInAllTerms = true;
		for (int j = 1;bFoundInAllTerms && j < numTermList.size(); j++){
			Term term = (Term)numTermList.elementAt(j);
			if (!term.hasSymbol(symbols[i])){
				bFoundInAllTerms = false;
			}
		}
		for (int j = 0;bFoundInAllTerms && j < denTermList.size(); j++){
			Term term = (Term)denTermList.elementAt(j);
			if (!term.hasSymbol(symbols[i])){
				bFoundInAllTerms = false;
			}
		}
		if (bFoundInAllTerms){
			for (int j = 0; j < numTermList.size(); j++){
				Term term = (Term)numTermList.elementAt(j);
				term.removeSymbol(symbols[i]);
			}
			for (int j = 0; j < denTermList.size(); j++){
				Term term = (Term)denTermList.elementAt(j);
				term.removeSymbol(symbols[i]);
			}
		}
	}
	//
	// collect all integer-only terms
	//
	// .... to do
	collectTerms(numTermList);
	collectTerms(denTermList);

	//
	// if simply coefficients (divide all terms by greatest common factor)
	//
	long gcf_numerator = ((Term)numTermList.elementAt(0)).getCoefficient();
	for (int i = 1; i < numTermList.size(); i++){
		gcf_numerator = RationalNumber.getGreatestCommonFactor(gcf_numerator,((Term)numTermList.elementAt(i)).getCoefficient());
	}
	long gcf_denominator = ((Term)denTermList.elementAt(0)).getCoefficient();
	for (int i = 1; i < denTermList.size(); i++){
		gcf_denominator = RationalNumber.getGreatestCommonFactor(gcf_denominator,((Term)denTermList.elementAt(i)).getCoefficient());
	}
	if (gcf_numerator>1 && gcf_denominator>1){
		long gcf = RationalNumber.getGreatestCommonFactor(gcf_numerator,gcf_denominator);
		if (gcf > 1){
			for (int i = 0; i < numTermList.size(); i++){
				Term term = (Term)numTermList.elementAt(i);
				term.coefficient /= gcf;
			}
			for (int i = 0; i < denTermList.size(); i++){
				Term term = (Term)denTermList.elementAt(i);
				term.coefficient /= gcf;
			}
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/22/2006 2:09:08 PM)
 */
public RationalExp simplify() {
	try {
		//
		// use symbolic capabilities of JSCL Mediator library to further simplify
		//
		cbit.vcell.parser.Expression exp = new cbit.vcell.parser.Expression(infixString());
		jscl.math.Expression jsclExpression = null;
		String jsclExpressionString = exp.infix_JSCL();
		try {
			jsclExpression = jscl.math.Expression.valueOf(jsclExpressionString);
		}catch (jscl.text.ParseException e){
			e.printStackTrace(System.out);
			System.out.println("JSCL couldn't parse \""+jsclExpressionString+"\"");
			return null;
		}
		cbit.vcell.parser.Expression solution = null;
		jscl.math.Generic jsclSolution = jsclExpression.expand().simplify();
		try {
			solution = new cbit.vcell.parser.Expression(jsclSolution.toString());
		}catch (Throwable e){
			e.printStackTrace(System.out);
		}
		if (solution!=null){
			String[] jsclSymbols = solution.getSymbols();
			for (int i = 0;jsclSymbols!=null && i < jsclSymbols.length; i++){
				String restoredSymbol = cbit.util.TokenMangler.getRestoredStringJSCL(jsclSymbols[i]);
				if (!restoredSymbol.equals(jsclSymbols[i])){
					solution.substituteInPlace(new cbit.vcell.parser.Expression(jsclSymbols[i]),new cbit.vcell.parser.Expression(restoredSymbol));
				}
			}
			return cbit.vcell.parser.RationalExpUtils.getRationalExp(solution);
		}else{
			return this;
		}
	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/27/2003 12:50:27 PM)
 * @return cbit.vcell.mapping.RationalNumber
 * @param num cbit.vcell.mapping.RationalNumber
 */
public RationalExp sub(RationalExp rational) {
	if (isZero()){
		if (rational.isZero()){
			return new RationalExp(0);
		}else{
			return rational.minus();
		}
	}else{
		if (rational.isZero()){
			return this;
		}else{
			//
			// get the common denominator by cross-multiply and subtract
			//
			Vector<Term> newNumTermList = addTerms(multiplyTerms(this.numTermList,rational.denTermList),minusTerms(multiplyTerms(this.denTermList,rational.numTermList)));
			Vector<Term> newDenTermList = multiplyTerms(this.denTermList,rational.denTermList);

			RationalExp newRationalExp = new RationalExp(newNumTermList,newDenTermList);
			
			return newRationalExp;
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/30/2003 10:36:03 AM)
 * @return java.lang.String
 */
public String toString() {
//	return "RationalExp@"+Integer.toHexString(hashCode())+" "+infixString();
	return infixString();
}
}