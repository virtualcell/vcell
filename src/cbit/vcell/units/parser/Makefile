JAVACC		= java -classpath ..\..\..\..\..\lib\javacc4.0.jar javacc
JJTREE		= java -classpath ..\..\..\..\..\lib\javacc4.0.jar jjtree
JJDOC		= java -classpath ..\..\..\..\..\lib\javacc4.0.jar jjdoc

.jjt.jj:
	$(JJTREE) $<

all::	UnitSymbolParser.class Parser.html

Parser.jj:	Parser.jjt
	del Parser.jj
	$(JJTREE) Parser.jjt
	

UnitSymbolParser.class:	Parser.jj
	$(JAVACC) Parser.jj

Parser.html:	Parser.jj
	$(JJDOC) Parser.jj
