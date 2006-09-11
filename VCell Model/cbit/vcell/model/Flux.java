package cbit.vcell.model;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.KeyValue;
import cbit.util.Matchable;

public class Flux extends ReactionParticipant
{
/**
 * This method was created in VisualAge.
 * @param reactionStep cbit.vcell.model.ReactionStep
 */
public Flux(KeyValue key, FluxReaction fluxReaction, SpeciesContext speciesContext) {
	super(key, fluxReaction, speciesContext, 1);
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (obj instanceof Flux){
		Flux f = (Flux)obj;
		return compareEqual0(f);
	}else{
		return false;
	}
}


/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
public void fromTokens(cbit.vcell.math.CommentStringTokenizer tokens, Model model) throws Exception {

	throw new Exception("not implemented");
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	String scName = (getSpeciesContext()!=null)?(getSpeciesContext().getName()):"null";
	return "Flux(id="+getKey()+", speciesContext="+scName+"')";
}


/**
 * This method was created by a SmartGuide.
 * @param ps java.io.PrintStream
 * @exception java.lang.Exception The exception description.
 */
public void writeTokens(java.io.PrintWriter pw) {
	System.out.println("not implemented");
}
}