/*
 * @(#)BasicSearchCellRenderer.java	1.15 06/10/30
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
/*
 * @(#) BasicSearchCellRenderer.java 1.15 - last change made 10/30/06
 */

package javax.help.plaf.basic;

/**
 * TreeCellRender for JSearchNavigator.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version   1.15     10/30/06
 */

import java.awt.*;
import java.net.URL;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.help.Map;
import javax.help.Map.ID;
import javax.help.SearchTOCItem;
import javax.help.SearchHit;
import javax.help.HelpUtilities;
import javax.accessibility.AccessibleValue;
import javax.accessibility.AccessibleContext;

/**
 * Displays an search entry in a tree.
 *
 * @author Roger D. Brinkley
 * @version   1.15     10/30/06
 */
public class BasicSearchCellRenderer extends JPanel implements TreeCellRenderer
{
    /** Is the value currently selected. */
    protected boolean selected;
    // These two ivars will be made protected later.
    /** True if has focus. */
    private boolean hasFocus;
    /** True if draws focus border around icon as well. */
    private boolean drawsFocusBorderAroundIcon;

    // Colors
    /** Color to use for the foreground for selected nodes. */
    protected Color textSelectionColor;

    /** Color to use for the foreground for non-selected nodes. */
    protected Color textNonSelectionColor;

    /** Color to use for the background when a node is selected. */
    protected Color backgroundSelectionColor;

    /** Color to use for the background when the node is not selected. */
    protected Color backgroundNonSelectionColor;

    /** Color to use for the background when the node is not selected. */
    protected Color borderSelectionColor;

    /** Map to use for rendering included images. */
    protected Map map;

    /** Hits */
    protected ValueJLabel hits;

    /** Quality */
    protected ValueJLabel quality;
    
    /** Title */
    protected JLabel title;

    /**
      * Returns a new instance of BasicSearchCellRender.  Left alignment is
      * set. Icons and text color are determined from the
      * UIManager.
      */
    public BasicSearchCellRenderer(Map map) {
	super();
	this.map = map;

	setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
	setTextNonSelectionColor(UIManager.getColor("Tree.textForeground"));
	setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
	setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
	setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
	Object value = UIManager.get("Tree.drawsFocusBorderAroundIcon");
	drawsFocusBorderAroundIcon = (value != null && ((Boolean)value).
				      booleanValue());

	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	hits = new ValueJLabel();
	hits.setHorizontalAlignment(JLabel.LEFT);
	quality = new ValueJLabel();
	title = new JLabel(); 
	title.setFont(getFont());
	title.setHorizontalAlignment(JLabel.LEFT);
	add(Box.createRigidArea(new Dimension(4,0)));
	add(quality);
	add(Box.createRigidArea(new Dimension(4,0)));
	add(hits);
	add(Box.createRigidArea(new Dimension(4,0)));
	add(title);

    }


    /**
      * Sets the color the text is drawn with when the node is selected.
      */
    public void setTextSelectionColor(Color newColor) {
	textSelectionColor = newColor;
    }

    /**
      * Returns the color the text is drawn with when the node is selected.
      */
    public Color getTextSelectionColor() {
	return textSelectionColor;
    }

    /**
      * Sets the color the text is drawn with when the node is not selected.
      */
    public void setTextNonSelectionColor(Color newColor) {
	textNonSelectionColor = newColor;
    }

    /**
      * Returns the color the text is drawn with when the node is not selected.
      */
    public Color getTextNonSelectionColor() {
	return textNonSelectionColor;
    }

    /**
      * Sets the color to use for the background if the node is selected.
      */
    public void setBackgroundSelectionColor(Color newColor) {
	backgroundSelectionColor = newColor;
    }


    /**
      * Returns the color to use for the background if the node is selected.
      */
    public Color getBackgroundSelectionColor() {
	return backgroundSelectionColor;
    }

    /**
      * Sets the background color to be used for unselected nodes.
      */
    public void setBackgroundNonSelectionColor(Color newColor) {
	backgroundNonSelectionColor = newColor;
    }

    /**
      * Returns the background color to be used for unselected nodes.
      */
    public Color getBackgroundNonSelectionColor() {
	return backgroundNonSelectionColor;
    }

    /**
      * Sets the color to use for the border.
      */
    public void setBorderSelectionColor(Color newColor) {
	borderSelectionColor = newColor;
    }

    /**
      * Returns the the border color.
      */
    public Color getBorderSelectionColor() {
	return borderSelectionColor;
    }

