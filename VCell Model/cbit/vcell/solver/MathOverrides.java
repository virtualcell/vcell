package cbit.vcell.solver;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.Enumeration;
import java.util.Hashtable;

import cbit.util.DataAccessException;
import cbit.vcell.parser.Expression;
import cbit.vcell.math.CommentStringTokenizer;
import cbit.vcell.math.Constant;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.VCML;
import cbit.vcell.parser.ExpressionBindingException;
/**
 * Constant expressions that override those specified in the MathDescription
 * for a given Simulation.  These expressions are to be bound to the Simulation
 * to which they belong.
 * Creation date: (8/17/2000 3:47:33 PM)
 * @author: John Wagner
 */
public class MathOverrides implements cbit.util.Matchable, java.io.Serializable {
	private Simulation simulation = null;
	//
	// key = constant name (String)
	// value = wrapper containing expression and flag(s) (MathOverrides.Element)
	//
	private java.util.Hashtable overridesHash = new java.util.Hashtable();
	protected transient cbit.vcell.solver.MathOverridesListener aMathOverridesListener = null;
	
	class Element implements java.io.Serializable, cbit.util.Matchable {
		private Expression actualValue;
		private String name;
		private ConstantArraySpec spec;
		private Element(String argName, Expression act) {
			// replace expressions with clones (so that they may be separately bound/resolved/evaluated).
			this.name = argName;
			Expression clonedExpression = new Expression(act);
			actualValue = clonedExpression;
		}
		private Element(String argName, ConstantArraySpec argSpec) {
			// replace expressions with clones (so that they may be separately bound/resolved/evaluated).
			this.name = argName;
			this.spec = ConstantArraySpec.clone(argSpec);
		}
		public boolean compareEqual(cbit.util.Matchable obj){
			if (obj instanceof MathOverrides.Element){
				MathOverrides.Element element = (MathOverrides.Element)obj;
				if (!cbit.util.Compare.isEqualOrNull(actualValue,element.actualValue) ||
					!cbit.util.Compare.isEqual(name,element.name) ||
					!cbit.util.Compare.isEqualOrNull(spec,element.spec)){
					return false;
				}
				return true;
			}
			return false;
		}
	}

/**
 * One of three ways to construct a MathOverrides.  This constructor
 * is used when creating a new MathOverrides.
 */
public MathOverrides(Simulation simulation) {
	setSimulation(simulation);
}


/**
 * One of three ways to construct a MathOverrides.  This constructor
 * is used when creating a new MathOverrides from XML (we read in Constants to be overriden from XML).
 */
public MathOverrides(Simulation simulation, Constant[] constants, ConstantArraySpec[] specs) throws cbit.vcell.parser.ExpressionException {
	setSimulation(simulation);
	for (int i = 0; i < constants.length; i++){
		putConstant(constants[i], false);
	}
	for (int i = 0; i < specs.length; i++){
		putConstantArraySpec(specs[i], false);
	}
}


/**
 * One of three ways to construct a MathOverrides.  This constructor
 * is used when creating a MathOverrides from the database.
 */
public MathOverrides(Simulation simulation, CommentStringTokenizer tokenizer) throws DataAccessException {
	setSimulation(simulation);
	readVCML(tokenizer);
}


/**
 * One of three ways to construct a MathOverrides.  This constructor
 * is used when copying a MathOverrides from an existing one.
 */
public MathOverrides(Simulation simulation, MathOverrides mathOverrides) {
	setSimulation(simulation);
	java.util.Set keySet = mathOverrides.getOverridesHash().keySet();
	java.util.Iterator keyIter = keySet.iterator();
	while (keyIter.hasNext()) {
		String constantName = (String)keyIter.next();
		MathOverrides.Element element = mathOverrides.getOverridesElement(constantName);
		if(element.actualValue != null) {
			// regular override
			getOverridesHash().put(constantName, new MathOverrides.Element(element.name,element.actualValue));
		} else {
			// scan override
			getOverridesHash().put(constantName, new MathOverrides.Element(element.name,element.spec));
		}
	}
	updateFromMathDescription();
}


/**
 * 
 * @param newListener cbit.vcell.solver.MathOverridesListener
 */
public void addMathOverridesListener(cbit.vcell.solver.MathOverridesListener newListener) {
	aMathOverridesListener = cbit.vcell.solver.MathOverridesEventMulticaster.add(aMathOverridesListener, newListener);
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (10/15/2005 6:25:28 PM)
 * @param mathDescription cbit.vcell.math.MathDescription
 */
private void checkUnresolved(Expression exp, MathDescription mathDescription, String name) {
	String symbols[] = exp.getSymbols();
	boolean bExpressionBad = false;
	for (int i=0;symbols!=null && i<symbols.length;i++){
		if (symbols[i].equals(name)){
			//
			// should not be recursively defined
			//
			bExpressionBad = true;
			break;
		}
		if (getOverridesHash().get(symbols[i])==null){
			bExpressionBad = true;
			break;
		}
	}
	if (bExpressionBad){
		Constant mathConstant = (Constant)mathDescription.getVariable(name);
		if (mathConstant!=null){
			getOverridesHash().remove(name);
		}else{
			throw new RuntimeException("Constant \""+name+"\" not found in MathDescription");
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/00 1:23:14 PM)
 * @return boolean
 */
public boolean compareEqual(cbit.util.Matchable obj) {
	if (!(obj instanceof MathOverrides)){
		return false;
	}
	//
	// use the superclass definition of equals(), it compares contents not references.
	//
	boolean returnValue = cbit.util.Compare.isEqual(toVector(getOverridesHash().elements()),toVector(((MathOverrides)obj).getOverridesHash().elements()));
	return returnValue;
}


/**
 * Method to support listener events.
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
protected void fireConstantAdded(cbit.vcell.solver.MathOverridesEvent event) {
	if (aMathOverridesListener == null) {
		return;
	};
	aMathOverridesListener.constantAdded(event);
}


/**
 * Method to support listener events.
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
protected void fireConstantChanged(cbit.vcell.solver.MathOverridesEvent event) {
	if (aMathOverridesListener == null) {
		return;
	};
	aMathOverridesListener.constantChanged(event);
}


/**
 * Method to support listener events.
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
protected void fireConstantRemoved(cbit.vcell.solver.MathOverridesEvent event) {
	if (aMathOverridesListener == null) {
		return;
	};
	aMathOverridesListener.constantRemoved(event);
}


/**
 * Returns the value to which the specified key is mapped in this hashtable.
 *
 * @param   key   a key in the hashtable.
 * @return  the value to which the key is mapped in this hashtable;
 *          <code>null</code> if the key is not mapped to any value in
 *          this hashtable.
 * @see     #put(Object, Object)
 */
public Expression getActualExpression(String key, int index) {
	MathOverrides.Element element = getOverridesElement(key);
	if (element==null){
		// not overriden
		return getDefaultExpression(key);
	} else if (!isScan(key)){
		// regular override
		return element.actualValue;
	} else{
		// scanned parameter
		String[] names = getScannedConstantNames();
		java.util.Arrays.sort(names); // must do things in a consistent way
		int[] bounds = new int[names.length]; // bounds of scanning matrix
		for (int i = 0; i < names.length; i++){
			bounds[i] = getConstantArraySpec(names[i]).getNumValues() - 1;
		}
		int[] coordinates = cbit.util.BeanUtils.indexToCoordinate(index, bounds);
		int localIndex = coordinates[java.util.Arrays.binarySearch(names, key)];
		return getConstantArraySpec(key).getConstants()[localIndex].getExpression();
	}
}


public String[] getAllConstantNames() {
	Enumeration en = getSimulation().getMathDescription().getConstants();
	java.util.Vector v = new java.util.Vector();
	while (en.hasMoreElements()) {
		v.add(((Constant)en.nextElement()).getName());
	}
	return (String[])cbit.util.BeanUtils.getArray(v, String.class);
}


/**
 * Returns the value to which the specified key is mapped in this hashtable.
 *
 * @param   key   a key in the hashtable.
 * @return  the value to which the key is mapped in this hashtable;
 *          <code>null</code> if the key is not mapped to any value in
 *          this hashtable.
 * @see     #put(Object, Object)
 */
public ConstantArraySpec getConstantArraySpec(String key) {
	if(isScan(key)) {
		return getOverridesElement(key).spec;
	} else {
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 9:12:17 AM)
 * @return cbit.vcell.parser.Expression
 * @param key java.lang.String
 */
public Expression getDefaultExpression(String key) {
	cbit.vcell.math.Variable var = getSimulation().getMathDescription().getVariable(key);
	if (var instanceof Constant){
		Constant c = (Constant)var;
		return new Expression(c.getExpression()); // always returning clones...
	}else{
		return null;
	}
}


public String[] getOverridenConstantNames() {
	String keys[] = new String[getOverridesHash().size()];
	getOverridesHash().keySet().toArray(keys);
	return keys;
}


/**
 * Insert the method's description here.
 * Creation date: (7/25/2001 8:07:27 PM)
 * @return cbit.vcell.solver.MathOverrides.Element
 * @param key java.lang.String
 */
private MathOverrides.Element getOverridesElement(String key) {
	return (MathOverrides.Element)getOverridesHash().get(key);
}


/**
 * Insert the method's description here.
 * Creation date: (7/25/2001 7:39:57 PM)
 * @return java.util.Hashtable
 */
private java.util.Hashtable getOverridesHash() {
	return overridesHash;
}


/**
 * Insert the method's description here.
 * Creation date: (9/26/2005 2:54:25 PM)
 * @return int
 */
public int getScanCount() {
	int num = 1;
	String[] scannedParams = getScannedConstantNames();
	for (int i = 0; i < scannedParams.length; i++){
		num *= getConstantArraySpec(scannedParams[i]).getNumValues();
	}
	return num;
}


public String[] getScannedConstantNames() {
	String[] overrides = getOverridenConstantNames();
	java.util.Vector v = new java.util.Vector();
	for (int i = 0; i < overrides.length; i++){
		if (isScan(overrides[i])) v.add(overrides[i]);
	}
	return (String[])cbit.util.BeanUtils.getArray(v, String.class);
}


/**
 * Insert the method's description here.
 * Creation date: (9/30/2004 8:48:43 PM)
 * @return cbit.vcell.solver.Simulation
 */
private Simulation getSimulation() {
	return simulation;
}


public int getSize() {
	return(getOverridesHash().size());
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/00 12:41:09 PM)
 * @return java.lang.String
 */
public String getVCML() {

	//
	// write format as follows:
	//
	//   MathOverrides {
	//       Constant K1 200;
	//       Constant K2 400;
	//       Constant K3 494.0;
	//   }
	//
	//	
	StringBuffer buffer = new StringBuffer();

	buffer.append(VCML.MathOverrides+" "+VCML.BeginBlock+"\n");
	
	java.util.Enumeration enum1 = getOverridesHash().keys();
	while (enum1.hasMoreElements()){
		String name = (String)enum1.nextElement();
		MathOverrides.Element element = (MathOverrides.Element)getOverridesHash().get(name);
		Expression exp = element.actualValue;
		if (exp != null) {
			// regular override
			Constant constant = new Constant(name,exp);
			buffer.append("   "+constant.getVCML());
		} else {
			// scan override
			buffer.append("   "+element.spec.getVCML());
		}
	}

	buffer.append("\n"+VCML.EndBlock+"\n");
		
	return buffer.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 9:14:00 AM)
 * @return boolean
 * @param key java.lang.String
 */
public boolean isDefaultExpression(String key) {
	return !getOverridesHash().containsKey(key);
}


/**
 * Insert the method's description here.
 * Creation date: (9/22/2005 3:20:48 PM)
 * @return boolean
 * @param parameterName java.lang.String
 */
public boolean isScan(String parameterName) {
	return !isDefaultExpression(parameterName) && (getOverridesElement(parameterName).spec != null);
}


/**
 * Insert the method's description here.
 * Creation date: (5/4/01 11:44:22 AM)
 * @return boolean
 */
public boolean isValid(MathDescription mathDescription) {
	//
	// returns true if MathOverrides has exactly the same list of Constants as MathDescription
	//
	
	//
	// look for Constants from MathDescription not present in Overrides
	//
	Enumeration enumeration = mathDescription.getConstants();
	java.util.HashSet mathDescriptionHash = new java.util.HashSet();
	while (enumeration.hasMoreElements()) {
		Constant constant = (Constant) enumeration.nextElement();
		mathDescriptionHash.add(constant.getName());
		if (!getOverridesHash().containsKey(constant.getName())){
			return false;
		}
	}
	//
	//  look for Constants from Overrides not present in MathDescription
	//
	Enumeration mathOverrideNamesEnum = getOverridesHash().keys();
	while (mathOverrideNamesEnum.hasMoreElements()){
		String name = (String) mathOverrideNamesEnum.nextElement();
		if (!mathDescriptionHash.contains(name)){
			return false;
		}
	}
	return true;
}


/**
 * Maps the specified <code>key</code> to the specified 
 * <code>value</code> . Neither the key nor the 
 * value can be <code>null</code>. <p>
 */
public void putConstant(Constant value) throws cbit.vcell.parser.ExpressionException {
	putConstant(value, true);
}


/**
 * Maps the specified <code>key</code> to the specified 
 * <code>value</code> . Neither the key nor the 
 * value can be <code>null</code>. <p>
 */
private void putConstant(Constant value, boolean bFireEvent) throws cbit.vcell.parser.ExpressionException {
	//
	// verify that new expression can be bound properly
	//
	verifyExpression(value, false);
	// put new element in the hash if value different from default
	// if same as default, remove element if we had it in hash, otherwise do nothing
	String name = value.getName();
	Expression act = value.getExpression();
	Expression def = null;
	cbit.vcell.math.Variable var = getSimulation().getMathDescription().getVariable(name);
	if (var != null && var instanceof Constant) {
		def = ((Constant)var).getExpression();
	} else {
		// ignore
		System.out.println(">>>>WARNING: Math does not have constant with name: "+name);
		return;
	}
	if (act.compareEqual(def)) {
		if (getOverridesHash().containsKey(name)) {
			getOverridesHash().remove(name);
		}
	} else {
		getOverridesHash().put(name, new MathOverrides.Element(name, act));
	}
	if (bFireEvent) {
		fireConstantChanged(new MathOverridesEvent(value.getName()));
	}
}


/**
 * Maps the specified <code>key</code> to the specified 
 * <code>value</code> . Neither the key nor the 
 * value can be <code>null</code>. <p>
 */
public void putConstantArraySpec(ConstantArraySpec spec) throws cbit.vcell.parser.ExpressionException {
	putConstantArraySpec(spec, true);
}


/**
 * Maps the specified <code>key</code> to the specified 
 * <code>value</code> . Neither the key nor the 
 * value can be <code>null</code>. <p>
 */
private void putConstantArraySpec(ConstantArraySpec spec, boolean bFireEvent) throws cbit.vcell.parser.ExpressionException {
	//
	// verify that expressions can be bound properly and make Expression array
	//
	String name = spec.getName();
	Constant[] constants = spec.getConstants();
	for (int i = 0; i < constants.length; i++){
		verifyExpression(constants[i], true); // this will throw if there's a problem with the expression
	}
	// put new element in the hash
	// always different from default, since it's a scan list
	getOverridesHash().put(name, new MathOverrides.Element(name, spec));
	if (bFireEvent) {
		fireConstantChanged(new MathOverridesEvent(name));
	}
}


/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
private void readVCML(CommentStringTokenizer tokens)
	throws DataAccessException {


	//  Read format as follows:
	//
	//   MathOverrides {
	//       Constant K1 200;
	//       Constant K2 400;
	//       Constant K3 494.0;
	//   }
	try {
		String token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.MathOverrides)) {
			token = tokens.nextToken();
			if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
				throw new DataAccessException(
					"unexpected token " + token + " expecting " + VCML.BeginBlock); 
			}
		}
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)) {
				break;
			}
			if (token.equalsIgnoreCase(VCML.Constant)) {
				String name = tokens.nextToken();
				Expression act = new Expression(tokens);
				Expression def = getDefaultExpression(name);
				//
				// ignore override if not present in math or if it is the same
				//
				if (def!=null && !act.compareEqual(def)){
					getOverridesHash().put(name, new MathOverrides.Element(name, act));
				}
				continue;
			}
			if (token.equalsIgnoreCase(VCML.ConstantArraySpec)) {
				String name = tokens.nextToken();
				int type = Integer.parseInt(tokens.nextToken());
				String description = "";
				boolean bDone = false;
				while (!bDone) {
					String t = tokens.nextToken();
					if (t.equals(";")) {
						bDone = true;
					} else if (t.endsWith(";") && (t.length() > 1)) {
						description += t.substring(0,t.length()-2);
						bDone = true;
					} else {
						description += t + " ";
					}
				}
				getOverridesHash().put(name, new MathOverrides.Element(name, ConstantArraySpec.createFromString(name, description.trim(), type)));
				continue;
			}
			throw new DataAccessException("unexpected identifier " + token);
		}
	} catch (Throwable e) {
		throw new DataAccessException(
			"line #" + (tokens.lineIndex()+1) + " Exception: " + e.getMessage()); 
	}
}


