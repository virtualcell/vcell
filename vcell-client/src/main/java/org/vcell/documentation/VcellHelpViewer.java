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
import javax.swing.JFrame;
import javax.swing.JPanel;

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
    private static WeakReference<JFrame> standaloneRef = null;

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
        JFrame frame = standaloneRef != null ? standaloneRef.get() : null;
        if (frame == null) {
            VcellHelpViewer helpViewer = new VcellHelpViewer(VcellHelpViewer.VCELL_DOC_URL);
            frame = new JFrame("Virtual Cell Help");
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