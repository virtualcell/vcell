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

/**
 * Insert the type's description here.
 * Creation date: (8/1/2003 10:49:36 AM)
 * @author: Jim Schaff
 */
public class SimpleNameScopeTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		testSingleScope();
		testMultiscope();
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2003 11:08:16 AM)
 */
public static void testMultiscope() {
	try {
		System.out.println("\n\n------------- Test multiple scopes (lower scope is parent)---------------\n\n");
		Expression exp = new Expression("x0+x1+x2");
		String symbols[] = exp.getSymbols();
		final int numScopes = symbols.length;
		
		SimpleNameScope scopes[] = new SimpleNameScope[numScopes];
		ScopedSymbolTable symbolTables[] = new SimpleSymbolTable[numScopes];
		//
		// make linear list of scopes and symbolTables (and associate 1 symbol per scope).
		//
		for (int i = 0; i < scopes.length; i++){
			scopes[i] = new SimpleNameScope("SCOPE_"+i);
			symbolTables[i] = new SimpleSymbolTable(new String[] { symbols[i] },scopes[i]);
			scopes[i].setScopedSymbolTable(symbolTables[i]);
			System.out.println("'"+symbols[i]+"' bound to scope '"+scopes[i].getName()+"'");
			if (i>0){
				scopes[i].setParent(scopes[i-1]);
				scopes[i-1].addChild(scopes[i]);
			}
		}
		//
		// bind to lowest level symbolTable .... should propagate up to more global if undefined.
		//
		System.out.println("\n--------binding with no explicit scoping----------\n");
		exp.bindExpression(symbolTables[numScopes-1]);
		
		//
		// print expression for each scope
		//
		for (int j = 0; j < scopes.length; j++){
			System.out.println("\n\nexp = '"+exp+"', wrt "+scopes[j].getName()+" = '"+exp.renameBoundSymbols(scopes[j]).infix()+"'");

			System.out.println("\n--------binding with explicit scoping----------\n");
			Expression exp2 = new Expression(exp.renameBoundSymbols(scopes[j]).infix());
			for (int i = 0; i < symbolTables.length; i++){
				try {
					exp2.bindExpression(symbolTables[j]);
					System.out.println("exp '"+exp2+"' bound to symbolTable "+i);
					for (int k = 0; k < scopes.length; k++){
						System.out.println("\t\texp2 = '"+exp2+"', wrt "+scopes[k].getName()+" = '"+exp.renameBoundSymbols(scopes[k]).infix()+"'");
					}
				}catch (Throwable e){
					System.out.print("exp '"+exp2+"' can't bind to symbolTable "+i+":  ");
					System.out.println(e.getMessage());
					e.printStackTrace(System.out);
				}
			}
		}
		System.out.println("\n\n");
		//
		// print expression for each scope
		//
		for (int j = 0; j < scopes.length; j++){
			System.out.println("exp = '"+exp+"', wrt "+scopes[j].getName()+" = '"+exp.renameBoundSymbols(scopes[j]).infix()+"'");
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2003 11:08:16 AM)
 */
public static void testSingleScope() {
	try {
		System.out.println("\n\n------------- Test single scope ---------------\n\n");
		Expression exp = new Expression("x");
		String symbols[] = exp.getSymbols();
		SimpleNameScope scopes[] = new SimpleNameScope[3];
		scopes[0] = new SimpleNameScope("A");
		scopes[1] = new SimpleNameScope("B");
		scopes[2] = new SimpleNameScope("C");
		
		scopes[0].addChild(scopes[1]);
		scopes[1].setParent(scopes[0]);
		scopes[1].addChild(scopes[2]);
		scopes[2].setParent(scopes[1]);
		
		scopes[0].setScopedSymbolTable(new SimpleSymbolTable(symbols,scopes[0]));
		scopes[1].setScopedSymbolTable(new SimpleSymbolTable(symbols,scopes[1]));
		scopes[2].setScopedSymbolTable(new SimpleSymbolTable(symbols,scopes[2]));
		
		for (int i = 0; i < scopes.length; i++){
			SimpleNameScope boundScope = scopes[i];
			SimpleSymbolTable symbolTable = new SimpleSymbolTable(symbols,boundScope);
			exp.bindExpression(symbolTable);
			for (int j = 0; j < scopes.length; j++){
				System.out.println("exp = '"+exp+"', bound to "+boundScope.getName()+", exp wrt "+scopes[j].getName()+" = '"+exp.renameBoundSymbols(scopes[j]).infix()+"'");
			}
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}
