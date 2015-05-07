JAVACC		= java -classpath ..\..\..\..\..\testlib\javacc4.0.jar javacc
JJTREE		= java -classpath ..\..\..\..\..\testlib\javacc4.0.jar jjtree
JJDOC		= java -classpath ..\..\..\..\..\testlib\javacc4.0.jar jjdoc

.jjt.jj:
	$(JJTREE) $<

all::	BNGLParser.class Parser.html

Parser.jj:	Parser.jjt
	del Parser.jj
	$(JJTREE) Parser.jjt
	

BNGLParser.class:	Parser.jj
	$(JAVACC) Parser.jj

Parser.html:	Parser.jj
	$(JJDOC) Parser.jj
