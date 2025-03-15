package org.jmathml;

import org.jdom2.Element;

public interface IMathMLGenerator {

	Element getXML(ASTNode node);

}
