/*
 * @(#)JHelpPrintHandler.java	1.30 06/10/30
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

package com.sun.java.help.impl;

import javax.help.*;
import javax.help.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.print.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Vector;
import java.util.WeakHashMap;

/**
 * Print handler for JavaHelp. Because JDK1.1 is not supported since JavaHelp
 * version 2.0 this class contains all printing code.
 *
 * @author Roger D. Brinkley
 * @author Stepan Marek
 * @version   1.30     10/30/06
 */
public class JHelpPrintHandler implements ActionListener {
    
    public static final String PRINT_BUTTON_NAME = "PrintButton";
    public static final String PAGE_SETUP_BUTTON_NAME = "PageSetupButton";
    public static final String PRINT_LATER_BUTTON_NAME = "PrintLaterButton";
    
    /**
     * Specifies whether action is enabled; the default is true.
     */
    private boolean enabled = true;
    private PrinterJob printerJob;
    private PageFormat pageFormat;
    private JHelp help;
    private HelpModel helpModel;
    // private DefaultListModel listModel = new DefaultListModel();
    private URL documentURL;
    private String documentTitle;
    
    
    
    public JHelpPrintHandler(JHelp help) {
        this.help = help;
    }
    
    private static WeakHashMap handlers;
    
    public static JHelpPrintHandler getJHelpPrintHandler(JHelp help) {
        JHelpPrintHandler handler = null;
        if (handlers == null) {
            handlers = new WeakHashMap();
        } else {
            handler = (JHelpPrintHandler)handlers.get(help);
        }
        if (handler == null) {
            handler = new JHelpPrintHandler(help);
            handlers.put(help, handler);
        }
        return handler;
    }

    /**
     * If any <code>PropertyChangeListeners</code> have been registered, the
     * <code>changeSupport</code> field describes them.
     */
    protected SwingPropertyChangeSupport changeSupport;
    
    /**
     * Supports reporting bound property changes.  This method can be called
     * when a bound property has changed and it will send the appropriate
     * <code>PropertyChangeEvent</code> to any registered
     * <code>PropertyChangeListeners</code>.
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (changeSupport == null) {
            return;
        }
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    
    /**
     * Adds a <code>PropertyChangeListener</code> to the listener list.
     * The listener is registered for all properties.
     * <p>
     * A <code>PropertyChangeEvent</code> will get fired in response to setting
     * a bound property, e.g. <code>setFont</code>, <code>setBackground</code>,
     * or <code>setForeground</code>.
     * Note that if the current component is inheriting its foreground,
     * background, or font from its container, then no event will be
     * fired in response to a change in the inherited property.
     *
     * @param listener  The <code>PropertyChangeListener</code> to be added
     *
     * @see Action#addPropertyChangeListener
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport == null) {
            changeSupport = new SwingPropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(listener);
    }
    
    
    /**
     * Removes a <code>PropertyChangeListener</code> from the listener list.
     * This removes a <code>PropertyChangeListener</code> that was registered
     * for all properties.
     *
     * @param listener  the <code>PropertyChangeListener</code> to be removed
     *
     * @see Action#removePropertyChangeListener
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport == null) {
            return;
        }
        changeSupport.removePropertyChangeListener(listener);
    }
    
    /**
     * Returns true if the action is enabled.
     *
     * @return true if the action is enabled, false otherwise
     * @see Action#isEnabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Enables or disables the action.
     *
     * @param newValue  true to enable the action, false to
     *                  disable it
     * @see Action#setEnabled
     */
    public void setEnabled(boolean newValue) {
        boolean oldValue = this.enabled;
        this.enabled = newValue;
        firePropertyChange("enabled", new Boolean(oldValue), new Boolean(newValue));
    }
    
    /**
     * ActionListener to perform printing. The current implementation of Swing
     * requires that this go through an ActionListener as it must be in the
     * EventDispatchThread.
     */
    public void actionPerformed(ActionEvent e) {
        
        String name = null;
        Object source = e.getSource();
        if (source instanceof MenuComponent) {
            name = ((MenuComponent)source).getName();
        } if (source instanceof Component) {
            name = ((Component)source).getName();
        }
        
        if (isEnabled()) {
            if (name != null && name.equals(JHelpPrintHandler.PAGE_SETUP_BUTTON_NAME)) {
                printSetup();
            } else {
                if (getHelp() != null) {
                    print(getURLs());
                } else if (getHelpModel() != null) {
                    print(getHelpModel().getCurrentURL());
                }
            }
        }
    }
    
    
    public void printSetup() {
        if (isEnabled()) {
            new PageSetupThread().start();
        }
    }
    
