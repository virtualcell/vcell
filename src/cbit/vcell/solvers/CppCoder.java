package cbit.vcell.solvers;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;
import java.util.*;

public abstract class CppCoder implements Coder
{
	protected Vector<CppClassCoder> cppClassCoderList = new Vector<CppClassCoder>();
	protected String baseFilename = null;
	protected File dir = null;
   protected CppCoder(String baseFilename, File directory)
   {
   	this.baseFilename = baseFilename;
   	this.dir = directory;
   }                        
/**
 * This method was created by a SmartGuide.
 * @param cppClassCoder cbit.vcell.solvers.CppClassCoder
 */
public void addCppClassCoder(CppClassCoder cppClassCoder) {
	cppClassCoderList.addElement(cppClassCoder);
}
public void code(OutputStream headerStream,OutputStream codeStream) throws Exception {
   
	PrintWriter headerFile = new PrintWriter(headerStream);
	writeCppHeaderBegin(headerFile);
	Enumeration<CppClassCoder> enum1 = getCppClassCoders();
	while (enum1.hasMoreElements()){
		CppClassCoder cppClassCoder = enum1.nextElement();
//System.out.println("generating C++ header for class " + cppClassCoder.getClassName());
		cppClassCoder.writeDeclaration(headerFile);
		headerFile.println("");
	}	
	writeCppHeaderEnd(headerFile);
	headerFile.close();
	
	PrintWriter codeFile = new PrintWriter(codeStream);
	writeCppCodeBegin(codeFile);
	enum1 = getCppClassCoders();
	while (enum1.hasMoreElements()){
		CppClassCoder cppClassCoder = (CppClassCoder)enum1.nextElement();
//System.out.println("generating C++ implementation for class " + cppClassCoder.getClassName());
		cppClassCoder.writeImplementation(codeFile);
		headerFile.println("");
	}	
	writeCppCodeEnd(codeFile);
	codeFile.close();
}      
/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String getBaseFilename() {
	return baseFilename;
}
   protected abstract String[] getCodeDefines();      
   protected abstract String[] getCodeIncludes();      
/**
 * This method was created by a SmartGuide.
 * @return java.util.Enumeration
 */
public Enumeration<CppClassCoder> getCppClassCoders() {
	return cppClassCoderList.elements();
}
   protected abstract String[] getHeaderClassDefines();      
   //protected abstract String[] getHeaderConstants();      
   protected abstract String[] getHeaderIncludes();      
/**
 * This method was created by a SmartGuide.
 */
public void initialize() throws Exception {
	cppClassCoderList.removeAllElements();
}	
/**
 * This method was created by a SmartGuide.
 */
protected void initializeCppClassCoders() throws Exception {
	Enumeration<CppClassCoder> enum1 = getCppClassCoders();
	while (enum1.hasMoreElements()){
		CppClassCoder classCoder = enum1.nextElement();
		classCoder.initialize();
	}		
}
protected void writeCppCodeBegin(PrintWriter out) throws Exception {

	out.println("//---------------------------------------------");
	out.println("//  " + baseFilename + ".cpp");
	out.println("//---------------------------------------------");
	out.println("");
	out.println("#ifdef WIN32");
	out.println("#include <Windows.h>");
	out.println("#endif");	  
	out.println("");
	out.println("#include \"" + baseFilename + ".h\"");
	  
	String includes[] = getCodeIncludes();
	if (includes!=null){
		for (int i=0;i<includes.length;i++){
			out.println("#include " + includes[i]);
		}
		out.println("");
	} 

	String defines[] = getCodeDefines();
	if (defines!=null){
		for (int i=0;i<defines.length;i++){
			out.println("#define " + defines[i]);
		}
		out.println("");
	}   
}	                      
   protected void writeCppCodeEnd(PrintWriter out) throws Exception
   {
	  out.println("");
   }            
protected void writeCppHeaderBegin(PrintWriter out) throws Exception {
	out.println("//-------------------------");
	out.println("//  " + baseFilename + ".h");
	out.println("//-------------------------");
	out.println("#ifndef " + baseFilename + "_H");
	out.println("#define " + baseFilename + "_H");
	out.println("");

	String includes[] = getHeaderIncludes();
	if (includes!=null){
		for (int i=0;i<includes.length;i++){
			out.println("#include " + includes[i]);
		}
		out.println("");
	}	

	String classes[] = getHeaderClassDefines();
	if (classes!=null){
		for (int i=0;i<classes.length;i++){
			out.println("class " + classes[i] + ";");
		}
		out.println("");
	}	

//	String constants[] = getHeaderConstants();
//	if (constants!=null){
//		for (int i=0;i<constants.length;i++){
//			out.println("const " + constants[i] + ";");
//		}
//		out.println("");
//		out.println("");
//	}  
}	                   
protected void writeCppHeaderEnd(PrintWriter out) throws Exception {
	out.println("");
	out.println("#endif");
}
}
