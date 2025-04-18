/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.parser;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Vector;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import cbit.vcell.parser.ASTFuncNode.FunctionType;
import org.jmathml.ASTNode;
import org.jmathml.ASTToXMLElementVisitor;

/**
 * This class was generated by a SmartGuide.
 * 
 */
public class ExpressionMathMLParser {
	private LambdaFunction lambdaFunctions[] = new LambdaFunction[0];

public ExpressionMathMLParser(LambdaFunction[] argLambdaFunctions) {
	this.lambdaFunctions = argLambdaFunctions;
}


public Expression fromMathML(String mathML, String timeSymbolOverride) throws ExpressionException {
	if (mathML== null || mathML.length()==0) {
		throw new ExpressionException("Invalid null or empty MathML string");
	}
	Element rootElement;
	try {
		SAXBuilder builder = new SAXBuilder(false); 
		Document sDoc = builder.build(new StringReader(mathML));
		rootElement = sDoc.getRootElement();
	} catch (Exception e) {
		e.printStackTrace(System.out);
		throw new ExpressionException("Unable to parse the xml string.");
	}
	Expression exp = fromMathML(rootElement, timeSymbolOverride);
	
	return exp;
}


public Expression fromMathML(ASTNode mathmlASTNode, String timeSymbolOverride) throws ExpressionException {
	ASTToXMLElementVisitor printVisitor = new ASTToXMLElementVisitor();
	mathmlASTNode.accept(printVisitor);
	Element mathMLElement = printVisitor.getElement();
	return fromMathML(mathMLElement, timeSymbolOverride);
}

