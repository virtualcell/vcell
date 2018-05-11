/*
 * @(#)TextHelpModel.java	1.7 06/10/30
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package javax.help;

import javax.help.event.*;

/**
 * The interface to a HelpModel that manipulates text.
 *
 * It provides additional text operations.
 */

public interface TextHelpModel extends HelpModel {
    /**
     * Gets the title of the document.
     *
     * @return The title of document visited.
     */
    public String getDocumentTitle();

    /**
     * Sets the title of the document.
     * A property change event is generated.
     *
     * @param title The title currently shown.
     */
    public void setDocumentTitle(String title);


    /**
     * Removes all highlights on the current document.
     */
    public void removeAllHighlights();

    /**
     * Adds a highlight to a range of positions in a document.
     *
     * @param pos0 Start position.
     * @param pos1 End position.
     */
    public void addHighlight(int pos0, int pos1);

    /**
     * Sets the highlights to be a range of positions in a document.
     *
     * @param h The array of highlight objects.
     */
    public void setHighlights(Highlight[] h);

    /**
     * Gets all highlights.
     */
    public Highlight[] getHighlights();

    /**
     * Adds a listener for a TextHelpModel.
     */
    public void addTextHelpModelListener(TextHelpModelListener l);

    /**
     * Removes a listener for a TextHelpModel.
     */
    public void removeTextHelpModelListener(TextHelpModelListener l);

    /**
     * This is very similar to javax.swing.text.Highlighter.Highlight
     * except that it does not use the notion of HighlightPainter.
     */
    public interface Highlight {
	/**
	 * Gets the starting model offset of the highlight.
	 *
	 * @return The starting offset >= 0.
	 */
	public int getStartOffset();

	/**
	 * Gets the ending model offset of the highlight.
	 *
	 * @return The ending offset >= 0.
	 */
	public int getEndOffset();
    }
}
