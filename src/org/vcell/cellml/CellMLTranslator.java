package org.vcell.cellml;

import java.io.File;
import java.io.StringReader;

import org.vcell.util.document.VCDocument;

import cbit.util.xml.XmlUtil;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.xml.XmlHelper;

public class CellMLTranslator {

public static void main(String[] args) {
	String cellmlStr = null;
	try {
		cellmlStr = XmlUtil.getXMLString(args[0]);
		Translator cellmlTranslator = new CellQuanVCTranslator();
		VCDocument vcDoc = cellmlTranslator.translate(new StringReader(cellmlStr), false);
		String vcmlStr = XmlHelper.mathModelToXML((MathModel)vcDoc);
		XmlUtil.writeXMLString(vcmlStr, "C:\\VCell\\CellMLModels\\test_bindschadler.vcml");
	} catch (Exception e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Error translating from CellML -> VCML : " + e.getMessage());
	}
}
	
}