    public void print(URL url) {
        if (isEnabled()) {
            new PrintThread(url).start();
        }
    }
    
    public void print(URL[] urls) {
        if (isEnabled()) {
            new PrintThread(urls).start();
        }
    }
    
    private JHelp getHelp() {
        return help;
    }
    
    private URL[] getURLs() {
        URL[] urls = null;
        if (getHelp() != null) {
            TreeItem[] items = getHelp().getSelectedItems();
            if (items != null) {
                debug("pages to print: "+items.length);
                urls = new URL[items.length];
                for (int i = 0; i < items.length; i++) {
                    debug("   "+items[i].getName()+": "+items[i].getURL());
                    urls[i] = items[i].getURL();
                }
            }
        }
        return urls;
    }
    
    /** Getter for property pageFormat.
     * @return Value of property pageFormat.
     */
    public PageFormat getPageFormat() {
        if ((pageFormat == null) && (getPrinterJob() != null)) {
            pageFormat = getPrinterJob().defaultPage();
        }
        return pageFormat;
    }
    
    /**
     * @deprecated As of JavaHelp version 2.0,
     * replaced by <code>getPageFormat()</code>.
     */
    public PageFormat getPF() {
        return getPageFormat();
    }
    
    /** Setter for property pageFormat.
     * @param pageFormat New value of property pageFormat.
     */
    public void setPageFormat(PageFormat pageFormat) {
        if (this.pageFormat == pageFormat) return;
        PageFormat oldPageFormat = this.pageFormat;
        this.pageFormat = pageFormat;
        firePropertyChange("pageFormat", oldPageFormat, pageFormat);
    }
    
    /**
     * @deprecated As of JDK version 2.0,
     * replaced by <code>setPageFormat(pageFormat)</code>.
     */
    public void setPF(PageFormat pf) {
        setPageFormat(pf);
    }
    
    public PrinterJob getPrinterJob() {
        if (printerJob == null) {
            try {
                printerJob = PrinterJob.getPrinterJob();
            } catch (SecurityException se) {
                setEnabled(false);
                processException(se);
            }
        }
        return printerJob;
    }
    
    /**
     * Sets the PrintHandler to handle pageSetup as a separate action from
     * printing. PageSetup is only in 1.2 but we put in the 1.1 code to make
     * it easier. On 1.1 this will essential do nothing. On 1.2 this sets up
     * the button so we know if we're printing or just doing a page setup.
     */
    public void handlePageSetup(Component psComp) {
        psComp.setName(JHelpPrintHandler.PAGE_SETUP_BUTTON_NAME);
    }
    
    public void setHelpModel(HelpModel helpModel) {
        HelpModel oldModel = this.helpModel;
        this.helpModel = helpModel;
        firePropertyChange("helpModel", oldModel, helpModel);
    }
    
    /** Getter for property model.
     * @return Value of property model.
     */
    public HelpModel getHelpModel() {
        return helpModel;
    }
    
    /**
     * Returns the specified component's toplevel <code>Frame</code> or
     * <code>Dialog</code>.
     *
     * @param comp the <code>Component</code> or <code>MenuComponent</code> to
     *          check for a <code>Frame</code> or <code>Dialog</code>
     * @return the <code>Frame</code> or <code>Dialog</code> that
     *		contains the <code>Component</code> or <code>MenuComponent</code>,
     *          or <code>null</code>, if there is not a valid
     *         	<code>Frame</code> or <code>Dialog</code> parent
     */
    static Window getWindowForObject(Object comp) {
        if (comp == null || comp instanceof Frame || comp instanceof Dialog) {
            return (Window)comp;
        }
        Object parent = null;
        if  (comp instanceof MenuComponent) {
            parent = ((MenuComponent)comp).getParent();
        } else if (comp instanceof Component) {
            parent = ((Component)comp).getParent();
        }
        return getWindowForObject(parent);
    }
    