/**
 * 
 * @param newListener cbit.vcell.solver.MathOverridesListener
 */
public void removeMathOverridesListener(cbit.vcell.solver.MathOverridesListener newListener) {
	aMathOverridesListener = cbit.vcell.solver.MathOverridesEventMulticaster.remove(aMathOverridesListener, newListener);
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (7/6/01 8:38:02 AM)
 * @param mathDescription cbit.vcell.math.MathDescription
 */
private void revertUnboundExpressions(MathDescription mathDescription) {
	//
	//  remove those expressions that contain unresolved symbols
	//
	Enumeration mathOverrideNamesEnum = getOverridesHash().keys();
	while (mathOverrideNamesEnum.hasMoreElements()){
		String name = (String) mathOverrideNamesEnum.nextElement();
		MathOverrides.Element element = (MathOverrides.Element)getOverridesHash().get(name);
		if (element.actualValue != null) {
			// regular override
			Expression exp = element.actualValue;
			checkUnresolved(exp, mathDescription, name);
		} else {
			// scan override
			ConstantArraySpec cas = element.spec;
			for (int i = 0; i < cas.getConstants().length; i++){
				Expression exp = cas.getConstants()[i].getExpression();
				checkUnresolved(exp, mathDescription, name);
			}
		}
	}

}


/**
 * Insert the method's description here.
 * Creation date: (9/30/2004 8:48:43 PM)
 * @param newSimulation cbit.vcell.solver.Simulation
 */
void setSimulation(Simulation newSimulation) {
	simulation = newSimulation;
}


private static java.util.Vector toVector (java.util.Enumeration enumeration) {
	java.util.Vector vector = new java.util.Vector();
	while (enumeration.hasMoreElements()) {
		vector.add(enumeration.nextElement());
	}
	return (vector);
}


/**
 * Returns the value to which the specified key is mapped in this hashtable.
 *
 * @param   key   a key in the hashtable.
 * @return  the value to which the key is mapped in this hashtable;
 *          <code>null</code> if the key is not mapped to any value in
 *          this hashtable.
 * @see     #put(Object, Object)
 */
void updateFromMathDescription () {
	MathDescription mathDescription = getSimulation().getMathDescription();
	//
	// get list of names of constants in this math
	//
	Enumeration enumeration = mathDescription.getConstants();
	java.util.HashSet mathDescriptionHash = new java.util.HashSet();
	while (enumeration.hasMoreElements()) {
		Constant constant = (Constant) enumeration.nextElement();
		mathDescriptionHash.add(constant.getName());
	}
	//
	//  Now remove any elements in this MathOverrides NOT in the MathDescription...
	//
	Enumeration mathOverrideNamesEnum = getOverridesHash().keys();
	while (mathOverrideNamesEnum.hasMoreElements()){
		String name = (String) mathOverrideNamesEnum.nextElement();
		if (!mathDescriptionHash.contains(name)){
			getOverridesHash().remove(name);
		}
	}
	//
	//  revert those expressions that contain unresolved symbols (back to the MathDescription expressions).
	//
	revertUnboundExpressions(mathDescription);
}


/**
 * Insert the method's description here.
 * Creation date: (9/22/2005 3:24:36 PM)
 */
private void verifyExpression(Constant value, boolean checkScan) throws ExpressionBindingException {
	Expression exp = value.getExpression();
	String symbols[] = exp.getSymbols();
	for (int i = 0; symbols!=null && i < symbols.length; i++){
		if (cbit.vcell.math.ReservedVariable.fromString(symbols[i])!=null){
			//
			// expression is a function of x,y,z,t (OK)
			//
		}else{
			//
			// expression must be a function of another Simulation parameter
			//
			if (!(getSimulation().getVariable(symbols[i]) != null && getSimulation().getVariable(symbols[i]) instanceof Constant)){
				throw new ExpressionBindingException("identifier "+symbols[i]+" not found");
			}
			if (checkScan && isScan(symbols[i])) {
				throw new ExpressionBindingException("cannot depend on another scanned parameter "+symbols[i]);
			}
			//
			// look for trivial algebraic loops (x = f(x)).
			//
			if (symbols[i].equals(value.getName())){
				throw new ExpressionBindingException("recursive definition, can't use identifier "+value.getName()+" in expression for "+value.getName());
			}
		}
	}
}
}