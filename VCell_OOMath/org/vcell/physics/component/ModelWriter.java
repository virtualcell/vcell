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
public org.jdom.Element print(OOModel oOModel) {
	Element vcmlElement = new Element("vcml");
	Element physicalModelElement = new Element("physicalModel");
	ModelComponent[] modelComponents = oOModel.getModelComponents();
	for (int i = 0; i < modelComponents.length; i++){
		Element classElement = print(modelComponents[i]);
		physicalModelElement.addContent(classElement);
	}
	Connection[] connections = oOModel.getConnections();
	for (int i = 0; i < connections.length; i++){
		Element connectionElement = new Element("connection");
		Connector[] connectors = connections[i].getConnectors();
		for (int j = 0; j < connectors.length; j++){
			Element connectorRefElement = new Element("connectorRef");
			connectorRefElement.setAttribute("device",connectors[j].getParent().getName());
			connectorRefElement.setAttribute("name",connectors[j].getName());
			connectionElement.addContent(connectorRefElement);
		}
		physicalModelElement.addContent(connectionElement);
	}
	vcmlElement.addContent(physicalModelElement);
	return vcmlElement;
}


public Element print(ModelComponent modelComponent){
	Element classElement = new Element("class");
	classElement.setAttribute("name",modelComponent.getName());
	PhysicalSymbol[] symbols = modelComponent.getSymbols();
	for (int j = 0; j < symbols.length; j++){
		if (symbols[j] instanceof Parameter){
			Parameter parameter = (Parameter)symbols[j];
			Element parameterElement = new Element("parameter");
			parameterElement.setAttribute("name",parameter.getName());
			classElement.addContent(parameterElement);
		}else if (symbols[j] instanceof Variable){
			Variable variable = (Variable)symbols[j];
			Element variableElement = new Element("variable");
			variableElement.setAttribute("name",variable.getName());
			classElement.addContent(variableElement);
		}
	}
	Expression[] equations = modelComponent.getEquations();
	for (int j = 0; j < equations.length; j++){
		Element equationElement = new Element("equation");
		equationElement.setAttribute("exp",equations[j].infix());
		classElement.addContent(equationElement);
	}
	Connector[] connectors = modelComponent.getConnectors();
	for (int j = 0; j < connectors.length; j++){
		Element connectorElement = new Element("connector");
		connectorElement.setAttribute("name",connectors[j].getName());
		if (connectors[j].getEffortVariable()!=null){
			connectorElement.setAttribute("effort",connectors[j].getEffortVariable().getName());
		}
		if (connectors[j].getFlowVariable()!=null){
			connectorElement.setAttribute("flow",connectors[j].getFlowVariable().getName());
		}
		classElement.addContent(connectorElement);
	}
	return classElement;
}

}