    static Insets getInsetsForContainer(Container c) {
        Insets i = c.getInsets();
        for (c = c.getParent(); c != null; c = c.getParent()) {
            Insets ii = c.getInsets();
            i.bottom += ii.bottom;
            i.left   += ii.left;
            i.right  += ii.right;
            i.top    += ii.top;
            if (c instanceof Window) {
                break;
            }
        }
        return i;
    }
    
    /**
     * Create a handler for the given type from HelpSet or from the
     * default registry of editor kits.
     *
     * @param type the content type
     * @return the editor kit, or null if there is nothing
     *   registered for the given type.
     */
    EditorKit createEditorKitForContentType(String type) {
        
        HelpModel model = null;
        JHelp help = getHelp();
        
        if (help != null) {
            model = help.getModel();
        } else {
            model = getHelpModel();
        }
        
        if (model == null) {
            return null;
        }
        
        HelpSet hs = model.getHelpSet();
        if (hs == null) {
            return null;
        }
        
        // try to dynamically load the support
        String classname = (String) hs.getKeyData(HelpSet.kitTypeRegistry, type);
        // I don't know of a class for this type
        if (classname == null) {
            return null;
        }
        
        ClassLoader loader = (ClassLoader) hs.getKeyData(HelpSet.kitLoaderRegistry, type);
        if (loader == null) {
            loader = hs.getLoader();
        }
        
        EditorKit k = null;
        try {
            Class c;
            if (loader != null) {
                c = loader.loadClass(classname);
            } else {
                c = Class.forName(classname);
            }
            k = (EditorKit) c.newInstance();
            // kitRegistry.put(type, k);
        } catch (Throwable e) {
            System.err.println(e);
            k = null;
        }
        
        // create a copy of the prototype or null if there
        // is no prototype.
        if (k != null) {
            return (EditorKit) k.clone();
        }
        return k;
    }
    
    class PageSetupThread extends Thread {
        
        PrinterJob job;
        
        PageSetupThread() {
            setDaemon(true);
        }
        
        public void run() {
            PrinterJob job = getPrinterJob();
            if (job != null) {
                synchronized (job) {
                    try {
                        setPageFormat(job.pageDialog(getPageFormat()));
                    } catch (Exception e) {
                        processException(e);
                    }
                }
            }
        }
    }
    
    class PrintThread extends Thread {
        
        private URL[] urls;
        private int index = 0;
        
        PrintThread(URL url) {
            this(new URL[] {url});
        }
        
        PrintThread(URL[] urls) {
            this.urls = urls;
            setDaemon(true);
        }
        
        public void run() {
            PrinterJob job = getPrinterJob();
            if (job != null) {
                synchronized (job) {
		    JEditorPane editor = new JHEditorPane();
		    
		    JFrame frame = new JHFrame();
		    frame.setContentPane(editor);
		    
		    try {
			job.setPageable(new JHPageable(editor, urls, 
						       (PageFormat)JHelpPrintHandler.this.getPageFormat().clone()));
		    } catch (Exception e) {
			processException(e);
		    }
                        
                    if (job.printDialog()) {
                        try {
                            job.print();
                        } catch (Exception e) {
                            processException(e);
                        }
                    }
		    frame.dispose();
                }
            }
        }
    }
    
    class PageTransform extends AffineTransform {
        
        /** Holds value of property height. */
        private double height;
        
        /** Getter for property height.
         * @return Value of property height.
         */
        public double getHeight() {
            return height;
        }
        
        /** Setter for property height.
         * @param height New value of property height.
         *
         * @throws PropertyVetoException
         */
        public void setHeight(double height) {
            this.height = height;
        }
        
    }
    
    class JHPrintable implements Printable, PropertyChangeListener {
        
        JEditorPane editor;
        URL url;
        PageFormat pf;
        int firstPage;
        boolean scaleToFit;
        Vector transforms;
        
        public JHPrintable(JEditorPane editor, URL url, PageFormat pf, int firstPage, boolean scaleToFit) {
            this.editor = editor;
            this.url = url;
            this.pf = pf;
            this.firstPage = firstPage;
            this.scaleToFit = scaleToFit;
        }
        
        private synchronized void loadPage() {
            debug("JHPrintable.loadPage(): "+url);
	    URL currentURL = editor.getPage();
	    if (currentURL != null && currentURL.equals(url)) {
		// If the editors have the same url return otherwise
		// you'll just wait forever since a set page with
		// the same URL doesn't send a property change notice
		return;
	    }
            editor.addPropertyChangeListener("page", this);
            try {
                editor.setPage(url);
                wait();
            } catch (Exception e) {
                processException(e);
            }
            editor.removePropertyChangeListener("page", this);
        }
        
