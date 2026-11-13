package cbit.vcell.client;

import java.awt.Component;

import javax.swing.JPopupMenu;

import org.vcell.api.server.ConnectionStatus;
import org.vcell.util.document.VCDocumentInfo;

import cbit.vcell.client.desktop.ModelExplorerPanel;
import cbit.vcell.client.desktop.biomodel.BioModelsNetModelInfo;
import cbit.vcell.client.desktop.biomodel.BioModelsNetPanel;

/**
 * The Model Explorer's window: the VCell database's models, geometries and the BioModels Database, to browse, read about
 * and open. There is one, made the first time it's asked for (from File > Model Explorer, the welcome window, or a model
 * window's status bar); closing it only hides it.
 */
public class ModelExplorerWindowManager extends TopLevelWindowManager {
	private final ModelExplorerPanel panel;
	private ListedModelActions listedModelActions = null; // the tree's right-click menus, made on first use

	public ModelExplorerWindowManager(RequestManager requestManager) {
		super(requestManager);
		this.panel = new ModelExplorerPanel(this);
	}

	@Override
	public Component getComponent() {
		return this.panel;
	}

	@Override
	public String getManagerID() {
		return ClientMDIManager.MODEL_EXPLORER_WINDOW_ID;
	}

	/** Closing only hides it. */
	@Override
	public boolean isRecyclable() {
		return true;
	}

	/** Opens a saved model (or geometry), in a window of its own. */
	public void openModel(VCDocumentInfo documentInfo) {
		this.getRequestManager().openDocument(documentInfo, this, true);
	}

	/** Imports a BioModels Database model, as a new BioModel in a window of its own. */
	public void importModel(BioModelsNetModelInfo info) {
		BioModelsNetPanel.importModel(info, this);
	}

	/** A model's right-click menu, as the welcome window's lists have it. */
	public JPopupMenu createModelMenu(VCDocumentInfo documentInfo) {
		if (this.listedModelActions == null) this.listedModelActions = new ListedModelActions(this, this::startComparison);
		return this.listedModelActions.createMenu(documentInfo, () -> this.openModel(documentInfo));
	}

	/** Asks for the model to compare this one with, to be picked in the tree. */
	public void startComparison(VCDocumentInfo documentInfo) {
		this.panel.startComparison(documentInfo);
	}

	/** Shows VCell's comparison of two saved models of the same kind: what changed from the baseline to the other. */
	public void compare(VCDocumentInfo baseline, VCDocumentInfo modified) {
		DatabaseWindowManager.compare(this, baseline, modified);
	}

	/** Selects a model in the tree, if given one. */
	public void showModel(VCDocumentInfo documentInfo) {
		this.panel.showModel(documentInfo);
	}

	/** Lists the database's models for whoever is logged in; nothing, for no one. */
	public void updateConnectionStatus(ConnectionStatus connectionStatus) {
		boolean connected = null != connectionStatus && ConnectionStatus.CONNECTED == connectionStatus.getStatus();
		boolean reconnecting = null != connectionStatus && (ConnectionStatus.DISCONNECTED == connectionStatus.getStatus()
				|| ConnectionStatus.INITIALIZING == connectionStatus.getStatus());
		if (reconnecting) return; // keeps what it lists through a dropped connection
		this.panel.setDocumentManager(connected ? this.getRequestManager().getDocumentManager() : null);
	}
}
