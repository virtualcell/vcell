package org.jmathml;

import org.jdom.Element;

public interface IMathMLGenerator {

	Element getXML(ASTNode node);

}
