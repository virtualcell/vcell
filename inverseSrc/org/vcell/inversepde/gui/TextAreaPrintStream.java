/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package org.vcell.inversepde.gui;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

public class TextAreaPrintStream extends PrintStream {
	private JTextArea consoleTextArea;

	TextAreaPrintStream(JTextArea consoleTextArea) {
		super(new OutputStream(){

			@Override
			public void write(int b) throws IOException {
				// TODO Auto-generated method stub
				
			}});
		this.consoleTextArea = consoleTextArea;
	}

	@Override
	public void println(String s) {
		consoleTextArea.append(s + '\n');
	}

	@Override
	public void print(char c) {
		consoleTextArea.append(String.valueOf(c));
	}

	@Override
	public void print(String s) {
		consoleTextArea.append(s);
	}

}
