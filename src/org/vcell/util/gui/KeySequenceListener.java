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