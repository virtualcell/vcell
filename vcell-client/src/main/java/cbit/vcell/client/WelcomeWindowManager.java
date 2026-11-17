package cbit.vcell.client;

import cbit.vcell.client.desktop.WelcomePanel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.resource.VCellConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.api.server.ClientServerManager;
import org.vcell.api.server.ConnectionStatus;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.VCDocumentInfo;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;

/**
 * The welcome window VCell starts at (unless given a model to open), and comes back to when the last model window
 * closes: recently saved models to open, new models to create, and the login. Opening or creating a model hides it,
 * unless the user chose to keep it open.
 */
public class WelcomeWindowManager extends TopLevelWindowManager {
	private static final Logger lg = LogManager.getLogger(WelcomeWindowManager.class);
	// Whether the user keeps the window open after opening a model, kept in ~/.vcell/vcellconfig.properties
	private static final String KEEP_OPEN_KEY = "vcell.client.welcome.keepOpen";

	/** The kinds of new model the welcome window offers, as File > New does; the MathModels labeled as in its MathModel menu. */
	public enum NewModel {
		BIOMODEL("New BioModel", VCDocumentType.BIOMODEL_DOC, VCDocument.BIO_OPTION_DEFAULT),
		MATHMODEL_NONSPATIAL("Non-Spatial", VCDocumentType.MATHMODEL_DOC, VCDocument.MATH_OPTION_NONSPATIAL),
		MATHMODEL_SPATIAL_EXISTING("Spatial from database geometry", VCDocumentType.MATHMODEL_DOC, VCDocument.MATH_OPTION_SPATIAL_EXISTS),
		MATHMODEL_SPATIAL_NEW("Spatial from new geometry", VCDocumentType.MATHMODEL_DOC, VCDocument.MATH_OPTION_SPATIAL_NEW),
		MATHMODEL_FROM_BIOMODEL("From BioModel", VCDocumentType.MATHMODEL_DOC, VCDocument.MATH_OPTION_FROMBIOMODELAPP);

		public final String label;
		public final VCDocumentType documentType;
		private final int option;

		NewModel(String label, VCDocumentType documentType, int option) {
			this.label = label;
			this.documentType = documentType;
			this.option = option;
		}
	}

	private final WelcomePanel panel;
	private ListedModelActions listedModelActions = null; // the lists' right-click menus, made on first use

	public WelcomeWindowManager(RequestManager requestManager) {
		super(requestManager);
		this.panel = new WelcomePanel(this);
		// a client not matching the server is shown in the window rather than in a dialog at every login
		ClientServerManager clientServerManager = ((ClientRequestManager) requestManager).getClientServerManager();
		clientServerManager.addPropertyChangeListener(e -> {
			if (!ClientServerManager.PROPERTY_VERSION_MISMATCH.equals(e.getPropertyName())) return;
			ClientServerManager.VersionMismatch mismatch = (ClientServerManager.VersionMismatch) e.getNewValue();
			SwingUtilities.invokeLater(() -> this.panel.showVersionMismatch(mismatch)); // checked while connecting, off the Swing thread
		});
		this.panel.showVersionMismatch(clientServerManager.getVersionMismatch());
	}

	@Override
	public Component getComponent() {
		return this.panel;
	}

	@Override
	public String getManagerID() {
		return ClientMDIManager.WELCOME_WINDOW_ID;
	}

	/** Closing only hides it, to come back when the last model window closes. */
	@Override
	public boolean isRecyclable() {
		return true;
	}

	/** A listed model's right-click menu: open, export, compare, copy its name or ID number, and its owner's actions. */
	public JPopupMenu createModelMenu(VCDocumentInfo documentInfo) {
		if (this.listedModelActions == null) this.listedModelActions = new ListedModelActions(this, this::compareInModelExplorer);
		return this.listedModelActions.createMenu(documentInfo, () -> this.openModel(documentInfo), () -> this.showModelExplorer(documentInfo));
	}

	/** Shows the Model Explorer at this model, to pick the model to compare it with there. */
	public void compareInModelExplorer(VCDocumentInfo documentInfo) {
		((ClientRequestManager) this.getRequestManager()).compareInModelExplorer(documentInfo);
	}

	/** Shows the Model Explorer, with this model selected in it (if not null). */
	public void showModelExplorer(VCDocumentInfo documentInfo) {
		((ClientRequestManager) this.getRequestManager()).showModelExplorer(documentInfo);
	}

	/** Opens a saved model, in a window of its own. */
	public void openModel(VCDocumentInfo documentInfo) {
		this.getRequestManager().openDocument(documentInfo, this, true);
	}

	/** Opens a model from a file the user picks, in a window of its own. */
	public void importModel() {
		((ClientRequestManager) this.getRequestManager()).importDocument(this);
	}

	/** Creates a new model of this kind, in a window of its own. */
	public void newModel(NewModel kind) {
		AsynchClientTask[] tasks = this.newDocument(new VCDocument.DocumentCreationInfo(kind.documentType, kind.option));
		// a spatial MathModel on a new geometry asks for the geometry first, and goes on from there itself
		if (NewModel.MATHMODEL_SPATIAL_NEW == kind) return;
		Hashtable<String, Object> hash = new Hashtable<>();
		hash.put(ClientRequestManager.GUI_PARENT, this.panel);
		hash.put("requestManager", this.getRequestManager());
		ClientTaskDispatcher.dispatch(this.panel, hash, tasks, false);
	}

	/** Logs in through the browser, or as the guest user. */
	public void logIn(boolean guest) {
		((ClientRequestManager) this.getRequestManager()).logIn(this, guest);
	}

	/** Ends a guest session and signs up in the browser, logging in with the new account. */
	public void signUp() {
		((ClientRequestManager) this.getRequestManager()).signUpFromGuest(this);
	}

	public void logOut() {
		this.getRequestManager().logOut(this);
	}

	/** Shows the user's login (or its absence) and the recent models of the user logged in. */
	public void updateConnectionStatus(ConnectionStatus connectionStatus) {
		this.panel.updateConnectionStatus(connectionStatus, this.getRequestManager().getDocumentManager());
	}

	/** Whether the welcome window stays open after a model is opened or created from it. */
	public static boolean isKeptOpen() {
		try {
			return Boolean.parseBoolean(VCellConfiguration.getValue(KEEP_OPEN_KEY, "false"));
		} catch (Exception e) {
			lg.warn("Could not read {} from the VCell configuration", KEEP_OPEN_KEY, e);
			return false;
		}
	}

	public static void setKeptOpen(boolean keptOpen) {
		try {
			VCellConfiguration.putValue(KEEP_OPEN_KEY, Boolean.toString(keptOpen));
		} catch (Exception e) {
			lg.warn("Could not save {} to the VCell configuration", KEEP_OPEN_KEY, e);
		}
	}
}
