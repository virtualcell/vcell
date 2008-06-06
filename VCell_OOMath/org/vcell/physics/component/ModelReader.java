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
public class ModelReader {
/**
 * Insert the method's description here.
 * Creation date: (2/17/2006 1:28:25 PM)
 * @param xmlString java.lang.String
 */
public OOModel getOOModel(Element root) throws ParseException {
	 
	//
	// make sure root is a "physicalModel"
	//
	if (!root.getName().equals(PhysicsXMLTags.VCML_TAG)){
		throw new RuntimeException("this is not a vcml document");
	}
	java.util.List rootChildren = root.getChildren();
	if (rootChildren.size()!=1 || !((org.jdom.Element)rootChildren.get(0)).getName().equals(PhysicsXMLTags.PHYSICALMODEL_TAG)){
		throw new RuntimeException("this is not a physicalModel document");
	}
	org.jdom.Element physicalModelRoot = (org.jdom.Element)rootChildren.get(0);
	OOModel oOModel = new OOModel();
	java.util.List classList = physicalModelRoot.getChildren(PhysicsXMLTags.CLASS_TAG);
	for (int i = 0; i < classList.size(); i++){
		org.jdom.Element classElement = (org.jdom.Element)classList.get(i);
		org.jdom.Attribute classNameAttr = classElement.getAttribute(PhysicsXMLTags.CLASS_NAME_ATTR);
		String className = classNameAttr.getValue();
		ModelComponent modelComponent = new ModelComponent(className);
		//
		// read variables, parameters, equations, connectors
		//
		java.util.List variableList = classElement.getChildren(PhysicsXMLTags.VARIABLE_TAG);
		for (int j = 0; j < variableList.size(); j++){
			org.jdom.Element variableElement = (org.jdom.Element)variableList.get(j);
			org.jdom.Attribute variableNameAttr = (org.jdom.Attribute)variableElement.getAttribute(PhysicsXMLTags.VARIABLE_NAME_ATTR);
			org.jdom.Attribute unitSymbolAttr = (org.jdom.Attribute)variableElement.getAttribute(PhysicsXMLTags.VARIABLE_UNIT_ATTR);
			modelComponent.addSymbol(new Variable(variableNameAttr.getValue(),VCUnitDefinition.getInstance(unitSymbolAttr.getValue())));
		}
		java.util.List parameterList = classElement.getChildren(PhysicsXMLTags.PARAMETER_TAG);
		for (int j = 0; j < parameterList.size(); j++){
			org.jdom.Element parameterElement = (org.jdom.Element)parameterList.get(j);
			org.jdom.Attribute parameterNameAttr = (org.jdom.Attribute)parameterElement.getAttribute(PhysicsXMLTags.PARAMETER_NAME_ATTR);
			org.jdom.Attribute unitSymbolAttr = (org.jdom.Attribute)parameterElement.getAttribute(PhysicsXMLTags.PARAMETER_UNIT_ATTR);
			//org.jdom.Attribute expressionAttr = (org.jdom.Attribute)parameterElement.getAttribute("exp");
			modelComponent.addSymbol(new Parameter(parameterNameAttr.getValue(),VCUnitDefinition.getInstance(unitSymbolAttr.getValue())));
		}
		java.util.List equationList = classElement.getChildren(PhysicsXMLTags.EQUATION_TAG);
		for (int j = 0; j < equationList.size(); j++){
			org.jdom.Element equationElement = (org.jdom.Element)equationList.get(j);
			org.jdom.Attribute expressionAttr = (org.jdom.Attribute)equationElement.getAttribute(PhysicsXMLTags.EQUATION_EXP_ATTR);
			modelComponent.addEquation(Expression.valueOf(expressionAttr.getValue()));
		}
		java.util.List connectorList = classElement.getChildren(PhysicsXMLTags.CONNECTOR_TAG);
		for (int j = 0; j < connectorList.size(); j++){
			org.jdom.Element connectorElement = (org.jdom.Element)connectorList.get(j);
			org.jdom.Attribute nameAttr = (org.jdom.Attribute)connectorElement.getAttribute(PhysicsXMLTags.CONNECTOR_NAME_ATTR);
			org.jdom.Attribute effortAttr = (org.jdom.Attribute)connectorElement.getAttribute(PhysicsXMLTags.CONNECTOR_EFFORT_ATTR);
			org.jdom.Attribute flowAttr = (org.jdom.Attribute)connectorElement.getAttribute(PhysicsXMLTags.CONNECTOR_FLOW_ATTR);
			String name = nameAttr.getValue();
			Variable effortVar = null;
			Variable flowVar = null;
			if (effortAttr != null){
				effortVar = (Variable)modelComponent.getSymbol(effortAttr.getValue());
			}
			if (flowAttr != null){
				flowVar = (Variable)modelComponent.getSymbol(flowAttr.getValue());
			}
			modelComponent.addConnector(new Connector(modelComponent, name, effortVar, flowVar));
		}
		oOModel.addModelComponent(modelComponent);
	}
	//
	// read connections
	//
	java.util.List connectionList = physicalModelRoot.getChildren(PhysicsXMLTags.CONNECTION_TAG);
	for (int i = 0; i < connectionList.size(); i++){
		org.jdom.Element connectionElement = (org.jdom.Element)connectionList.get(i);
		java.util.List connectorRefList = connectionElement.getChildren(PhysicsXMLTags.CONNECTORREF_TAG);
		Connection connection = new Connection();
		for (int j = 0; j < connectorRefList.size(); j++){
			org.jdom.Element connectorRefElement = (org.jdom.Element)connectorRefList.get(j);
			org.jdom.Attribute deviceAttr = (org.jdom.Attribute)connectorRefElement.getAttribute(PhysicsXMLTags.CONNECTORREF_DEVICE_ATTR);
			org.jdom.Attribute nameAttr = (org.jdom.Attribute)connectorRefElement.getAttribute(PhysicsXMLTags.CONNECTORREF_NAME_ATTR);
			ModelComponent device = oOModel.getModelComponent(deviceAttr.getValue());
			Connector connector = device.getConnector(nameAttr.getValue());
			connection.addConnector(connector);
		}
		oOModel.addConnection(connection);
	}

	return oOModel;
}

}