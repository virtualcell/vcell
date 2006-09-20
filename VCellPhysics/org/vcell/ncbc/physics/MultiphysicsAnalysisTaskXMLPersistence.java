package org.vcell.ncbc.physics;
import java.beans.PropertyVetoException;

import org.jdom.Element;

import cbit.image.ImageException;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (5/5/2006 9:00:56 AM)
 * @author: Jim Schaff
 */
public class MultiphysicsAnalysisTaskXMLPersistence {
	
	public final static String MultiphysicsAnalysisTaskTag = "MultiphysicsAnalysisTask";
	public final static String NameAttribute = "name";
	public final static String AnnotationTag = "annotation";

/**
 * Insert the method's description here.
 * Creation date: (5/5/2006 4:50:36 PM)
 * @return cbit.vcell.modelopt.ParameterEstimationTask
 * @param element org.jdom.Element
 * @param simContext cbit.vcell.mapping.SimulationContext
 * @throws PropertyVetoException 
 * @throws GeometryException 
 * @throws ImageException 
 * @throws ExpressionException 
 */
public static MultiphysicsAnalysisTask getMultiphysicsAnalysisTask(Element parameterEstimationTaskElement, cbit.vcell.mapping.SimulationContext simContext) 
throws ExpressionException, ImageException, GeometryException, PropertyVetoException {
		
	MultiphysicsAnalysisTask multiphysicsAnalysisTask = new MultiphysicsAnalysisTask(simContext,"unnamed");
	String name = parameterEstimationTaskElement.getAttributeValue(NameAttribute);
	multiphysicsAnalysisTask.setName(name);
	Element annotationElement = parameterEstimationTaskElement.getChild(AnnotationTag);
	if (annotationElement!=null){
		String annotationText = annotationElement.getText();
		multiphysicsAnalysisTask.setAnnotation(annotationText);
	}
	
	return multiphysicsAnalysisTask;
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2006 9:02:39 AM)
 * @return java.lang.String
 * @param parameterEstimationTask cbit.vcell.modelopt.ParameterEstimationTask
 */
public static Element getXML(MultiphysicsAnalysisTask multiphysicsAnalysisTask) {

	
	Element multiphysicsAnalysisTaskElement = new Element(MultiphysicsAnalysisTaskTag);
	// name attribute
	multiphysicsAnalysisTaskElement.setAttribute(NameAttribute,mangle(multiphysicsAnalysisTask.getName()));
	// annotation content (optional)
	String annotation = multiphysicsAnalysisTask.getAnnotation();
	if (annotation!=null && annotation.length()>0) {
		org.jdom.Element annotationElement = new org.jdom.Element(AnnotationTag);
		annotationElement.setText(mangle(annotation));
		multiphysicsAnalysisTaskElement.addContent(annotationElement);
	}
	
	return multiphysicsAnalysisTaskElement;
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2006 9:39:57 AM)
 * @return java.lang.String
 * @param input java.lang.String
 */
private static String mangle(String input) {
	return input;
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2006 11:25:52 PM)
 * @return double
 * @param doubleString java.lang.String
 */
private static double parseDouble(String doubleString) {
	try {
		return Double.parseDouble(doubleString);
	}catch (NumberFormatException e){
		if (doubleString.equalsIgnoreCase("-Infinity")){
			return Double.NEGATIVE_INFINITY;
		}else if (doubleString.equalsIgnoreCase("Infinity")){
			return Double.POSITIVE_INFINITY;
		}else{
			throw e;
		}
	}
}
}