/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client;

import java.awt.Component;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.VersionableType;
import org.vcell.util.importer.PathwayImportPanel.PathwayImportOption;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.ExportEvent;
import cbit.vcell.client.desktop.biomodel.DocumentEditor;
import cbit.vcell.client.desktop.simulation.SimulationWindow;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.xml.merge.XmlTreeDiff;

public abstract class DocumentWindowManager extends TopLevelWindowManager implements java.awt.event.ActionListener, DataViewerManager {

    /**
     * identifying string, for hashtable passing, et. al
     */
    public final static String IDENT = "DocumentWindowManager";
    public static final String SELECT_GEOM_POPUP = "SelectGeomPopup";

    private JPanel jPanel = null;
    private String documentID = null;

    public static class GeometrySelectionInfo {
        private VCDocumentInfo vcDocumentInfo;
        private VCDocument.DocumentCreationInfo selectedGeometryDocument;
        private boolean bFromCurrentGeom = false;

        public GeometrySelectionInfo(VCDocumentInfo vcDocumentInfo) {
            if (!vcDocumentInfo.getVersionType().equals(VersionableType.BioModelMetaData) &&
                    !vcDocumentInfo.getVersionType().equals(VersionableType.MathModelMetaData) &&
                    !vcDocumentInfo.getVersionType().equals(VersionableType.Geometry)) {
                throw new IllegalArgumentException("GeometrySelectionInfo(VCDocumentInfo vcDocumentInfo) must be of VersionableType BioModelMetaData,MathModelMetaData or Geometry");
            }
            this.vcDocumentInfo = vcDocumentInfo;
        }

        public GeometrySelectionInfo(VCDocument.DocumentCreationInfo selectedGeometryDocument) {
            if (selectedGeometryDocument.getDocumentType() != VCDocumentType.GEOMETRY_DOC) {
                throw new IllegalArgumentException("GeometrySelectionInfo(VCDocument.DocumentCreationInfo selectedGeometryDocument) must be of type VCDocument.GEOMETRY_DOC");
            }
            this.selectedGeometryDocument = selectedGeometryDocument;
        }

        public GeometrySelectionInfo() {
            this.bFromCurrentGeom = true;
        }

        public VCDocumentInfo getVCDocumentInfo() {
            return this.vcDocumentInfo;
        }

        public VCDocument.DocumentCreationInfo getDocumentCreationInfo() {
            return this.selectedGeometryDocument;
        }

        public boolean bFromCurrentGeom() {
            return this.bFromCurrentGeom;
        }
    }


    public DocumentWindowManager(JPanel panel, RequestManager requestManager, VCDocument vcDocument) {
        super(requestManager);
        this.setJPanel(panel);
        // figure out unique documentID
        this.setDocumentID(vcDocument);
    }


    public abstract void addResultsFrame(SimulationWindow simWindow);