        public int getNumberOfPages() {
            if (transforms == null) {
                loadPage();
                transforms = createTransforms();
            }
            return transforms.size();
        }
        
        public synchronized void propertyChange(PropertyChangeEvent evt) {
            notifyAll();
        }
        
        public Vector createTransforms() {
	    
	    int page = 0;
            Vector transforms = new Vector();
            
            double curHeight = 0.0, height = 0.0;
            double yval = 0.0;
            double scale = 1.0;
            
            Component root = SwingUtilities.windowForComponent(editor);
            if (root == null) {
                root = editor;
            }
            
            root.addNotify();
            
            View rootview = editor.getUI().getRootView(editor);
            Rectangle viewRec = JHelpPrintHandler.getViewRec(rootview, (float)pf.getImageableWidth(), (float)pf.getImageableHeight());
            debug("viewRec=" + viewRec);
            
            Insets i = JHelpPrintHandler.getInsetsForContainer(editor);
            root.setBounds(0, 0, viewRec.width + i.left + i.right, viewRec.height + i.top + i.bottom);
            root.setVisible(true);
            
            // Do any scaling that is necessary
            if (scaleToFit) {
                if (viewRec.getWidth() > pf.getImageableWidth()) {
                    scale = pf.getImageableWidth() / viewRec.getWidth();
                }
                debug("scale=" + scale
                + " ImageableWidth=" + pf.getImageableWidth()
                + " width=" + viewRec.getWidth());
            }
            
            // Determine the print Rectangle size
            Rectangle2D.Double printRec =
            new Rectangle2D.Double(0.0, 0.0, pf.getImageableWidth(), pf.getImageableHeight() / scale);
            debug("printRec=" + printRec);
            Position.Bias[] bias = new Position.Bias[1];
            
            while (true) {
                debug("preparing page=" + page + " curHeight=" + curHeight);
                
                double maxHeight = printRec.getHeight() + curHeight;
                
                // Find the point that corresponds to the end of the current
                // page that we are printing.
                int point = rootview.viewToModel(0, (float) maxHeight, viewRec, bias);
                debug("point=" + point);
                try {
                    // Get the Rectangle around that point
                    Shape s = rootview.modelToView(point, viewRec, bias[0]);
                    Rectangle2D pointRec = s.getBounds2D();
                    debug("pointRec=" + pointRec);
                    
                    // Create a proper offset on the printed page
                    yval = curHeight;
                    
                    // Create the proper height to print.
                    // Be carefull not chop anything off.
                    // All print the full view if it will fit, otherwise
                    // print the point above.
                    height = pointRec.getY() - 1.0;
                    debug("Starting height=" + height);
                    if (maxHeight >= pointRec.getY() + pointRec.getHeight()) {
                        height = pointRec.getY() + pointRec.getHeight() - 1.0;
                        debug("Adjusted height=" + height);
                    }
                    
                    // Make sure there isn't any other object that would be slightly
                    // lower.
                    double baseY = pointRec.getY();
                    double baseHeight = pointRec.getY() + pointRec.getHeight() - 1.0;
                    // move over 20 pixels. We'd user pointRec.getWidth() but it's 0
                    double nextX = pointRec.getX() + 20.0;
                    double testY = 0.0, testHeight = 0.0;
                    Rectangle2D testRec = pointRec;
                    while (true) {
                        // don't look past max width.
                        if (nextX > (pf.getImageableWidth() * scale)) {
                            break;
                        }
                        int point2 =
                        rootview.viewToModel((float) nextX,
                        (float) maxHeight,
                        viewRec, bias);
                        Shape s2 = rootview.modelToView(point2, viewRec, bias[0]);
                        Rectangle2D pointRec2 = s2.getBounds2D();
                        // if this is same Rectangle then try 20 pixels over
                        if (testRec.equals(pointRec2) || pointRec2.getX() < nextX) {
                            nextX += 20.0;
                            continue;
                        }
                        debug("pointRec2=" + pointRec2);
                        testY = pointRec2.getY();
                        testHeight = pointRec2.getY() + pointRec2.getHeight() - 1.0;
                        
                        // if test height is less than base y then don't bother
                        // testing. Just pass through
                        if (testHeight > baseY) {
                            
                            // if the testHeight is greater than the baseHeight
                            // we might want to use the test height values
                            if (testHeight > baseHeight) {
                                // if the test height is less than the max height
                                // use that height instead
                                if (testHeight < maxHeight) {
                                    height = testHeight;
                                    baseHeight = testHeight;
                                    if (testY < baseY) {
                                        baseY = testY;
                                    }
                                    debug("Adjust height to testheight " + height);
                                } else {
                                    // the test height is greater than the max
                                    // height.
                                    if (testY > baseHeight) {
                                        // test Y is greater than the base height
                                        // move the base height to just above
                                        // test Y
                                        height = testY - 1.0;
                                        baseHeight = testHeight;
                                        baseY = testY;
                                        debug("new base component " + height);
                                    } else {
                                        // test Y is less than base height.
                                        // if the test Y is less than the base y
                                        // use it
                                        if (testY < baseY) {
                                            height = testY - 1.0;
                                            baseY = testY;
                                            baseHeight = testHeight;
                                            debug("test height > maxheight. Adjust height testY - 1 " + height);
                                        } else {
                                            // otherwise use the base y
                                            height = baseY - 1.0;
                                            baseHeight = testHeight;
                                            debug("test height > maxheight. Adjust height baseY - 1 " + height);
                                        }
                                    }
                                }
                            } else {
                                // the base height is greater than the test
                                // height use the base height values
                                if (baseHeight < maxHeight) {
                                    // this value has already been set to this
                                    // height but I'm doing this for consistency
                                    // The baseHeight is valid so use it.
                                    height = baseHeight;
                                    if (testY < baseY) {
                                        baseY = testY;
                                    }
                                    debug("baseHeight ok " + height);
                                } else {
                                    // the base height is greater than the max height
                                    // if the base Y is less than the test y use it
                                    if (baseY <= testY) {
                                        height = baseY - 1.0;
                                        debug("baseHeight too long - height ok" + height);
                                    } else {
                                        // otherwise use the test Y
                                        height = testY - 1.0;
                                        baseY = testY;
                                        debug("baseHeight too long - use testY - 1 " + height);
                                    }
                                }
                            }
                        }
                        testRec = pointRec2;
                        // move over 20 pixels. pointRec.width is 0
                        nextX = testRec.getX() + 20.0;
                    }
                    
                    // Create new transform for each page to be printed
                    PageTransform t = new PageTransform();
                    
                    // Set the translation values so we print the
                    // correct part of editor componenet.
                    t.translate(pf.getImageableX(), pf.getImageableY());
                    debug("t.translate="+t);
                    t.translate(- (double) i.left * scale, - ((double) i.top + yval) * scale);
                    debug("t.translate="+t);
                    
                    // Scaling will advirsely affect the yval. Multiple yval to scale
                    // to correct the future scaling.
                    t.scale(scale, scale);
                    debug("t.scale="+t);
                    
                    // Store height
                    t.setHeight(height + (double) i.top);
                    
		    // bug fix for JDK1.4 which caused repeated pages to be printed
		    if (curHeight == height + 1.0) {
			break;
		    }

                    // Strore transform
                    transforms.add(page, t);
                    
                    // Set the current Height for the next page.
                    curHeight = height + 1.0;
                    debug("Setting curHeight=" + curHeight);
                    
                    // increment the page counter
                    page++;
                    
                    // If the current Height is greater than the viewRec height
                    // then we are done.
		    debug ("curHeight="+curHeight+" viewRec.getHeight()="+ viewRec.getHeight());
                    if (curHeight >= viewRec.getHeight()) {
                        break;
                    }
                    
                } catch (BadLocationException ble) {
                    break;
                }
            }
            return transforms;
        }
        
