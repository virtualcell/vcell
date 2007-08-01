/**
 * 
 */
package cbit.vcell.xml.sbml_transform;


/**
 * @author mlevin
 *
 */
abstract class ASbmlTransformer implements ISbmlTransformer {

	protected int countParameters() {	return 0;}
	
	public void addTransformation(String[] str) {
		int n = countParameters();
		if( str.length > n ) {
			String msg = "" + n + " parameters expected; unknown parameter: \"" + str[n] + "\"";
			throw new SbmlTransformException(msg);
		}
		if( str.length < countParameters() ) {
			String msg = "" + str.length + " parameters found; " + n + " expected;";
			throw new SbmlTransformException(msg);
		}
	}
	
	
}