	public Expression fromMathML(Element mathElement, String timeSymbolOverride) throws ExpressionException {

		SimpleNode root = getRootNode(mathElement, timeSymbolOverride);
		Expression exp = new Expression(root);
		return exp;	
	}


public Expression fromMathML(org.w3c.dom.NodeList mathMLArg, String overrideTimeSymbol) throws ExpressionException {
    if (mathMLArg == null) {
        throw new IllegalArgumentException("Invalid null NodeList");
    } 

    //build jdom tree
    //take the first element
    int firstelem =0; 
    while (firstelem<mathMLArg.getLength() && mathMLArg.item(firstelem).getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {
    	firstelem++;
    }
    //if no element node was found throw an exception!
    if (firstelem>=mathMLArg.getLength()) {
        throw new ExpressionException("No element could be found in the NodeList!");
    }

    org.jdom2.Element mathElement = node2Element(mathMLArg.item(firstelem));
	Expression exp = fromMathML(mathElement, overrideTimeSymbol);

	//return flat version
	return exp.flatten();
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2005 4:50:20 PM)
 * @return cbit.vcell.parser.LambdaFunction[]
 */
public cbit.vcell.parser.LambdaFunction[] getFunctions() {
	return lambdaFunctions;
}


/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 2:33:36 PM)
 * @return cbit.vcell.parser.SimpleNode
 * @param nodeMathML org.jdom2.Element
 */
private SimpleNode getRootNode(Element nodeMathML, String timeSymbol) throws ExpressionException {
	if (nodeMathML.getName().equals(MathMLTags.APPLY)){
		//
		// <APPLY>
		//    <CMD>
		//    <ARG1>
		//    <ARG2>
		// </APPLY>
		//
		Element applyNode = nodeMathML;
		java.util.List children = applyNode.getChildren();
		if (children.size()==0) {
			throw new IllegalArgumentException("Invalid MathML! Empty APPLY element found!");
		}
		Element operation = (Element)children.get(0); 
		SimpleNode vcellOperationNode = null;

		if (operation.getName().equals(MathMLTags.EQUAL) ||
			operation.getName().equals(MathMLTags.NOT_EQUAL) ||
			operation.getName().equals(MathMLTags.GREATER) ||
			operation.getName().equals(MathMLTags.LESS) ||
			operation.getName().equals(MathMLTags.GREATER_OR_EQUAL) ||
			operation.getName().equals(MathMLTags.LESS_OR_EQUAL)){
			//
			// EQUAL can be interpreted either as relational operator or as an Assignment operator
			//
			String opString = ASTRelationalNode.getOperationFromMathML(operation.getName());
			vcellOperationNode = new ASTRelationalNode();
			((ASTRelationalNode)vcellOperationNode).setOperationFromToken(opString);
		} else if (operation.getName().equals(MathMLTags.AND)){
			vcellOperationNode = new ASTAndNode();
		} else if (operation.getName().equals(MathMLTags.OR)){
			vcellOperationNode = new ASTOrNode();
		} else if (operation.getName().equals(MathMLTags.XOR)){
			// XOR(A,B) --> OR(AND(A,NOT(B)),AND(NOT(A),B))
			// NOT(B)
			ASTNotNode notNode = new ASTNotNode();
			notNode.jjtAddChild(getRootNode((Element)children.get(2), timeSymbol));
			// AND(A,NOT(B))
			ASTAndNode andNode1 = new ASTAndNode();
			andNode1.jjtAddChild(getRootNode((Element)children.get(1), timeSymbol));
			andNode1.jjtAddChild(notNode);
			// NOT(A)
			ASTNotNode notNode2 = new ASTNotNode();
			notNode2.jjtAddChild(getRootNode((Element)children.get(1), timeSymbol));
			// AND(NOT(A),B)
			ASTAndNode andNode2 = new ASTAndNode();
			andNode2.jjtAddChild(notNode2);
			andNode2.jjtAddChild(getRootNode((Element)children.get(2), timeSymbol));
			// OR(AND(A,NOT(B)),AND(NOT(A),B))
			ASTOrNode orNode = new ASTOrNode();
			orNode.jjtAddChild(andNode1);
			orNode.jjtAddChild(andNode2);
			return orNode;
		} else if (operation.getName().equals(MathMLTags.NOT)){
			vcellOperationNode = new ASTNotNode();
		} else if (operation.getName().equals(MathMLTags.PLUS)){
			vcellOperationNode = new ASTAddNode();
		} else if (operation.getName().equals(MathMLTags.MINUS)){
			//
			// if unary minus
			//
			if (children.size()==2){ // if only operator and one argument
				vcellOperationNode = new ASTMinusTermNode();
				vcellOperationNode.jjtAddChild(getRootNode((Element)children.get(1), timeSymbol));
				return vcellOperationNode;
			} else if (children.size()==3){ // operator and two arguments
				//
				// if binary minus, vcell is unary so must SUB(A,B) --> ADD(A,MINUS(B))
				//
				vcellOperationNode = new ASTAddNode();
				vcellOperationNode.jjtAddChild(getRootNode((Element)children.get(1), timeSymbol));
				ASTMinusTermNode minusNode = new ASTMinusTermNode();
				minusNode.jjtAddChild(getRootNode((Element)children.get(2), timeSymbol));
				vcellOperationNode.jjtAddChild(minusNode);
				return vcellOperationNode;
			} else{
				throw new RuntimeException("expecting either 1 or 2 arguments for "+MathMLTags.MINUS);
			}
		} else if (operation.getName().equals(MathMLTags.TIMES)){
			vcellOperationNode = new ASTMultNode();
				
		} else if (operation.getName().equals(MathMLTags.DIVIDE)){
			//
			// mathML divides are alway binary, vcell is unary so must DIVIDE(NUM,DEN) --> MULT(NUM,INV(DEN))
			//
			vcellOperationNode = new ASTMultNode();
			vcellOperationNode.jjtAddChild(getRootNode((Element)children.get(1), timeSymbol));
			ASTInvertTermNode invNode = new ASTInvertTermNode();
			invNode.jjtAddChild(getRootNode((Element)children.get(2), timeSymbol));
			vcellOperationNode.jjtAddChild(invNode);
			return vcellOperationNode;
		} else if (operation.getName().equals(MathMLTags.LOG_10)){
			//
			// doesn't have a direct mapping, but can still be supported.
			//
			// the 'if' loop handles
			//
			//  LOG_10(A) --> LN(A)/LN(10.0)
			//
			// the 'else' loop handles the logbase case
			//
			// - <apply>
			//  	  <log /> 
			//		  - <logbase>
			//		      <cn>2</cn> 
			//		    </logbase>			=> log_2 4 (log of 4 to the base 2) = LN(4)/LN(2)
			//		  - <apply>
			//			  <cn>4</cn> 
			//	  	    </apply>
			//	</apply>
			//
			vcellOperationNode = new ASTMultNode();
			if (children.size()==2){ 	// This is the case where base is 10. 
				ASTFuncNode vcellOpNode1 = new ASTFuncNode();						// LN(A)
				vcellOpNode1.setFunctionType(FunctionType.LOG);
				vcellOpNode1.jjtAddChild(getRootNode((Element)children.get(1), timeSymbol));
				
				ASTFuncNode vcellOpNode2 = new ASTFuncNode();						// LN(10)
				vcellOpNode2.setFunctionType(FunctionType.LOG);
				ASTFloatNode floatNode = new ASTFloatNode(10.0);
				vcellOpNode2.jjtAddChild(floatNode);
				ASTInvertTermNode invVcellOpNode = new ASTInvertTermNode();			//  1 / LN(10)
				invVcellOpNode.jjtAddChild(vcellOpNode2);
				
				vcellOperationNode.jjtAddChild(vcellOpNode1);						// LN(A) * (1 / LN(10)) = LOG_10 (A)
				vcellOperationNode.jjtAddChild(invVcellOpNode);
			}else{						// This is the case where the base is other than 10 or e
				ASTFuncNode vcellOpNode1 = new ASTFuncNode();						// LN(Arg)
				vcellOpNode1.setFunctionType(FunctionType.LOG);
				vcellOpNode1.jjtAddChild(getRootNode((Element)children.get(2), timeSymbol));
				
				ASTFuncNode vcellOpNode2 = new ASTFuncNode();						// LN(Base)
				vcellOpNode2.setFunctionType(FunctionType.LOG);
				Element logBaseMathMLNode = (Element)children.get(1);
				vcellOpNode2.jjtAddChild(getRootNode((Element)logBaseMathMLNode.getChildren().get(0), timeSymbol));
				ASTInvertTermNode invVcellOpNode = new ASTInvertTermNode();			//  1 / LN(Base)
				invVcellOpNode.jjtAddChild(vcellOpNode2);
				
				vcellOperationNode.jjtAddChild(vcellOpNode1);						// LN(Arg) * (1 / LN(Base)) = LOG_Base (Arg)
				vcellOperationNode.jjtAddChild(invVcellOpNode);
			}
			return vcellOperationNode;
		} else if (operation.getName().equals(MathMLTags.ROOT)){
			//
			// doesn't have a direct mapping, but can still be supported.
			//
			//  ROOT(A,R) --> POW(A,INV(R))
			//  <apply>
			//    <root/>
			//    <degree>...exponentExp...</degree>  (optional with 2 as default)
			//    ...baseExp...
			//  </apply>
			//
			String powFunctionName = ASTFuncNode.getVCellFunctionNameFromMathMLFuncName(MathMLTags.POWER);
			vcellOperationNode = new ASTFuncNode();
			((ASTFuncNode)vcellOperationNode).setFunctionFromName(powFunctionName);
			if (children.size()==2){
				vcellOperationNode.jjtAddChild(getRootNode((Element)children.get(1), timeSymbol));
				ASTFloatNode floatNode = new ASTFloatNode(0.5);
				vcellOperationNode.jjtAddChild(floatNode);
			}else{
				vcellOperationNode.jjtAddChild(getRootNode((Element)children.get(2), timeSymbol));
				ASTInvertTermNode invNode = new ASTInvertTermNode();
				ASTMultNode multNode = new ASTMultNode();
				Element degreeMathMLNode = (Element)children.get(1);
				invNode.jjtAddChild(getRootNode((Element)degreeMathMLNode.getChildren().get(0), timeSymbol));
				multNode.jjtAddChild(new ASTFloatNode(1.0));
				multNode.jjtAddChild(invNode);
				vcellOperationNode.jjtAddChild(multNode);
			}
			return vcellOperationNode;
		} else if (operation.getName().equals(MathMLTags.IDENTIFIER)){
			// if there is a <ci> tag right after an <apply> tag, it must (?) be a lambda function; handle accordingly
			//
			// The lambda function (say, 'f') is used as follows ...
			// 		<apply>
			//			<ci> f </ci>
			// 			<cn> 3 </cn>
			//				....
			//		</apply>
			// The 'ci' element for 'f' is followed by other 'ci' or 'cn' elements (could also be expressions - more apply nodes)
			// 

			// get the lambda function name from the Identifier node
			java.util.List lFnList = operation.getContent();
			String lFnName = ((org.jdom2.Text)lFnList.get(0)).getTextTrim();

			// get the list of lambda functions filled out in SBVCTranslator, find the fn matching 'lFn'
			LambdaFunction theLambdaFn = null;
			for (int i = 0; i < lambdaFunctions.length; i++) {
				if (lambdaFunctions[i].getName().equals(lFnName)) {
					theLambdaFn = lambdaFunctions[i];
					break;
				}
			}
			
			Iterator applyNodeChildrenIterator = children.iterator();
			Expression[] argExprs = new Expression[theLambdaFn.getFunctionArgs().length];
			int count = 0;
			
			while (applyNodeChildrenIterator.hasNext()) {
				Element child = (Element)applyNodeChildrenIterator.next();
				// ignore the first child of the 'apply' node (which is the labmda function identifier/name)
				if ( ((org.jdom2.Text)(child.getContent()).get(0)).getTextTrim().equals(theLambdaFn.getName())) {
					continue;
				}
				SimpleNode childNode = getRootNode(child, timeSymbol);
				argExprs[count] = new Expression(childNode);
				count++;
			}
			Expression finalExpr = theLambdaFn.substitute(argExprs);
			return finalExpr.getRootNode();
		} else {
			String cellFunctionNameFromMathMLFuncName = ASTFuncNode.getVCellFunctionNameFromMathMLFuncName(operation.getName());
			if (cellFunctionNameFromMathMLFuncName != null){
				vcellOperationNode = new ASTFuncNode();
				((ASTFuncNode)vcellOperationNode).setFunctionFromName(cellFunctionNameFromMathMLFuncName);
			} else{
				throw new ExpressionException("cannot translate "+operation.getName()+" from MathML");
			}
		}
		//
		// MathML "APPLY" places the operator and arguments as siblings
		// VCell places arguments as children of operator
		//
		// add children to vcell operation node
		//
		for (int i = 1; i < children.size(); i++){
			Element childMathML = (Element)children.get(i);
			SimpleNode n = getRootNode(childMathML, timeSymbol);
			vcellOperationNode.jjtAddChild(n);
		}
		return vcellOperationNode;

	}else if (nodeMathML.getName().equals(MathMLTags.PIECEWISE)){
		// 
		// <piecewise>                           This is translated to VCell as follows:
		//    <piece>                            
		//       <apply>                         <addNode>
		//         ...expression1                    <multNode>
		//       </apply>                                expression1
		//       <apply>                                 condition1
		//         ...condition1                     </multNode>
		//       </apply>                            <multNode>
		//    </piece>                                   expression2
		//    <piece>                                    condition2
		//       <apply>                             </multNode>
		//         ...expression2                    <multNode>
		//       </apply>                                expressionOtherwise
		//       <apply>                                 <notNode>
		//         ...condition2                             <orNode>
		//       </apply>                                        condition1
		//    </piece>                                           condition2
		//    <otherwise>                                    </orNode>
		//       <apply>                                 </notNode>   
		//          ...expressionOtherwise           </multNode>    
		//       </apply>                        </addNode>    
		//    </othewise>                        
		// </piecewise>                             
		//
		//
		//
		ASTAddNode addNode = new ASTAddNode();
		Element piecewise = nodeMathML;
		Vector<SimpleNode> conditionExpList = new Vector<SimpleNode>();
		java.util.List<Element> children = piecewise.getChildren();
		for (int i = 0; i < children.size(); i++){
			Element child = children.get(i);
			if (child.getName().equals(MathMLTags.PIECE)){
				int numChildren = child.getChildren().size();
				Element pieceExp = (Element)child.getChildren().get(0);
				Element pieceCond = (Element)child.getChildren().get(1);
				SimpleNode vcellExp = getRootNode(pieceExp, timeSymbol);
				SimpleNode vcellCond = getRootNode(pieceCond, timeSymbol);
				//
				// save condition for later (if "otherwise" element is used);
				//
				conditionExpList.add(vcellCond); 
				//
				// add term for this conditional expression (pieceExp * pieceCond)
				//
				ASTMultNode multNode = new ASTMultNode();
				multNode.jjtAddChild(vcellExp);
				multNode.jjtAddChild(vcellCond);
				addNode.jjtAddChild(multNode);
			}else if (child.getName().equals(MathMLTags.OTHERWISE)){
				Element pieceExp = (Element)child.getChildren().get(0);
				SimpleNode vcellExp = getRootNode(pieceExp, timeSymbol);
				boolean bOthewiseValueZero = false;
				if (vcellExp instanceof ASTFloatNode && ((ASTFloatNode)vcellExp).value == 0.0){
					bOthewiseValueZero = true;
				}
				//
				// add term for this conditional expression (pieceExp * pieceCond)
				//
				ASTMultNode multNode = new ASTMultNode();
				multNode.jjtAddChild(vcellExp);
				//
				// gather conditionals
				//
				if (conditionExpList.size() > 1) {
					if (bOthewiseValueZero){
						return addNode;
					}
					ASTOrNode orNode = new ASTOrNode();
					for (int j = 0; j < conditionExpList.size(); j++){
						orNode.jjtAddChild(conditionExpList.elementAt(j));
					}
					//
					// form "otherwise" conditional expression
					//
					ASTNotNode notNode = new ASTNotNode();
					notNode.jjtAddChild(orNode);
					multNode.jjtAddChild(notNode);
					addNode.jjtAddChild(multNode);
				} else if (conditionExpList.size() == 1){		// only one condition (piece) in piecewise.
					if (bOthewiseValueZero){
						return (SimpleNode) addNode.jjtGetChild(0);
					}
					ASTNotNode notNode = new ASTNotNode();
					notNode.jjtAddChild(conditionExpList.elementAt(0));
					multNode.jjtAddChild(notNode);
					addNode.jjtAddChild(multNode);
				} else if (conditionExpList.size() < 1) {		// no piece elements in piecewise
					addNode.jjtAddChild(vcellExp);
				}
			}
		}
		return addNode;
	} else if (nodeMathML.getName().equals(MathMLTags.CONSTANT)){
		org.jdom2.Attribute typeAttribute = nodeMathML.getAttribute("type");
		if (typeAttribute == null || typeAttribute.getValue().equals("integer") || typeAttribute.getValue().equals("real")) {
			String floatString = nodeMathML.getTextTrim();
			return new ASTFloatNode(Double.parseDouble(floatString));
		} else if (typeAttribute.getValue().equals("rational")) {
			java.util.List children = nodeMathML.getContent();
			org.jdom2.Text numeratorTxt = (org.jdom2.Text)children.get(0);
			long numerator = Long.parseLong(numeratorTxt.getTextTrim());
			org.jdom2.Text denominatorTxt = (org.jdom2.Text)children.get(2);
			long denominator = Long.parseLong(denominatorTxt.getTextTrim());
			double value = ((double)numerator)/denominator;
			return new ASTFloatNode(value);
		} else if (typeAttribute.getValue().equals("e-notation")) {
			java.util.List children = nodeMathML.getContent();
			org.jdom2.Text mantissaTxt = (org.jdom2.Text)children.get(0);
			double mantissa = Double.parseDouble(mantissaTxt.getTextTrim());
			org.jdom2.Text exponentTxt = (org.jdom2.Text)children.get(2);
			long exponent = Long.parseLong(exponentTxt.getTextTrim());
			double value = Double.parseDouble(mantissa+"E"+exponent);
			return new ASTFloatNode(value);
		} else {
			throw new RuntimeException("MathML Parsing error : constant type "+typeAttribute.getValue() + " not supported.");
		}
	} else if (nodeMathML.getName().equals(MathMLTags.IDENTIFIER)){
		ASTIdNode idNode = new ASTIdNode();
		idNode.name = nodeMathML.getTextTrim();
		return idNode;
	} else if (nodeMathML.getName().equals(MathMLTags.FALSE)){
		ASTFloatNode falseNode = new ASTFloatNode(0.0);
		return falseNode;
	} else if (nodeMathML.getName().equals(MathMLTags.TRUE)){
		ASTFloatNode trueNode = new ASTFloatNode(1.0);
		return trueNode;
	} else if (nodeMathML.getName().equals(MathMLTags.E)){
		ASTFloatNode eNode = new ASTFloatNode(Math.E);
		return eNode;
	} else if (nodeMathML.getName().equals(MathMLTags.PI)){
		ASTFloatNode piNode = new ASTFloatNode(Math.PI);
		return piNode;
	} else if (nodeMathML.getName().equals(MathMLTags.BVAR)){
		SimpleNode bVarNode = getRootNode((Element)nodeMathML.getChildren().get(0), timeSymbol);
		return bVarNode;
	} else if (nodeMathML.getName().equals(MathMLTags.MATH)){
		SimpleNode node = getRootNode((Element)nodeMathML.getChildren().get(0), timeSymbol);
		return node;
//	} else if (nodeMathML.getName().equals(MathMLTags.NOT_A_NUMBER)){
//		ASTIdNode idNode = new ASTIdNode();
//		idNode.name = "_NaN_";
//		return idNode;
	} else if (nodeMathML.getName().equals(MathMLTags.CSYMBOL)){
		// check that the CSYMBOL element represents 'time' (possible options : time, delay)
		if ((nodeMathML.getAttributeValue(MathMLTags.DEFINITIONURL)).indexOf("time") > -1) {
			ASTIdNode idNode = new ASTIdNode();
			idNode.name = timeSymbol;
			return idNode;
		} else {
			throw new ExpressionException("csymbol node type "+nodeMathML.getAttributeValue(MathMLTags.DEFINITIONURL)+" not supported yet");
		}	
	} else if (nodeMathML.getName().equals(MathMLTags.NOT_A_NUMBER)){
		ASTFloatNode zeroNode = new ASTFloatNode(0.0);
		ASTFloatNode zeroNode2 = new ASTFloatNode(0.0);
		ASTInvertTermNode invNode = new ASTInvertTermNode();
		invNode.jjtAddChild(zeroNode2);
		ASTMultNode multNode = new ASTMultNode();
		multNode.jjtAddChild(zeroNode);
		multNode.jjtAddChild(invNode);
		return multNode;
	} else if (nodeMathML.getName().equals(MathMLTags.INFINITY)) {
		ASTFloatNode infNode = new ASTFloatNode(Double.MAX_VALUE);
		return infNode;
	}else{
		throw new ExpressionException("node type '"+nodeMathML.getName()+"' not supported yet");
	}		
}

public org.jdom2.Element node2Element(org.w3c.dom.Node nodeArg) throws ExpressionException{
	if (nodeArg== null) {
		throw new IllegalArgumentException("Invalid null Node");
	}
	
	//process node
	org.jdom2.Element element = null;
	
	if (nodeArg.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE){
		element = new org.jdom2.Element(nodeArg.getNodeName());

		//process attributes
		org.w3c.dom.NamedNodeMap attrMap = nodeArg.getAttributes();
		if (attrMap !=null) {
			for (int i = 0; i < attrMap.getLength(); i++){
				org.w3c.dom.Node temp = attrMap.item(i);

				if (temp.getNodeType()== org.w3c.dom.Node.ATTRIBUTE_NODE) {
					org.jdom2.Attribute attribute = new org.jdom2.Attribute(temp.getLocalName(), temp.getNodeValue());
					element.setAttribute(attribute);
				} else {
					throw new ExpressionException("Unknown node "+temp.getNodeName()+" type "+ temp.getNodeType() + " when processing attributes!");
				}
			}
		}
		
		//process content
		//get nodes
		org.w3c.dom.NodeList nodeList = nodeArg.getChildNodes();

		if (nodeList.getLength()==1) {
			element.addContent(nodeList.item(0).getNodeValue());
		}
		else {
			for (int i = 0; i < nodeList.getLength(); i++){
				if (nodeList.item(i).getNodeType()==org.w3c.dom.Node.ELEMENT_NODE) {
					element.addContent(node2Element(nodeList.item(i)));
				}
			}
		}
	} else {
		throw new ExpressionException("Unknown node "+nodeArg.getNodeName()+" type "+ nodeArg.getNodeType());
	}
	return element;
}
}