    public void compareWithSaved() {

        if (this.getVCDocument().getVersion() == null) {
            // shouldn't happen (menu should not be available), but check anyway...
            PopupGenerator.showErrorDialog(this, "There is no saved version of this document");
            return;
        }
        final MDIManager mdiManager = new ClientMDIManager(this.getRequestManager());
        mdiManager.blockWindow(this.getManagerID());

        String taskName = "Comparing with saved";
        AsynchClientTask task1 = new AsynchClientTask(taskName, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

            @Override
            public void run(Hashtable<String, Object> hashTable) throws Exception {
                XmlTreeDiff xmlTreeDiff = DocumentWindowManager.this.getRequestManager().compareWithSaved(DocumentWindowManager.this.getVCDocument());
                hashTable.put("xmlTreeDiff", xmlTreeDiff);
            }
        };
        AsynchClientTask task2 = new AsynchClientTask(taskName, AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, false) {

            @Override
            public void run(Hashtable<String, Object> hashTable) throws Exception {
                try {
                    if (hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR) == null) {
                        XmlTreeDiff xmlTreeDiff = (XmlTreeDiff) hashTable.get("xmlTreeDiff");
                        String baselineDesc = DocumentWindowManager.this.getVCDocument() + ", " + (DocumentWindowManager.this.getVCDocument().getVersion() == null ? "not saved" : DocumentWindowManager.this.getVCDocument().getVersion().getDate());
                        String modifiedDesc = "Opened document instance";
                        DocumentWindowManager.this.getRequestManager().showComparisonResults(DocumentWindowManager.this, xmlTreeDiff, baselineDesc, modifiedDesc);
                    }
                } finally {
                    mdiManager.unBlockWindow(DocumentWindowManager.this.getManagerID());
                }
            }
        };
        ClientTaskDispatcher.dispatch(this.getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[]{task1, task2}, false);
    }


    private String createTempID() {
        return "TempID" + System.currentTimeMillis();
    }


    public void dataJobMessage(DataJobEvent event) {

        // just pass them along...
        this.fireDataJobMessage(event);
    }


    public void exportMessage(ExportEvent exportEvent) {
        //
        // may not come from a simulation, but we'll create a VCSimulationIdentifer to match with the window just in case
        // if it is not a match, no harm.
        //
        VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(exportEvent.getDataKey(), exportEvent.getUser());
        if (this.haveSimulationWindow(vcSimID) == null) {// && exportEvent.getEventTypeID() != ExportEvent.EXPORT_COMPLETE) {
            return;
        }
        // just pass them along...
        this.fireExportMessage(exportEvent);
    }


    public JComponent getComponent() {
        return this.jPanel;
    }


    public String getManagerID() {
        return this.documentID;
    }


    public User getUser() {
        return this.getRequestManager().getDocumentManager().getUser();
    }


    public abstract VCDocument getVCDocument();


    abstract SimulationWindow haveSimulationWindow(VCSimulationIdentifier vcSimulationIdentifier);

    public void openDocument(VCDocumentType documentType) {
        this.getRequestManager().openDocument(documentType, this);
    }

    public void importPathway(PathwayImportOption pathwayImportOption) {
        this.getRequestManager().openPathway(this, pathwayImportOption);
    }


    public void reconnect() {
        this.getRequestManager().reconnect(this);
    }


    public abstract void resetDocument(VCDocument newDocument);


    public void revertToSaved() {
        if (this.getVCDocument().getVersion() == null) {
            // shouldn't happen (menu should not be available), but check anyway...
            PopupGenerator.showErrorDialog(this, "There is no saved version of this document");
            return;
        }
        String confirm = PopupGenerator.showWarningDialog(this, this.getRequestManager().getUserPreferences(), UserMessage.warn_RevertToSaved, null);
        if (confirm.equals(UserMessage.OPTION_CANCEL)) {
            //user canceled
            return;
        }
        this.getRequestManager().revertToSaved(this);
    }


    public void saveDocument(boolean replace) {
        this.getRequestManager().saveDocument(this, replace);
    }


    public void saveDocumentAsNew() {
        this.getRequestManager().saveDocumentAsNew(this);
    }


    protected void setDocumentID(VCDocument vcDocument) {
        String oldID = this.getManagerID();
        if (vcDocument.getVersion() != null) {
            this.setDocumentID(vcDocument.getVersion().getVersionKey().toString());
        } else {
            // if vcDocument has no Version it was never saved, it was created in this session
            // we generate a temporary ID (until first save occurs, if ever)
            this.setDocumentID(this.createTempID());
        }
        this.getRequestManager().managerIDchanged(oldID, this.getManagerID());
    }


    private void setDocumentID(java.lang.String newDocumentID) {
        this.documentID = newDocumentID;
    }


    private void setJPanel(javax.swing.JPanel newJPanel) {
        this.jPanel = newJPanel;
    }


    public void showFieldDataWindow() {
        this.getRequestManager().showFieldDataWindow(null);
    }


    public void showTestingFrameworkWindow() {
        this.getRequestManager().showTestingFrameworkWindow();
    }


    public void startExport(Component requester, OutputContext outputContext, ExportSpecs exportSpecs) {
        this.getRequestManager().startExport(outputContext, requester, exportSpecs);
    }

    public abstract void updateConnectionStatus(ConnectionStatus connStatus);

    public abstract DocumentEditor getDocumentEditor();
}

