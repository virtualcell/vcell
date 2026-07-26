/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.documentation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.*;

import javax.help.HelpSet;
import javax.help.JHelp;
import javax.help.Map.ID;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.vcell.client.logicalwindow.LWTopFrame;
import org.vcell.util.gui.GeneralGuiUtils;
import org.vcell.util.document.VCellSoftwareVersion;

import cbit.vcell.client.ChildWindowManager.ChildWindow;

/**
 * This helpviewer enables navigate virtual frap help through table of contents.
 * The contents are displayed as html files which enable hyperlinks.
 * In addition, the helpviewer provides word search.
 * JavaHelp map, TOC, index, and HelpSet files have to be created to make helpviewer work.
 *
 * @author Tracy LI
 * Created in June 2008.
 * @version 1.0
 */
@SuppressWarnings("serial")
public class VcellHelpViewer extends JPanel {
    public static final int DEFAULT_HELP_DIALOG_WIDTH = 900;
    public static final int DEFAULT_HELP_DIALOG_HEIGHT = 700;

    public static final String VFRAP_DOC_URL = "/doc/HelpSet.hs";
    public static final String VCELL_DOC_URL = "/vcellDoc/HelpSet.hs";

    private JButton btnCloseHelp;
    private ChildWindow closeableWindow;
    /**
     * reusable reference to viewer; allows garbage (if not visible)
     */
    private static WeakReference<LWTopFrame> standaloneRef = null;

    /**
     * issue: window z-order. The standalone Help window is a top-level, document-independent window,
     * so it is an {@link LWTopFrame} (a tracked LW root that appears in the "Window" menu and is
     * brought to front on open) rather than a raw, un-owned {@code JFrame} that could hide behind the
     * desktop. It is intentionally NOT an owned child window — global Help must outlive any single
     * document window. See {@code docs/windowing-design-patterns.md}.
     */
    @SuppressWarnings("serial")
    private static class VcellHelpWindow extends LWTopFrame {
        private VcellHelpWindow(String title) {
            super();
            setTitle(title);
        }
        @Override
        public String menuDescription() {
            return getTitle();
        }
    }

    public void setCloseMyParent(ChildWindow closeableWindow) {
        this.closeableWindow = closeableWindow;
        if (closeableWindow != null) {
            this.getCloseJButton().setVisible(true);
        }
    }

    private JButton getCloseJButton() {
        if (this.btnCloseHelp != null) return this.btnCloseHelp;
        this.btnCloseHelp = new JButton("Close");
        this.btnCloseHelp.addActionListener(e -> {
            if (this.closeableWindow == null) return;
            this.closeableWindow.close();
            this.closeableWindow = null;
        });
        return this.btnCloseHelp;
    }

    public VcellHelpViewer(String docUrl) {
        URL resourceURL = VcellHelpViewer.class.getResource(docUrl);

        HelpSet helpSet;
        try {
            // get the system class loader
            ClassLoader cl = this.getClass().getClassLoader();
            // create helpset
            helpSet = new HelpSet(cl, resourceURL);
            JHelp jhelp = new JHelp(helpSet);

            if (helpSet.getLocalMap() != null){
                for (ID id : (ArrayList<ID>)Collections.list(helpSet.getLocalMap().getAllIDs())){
                    if (!"GeneralOverview".equals(id.getIDString())) continue;
                    jhelp.setCurrentID(id);
//				    helpSet.setHomeID(id.getIDString());
                    break;
                }
            }
            this.setLayout(new BorderLayout());
            this.add(jhelp);
            GeneralGuiUtils.addCloseWindowKeyboardAction(this);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        this.setPreferredSize(new Dimension(DEFAULT_HELP_DIALOG_WIDTH, DEFAULT_HELP_DIALOG_HEIGHT));

        JPanel panel = new JPanel();
        this.add(panel, BorderLayout.SOUTH);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        this.getCloseJButton().setVisible(false);
        panel.add(this.getCloseJButton());
    }

    public static void showStandaloneViewer() {
        LWTopFrame frame = standaloneRef != null ? standaloneRef.get() : null;
        if (frame == null) {
            VcellHelpViewer helpViewer = new VcellHelpViewer(VcellHelpViewer.VCELL_DOC_URL);
            frame = new VcellHelpWindow("Virtual Cell Help");
            String title = "Virtual Cell Help" + " -- VCell " + VCellSoftwareVersion.fromSystemProperty().getSoftwareVersionString();
            frame.setTitle(title);
            frame.setPreferredSize(new Dimension(VcellHelpViewer.DEFAULT_HELP_DIALOG_WIDTH, VcellHelpViewer.DEFAULT_HELP_DIALOG_HEIGHT));
            frame.pack();
            frame.getContentPane().add(helpViewer);
            GeneralGuiUtils.centerOnScreen(frame);
            standaloneRef = new WeakReference<>(frame);
        }
        frame.setVisible(true);
    }
}