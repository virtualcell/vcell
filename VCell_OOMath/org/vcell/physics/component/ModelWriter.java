package org.vcell.physics.component;
import jscl.plugin.Expression;
import jscl.plugin.ParseException;

import org.jdom.Element;
import org.vcell.units.VCUnitDefinition;


/**
 * Insert the type's description here.
 * Creation date: (2/17/2006 1:26:13 PM)
 * @author: Jim Schaff
 */
public class ModelWriter {
/**
 * Insert the method's description here.
 * Creation date: (2/22/2006 3:05:42 PM)
 * @return org.jdom.Element
 * @param oOModel ncbc.physics2.component.Model
 */
public org.jdom.Element getXML(OOModel oOModel) {
	Element vcmlElement = new Element(PhysicsXMLTags.VCML_TAG);
	Element physicalModelElement = new Element(PhysicsXMLTags.PHYSICALMODEL_TAG);
	ModelComponent[] modelComponents = oOModel.getModelComponents();
	for (int i = 0; i < modelComponents.length; i++){
		Element classElement = getXML(modelComponents[i]);
		physicalModelElement.addContent(classElement);
	}
	Connection[] connections = oOModel.getConnections();
	for (int i = 0; i < connections.length; i++){
		physicalModelElement.addContent(getXML(connections[i]));
	}
	vcmlElement.addContent(physicalModelElement);
	return vcmlElement;
}

public Element getXML(Connection connection){
	Element connectionElement = new Element(PhysicsXMLTags.CONNECTION_TAG);
	Connector[] connectors = connection.getConnectors();
	for (int j = 0; j < connectors.length; j++){
		Element connectorRefElement = new Element(PhysicsXMLTags.CONNECTORREF_TAG);
		connectorRefElement.setAttribute(PhysicsXMLTags.CONNECTORREF_DEVICE_ATTR,connectors[j].getParent().getName());
		connectorRefElement.setAttribute(PhysicsXMLTags.CONNECTORREF_NAME_ATTR,connectors[j].getName());
		connectionElement.addContent(connectorRefElement);
	}
	return connectionElement;
}

public Element getXML(Parameter parameter){
	Element parameterElement = new Element(PhysicsXMLTags.PARAMETER_TAG);
	parameterElement.setAttribute(PhysicsXMLTags.PARAMETER_NAME_ATTR,parameter.getName());
	parameterElement.setAttribute(PhysicsXMLTags.PARAMETER_UNIT_ATTR,parameter.getUnitDefinition().getSymbol());
	return parameterElement;
}

public Element getXML(Variable variable){
	Element variableElement = new Element(PhysicsXMLTags.VARIABLE_TAG);
	variableElement.setAttribute(PhysicsXMLTags.VARIABLE_NAME_ATTR,variable.getName());
	variableElement.setAttribute(PhysicsXMLTags.VARIABLE_UNIT_ATTR,variable.getUnitDefinition().getSymbol());
	return variableElement;
}

public Element getEquationXML(Expression equation){
	Element equationElement = new Element(PhysicsXMLTags.EQUATION_TAG);
	equationElement.setAttribute(PhysicsXMLTags.EQUATION_EXP_ATTR,equation.infix());
	return equationElement;
}

public Element getXML(Connector connector){
	Element connectorElement = new Element(PhysicsXMLTags.CONNECTOR_TAG);
	connectorElement.setAttribute(PhysicsXMLTags.CONNECTOR_NAME_ATTR,connector.getName());
	if (connector.getEffortVariable()!=null){
		connectorElement.setAttribute(PhysicsXMLTags.CONNECTOR_EFFORT_ATTR,connector.getEffortVariable().getName());
	}
	if (connector.getFlowVariable()!=null){
		connectorElement.setAttribute(PhysicsXMLTags.CONNECTOR_FLOW_ATTR,connector.getFlowVariable().getName());
	}
	return connectorElement;
}

public Element getXML(ModelComponent modelComponent){
	Element classElement = new Element(PhysicsXMLTags.CLASS_TAG);
	classElement.setAttribute(PhysicsXMLTags.CLASS_NAME_ATTR,modelComponent.getName());
	PhysicalSymbol[] symbols = modelComponent.getSymbols();
	for (int j = 0; j < symbols.length; j++){
		if (symbols[j] instanceof Parameter){
			Parameter parameter = (Parameter)symbols[j];
			classElement.addContent(getXML(parameter));
		}else if (symbols[j] instanceof Variable){
			Variable variable = (Variable)symbols[j];
			classElement.addContent(getXML(variable));
		}
	}
	Expression[] equations = modelComponent.getEquations();
	for (int j = 0; j < equations.length; j++){
		classElement.addContent(getEquationXML(equations[j]));
	}
	Connector[] connectors = modelComponent.getConnectors();
	for (int j = 0; j < connectors.length; j++){
		classElement.addContent(getXML(connectors[j]));
	}
	return classElement;
}

}