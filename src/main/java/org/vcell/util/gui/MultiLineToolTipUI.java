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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JToolTip;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;

/**
 * Insert the type's description here.
 * Creation date: (3/18/2004 11:39:31 AM)
 * @author: Anuradha Lakshminarayana
 */

 /**
 * @ Original author :  Zafir Anjum
 */

class MultiLineToolTipUI extends BasicToolTipUI {
	static MultiLineToolTipUI sharedInstance = new MultiLineToolTipUI();
	Font smallFont; 			     
	static JToolTip tip;
	protected CellRendererPane rendererPane;
	
	private static JTextArea textArea ;
	
	public MultiLineToolTipUI() {
	    super();
	}
	public static ComponentUI createUI(JComponent c) {
	    return sharedInstance;
	}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2004 5:46:59 PM)
 * @return java.lang.String[]
 * @param tiptext java.lang.String
 */
public String[] getLinesInTipText(String tiptext) {
	StringBuffer stringBuffer = new StringBuffer(tiptext);

	String newLineDelimiters = "\n\r";
	StringTokenizer lineTokenizer = new StringTokenizer(stringBuffer.toString(),newLineDelimiters);
	String token = new String("");
	Vector<String> linesVector = new Vector<String>();
	int j=0;

	//
	// Each token is a line with newline and carriage return chars in the annotation
	// 
	while (lineTokenizer.hasMoreTokens()) {
		token = lineTokenizer.nextToken()+"\n";
		linesVector.addElement(token);
		j++;
	}

	String[] linesArray = (String[])org.vcell.util.BeanUtils.getArray(linesVector, String.class);

	return linesArray;
}
	public Dimension getMaximumSize(JComponent c) {
	    return getPreferredSize(c);
	}
	public Dimension getMinimumSize(JComponent c) {
	    return getPreferredSize(c);
	}
	public Dimension getPreferredSize(JComponent c) {
		String tipText = ((JToolTip)c).getTipText();
		if (tipText == null) {
			return new Dimension(0,0);
		}

		String tipTextSubString = "";
		String[] linesInTipText = getLinesInTipText(tipText);
		boolean bLongLines = linesLongerThanTextArea(linesInTipText);
		int totalLinesCount = linesInTipText.length;
		int displayedLinesCount = 0;

		if (totalLinesCount > 1) {
			// A little bit of twisted logic to limit the lines number of lines displayed in the multilinetooltip,
			if (bLongLines) {
				displayedLinesCount = Math.min(totalLinesCount, 3);
				int i = 0;
				for (i = 0; i < displayedLinesCount-1; i++) {
					tipTextSubString = tipTextSubString+linesInTipText[i];
				}
				if (totalLinesCount > displayedLinesCount) {
					tipTextSubString = tipTextSubString+linesInTipText[i]+" ...";
				} else {
					tipTextSubString = tipTextSubString+linesInTipText[i];
				}
			} else {
				displayedLinesCount = Math.min(totalLinesCount, 4);
				int i = 0;
				for (i = 0; i < displayedLinesCount-1; i++) {
					tipTextSubString = tipTextSubString+linesInTipText[i];
				}
				if (totalLinesCount > displayedLinesCount) {
					tipTextSubString = tipTextSubString+linesInTipText[i]+" ...";
				} else {
					tipTextSubString = tipTextSubString+linesInTipText[i];
				}				
			}
		} else {
			// if there is only 1 line ...
			if (bLongLines) {
				// if the line is longer than 200 chars, display only first 200 chars, append ...
				// else leave text as is. 
				if (tipText.length() > 200) {
					tipTextSubString = tipText.substring(0, 199)+" ...";
				} else {
					tipTextSubString = tipText;
				}
			} else {
				// if there is only one line and its not a long line, leave text as is.
				tipTextSubString = tipText;
			}
		}
		
		textArea = new JTextArea(tipTextSubString);
	    rendererPane.removeAll();
		rendererPane.add(textArea);
		textArea.setWrapStyleWord(true);

		int width = ((MultiLineToolTip)c).getFixedWidth();
		int columns = ((MultiLineToolTip)c).getColumns();
		
		if( columns > 0 ) {
			textArea.setColumns(columns);
			textArea.setSize(0,0);
			textArea.setLineWrap(true);
			textArea.setSize( textArea.getPreferredSize() );
		} else if( width > 0 ) {
			textArea.setLineWrap(true);
			Dimension d = textArea.getPreferredSize();
			d.width = width;
			d.height++;
			textArea.setSize(d);
		} else {
			textArea.setLineWrap(false);
		}  
		
		Dimension dim = textArea.getPreferredSize();
		dim.height += 1;
		dim.width += 1;
		dim.width = Math.min(dim.width, 400);
		if ( (displayedLinesCount == 3 && bLongLines) || displayedLinesCount == 4 || (totalLinesCount == 1 && bLongLines)) {
			dim.height = 100;
		} else {
			dim.height = Math.min(dim.height, 100);
		}

		// Forcing line wrap for JTextarea. Also, forcng font to be "Panel.font". Else it
		// takes "ToolTip.font" as default which is the monosansserif font, which is different
		// from the other tooltip fonts (Panel.font - dialog).
		textArea.setLineWrap(true);
		textArea.setFont(UIManager.getFont("Panel.font"));

		return dim;
	}
	public void installUI(JComponent c) {
	    super.installUI(c);
		tip = (JToolTip)c;
	    rendererPane = new CellRendererPane();
	    c.add(rendererPane);
	}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2004 5:47:50 PM)
 * @return boolean
 * @param linesintiptext java.lang.String[]
 */
public boolean linesLongerThanTextArea(String[] linesintiptext) {
	boolean bLinesLong = false;
	for (int i = 0; i < linesintiptext.length; i++){
		if (linesintiptext[i].length() > 60) {
			bLinesLong = true;
		}
	}
	return bLinesLong;
}
	public void paint(Graphics g, JComponent c) {
	    Dimension size = c.getSize();
	    textArea.setBackground(c.getBackground());
		rendererPane.paintComponent(g, textArea, c, 1, 1, size.width - 1, size.height - 1, true);
	}
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		c.remove(rendererPane);
	    rendererPane = null;
	}
}