    /**
     * Subclassed to only accept the font if it is not a FontUIResource.
     */
    public void setFont(Font font) {
	if(font instanceof FontUIResource) {
	    font = null;
	}
	if (font != null) {
	    if (title != null) {
		title.setFont(font);
	    }
	}
	super.setFont(font);
    }

    /**
     * Subclassed to only accept the color if it is not a ColorUIResource.
     */
    public void setBackground(Color color) {
	if(color instanceof ColorUIResource)
	    color = null;
	super.setBackground(color);
    }

    /**
      * Configures the renderer based on the components passed in.
      * Sets the value from messaging value with toString().
      * The foreground color is set based on the selection and the icon
      * is set based on on leaf and expanded.
      */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
						  boolean sel,
						  boolean expanded,
						  boolean leaf, int row,
						  boolean hasFocus) {
	SearchTOCItem item = 
	    (SearchTOCItem) ((DefaultMutableTreeNode) value).getUserObject();
	    
	String stringValue = "";

	if (item != null) {
	    stringValue = item.getName();
	}

	if (sel) {
	    hits.setForeground(getTextNonSelectionColor());
	    title.setForeground(getTextSelectionColor());
	    title.setBackground(getBackgroundSelectionColor());
	} else {
	    Color bColor = getBackgroundNonSelectionColor();
	    if(bColor == null)
		bColor = getBackground();
	    hits.setForeground(getTextNonSelectionColor());
	    title.setForeground(getTextNonSelectionColor());
	    title.setBackground(bColor);
	}

	Locale locale = Locale.getDefault();

	// Set the locale of this if there is a lang value
	if (item != null) {
	    locale = item.getLocale();
	    if (locale != null) {
		setLocale(locale);
	    }
	}

	if (item != null) {
	    // Set the hit count
	    int hitCount = item.hitCount();
	    hits.setText(String.valueOf(hitCount));
	    hits.setAccessibility(null, 
				  HelpUtilities.getString(locale, "search.hitDesc"),
				  new Integer(hitCount),
				  new Integer(hitCount),
				  new Integer(hitCount));

	    String qualityLevel=null;
	    // Set the quality index
	    double penalty = item.getConfidence();
	    if (penalty < 1) {
		quality.setIcon(high);
		qualityLevel = HelpUtilities.getString(locale, "search.high");
	    } else if (penalty < 5) {
		quality.setIcon(medhigh);
		qualityLevel = HelpUtilities.getString(locale, "search.midhigh");
	    } else if (penalty < 12.5) {
		quality.setIcon(med);
		qualityLevel = HelpUtilities.getString(locale, "search.mid");
	    } else if (penalty < 25) {
		quality.setIcon(medlow);
		qualityLevel = HelpUtilities.getString(locale, "search.midlow");
	    } else {
		quality.setIcon(low);
		qualityLevel = HelpUtilities.getString(locale, "search.low");
	    }

	    quality.setAccessibility(qualityLevel, 
				     HelpUtilities.getString(locale, "search.qualityDesc"),
				     new Double(penalty),
				     new Double(penalty),
				     new Double(penalty));
	    
	    // Finally set the title
	    title.setText(stringValue);
	}

	selected = sel;
	this.hasFocus = hasFocus;
	return this;
    }

    /**
      * Paints the value.  The background is filled based on selected color.
      */
    public void paint(Graphics g) {
	Color bColor;

	if(selected) {
	    bColor = getBackgroundSelectionColor();
	} else {
	    bColor = getBackgroundNonSelectionColor();
	    if(bColor == null)
		bColor = getBackground();
	}
	int imageOffset = -1;
	if(bColor != null) {

	    imageOffset = getLabelStart();
	    g.setColor(bColor);
	    Dimension size = getSize();
	    g.fillRect(imageOffset, 0, size.width - 1 - imageOffset,
		       size.height);
	}
	if (hasFocus) {
	    if (drawsFocusBorderAroundIcon) {
		imageOffset = 0;
	    }
	    else if (imageOffset == -1) {
		imageOffset = getLabelStart();
	    }
	    g.setColor(getBorderSelectionColor());
	    g.drawRect(imageOffset, 0, getWidth() - 1 - imageOffset,
		       getHeight() - 1);
	}
	// call paintChildren and not paint so we don't
	// erase everyting we've already done.
	super.paintChildren(g);

    }

    private int getLabelStart() {
	return title.getX() - 1;
    }

    /**
     * Overrides <code>JComponent.getPreferredSize</code> to
     * return slightly taller preferred size value.
     */
    public Dimension getPreferredSize() {
	Dimension        retDimension = super.getPreferredSize();
	int width = 0;
	int height = 0;

	// Set the initial height;
	if (retDimension != null) {
	    height = retDimension.height;
	}
	
	// The the insets
	Insets insets = getInsets();
	width += insets.left + insets.right;

	// Add the width of the quality
	width += 4;
	Dimension size;
	size = quality.getPreferredSize();
	width += size.width;
	height = Math.max(height, size.height + insets.top + insets.bottom);

	// Add the width of quantity
	width += 4;
	size = hits.getPreferredSize();
	width += size.width;
	height = Math.max(height, size.height + insets.top + insets.bottom);

	// Add the width of titl
	width += 4;
	size = title.getPreferredSize();
	width += size.width;
	height = Math.max(height, size.height + insets.top + insets.bottom);

	// Add 3 to the width for good measure
	width += 3;

	if(retDimension != null)
	    retDimension = new Dimension(retDimension.width + 3,
					 retDimension.height);
	retDimension.setSize(width, height);
	return retDimension;
    }

    // icons used for the BasicSearchCellRender
    private static Icon high = UIManager.getIcon("SearchHigh.icon");
    private static Icon medhigh = UIManager.getIcon("SearchMedHigh.icon");
    private static Icon med = UIManager.getIcon("SearchMed.icon");
    private static Icon medlow = UIManager.getIcon("SearchMedLow.icon");
    private static Icon low = UIManager.getIcon("SearchLow.icon");

    private class ValueJLabel extends JLabel {

	String accessName;
	String accessDesc;
	Number accessValue;
	Number minValue;
	Number maxValue;

	public void setAccessibility (String accessName, String accessDesc, 
				      Number accessValue, Number minValue, 
				      Number maxValue) {
	    this.accessName = accessName;
	    this.accessDesc = accessDesc;
	    this.accessValue = accessValue;
	    this.minValue = minValue;
	    this.maxValue = maxValue;
	}


	/////////////////
	// Accessibility support
	////////////////
	
	/**
	 * Get the AccessibleContext associated with this JComponent
	 *
	 * @return the AccessibleContext of this JComponent
	 */
	public AccessibleContext getAccessibleContext() {
	    if (accessibleContext == null) {
		accessibleContext = new AccessibleValueJLabel();
	    }
	    accessibleContext.setAccessibleDescription(accessDesc);
	    accessibleContext.setAccessibleName(accessName);
	    return accessibleContext;
	}

	/**
	 * The class used to obtain the accessible role for this object.
	 * <p>
	 * <strong>Warning:</strong>
	 * Serialized objects of this class will not be compatible with
	 * future Swing releases.  The current serialization support is	 appropriate
	 * for short term storage or RMI between applications running the same
	 * version of Swing.  A future release of Swing will provide support for
	 * long term persistence.
	 */
	protected class AccessibleValueJLabel extends AccessibleJLabel implements AccessibleValue {


	    public AccessibleValue getAccessibleValue() {
		return this;
	    }

	    /**
	     * Get the value of this object as a Number.  
	     * If the value has not  been
	     * set, the return value will be null.
	     *
	     * @return value of the object
	     * @see #setCurrentAccessibleValue
	     */
	    public Number getCurrentAccessibleValue() {
		return accessValue;
	    }

	    /**
	     * Set the value of this object as a Number.
	     *
	     * @return True if the value was set; else False
	     * @see #getCurrentAccessibleValue
	     */
	    public boolean setCurrentAccessibleValue(Number n) {
		return false;       // I assume the value can't be set
	    }

	    /**
	     * Get the minimum value of this object as a Number.
	     *
	     * @return Minimum value of the object; null if this object does not
	     * have a minimum value
	     * @see #getMaximumAccessibleValue
	     */
	    public Number getMinimumAccessibleValue() {
		return minValue;
	    }

	    /**
	     * Get the maximum value of this object as a Number.
	     *
	     * @return Maximum value of the object; null if this object does not
	     * have a maximum value
	     * @see #getMinimumAccessibleValue
	     */
	    public Number getMaximumAccessibleValue() {
		return maxValue;
	    }
	}
    }
    
    /**
     * For printf debugging.
     */
    private static boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.out.println("BasicSearchCellRenderer: " + str);
        }
    }
}
