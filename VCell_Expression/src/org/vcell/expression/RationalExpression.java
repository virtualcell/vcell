package org.vcell.expression;
import java.util.Vector;



/**
 * Insert the type's description here.
 * Creation date: (3/27/2003 12:16:31 PM)
 * @author schaff
 * @version $Revision: 1.0 $
 */
public class RationalExpression implements java.io.Serializable {

	private Vector<Term> numTermList = new Vector<Term>();
	private Vector<Term> denTermList = new Vector<Term>();

	/**
	 */
	private class Term implements java.io.Serializable {
		private long coefficient = 1;
		private Vector<String> symbolList = new Vector<String>();

		/**
		 * Constructor for Term.
		 * @param coeff long
		 */
		public Term(long coeff){
			coefficient = coeff;
		}
		/**
		 * Constructor for Term.
		 * @param coeff long
		 * @param symbols String[]
		 */
		public Term(long coeff, String symbols[]){
			coefficient = coeff;
			for (int i=0;symbols!=null && i<symbols.length;i++){
				symbolList.add(symbols[i]);
			} 
		}
		/**
		 * Constructor for Term.
		 * @param symbol String
		 */
		public Term(String symbol){
			coefficient = 1;
			symbolList.add(symbol);
		}
		/**
		 * Constructor for Term.
		 * @param term Term
		 */
		public Term(Term term){
			coefficient = term.coefficient;
			symbolList = (Vector<String>)term.symbolList.clone();
		}
		/**
		 * Method hasSymbol.
		 * @param symbol String
		 * @return boolean
		 */
		public boolean hasSymbol(String symbol){
			return symbolList.contains(symbol);
		}
		/**
		 * Method isConstant.
		 * @return boolean
		 */
		public boolean isConstant(){
			return symbolList.size()==0;
		}
		/**
		 * Method removeSymbol.
		 * @param symbol String
		 * @return boolean
		 */
		public boolean removeSymbol(String symbol){
			return symbolList.remove(symbol);
		}
		/**
		 * Method isSummable.
		 * @param term Term
		 * @return boolean
		 */
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
		/**
		 * Method getCoefficient.
		 * @return long
		 */
		public long getCoefficient(){
			return coefficient;
		}
		/**
		 * Method getSymbols.
		 * @return String[]
		 */
		public String[] getSymbols(){
			if (symbolList==null || symbolList.size()==0){
				return new String[0];
			}else{
				String symbols[] = new String[symbolList.size()];
				for (int i = 0; i < symbols.length; i++){
					symbols[i] = symbolList.elementAt(i);
				}
				return symbols;
			}
		}
		/**
		 * Method mult.
		 * @param term1 Term
		 * @param term2 Term
		 * @return Term
		 */
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
		/**
		 * Method infixString.
		 * @return String
		 */
		public String infixString(){
			if (symbolList.size()==0){
				return String.valueOf(coefficient);
			}else{
				StringBuffer buffer = new StringBuffer();
				for (int i=0;i<symbolList.size();i++){
					if (i>0){
						buffer.append('*');
					}
					buffer.append(symbolList.elementAt(i));
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
		/**
		 * Method toString.
		 * @return String
		 */
		public String toString() {
			return "Term@"+Integer.toHexString(hashCode())+" "+infixString();
		}
	}

/**
 * Insert the method's description here.
 * Creation date: (3/28/2003 5:43:11 PM)
 * @param num long
 */
public RationalExpression(long num) {
	numTermList.add(new Term(num));
	denTermList.add(new Term(1));
}


/**
 * Insert the method's description here.
 * Creation date: (3/28/2003 5:43:11 PM)
 * @param num long
 * @param den long
 */
public RationalExpression(long num, long den) {
	numTermList.add(new Term(num));
	denTermList.add(new Term(den));
}


/**
 * Insert the method's description here.
 * Creation date: (3/28/2003 5:43:11 PM)
 * @param symbol String
 */
public RationalExpression(String symbol) {
	//if (!cbit.util.TokenMangler.fixTokenStrict(symbol,0).equals(symbol)){
		//throw new IllegalArgumentException("symbol '"+symbol+"' invalid");
	//}
	numTermList.add(new Term(symbol));
	denTermList.add(new Term(1));
}


/**
 * Insert the method's description here.
 * Creation date: (3/30/2003 1:49:18 PM)
 * @param argNumTermList Vector<Term>
 * @param argDenTermList Vector<Term>
 */
private RationalExpression(Vector<Term> argNumTermList, Vector<Term> argDenTermList){
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


/* (non-Javadoc)
 * @see org.vcell.expression.IRationalExpression#add(org.vcell.expression.RationalExp)
 */
/**
 * Method add.
 * @param rationalExpression IRationalExpression
 * @return RationalExp
 * @see org.vcell.expression.IRationalExpression#add(IRationalExpression)
 */
public RationalExpression add(RationalExpression rationalExpression) {
	RationalExpression rational = (RationalExpression)rationalExpression;
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

			RationalExpression newRationalExp = new RationalExpression(newNumTermList,newDenTermList);
			
			return newRationalExp;
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2003 10:15:41 AM)
 * @param vector1 java.util.Vector
 * @param vector2 java.util.Vector
 * @return java.util.Vector
 */
private Vector<Term> addTerms(Vector<Term> vector1, Vector<Term> vector2) {
	if (vector1 == null && vector2 == null){
		return null;
	}
	if (vector1 == null && vector2 != null){
		return (Vector<Term>) vector2.clone();
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
		Term term1 = vector.elementAt(i);
		long coeff = term1.getCoefficient();
		for (int j = i+1; j < vector.size(); j++){
			Term term2 = vector.elementAt(j);
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


/* (non-Javadoc)
 * @see org.vcell.expression.IRationalExpression#div(org.vcell.expression.RationalExp)
 */
/**
 * Method div.
 * @param rationalExpression IRationalExpression
 * @return RationalExp
 * @see org.vcell.expression.IRationalExpression#div(IRationalExpression)
 */
public RationalExpression div(RationalExpression rationalExpression) {
	RationalExpression rational = (RationalExpression)rationalExpression;
	if (rational.isZero()){
		throw new RuntimeException("divide by zero");
	}else if (isZero()){
		return new RationalExpression(0);
	}else{
		Vector<Term> newNumTermList = multiplyTerms(this.numTermList,rational.denTermList);
		Vector<Term> newDenTermList = multiplyTerms(this.denTermList,rational.numTermList);

		RationalExpression newRationalExp = new RationalExpression(newNumTermList,newDenTermList);
		
		return newRationalExp;
	}
}


/* (non-Javadoc)
 * @see org.vcell.expression.IRationalExpression#getConstant()
 */
public RationalNumber getConstant() {
	if (numTermList.size()==1 && denTermList.size()==1){
		Term numTerm = numTermList.elementAt(0);
		Term denTerm = denTermList.elementAt(0);
		if (numTerm.isConstant() && denTerm.isConstant()){
			return new RationalNumber(numTerm.getCoefficient(),denTerm.getCoefficient());
		}
	}
	throw new RuntimeException("RationalExp.getConstant(): expression '"+infixString()+"' is not constant");
}


/* (non-Javadoc)
 * @see org.vcell.expression.IRationalExpression#infixString()
 */
public String infixString() {
	if (denTermList.size()==1 && denTermList.elementAt(0).getCoefficient()==1 && denTermList.elementAt(0).getSymbols().length==0){
		// numerator only
		if (numTermList.size()>1){
			return '(' + infixString(numTermList) + ')';
		}else{
			return infixString(numTermList);
		}
	}else{
		StringBuffer buffer = new StringBuffer();
		if (numTermList.size()>1){
			buffer.append('(');
			buffer.append(infixString(numTermList));
			buffer.append(")/");
		}else{
			buffer.append(infixString(numTermList));
			buffer.append('/');
		}
		if (denTermList.size()>1){
			buffer.append('(');
			buffer.append(infixString(denTermList));
			buffer.append(')');
		}else{
			buffer.append(infixString(denTermList));
		}
		return buffer.toString();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/28/2003 5:48:52 PM)
 * @param termList Vector<Term>
 * @return java.lang.String
 */
private String infixString(Vector<Term> termList) {
	StringBuffer buffer = new StringBuffer();
	for (int i = 0; i < termList.size(); i++){
		Term term = termList.elementAt(i);
		if (i>0 && term.getCoefficient()>=0){
			buffer.append('+');
		}
		buffer.append(term.infixString());
	}
	return buffer.toString();
}


/* (non-Javadoc)
 * @see org.vcell.expression.IRationalExpression#inverse()
 */
public RationalExpression inverse() {
	return new RationalExpression(denTermList,numTermList);
}


/* (non-Javadoc)
 * @see org.vcell.expression.IRationalExpression#isConstant()
 */
public boolean isConstant() {
	if (numTermList.size()==1 && denTermList.size()==1){
		Term numTerm = numTermList.elementAt(0);
		Term denTerm = denTermList.elementAt(0);
		if (numTerm.isConstant() && denTerm.isConstant()){
			return true;
		}
	}
	return false;
}


/* (non-Javadoc)
 * @see org.vcell.expression.IRationalExpression#isZero()
 */
public boolean isZero() {
	return numTermList.elementAt(0).getCoefficient()==0;
}


/* (non-Javadoc)
 * @see org.vcell.expression.IRationalExpression#minus()
 */
public RationalExpression minus() {
	return new RationalExpression(minusTerms(numTermList),denTermList);
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2003 10:15:41 AM)
 * @param vector Vector<Term>
 * @return java.util.Vector
 */
private Vector<Term> minusTerms(Vector<Term> vector) {
	Vector<Term> newVector = new Vector<Term>();
	
	for (int i = 0; i < vector.size(); i++){
		Term term = vector.elementAt(i);
		newVector.add(new Term(-term.getCoefficient(),term.getSymbols()));
	}
	return newVector;
}


/**
 * Method mult.
 * @param term1 Term
 * @param term2 Term
 * @return Term
 */
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

/* (non-Javadoc)
 * @see org.vcell.expression.IRationalExpression#mult(org.vcell.expression.RationalExp)
 */
/**
 * Method mult.
 * @param rationalExpression IRationalExpression
 * @return RationalExp
 * @see org.vcell.expression.IRationalExpression#mult(IRationalExpression)
 */
public RationalExpression mult(RationalExpression rationalExpression) {
	RationalExpression rational = (RationalExpression)rationalExpression;
	if (isZero()){
		return new RationalExpression(0);
	}else if (rational.isZero()){
		return new RationalExpression(0);
	}else{
		Vector<Term> newNumTermList = multiplyTerms(this.numTermList,rational.numTermList);
		Vector<Term> newDenTermList = multiplyTerms(this.denTermList,rational.denTermList);

		RationalExpression newRationalExp = new RationalExpression(newNumTermList,newDenTermList);
		
		return newRationalExp;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2003 10:15:41 AM)
 * @param vector1 java.util.Vector
 * @param vector2 java.util.Vector
 * @return java.util.Vector
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
		Term term1 = vector1.elementAt(i);
		for (int j = 0; j < vector2.size(); j++){
			Term term2 = vector2.elementAt(j);
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
	String symbols[] = numTermList.elementAt(0).getSymbols();
	for (int i = 0; i < symbols.length; i++){
		boolean bFoundInAllTerms = true;
		for (int j = 1;bFoundInAllTerms && j < numTermList.size(); j++){
			Term term = numTermList.elementAt(j);
			if (!term.hasSymbol(symbols[i])){
				bFoundInAllTerms = false;
			}
		}
		for (int j = 0;bFoundInAllTerms && j < denTermList.size(); j++){
			Term term = denTermList.elementAt(j);
			if (!term.hasSymbol(symbols[i])){
				bFoundInAllTerms = false;
			}
		}
		if (bFoundInAllTerms){
			for (int j = 0; j < numTermList.size(); j++){
				Term term = numTermList.elementAt(j);
				term.removeSymbol(symbols[i]);
			}
			for (int j = 0; j < denTermList.size(); j++){
				Term term = denTermList.elementAt(j);
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
	long gcf_numerator = numTermList.elementAt(0).getCoefficient();
	for (int i = 1; i < numTermList.size(); i++){
		gcf_numerator = RationalNumber.getGreatestCommonFactor(gcf_numerator,numTermList.elementAt(i).getCoefficient());
	}
	long gcf_denominator = denTermList.elementAt(0).getCoefficient();
	for (int i = 1; i < denTermList.size(); i++){
		gcf_denominator = RationalNumber.getGreatestCommonFactor(gcf_denominator,denTermList.elementAt(i).getCoefficient());
	}
	if (gcf_numerator>1 && gcf_denominator>1){
		long gcf = RationalNumber.getGreatestCommonFactor(gcf_numerator,gcf_denominator);
		if (gcf > 1){
			for (int i = 0; i < numTermList.size(); i++){
				Term term = numTermList.elementAt(i);
				term.coefficient /= gcf;
			}
			for (int i = 0; i < denTermList.size(); i++){
				Term term = denTermList.elementAt(i);
				term.coefficient /= gcf;
			}
		}
	}
}



/* (non-Javadoc)
 * @see org.vcell.expression.IRationalExpression#sub(org.vcell.expression.RationalExp)
 */
/**
 * Method sub.
 * @param rationalExpression IRationalExpression
 * @return RationalExp
 * @see org.vcell.expression.IRationalExpression#sub(IRationalExpression)
 */
public RationalExpression sub(RationalExpression rationalExpression) {
	RationalExpression rational = (RationalExpression)rationalExpression;
	if (isZero()){
		if (rational.isZero()){
			return new RationalExpression(0);
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

			RationalExpression newRationalExp = new RationalExpression(newNumTermList,newDenTermList);
			
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