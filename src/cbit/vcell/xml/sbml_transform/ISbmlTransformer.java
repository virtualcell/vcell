/**
 * 
 */
package cbit.vcell.xml.sbml_transform;

import org.w3c.dom.Document;

/** Iterface to classes that introduce changes to SBML document
 * @author mlevin
 *
 */
interface ISbmlTransformer {
	
	/**
	 * @return number of stored transformation expressions
	 */
	int countTransformations();
	
	/**
	 * @param i
	 * @return stored transformation if <code>0 &lt;= i &lt; countTransformations()</code>
	 * or an array of empty strings of correct size;
	 */
	String[] getTransformation(int i);

	/** 
	 * @param transform should contain correct number of valid transformation 
	 * description strings (go figure :--)
	 */
	void addTransformation(String[] transform);
	
	/** Applies stored transformations to a DOM object
	 * @param doc
	 */
	void transform(Document doc);
	
//	void setDefaultTransformations();
	
	/**
	 * @param i index of the trasformation that will be removed from the list
	 */
	void removeTransformation(int i);
	
	String getName();

}