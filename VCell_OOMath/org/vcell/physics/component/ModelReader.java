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
public OOModel parse(String xmlString) throws ParseException {

	xmlString =
	 "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "\n" + 
//	 "<vcml xmlns=\"http://www.vcell.org/vcml/level1\" level=\"1\" version=\"1\">" + "\n" +
	 "<vcml>" + "\n" +
	 "\t<physicalModel>" + "\n" +
	 "\t\t<class name=\"triangle\">" + "\n" +
	 "\t\t\t<variable name=\"a\" unit=\"m\"/>" + "\n" +
	 "\t\t\t<variable name=\"b\" unit=\"m\"/>" + "\n" +
	 "\t\t\t<variable name=\"h\" unit=\"m\"/>" + "\n" +
	 "\t\t\t<variable name=\"area\" unit=\"m2\"/>" + "\n" +
	 "\t\t\t<equation exp=\"a*a+b*b-h*h\"/>" + "\n" +
	 "\t\t\t<equation exp=\"area - a*b/2\"/>" + "\n" +
	 "\t\t\t<connector name=\"pin_a\" effort=\"a\"/>" + "\n" +
	 "\t\t\t<connector name=\"pin_b\" effort=\"b\"/>" + "\n" +
	 "\t\t\t<connector name=\"pin_h\" effort=\"h\"/>" + "\n" +
	 "\t\t\t<connector name=\"pin_area\" effort=\"area\"/>" + "\n" +
	 "\t\t</class>" + "\n" +
	 "\t\t<class name=\"source_a\">" + "\n" +
	 "\t\t\t<parameter name=\"value\" unit=\"m\"/>" + "\n" +
	 "\t\t\t<variable name=\"a\" unit=\"m\"/>" + "\n" +
	 "\t\t\t<equation exp=\"value - 3\"/>" + "\n" +
	 "\t\t\t<equation exp=\"a - value\"/>" + "\n" +
	 "\t\t\t<connector name=\"pin\" effort=\"a\"/>" + "\n" +
	 "\t\t</class>" + "\n" +
	 "\t\t<class name=\"source_b\">" + "\n" +
	 "\t\t\t<parameter name=\"value\" unit=\"m\"/>" + "\n" +
	 "\t\t\t<variable name=\"b\" unit=\"m\"/>" + "\n" +
	 "\t\t\t<equation exp=\"value - 4\"/>" + "\n" +
	 "\t\t\t<equation exp=\"b - value\"/>" + "\n" +
	 "\t\t\t<connector name=\"pin\" effort=\"b\"/>" + "\n" +
	 "\t\t</class>" + "\n" +
	 "\t\t<connection>" + "\n" +
	 "\t\t\t<connectorRef device=\"triangle\" name=\"pin_a\"/>" + "\n" +
	 "\t\t\t<connectorRef device=\"source_a\" name=\"pin\"/>" + "\n" +
	 "\t\t</connection>" + "\n" +
	 "\t\t<connection>" + "\n" +
	 "\t\t\t<connectorRef device=\"triangle\" name=\"pin_b\"/>" + "\n" +
	 "\t\t\t<connectorRef device=\"source_b\" name=\"pin\"/>" + "\n" +
	 "\t\t</connection>" + "\n" +
	 "\t</physicalModel>" + "\n" +
	 "</vcml>";


	 System.out.println(xmlString);
	 
	//// <equation exp>
	//// <connector name, effortVar, flowVar>
	//
	// list of connections
	//// connection
	////// list of connectors
	
	org.jdom.Element root =  cbit.util.xml.XmlUtil.stringToXML(xmlString, null);
	

	//
	// make sure root is a "physicalModel"
	//
	if (!root.getName().equals("vcml")){
		throw new RuntimeException("this is not a vcml document");
	}
	java.util.List rootChildren = root.getChildren();
	if (rootChildren.size()!=1 || !((org.jdom.Element)rootChildren.get(0)).getName().equals("physicalModel")){
		throw new RuntimeException("this is not a physicalModel document");
	}
	org.jdom.Element physicalModelRoot = (org.jdom.Element)rootChildren.get(0);
	OOModel oOModel = new OOModel();
	java.util.List classList = physicalModelRoot.getChildren("class");
	for (int i = 0; i < classList.size(); i++){
		org.jdom.Element classElement = (org.jdom.Element)classList.get(i);
		org.jdom.Attribute classNameAttr = classElement.getAttribute("name");
		String className = classNameAttr.getValue();
		ModelComponent modelComponent = new ModelComponent(className);
		//
		// read variables, parameters, equations, connectors
		//
		java.util.List variableList = classElement.getChildren("variable");
		for (int j = 0; j < variableList.size(); j++){
			org.jdom.Element variableElement = (org.jdom.Element)variableList.get(j);
			org.jdom.Attribute variableNameAttr = (org.jdom.Attribute)variableElement.getAttribute("name");
			org.jdom.Attribute unitSymbolAttr = (org.jdom.Attribute)variableElement.getAttribute("unit");
			modelComponent.addSymbol(new Variable(variableNameAttr.getValue(),VCUnitDefinition.getInstance(unitSymbolAttr.getValue())));
		}
		java.util.List parameterList = classElement.getChildren("parameter");
		for (int j = 0; j < parameterList.size(); j++){
			org.jdom.Element parameterElement = (org.jdom.Element)parameterList.get(j);
			org.jdom.Attribute parameterNameAttr = (org.jdom.Attribute)parameterElement.getAttribute("name");
			org.jdom.Attribute unitSymbolAttr = (org.jdom.Attribute)parameterElement.getAttribute("unit");
			//org.jdom.Attribute expressionAttr = (org.jdom.Attribute)parameterElement.getAttribute("exp");
			modelComponent.addSymbol(new Parameter(parameterNameAttr.getValue(),VCUnitDefinition.getInstance(unitSymbolAttr.getValue())));
		}
		java.util.List equationList = classElement.getChildren("equation");
		for (int j = 0; j < equationList.size(); j++){
			org.jdom.Element equationElement = (org.jdom.Element)equationList.get(j);
			org.jdom.Attribute expressionAttr = (org.jdom.Attribute)equationElement.getAttribute("exp");
			modelComponent.addEquation(Expression.valueOf(expressionAttr.getValue()));
		}
		java.util.List connectorList = classElement.getChildren("connector");
		for (int j = 0; j < connectorList.size(); j++){
			org.jdom.Element connectorElement = (org.jdom.Element)connectorList.get(j);
			org.jdom.Attribute nameAttr = (org.jdom.Attribute)connectorElement.getAttribute("name");
			org.jdom.Attribute effortAttr = (org.jdom.Attribute)connectorElement.getAttribute("effort");
			org.jdom.Attribute flowAttr = (org.jdom.Attribute)connectorElement.getAttribute("flow");
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
	java.util.List connectionList = physicalModelRoot.getChildren("connection");
	for (int i = 0; i < connectionList.size(); i++){
		org.jdom.Element connectionElement = (org.jdom.Element)connectionList.get(i);
		java.util.List connectorRefList = connectionElement.getChildren("connectorRef");
		Connection connection = new Connection();
		for (int j = 0; j < connectorRefList.size(); j++){
			org.jdom.Element connectorRefElement = (org.jdom.Element)connectorRefList.get(j);
			org.jdom.Attribute deviceAttr = (org.jdom.Attribute)connectorRefElement.getAttribute("device");
			org.jdom.Attribute nameAttr = (org.jdom.Attribute)connectorRefElement.getAttribute("name");
			ModelComponent device = oOModel.getModelComponent(deviceAttr.getValue());
			Connector connector = device.getConnector(nameAttr.getValue());
			connection.addConnector(connector);
		}
		oOModel.addConnection(connection);
	}

	return oOModel;
}

}