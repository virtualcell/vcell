/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public abstract class KeySequenceListener implements KeyListener {

	protected String typed = "";
	
	public void keyPressed(KeyEvent event) { }
	public void keyReleased(KeyEvent event) { }

	public void keyTyped(KeyEvent event) {
		typed = typed + event.getKeyChar();
		String sequence = getSequence();
		if(typed.length() > sequence.length()) {
			typed = typed.substring(typed.length() - sequence.length());
		}
		if(sequence.equals(typed)) {
			sequenceTyped();
			typed = "";
		}
	}

	public abstract String getSequence();
	public abstract void sequenceTyped();
	
}
