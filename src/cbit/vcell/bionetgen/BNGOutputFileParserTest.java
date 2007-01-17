package cbit.vcell.bionetgen;
import java.io.File;
/**
 * Insert the type's description here.
 * Creation date: (1/19/2006 11:14:29 AM)
 * @author: Jim Schaff
 */
public class BNGOutputFileParserTest {
/**
 * Insert the method's description here.
 * Creation date: (1/19/2006 11:15:19 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	BNGOutputFileParser bngNetFileParser = new BNGOutputFileParser(); 
	File bngNetFile = new java.io.File("C:\\BioNetGen\\BioNetGen_1.1.1j\\NEW\\NET\\map1.net");
	BNGOutputSpec outputSpec = null;
	try {
		outputSpec = bngNetFileParser.createBngOutputSpec(bngNetFile);
	} catch (java.io.FileNotFoundException e1) {
		throw new RuntimeException("could not read BNG .net file : "+e1.getMessage());
	} catch (java.io.IOException e2) {
		throw new RuntimeException("could not read BNG .net file : "+e2.getMessage());
	}
	bngNetFileParser.printBNGNetOutput(outputSpec);
}
}