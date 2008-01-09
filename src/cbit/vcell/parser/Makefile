JAVACC		= java -classpath ..\..\..\..\javacc4.0.jar javacc
JJTREE		= java -classpath ..\..\..\..\javacc4.0.jar jjtree
JJDOC		= java -classpath ..\..\..\..\javacc4.0.jar jjdoc

.jjt.jj:
	$(JJTREE) $<

all::	ExpressionParser.class Parser.html

Parser.jj:	Parser.jjt
	$(JJTREE) Parser.jjt

ExpressionParser.class:	Parser.jj
	$(JAVACC) Parser.jj

Parser.html:	Parser.jj
	$(JJDOC) Parser.jj