        private void printHeader(Graphics2D g2d, int pi) {
            
            g2d.setClip(new Rectangle2D.Double(0.0, 0.0, pf.getWidth(), pf.getHeight()));
            g2d.setFont(new Font("Serif", Font.ITALIC, 10));
            
            String s = (String)editor.getDocument().getProperty(Document.TitleProperty)
            + " " + (pi + 1) + "/" +transforms.size();
            
            Rectangle2D rec = g2d.getFontMetrics().getStringBounds(s, g2d);
            g2d.drawString(s, (float)(pf.getWidth() - rec.getX() - rec.getWidth() - 36.0), (float)36.0);
            g2d.setClip(new Rectangle2D.Double(
            pf.getImageableX(),
            pf.getImageableY(),
            pf.getImageableWidth(),
            pf.getImageableHeight()
            ));
        }
        
        public int print(Graphics pg, PageFormat pageFormat, int pi) {
            
            debug("Printing document page=" + pi);
            pi -= firstPage;
            debug("Printing page=" + pi);
            
            if (pi >= getNumberOfPages()) {
                return Printable.NO_SUCH_PAGE;
            }
            
            Graphics2D g2d = (Graphics2D) pg;
            
            printHeader(g2d, pi);
            
            // Apply transformation for current page
            g2d.transform((AffineTransform) transforms.get(pi));
            debug("Graphics tansform=" + g2d.getTransform());
            debug("Graphics clip=" + g2d.getClip());
            
            // Get the Rectangle of the adjusted print graphics. This helps in
            // printing the correct height. Indirectly gotten through
            // the shape to perserve the coordinates (prevent rounding)
            Rectangle2D g2dRec = g2d.getClip().getBounds2D();
            
            // See if any adjustments to the height are necessary
            // If so create a new Rectangle and apply it to the g2d
            double height = ((PageTransform)transforms.get(pi)).getHeight();
            double adjheight = g2dRec.getY() + g2dRec.getHeight() - 1.0 - height;
            if (adjheight > 0.0) {
                debug("Graphics adjusted height=" + adjheight);
                g2d.clip(new Rectangle2D.Double(
                g2dRec.getX(),
                g2dRec.getY(),
                g2dRec.getWidth(),
                g2dRec.getHeight() - adjheight
                ));
                g2dRec = g2d.getClip().getBounds2D();
                debug("Graphics tansform=" + g2d.getTransform());
                debug("Graphics clip=" + g2d.getClip());
            }
            
            // Just print editor
            if (g2dRec.getY() < height) {
                // using editor.printAll(g2d) here should be better for jdk1.3
                // but there are bugs in jdk1.2
                editor.paint(g2d);
            } else {
                return Printable.NO_SUCH_PAGE;
            }
            
            return Printable.PAGE_EXISTS;
        }
    }
    
