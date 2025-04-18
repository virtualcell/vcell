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

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import cbit.vcell.parser.ASTFuncNode.FunctionType;
import org.jdom2.output.XMLOutputter;

/**
 * This class was generated by a SmartGuide.
 * 
 */
public class ExpressionMathMLPrinter {
	private SimpleNode rootNode = null;
	public enum MathType {REAL, BOOLEAN};
	public enum Dialect {SBML_SUBSET, GENERAL};

	public final static Namespace MathMLNamespace = Namespace.getNamespace("http://www.w3.org/1998/Math/MathML");

ExpressionMathMLPrinter (SimpleNode rootNode) {
	this.rootNode = rootNode;
}
/**
 * Insert the method's description here.
 * Creation date: (2/8/2002 5:51:09 PM)
 * @return java.lang.String
 */
String getMathML(boolean bOnlyMathMLFragment, MathType desiredMathType, Dialect dialect) throws ExpressionException, java.io.IOException {
	XMLOutputter xmlwriter = new XMLOutputter();
	Element mathElement = new Element("math", MathMLNamespace);
	mathElement.addContent(getMathML(rootNode, desiredMathType, dialect));
	if (!bOnlyMathMLFragment) {
		Document mathDoc = new Document(mathElement);
		return xmlwriter.outputString(mathDoc);
	}
	return xmlwriter.outputString(mathElement);
}

/**
 * castChild :
 * @param element
 * @param outputType - returns the element converted to real if needed if TRUE; converts to boolean if FALSE
 * @param inputType	- is the input element type real - TRUE; if boolean - FALSE
 * @return
 */
private Element castChild(Element element, MathType outputType, MathType inputType) {
	Element castedElement = null;
	if (outputType == inputType) {
		castedElement = element;
//	} else if (inputType.equals(MathType.REAL) && outputType.equals(MathType.BOOLEAN)) {
//		// convert a REAL to BOOLEAN piecewise.
//		// <piecewise>                           
//		//    <piece>                            
//		//       <cn> 1 < /cn>                        
//		//       <apply>                                 
//		//          <neq/>
//		//  		<cn> 0 </cn>
//		//	  		<apply>
//		//				realElementCondn - 'element'
//		//			</apply>
//		//       </apply>                            
//		//    </piece>                                  
//		//    <otherwise>                                    
//		//       <cn> 0.0 </cn>
//		//    </othewise>                       
//		// </piecewise>                             
//
//		// Construct the piecewise element : create piece and otherwise separately and add.
//		Element piecewiseElement = new Element(MathMLTags.PIECEWISE, MathMLNamespace);
//		// construct the piece element :  create const (1.0) element and apply element and add to piece - refer to pseudocode above.
//		Element pieceElement  = new Element(MathMLTags.PIECE, MathMLNamespace);
//		Element constElement_1 = new Element(MathMLTags.CONSTANT, MathMLNamespace);
//		constElement_1.addContent("1.0");
//		Object a = MathMLTags.TRUE;
//		Element applyElement = new Element(MathMLTags.APPLY, MathMLNamespace);	
//		Element neqElement = new Element(MathMLTags.NOT_EQUAL, MathMLNamespace);
//		Element constElement_0 = new Element(MathMLTags.CONSTANT, MathMLNamespace);
//		constElement_0.addContent("0.0");
//		applyElement.addContent(neqElement);
//		applyElement.addContent(constElement_0);
//		applyElement.addContent(element);
//		pieceElement.addContent(constElement_1);
//		pieceElement.addContent(applyElement);
//		// construct the otherwise element : add
//		Element otherwiseElement = new Element(MathMLTags.OTHERWISE, MathMLNamespace);
//		Element constElement = new Element(MathMLTags.CONSTANT, MathMLNamespace);
//		constElement.addContent("0.0");
//		otherwiseElement.addContent(constElement);
//		// Now put together the piecewise element with the piece and otherwise. 
//		piecewiseElement.addContent(pieceElement);
//		piecewiseElement.addContent(otherwiseElement);
//		castedElement = piecewiseElement;
	} else if (inputType.equals(MathType.REAL) && outputType.equals(MathType.BOOLEAN)) {
		castedElement = element;
//		// convert a REAL to BOOLEAN piecewise.
//		// <apply>
//		//    <neq/>
//		//    <cn> 0 </cn>
//		//	  <apply>
//		//		realElementCondn - 'element'
//		//	  </apply>
//		// </apply>
//
//		Element applyElement = new Element(MathMLTags.APPLY, MathMLNamespace);
//		Element neqElement = new Element(MathMLTags.NOT_EQUAL, MathMLNamespace);
//		Element constElement_0 = new Element(MathMLTags.CONSTANT, MathMLNamespace);
//		constElement_0.addContent("0.0");
//		applyElement.addContent(neqElement);
//		applyElement.addContent(constElement_0);
//		applyElement.addContent(element);
//		castedElement = applyElement;
	} else if (inputType.equals(MathType.BOOLEAN) && outputType.equals(MathType.REAL)) {
		castedElement = element;
//		// convert a BOOLEAN to REAL piecewise.
//		// <piecewise>
//		//    <piece>
//		//       <cn> 1 < /cn>
//		//       <apply>
//		//         ...booleanElementCondn - - 'element'
//		//       </apply>
//		//    </piece>
//		//    <otherwise>
//		//       <cn> 0.0 </cn>
//		//    </othewise>
//		// </piecewise>
//
//		// Construct the piecewise element : create piece and otherwise separately and add.
//		Element piecewiseElement = new Element(MathMLTags.PIECEWISE, MathMLNamespace);
//		// construct the piece element :  create const (1.0) element and apply element (incoming argument) and add to piece - refer to pseudocode above.
//		Element pieceElement  = new Element(MathMLTags.PIECE, MathMLNamespace);
//		Element constElement_1 = new Element(MathMLTags.CONSTANT, MathMLNamespace);
//		constElement_1.addContent("1.0");
//		Element applyElement = element;
//		pieceElement.addContent(constElement_1);
//		pieceElement.addContent(applyElement);
//		// construct the otherwise element : add
//		Element otherwiseElement = new Element(MathMLTags.OTHERWISE, MathMLNamespace);
//		Element constElement_0 = new Element(MathMLTags.CONSTANT, MathMLNamespace);
//		constElement_0.addContent("0.0");
//		otherwiseElement.addContent(constElement_0);
//		// Now put together the piecewise element with the piece and otherwise.
//		piecewiseElement.addContent(pieceElement);
//		piecewiseElement.addContent(otherwiseElement);
//		castedElement = piecewiseElement;
	}
	
	return castedElement;
}

/**
 * Insert the method's description here.
 * Creation date: (2/8/2002 5:53:06 PM)
 * @return java.lang.String
 */
public static String getMathML(Expression exp, boolean bOnlyMathMLFragment) throws ExpressionException, java.io.IOException {
	return getMathML(exp, bOnlyMathMLFragment, MathType.REAL, Dialect.SBML_SUBSET);
}

public static String getMathML(Expression exp, boolean bOnlyMathMLFragment, MathType desiredMathType, Dialect dialect) throws ExpressionException, java.io.IOException {
	ExpressionMathMLPrinter mathMLPrinter = new ExpressionMathMLPrinter(exp.getRootNode());
	return mathMLPrinter.getMathML(bOnlyMathMLFragment, desiredMathType, dialect);
}

private Element getMathML(Node node, MathType desiredMathType, Dialect dialect) throws ExpressionException {
	//
	// Equals
	//
	if (node instanceof ASTRelationalNode){
		Element applyNode = new Element(MathMLTags.APPLY, MathMLNamespace);
		applyNode.addContent(new Element(((ASTRelationalNode)node).getMathMLElementTag(), MathMLNamespace));
		applyNode.addContent(getMathML(node.jjtGetChild(0), MathType.REAL, dialect));
		applyNode.addContent(getMathML(node.jjtGetChild(1), MathType.REAL, dialect));
		return castChild(applyNode, desiredMathType, MathType.BOOLEAN);
	}else if (node instanceof ASTAndNode){
		Element applyNode = new Element(MathMLTags.APPLY, MathMLNamespace);
		applyNode.addContent(new Element(MathMLTags.AND, MathMLNamespace));
		for (int i = 0; i < node.jjtGetNumChildren(); i++){
			applyNode.addContent(getMathML(node.jjtGetChild(i), MathType.BOOLEAN, dialect));	
		}
		return castChild(applyNode, desiredMathType, MathType.BOOLEAN);
	}else if (node instanceof ASTOrNode){
		Element applyNode = new Element(MathMLTags.APPLY, MathMLNamespace);
		applyNode.addContent(new Element(MathMLTags.OR, MathMLNamespace));
		for (int i = 0; i < node.jjtGetNumChildren(); i++){
			applyNode.addContent(getMathML(node.jjtGetChild(i), MathType.BOOLEAN, dialect));	
		}
		return castChild(applyNode, desiredMathType, MathType.BOOLEAN);
	}else if (node instanceof ASTNotNode){
		Element applyNode = new Element(MathMLTags.APPLY, MathMLNamespace);
		applyNode.addContent(new Element(MathMLTags.NOT, MathMLNamespace));
		applyNode.addContent(getMathML(node.jjtGetChild(0), MathType.BOOLEAN, dialect));
		return castChild(applyNode, desiredMathType, MathType.BOOLEAN);
	}else if (node instanceof ASTPowerNode){
		Element applyNode = new Element(MathMLTags.APPLY, MathMLNamespace);
		applyNode.addContent(new Element(MathMLTags.POWER, MathMLNamespace));
		applyNode.addContent(getMathML(node.jjtGetChild(0), MathType.REAL, dialect));
		applyNode.addContent(getMathML(node.jjtGetChild(1), MathType.REAL, dialect));
		return castChild(applyNode, desiredMathType, MathType.REAL);
	}else if (node instanceof DerivativeNode){
		Element applyNode = new Element(MathMLTags.APPLY, MathMLNamespace);
		applyNode.addContent(new Element(MathMLTags.DIFFERENTIAL, MathMLNamespace));
		Element bvarNode = new Element(MathMLTags.BVAR, MathMLNamespace);
		Element idNode = new Element(MathMLTags.IDENTIFIER, MathMLNamespace);
		idNode.setText(((DerivativeNode)node).independentVar);
		bvarNode.addContent(idNode);
		applyNode.addContent(bvarNode);
		applyNode.addContent(getMathML(node.jjtGetChild(0), MathType.REAL, dialect));
		return castChild(applyNode, desiredMathType, MathType.REAL);
	}else if (node instanceof ASTLaplacianNode){
		throw new RuntimeException("ExpressionMathMLPrinter.getMathML(), laplacian operator not yet supported");
	}else if (node instanceof ASTAssignNode){
		Element applyNode = new Element(MathMLTags.APPLY, MathMLNamespace);
		applyNode.addContent(new Element(MathMLTags.EQUAL, MathMLNamespace));
		applyNode.addContent(getMathML(node.jjtGetChild(0), MathType.REAL, dialect));
		applyNode.addContent(getMathML(node.jjtGetChild(1), MathType.REAL, dialect));
		return castChild(applyNode, desiredMathType, MathType.REAL);
	}else if (node instanceof ASTAddNode){
		Element applyNode = new Element(MathMLTags.APPLY, MathMLNamespace);
		applyNode.addContent(new Element(MathMLTags.PLUS, MathMLNamespace));
		for (int i = 0; i < node.jjtGetNumChildren(); i++){
			applyNode.addContent(getMathML(node.jjtGetChild(i), MathType.REAL, dialect));	
		}
		return castChild(applyNode, desiredMathType, MathType.REAL);
	}else if (node instanceof ASTFloatNode){
		Element floatNode = new Element(MathMLTags.CONSTANT, MathMLNamespace);
		Double value = ((ASTFloatNode)node).value;
		floatNode.addContent(value.toString());
		//floatNode.setAttribute(new Attribute(MathMLTags.CellML_units,MathMLTags.DIMENSIONLESS,Namespace.getNamespace(cbit.util.XMLTags.CELLML_NAMESPACE_PREFIX, cbit.util.XMLTags.CELLML_NAMESPACE_URI) ));
		return castChild(floatNode, desiredMathType, MathType.REAL);
	}else if (node instanceof ASTFuncNode){
		ASTFuncNode funcNode = (ASTFuncNode)node;
		//
		// functions that have direct MathML mappings
		//
		if (funcNode.getMathMLName()!=null){
			Element applyNode = new Element(MathMLTags.APPLY, MathMLNamespace);
			String mathMLFunctionName = funcNode.getMathMLName();
			applyNode.addContent(new Element(mathMLFunctionName, MathMLNamespace));
			for (int i = 0; i < node.jjtGetNumChildren(); i++){
				applyNode.addContent(getMathML(node.jjtGetChild(i), MathType.REAL, dialect));	
			}
			return castChild(applyNode, desiredMathType, MathType.REAL);
		//
		// functions that do not have direct MathML mappings
		//
		}else if (funcNode.getFunction() == FunctionType.SQRT){
			Element applyNode = new Element(MathMLTags.APPLY, MathMLNamespace);
			applyNode.addContent(new Element(MathMLTags.ROOT, MathMLNamespace));
			Element degreeNode = new Element(MathMLTags.DEGREE, MathMLNamespace);
			applyNode.addContent(degreeNode);
			Element constNode = new Element(MathMLTags.CONSTANT, MathMLNamespace);
			constNode.setAttribute("type", "integer");
			constNode.addContent("2");
			degreeNode.addContent(constNode);
			applyNode.addContent(getMathML(node.jjtGetChild(0), MathType.REAL, dialect));	
			//constNode.setAttribute(new Attribute(MathMLTags.CellML_units,MathMLTags.DIMENSIONLESS,Namespace.getNamespace(cbit.util.XMLTags.CELLML_NAMESPACE_PREFIX, cbit.util.XMLTags.CELLML_NAMESPACE_URI)));
			return castChild(applyNode, desiredMathType, MathType.REAL);
		}else if (funcNode.getFunction() == FunctionType.ATAN2){
			throw new ExpressionException("cannot translate atan(a,b) into MathML");
		}else if (funcNode.getFunction() == FunctionType.MIN){
//			if (dialect == Dialect.SBML_SUBSET) {
//				/* a < b ? a : b;
//					 <piecewise>
//						<piece>
//						   <ci> a < /ci>
//						   <apply>
//							 (a < b)
//						   </apply>
//						</piece>
//						<otherwise>
//						   <ci> b </ci>
//						</othewise>
//					 </piecewise>
//				 */
//
//				// Construct the piecewise element : create piece and otherwise separately and add.
//				Element piecewiseElement = new Element(MathMLTags.PIECEWISE, MathMLNamespace);
//				// construct the piece element :
//				Element pieceElement = new Element(MathMLTags.PIECE, MathMLNamespace);
//				pieceElement.addContent(getMathML(node.jjtGetChild(0), MathType.REAL, dialect));
//				Element applyElement = new Element(MathMLTags.APPLY, MathMLNamespace);
//				Element condnElement = new Element(MathMLTags.LESS, MathMLNamespace);
//				applyElement.addContent(condnElement);
//				applyElement.addContent(getMathML(node.jjtGetChild(0), MathType.REAL, dialect));
//				applyElement.addContent(getMathML(node.jjtGetChild(1), MathType.REAL, dialect));
//				pieceElement.addContent(applyElement);
//				// construct the otherwise element : add
//				Element otherwiseElement = new Element(MathMLTags.OTHERWISE, MathMLNamespace);
//				otherwiseElement.addContent(getMathML(node.jjtGetChild(1), MathType.REAL, dialect));
//				// Now put together the piecewise element with the piece and otherwise.
//				piecewiseElement.addContent(pieceElement);
//				piecewiseElement.addContent(otherwiseElement);
//				return castChild(piecewiseElement, desiredMathType, MathType.REAL);
//			}else{ // (dialect == Dialect.GENERAL)
				Element applyNode = new Element(MathMLTags.APPLY, MathMLNamespace);
				applyNode.addContent(new Element(MathMLTags.MIN, MathMLNamespace));
				applyNode.addContent(getMathML(node.jjtGetChild(0), MathType.REAL, dialect));
				applyNode.addContent(getMathML(node.jjtGetChild(1), MathType.REAL, dialect));
				return castChild(applyNode, desiredMathType, MathType.REAL);
//			}
		}else if (funcNode.getFunction() == FunctionType.MAX){
//			if (dialect == Dialect.SBML_SUBSET) {
//				/* a < b ? b : a;
//					 <piecewise>
//						<piece>
//						   <ci> b < /ci>
//						   <apply>
//							 (a < b)
//						   </apply>
//						</piece>
//						<otherwise>
//						   <ci> a </ci>
//						</othewise>
//					 </piecewise>
//				 */
//
//				// Construct the piecewise element : create piece and otherwise separately and add.
//				Element piecewiseElement = new Element(MathMLTags.PIECEWISE, MathMLNamespace);
//				// construct the piece element :
//				Element pieceElement = new Element(MathMLTags.PIECE, MathMLNamespace);
//				pieceElement.addContent(getMathML(node.jjtGetChild(1), MathType.REAL, dialect));
//				Element applyElement = new Element(MathMLTags.APPLY, MathMLNamespace);
//				Element condnElement = new Element(MathMLTags.LESS, MathMLNamespace);
//				applyElement.addContent(condnElement);
//				applyElement.addContent(getMathML(node.jjtGetChild(0), MathType.REAL, dialect));
//				applyElement.addContent(getMathML(node.jjtGetChild(1), MathType.REAL, dialect));
//				pieceElement.addContent(applyElement);
//				// construct the otherwise element : add
//				Element otherwiseElement = new Element(MathMLTags.OTHERWISE, MathMLNamespace);
//				otherwiseElement.addContent(getMathML(node.jjtGetChild(0), MathType.REAL, dialect));
//				// Now put together the piecewise element with the piece and otherwise.
//				piecewiseElement.addContent(pieceElement);
//				piecewiseElement.addContent(otherwiseElement);
//				return castChild(piecewiseElement, desiredMathType, MathType.REAL);
//			}else{ // (dialect == Dialect.GENERAL)
				Element applyNode = new Element(MathMLTags.APPLY, MathMLNamespace);
				applyNode.addContent(new Element(MathMLTags.MAX, MathMLNamespace));
				applyNode.addContent(getMathML(node.jjtGetChild(0), MathType.REAL, dialect));
				applyNode.addContent(getMathML(node.jjtGetChild(1), MathType.REAL, dialect));
				return castChild(applyNode, desiredMathType, MathType.REAL);
//			}
		}else{
			throw new ExpressionException("cannot translate "+funcNode.getName()+" into MathML");
		}
			
	}else if (node instanceof ASTIdNode){
		Element idNode = null;
		String nodeName = ((ASTIdNode)node).name;
		if (nodeName.equals("t")) {
			idNode = new Element(MathMLTags.CSYMBOL, MathMLNamespace);
			idNode.setAttribute(MathMLTags.ENCODING, "text");
			idNode.setAttribute(MathMLTags.DEFINITIONURL, "http://www.sbml.org/sbml/symbols/time");
			idNode.setText(nodeName);
		} else {
			idNode = new Element(MathMLTags.IDENTIFIER, MathMLNamespace);
			idNode.setText(nodeName);
		}
		return castChild(idNode, desiredMathType, MathType.REAL);
	}else if (node instanceof ASTInvertTermNode){
		Element applyNode = new Element(MathMLTags.APPLY, MathMLNamespace);
		applyNode.addContent(new Element(MathMLTags.DIVIDE, MathMLNamespace));
		Element unityNode = new Element(MathMLTags.CONSTANT, MathMLNamespace);
		//unityNode.setAttribute(new Attribute(MathMLTags.CellML_units,MathMLTags.DIMENSIONLESS,Namespace.getNamespace(cbit.util.XMLTags.CELLML_NAMESPACE_PREFIX, cbit.util.XMLTags.CELLML_NAMESPACE_URI)));
		unityNode.setText("1.0");
		applyNode.addContent(unityNode);
		applyNode.addContent(getMathML(node.jjtGetChild(0), MathType.REAL, dialect));
		return castChild(applyNode, desiredMathType, MathType.REAL);
	}else if (node instanceof ASTMinusTermNode){
		Element applyNode = new Element(MathMLTags.APPLY, MathMLNamespace);
		applyNode.addContent(new Element(MathMLTags.MINUS, MathMLNamespace));
		applyNode.addContent(getMathML(node.jjtGetChild(0), MathType.REAL, dialect));
		return castChild(applyNode, desiredMathType, MathType.REAL);
	}else if (node instanceof ASTMultNode){
		// See ASTMultNode.infix for C_language fix.
		// If children nodes are boolean, collect them with '&&' condition; else collect them as product of the values. 
		int numChildren = node.jjtGetNumChildren();
		boolean[] boolChildFlags = new boolean[numChildren];
		int numReal = 0;
		int numBoolean = 0;
		for (int i=0;i<numChildren;i++){
			if (node.jjtGetChild(i).isBoolean()) {
				boolChildFlags[i] = true;
				numBoolean++;
			} else {
				numReal++;
			}
		}
		Element resultantElement = null;
		Element valueParentElement  = null;
		Element condnParentElement  = null;
		Element pieceElement = new Element(MathMLTags.PIECE, MathMLNamespace);
		if (numReal > 0 && numBoolean > 0) {
			if (numReal > 1) {
				valueParentElement = new Element(MathMLTags.APPLY, MathMLNamespace);
				valueParentElement.addContent(new Element(MathMLTags.TIMES, MathMLNamespace));
				pieceElement.addContent(valueParentElement);
			} else {
				valueParentElement = pieceElement;
			}
			for (int i = 0; i < numChildren; i++){
				if (!boolChildFlags[i]) {
					valueParentElement.addContent(getMathML(node.jjtGetChild(i), MathType.REAL, dialect));
				} 
			}
			if (numBoolean > 1) {
				condnParentElement = new Element(MathMLTags.APPLY, MathMLNamespace);
				condnParentElement.addContent(new Element(MathMLTags.AND, MathMLNamespace));
				pieceElement.addContent(condnParentElement);
			} else {
				condnParentElement = pieceElement;
			}
			for (int i = 0; i < numChildren; i++){
				if (boolChildFlags[i]) {
					condnParentElement.addContent(getMathML(node.jjtGetChild(i), MathType.BOOLEAN, dialect));
				} 
			}
			// Construct the piecewise element : create piece and otherwise separately and add.
			resultantElement = new Element(MathMLTags.PIECEWISE, MathMLNamespace);
			// 'piece' element content already added above;  construct the otherwise element
			Element otherwiseElement = new Element(MathMLTags.OTHERWISE, MathMLNamespace);
			Element constElement_0 = new Element(MathMLTags.CONSTANT, MathMLNamespace);
			constElement_0.addContent("0.0");
			otherwiseElement.addContent(constElement_0);
			// Now put together the piecewise element with the piece and otherwise. 
			resultantElement.addContent(pieceElement);
			resultantElement.addContent(otherwiseElement);
			resultantElement = castChild(resultantElement, desiredMathType, MathType.REAL);
		} else if (numReal > 0){
			resultantElement = new Element(MathMLTags.APPLY, MathMLNamespace);
			resultantElement.addContent(new Element(MathMLTags.TIMES, MathMLNamespace));
			for (int i = 0; i < node.jjtGetNumChildren(); i++){
				resultantElement.addContent(getMathML(node.jjtGetChild(i), MathType.REAL, dialect));	
			}
			resultantElement = castChild(resultantElement, desiredMathType, MathType.REAL);
		} else if (numBoolean > 0){
			resultantElement = new Element(MathMLTags.APPLY, MathMLNamespace);
			resultantElement.addContent(new Element(MathMLTags.AND, MathMLNamespace));
			for (int i = 0; i < node.jjtGetNumChildren(); i++){
				resultantElement.addContent(getMathML(node.jjtGetChild(i), MathType.BOOLEAN, dialect));	
			}
			resultantElement = castChild(resultantElement, desiredMathType, MathType.BOOLEAN);
		}
		return resultantElement;
	}else{
		throw new ExpressionException("node type "+node.getClass().toString()+" not supported yet");
	}		
}
}
