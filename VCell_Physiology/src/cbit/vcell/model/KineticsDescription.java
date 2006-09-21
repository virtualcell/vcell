package cbit.vcell.model;

/**
 * Insert the type's description here.
 * Creation date: (8/5/2002 11:35:48 AM)
 * @author: Anuradha Lakshminarayana
 */
public  class KineticsDescription implements java.io.Serializable {
	private java.lang.String fieldName;
	private java.lang.String fieldDescription;
	private java.lang.String fieldVCMLName;
	private java.lang.Class fieldKineticsClass;
	private boolean fieldIsElectrical = false;
	private boolean fieldNeedsValence = false;

	public final static KineticsDescription MassAction = new KineticsDescription("MassActionKinetics", "MassAction", VCMODL.MassActionKinetics, false, false, MassActionKinetics.class);
	public final static KineticsDescription HMM_irreversible = new KineticsDescription("HMM_IRRKinetics", "HenriMichaelisMenten (Irreversible)", VCMODL.HMM_IrreversibleKinetics, false, false, HMM_IRRKinetics.class);
	public final static KineticsDescription HMM_reversible = new KineticsDescription("HMM_REVKinetics", "HenriMichaelisMenten (Reversible)", VCMODL.HMM_ReversibleKinetics, false, false, HMM_REVKinetics.class);
	public final static KineticsDescription General = new KineticsDescription("GeneralKinetics", "General", VCMODL.GeneralKinetics, false, false, GeneralKinetics.class);
	public final static KineticsDescription GeneralCurrent = new KineticsDescription("GeneralCurrentKinetics", "GeneralCurrent", VCMODL.GeneralCurrentKinetics, true, false, GeneralCurrentKinetics.class);
	public final static KineticsDescription GHK = new KineticsDescription("GHKKinetics", "Goldman-Hodgkin-Katz", VCMODL.GHKKinetics, true, true, GHKKinetics.class);
	public final static KineticsDescription Nernst = new KineticsDescription("NernstKinetics", "Nernst", VCMODL.NernstKinetics, true, true, NernstKinetics.class);

	private static KineticsDescription fieldKineticsList[] = new KineticsDescription[] { MassAction, HMM_irreversible, HMM_reversible, General, GeneralCurrent, GHK, Nernst };
	
/**
 * KineticsDescription constructor comment.
 */
private KineticsDescription(String name, String description, String vcmlName, boolean argIsElectrical, boolean needsValence, Class kineticsClass) {
	super();
	fieldName = name;
	fieldDescription = description;
	fieldVCMLName = vcmlName;
	fieldKineticsClass = kineticsClass;
	fieldIsElectrical = argIsElectrical;
	fieldNeedsValence = needsValence;
}
/**
 * Insert the method's description here.
 * Creation date: (8/5/2002 5:30:46 PM)
 * @return cbit.vcell.model.Kinetics
 */
public Kinetics createKinetics(ReactionStep reactionStep) {
	try {
		java.lang.reflect.Constructor constructor = fieldKineticsClass.getConstructor(new Class[] { ReactionStep.class });
		Kinetics kinetics = (Kinetics)constructor.newInstance(new Object[]{ reactionStep });
		return kinetics;
	}catch (NoSuchMethodException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}catch (InstantiationException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}catch (java.lang.reflect.InvocationTargetException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}catch (IllegalAccessException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/5/2002 5:21:06 PM)
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if (! (obj instanceof KineticsDescription)) {
		return false;
	}
	if ( ((KineticsDescription)obj).getName().equals(getName())) {
		return true;
	} else {
		return false;
	}
} 
/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 11:24:12 AM)
 * @return cbit.vcell.model.KineticsDescription
 * @param vcmlName java.lang.String
 */
public static KineticsDescription fromVCMLKineticsName(String vcmlName) {
	for (int i = 0; i < fieldKineticsList.length; i++){
		if (fieldKineticsList[i].fieldVCMLName.equals(vcmlName)){
			return fieldKineticsList[i];
		}
	}
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (8/5/2002 11:42:48 AM)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	return fieldDescription;
}
/**
 * KineticsTemplate constructor comment.
 */
public static KineticsDescription[] getKineticDescriptions() {
	return(fieldKineticsList);
}
/**
 * KineticsTemplate constructor comment.
 */
public static KineticsDescription getKineticDescriptions(int i) {
	return(fieldKineticsList[i]);
}
/**
 * Insert the method's description here.
 * Creation date: (8/5/2002 11:41:26 AM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return fieldName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 11:31:05 AM)
 * @return java.lang.String
 */
public String getVCMLKineticsName() {
	return fieldVCMLName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/5/2002 5:26:10 PM)
 * @return int
 */
public int hashCode() {
	return getName().hashCode();
}
/**
 * Insert the method's description here.
 * Creation date: (11/18/2002 4:42:41 PM)
 * @return boolean
 */
public boolean isElectrical() {
	return fieldIsElectrical;
}
/**
 * Insert the method's description here.
 * Creation date: (11/18/2002 4:42:41 PM)
 * @return boolean
 */
public boolean needsValence() {
	return fieldNeedsValence;
}
}