    class EmptyPrintable implements Printable {
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            return Printable.NO_SUCH_PAGE;
        }
    }
    
    public class JHPageable implements Pageable {
        
        private JEditorPane editor;
        private URL[] urls;
        private PageFormat pf;
        private int lastIndex = -1;
        private int lastPage = 0;
        private Printable printable = new EmptyPrintable();
	private int numPages = 0;
        
        public JHPageable(JEditorPane editor, URL[] urls, PageFormat pf) {
            this.editor = editor;
            this.urls = urls;
            this.pf = pf;
        }
        
        /**
         * Returns the <code>PageFormat</code> of the page specified by
         * <code>pageIndex</code>.
         * @param pageIndex the zero based index of the page whose
         *           <code>PageFormat</code> is being requested
         * @return the <code>PageFormat</code> describing the size and
         * 		orientation.
         * @exception <code>IndexOutOfBoundsException</code>
         *         the <code>Pageable</code> does not contain the requested
         * 		page.
         */
        public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
            return pf;
        }
        
        /**
         * Returns the number of pages in the set.
         * To enable advanced printing features,
         * it is recommended that <code>Pageable</code>
         * implementations return the true number of pages
         * rather than the
         * UNKNOWN_NUMBER_OF_PAGES constant.
         * @return the number of pages in this <code>Pageable</code>.
         */
        public int getNumberOfPages() {
	    if (numPages != 0) {
		// only do this once ???
		return numPages;
	    }
	    if (urls != null) {
		for (int i=0; i < urls.length; i++) {
		    JHPrintable printable = new JHPrintable(editor, urls[i],
							  pf, 0, true);
		    numPages += printable.getNumberOfPages();
		}
	    }
	    return numPages;
        }
        
        /**
         * Returns the <code>Printable</code> instance responsible for
         * rendering the page specified by <code>pageIndex</code>.
         * @param pageIndex the zero based index of the page whose
         *           <code>Printable</code> is being requested
         * @return the <code>Printable</code> that renders the page.
         * @exception <code>IndexOutOfBoundsException</code>
         *           the <code>Pageable</code> does not contain the requested
         * 		  page.
         */
        public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
            
            debug("JHPageable.getPrintable("+pageIndex+"): lastIndex="+lastIndex+", lastPage"+lastPage);
            
            if (pageIndex < 0) {
                throw new IndexOutOfBoundsException(pageIndex + " < 0");
            }
            
            if (urls != null) {
                while ((pageIndex >= lastPage) && ((lastIndex + 1) < urls.length)) {
                    if (urls[++lastIndex] != null) {
                        printable = new JHPrintable(editor, urls[lastIndex], 
						    pf, lastPage, true);
                        lastPage += ((JHPrintable)printable).getNumberOfPages();
                    } else {
                        printable = new EmptyPrintable();
                    }
                }
            }
            
            if (pageIndex > lastPage) {
                throw new IndexOutOfBoundsException(pageIndex + " > " + lastPage);
            }
            
            return printable;
        }
        
    }
    
    /**
     * Setups size of editor and return its optimal width.
     */
    static protected Rectangle getViewRec(View rootview, float width, float height) {
        Rectangle r = new Rectangle();
        rootview.setSize(width, height);
        r.width = (int) Math.max((long)Math.ceil(rootview.getMinimumSpan(View.X_AXIS)), (long) width);
        r.height = (int) Math.min((long)Math.ceil(rootview.getPreferredSpan(View.Y_AXIS)), (long) Integer.MAX_VALUE);
        rootview.setSize(r.width, r.height);
        if (rootview.getView(0) instanceof BoxView) {
            BoxView box = (BoxView) rootview.getView(0);
            r.width = box.getWidth();
            r.height = box.getHeight();
        } else {
            r.height = (int) Math.min((long)Math.ceil(rootview.getPreferredSpan(View.Y_AXIS)), (long) Integer.MAX_VALUE);
        }
        return r;
    }
    
    /**
     * Private implementation of invisible Frame which is used
     * as a parent for JHEditorPane. This Frame has no peer so it cannot
     * be displayed on screen and visibility is just predended.
     */
    class JHFrame extends JFrame {
        
        public void addNotify() {
            getRootPane().addNotify();
        }
        
        public void validate() {
            validateTree();
        }
        
        // The editor has to have a graphics set for it.
        // This is bug in jdk1.2.2. The Graphics is not necessery
        // for jdk1.1 and jdk1.3.
        public Graphics getGraphics() {
            return JHelpPrintHandler.this.getHelp().getGraphics();
        }
        
    }
    
    /**
     * Implementation of JEditorPane which support EditorKit created from
     * current HelpSet. Listeners are not necessery for printing and they are
     * skiped as workaround of bug in Component.addNotify() method in jdk1.1.
     */
    class JHEditorPane extends JEditorPane {
        
        public JHEditorPane() {
            setDoubleBuffered(false);
            setEditable(false);
            
            // turn of DnD support
            // necessary for jdk1.4
            setDropTarget(null);
        }
        
        public EditorKit getEditorKitForContentType(String type) {
            EditorKit k = JHelpPrintHandler.this.createEditorKitForContentType(type);
            if (k == null) {
                k = super.getEditorKitForContentType(type);
            }
            return k;
        }
        
        // This methods enable to keep references to listeners null. It is
        // workaround for bug in Component.addNotify() method in jdk1.1
        // and jdk1.2. Moreover it can save resources.
        public void addMouseListener(MouseListener l) {
        }
        
        public void removeMouseListener(MouseListener l) {
        }
        
        public void addMouseMotionListener(MouseMotionListener l) {
        }
        
        public void removeMouseMotionListener(MouseMotionListener l) {
        }
        
        public void addFocusListener(FocusListener l) {
        }
        
        public void removeFocusListener(FocusListener l) {
        }
        
        public void addKeyListener(KeyListener l) {
        }
        
        public void removeKeyListener(KeyListener l) {
        }
        
    }
    
    protected static void processException(Exception e) {
        if (debug) {
            e.printStackTrace();
        } else {
            System.err.println(e);
        }
    }
    
    /**
     * For printf debugging.
     */
    private static final boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.err.println("JHelpPrintHandler: " + str);
        }
    }
    
}
