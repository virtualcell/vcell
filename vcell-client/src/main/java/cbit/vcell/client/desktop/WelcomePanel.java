package cbit.vcell.client.desktop;

import cbit.vcell.client.DatabaseWindowManager;
import cbit.vcell.client.WelcomeWindowManager;
import cbit.vcell.client.WelcomeWindowManager.NewModel;
import cbit.vcell.client.desktop.biomodel.BioModelEditorPathwayCommonsPanel;
import cbit.vcell.client.desktop.biomodel.BioModelEditorPathwayCommonsPanel.PathwayData;
import cbit.vcell.client.desktop.biomodel.BioModelsNetPanel;
import cbit.vcell.client.desktop.biomodel.SelectionManager;
import cbit.vcell.client.laf.VCellColors;
import cbit.vcell.client.laf.VCellFonts;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.RecentModelsPanel;
import org.vcell.api.server.ClientServerManager.VersionMismatch;
import org.vcell.api.server.ConnectionStatus;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.VCellIcons;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.util.Arrays;

/**
 * What the welcome window shows, top to bottom: VCell's logo and version; the account, as the ways to log in when no
 * one is, or who is logged in, with a button to log out; buttons for new models; once logged in, tabs of the models
 * the user saved most recently and of the database's models; and whether the window stays open after a model opens.
 */
public class WelcomePanel extends JPanel {
	private static final String LOGIN_OPTIONS = "options", ACCOUNT_STATUS = "status";

	private final WelcomeWindowManager manager;
	private final RecentModelsPanel recentModels = new RecentModelsPanel();
	private final DatabaseWindowPanel databasePanel = new DatabaseWindowPanel(false, false); // as a model window's "VCell DB" tab
	private final DatabaseWindowManager databaseManager;
	private final BioModelsNetPanel bioModelsNetPanel = new BioModelsNetPanel(); // as a BioModel window's tabs
	private final BioModelEditorPathwayCommonsPanel pathwayCommonsPanel = new BioModelEditorPathwayCommonsPanel();
	private final JTabbedPane modelTabs = new JTabbedPane(); // recent models, the database's, and others'; only when logged in
	private JComponent startSection = null; // the new model buttons; once the user has chosen how to log in, or not to
	private final CardLayout accountCards = new CardLayout();
	private final JPanel account = new JPanel(this.accountCards) {
		@Override
		public Dimension getPreferredSize() { // the card showing, not the tallest
			for (Component card : this.getComponents()) {
				if (card.isVisible()) return card.getPreferredSize();
			}
			return super.getPreferredSize();
		}
	};
	private final JButton logInButton = new JButton("Log In");
	private final JButton guestButton = new JButton("Continue as Guest");
	private final JButton notNowButton = new JButton("Offline Mode");
	private final JLabel accountStatus = new JLabel();
	private final JButton accountButton = new JButton();
	private final JCheckBox keepOpen = new JCheckBox("Keep this window open after opening a model");
	private DocumentManager documentManager = null;
	private int state = ConnectionStatus.NOT_CONNECTED;
	private boolean loggedOut = true; // not connected, or a login that failed to connect
	private boolean loginOptionsDismissed = false; // "Not Now": a smaller "Log In" button in their place
	private long helpHiddenAt = 0; // when the login help last closed
	private final CautionIcon caution = new CautionIcon();
	private final JLabel versionNotice = new JLabel(this.caution); // beside the version, when it doesn't match the server's
	private Timer cautionPulse = null;
	private VersionMismatch versionMismatch = null; // the one shown, if any
	private boolean refitPending = false;

	public WelcomePanel(WelcomeWindowManager manager) {
		super(new BorderLayout(0, 14));
		this.manager = manager;
		this.databaseManager = new DatabaseWindowManager(this.databasePanel, manager.getRequestManager());
		this.databasePanel.setDatabaseWindowManager(this.databaseManager);
		this.bioModelsNetPanel.setDocumentWindowManager(manager);
		// a pathway found is shown by selecting it, as in a BioModel window; here, in a new BioModel
		SelectionManager pathwaySelection = new SelectionManager();
		pathwaySelection.addPropertyChangeListener(e -> {
			if (!e.getPropertyName().startsWith(SelectionManager.PROPERTY_NAME_SELECTED_OBJECTS)) return; // the name carries a timestamp
			Object[] selected = pathwaySelection.getSelectedObjects();
			if (null != selected && 1 == selected.length && selected[0] instanceof PathwayData pathway) manager.newModelFromPathway(pathway);
		});
		this.pathwayCommonsPanel.setSelectionManager(pathwaySelection);
		this.setName("WelcomePanel");
		this.setBorder(BorderFactory.createEmptyBorder(18, 22, 16, 22));
		this.startSection = this.createNewModels();
		this.startSection.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0)); // its own gap, gone when it is
		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(false);
		top.add(WelcomePanel.stack(this.createHeader(), this.createAccount()), BorderLayout.NORTH);
		top.add(this.startSection, BorderLayout.CENTER);
		this.add(top, BorderLayout.NORTH);
		this.add(this.createModelTabs(), BorderLayout.CENTER);
		this.add(this.createKeepOpen(), BorderLayout.SOUTH);
		this.recentModels.setOpener(manager::openModel);
		this.updateConnectionStatus(null, null);
	}

	/** Who is logged in, and their recent models; called as the connection changes, and on every status update. */
	public void updateConnectionStatus(ConnectionStatus status, DocumentManager documentManager) {
		int state = null == status ? ConnectionStatus.NOT_CONNECTED : status.getStatus();
		// logging out offers the ways to log in again, even if they were put away before
		if (ConnectionStatus.NOT_CONNECTED == state && ConnectionStatus.NOT_CONNECTED != this.state) this.loginOptionsDismissed = false;
		this.state = state;
		// Logged in, the models stay through a dropped connection and the reconnecting after it (no shrinking and
		// growing as the connection comes and goes); they go only on logging out
		boolean reconnecting = (ConnectionStatus.DISCONNECTED == state || ConnectionStatus.INITIALIZING == state) && null != this.documentManager;
		this.accountStatus.setText(switch (state) {
			case ConnectionStatus.CONNECTED -> User.isGuest(status.getUserName()) ? "Logged in as a guest" : "Logged in as " + status.getUserName();
			case ConnectionStatus.INITIALIZING -> reconnecting ? "Reconnecting..." : "Logging in...";
			case ConnectionStatus.DISCONNECTED -> "Disconnected from the VCell server";
			default -> "Not logged in";
		});
		// a login that failed to connect offers the ways to log in again, to try once more
		this.loggedOut = ConnectionStatus.NOT_CONNECTED == state || ConnectionStatus.DISCONNECTED == state && !reconnecting;
		this.accountButton.setText(this.loggedOut ? "Log In" : "Log Out");
		this.accountButton.setEnabled(ConnectionStatus.INITIALIZING != state);
		this.showAccountCard(this.loggedOut && !this.loginOptionsDismissed ? LOGIN_OPTIONS : ACCOUNT_STATUS);
		DocumentManager current = ConnectionStatus.CONNECTED == state ? documentManager : reconnecting ? this.documentManager : null;
		if (current != this.documentManager) { // status updates come often; only a new login changes the lists
			this.documentManager = current;
			this.recentModels.setDocumentManager(current);
			this.databasePanel.setDocumentManager(current);
			this.databasePanel.updateConnectionStatus(status);
			if (null != current) this.databaseManager.initializeAll();
		}
		if (this.modelTabs.isVisible() != (null != current)) {
			this.modelTabs.setVisible(null != current);
			this.packWindow();
		}
	}

	/**
	 * Fits the window to what it shows, centered on the screen as it is once logged in: logging in grows it downward,
	 * into the middle of the screen, rather than past the bottom.
	 */
	public void packAndCenter() {
		Window window = SwingUtilities.getWindowAncestor(this);
		boolean tabsShown = this.modelTabs.isVisible(), startShown = this.startSection.isVisible();
		this.modelTabs.setVisible(true);
		this.startSection.setVisible(true);
		window.pack();
		WelcomePanel.fitToScreen(window);
		Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds(); // below the menu bar
		window.setLocation(screen.x + (screen.width - window.getWidth()) / 2, screen.y + (screen.height - window.getHeight()) / 2);
		this.modelTabs.setVisible(tabsShown);
		this.startSection.setVisible(startShown);
		window.pack();
		WelcomePanel.fitToScreen(window);
	}

	// Fits the window to what it now shows: the model tabs come and go with the login. Growing, it keeps a width the user
	// gave it, if wider; shrinking (logging out), it fits snugly. Hidden (logging out from a model window), it is fitted
	// all the same, to come back at its new size.
	private void packWindow() {
		Window window = SwingUtilities.getWindowAncestor(this);
		if (null == window || !window.isDisplayable()) return; // not yet shown: packAndCenter() fits it then
		int width = window.getWidth(), height = window.getHeight();
		window.pack();
		if (window.getHeight() > height) window.setSize(Math.max(width, window.getWidth()), window.getHeight());
		WelcomePanel.fitToScreen(window);
	}

	// No taller than the screen, and moved up if it now runs past the bottom (the model tabs make do with less room)
	private static void fitToScreen(Window window) {
		Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		if (window.getHeight() > screen.height) window.setSize(window.getWidth(), screen.height);
		int overBottom = window.getY() + window.getHeight() - (screen.y + screen.height);
		if (overBottom > 0) window.setLocation(window.getX(), Math.max(screen.y, window.getY() - overBottom));
	}

	private JComponent createHeader() {
		JLabel logo = new JLabel(new ImageIcon(VCellIcons.getJFrameImageIcon()));
		JLabel title = new JLabel("Virtual Cell");
		VCellFonts.setCategory(title, VCellFonts.Category.TITLE);
		JLabel version = new JLabel("Version " + DocumentWindowAboutBox.getVERSION_NO() + " (build " + DocumentWindowAboutBox.getBUILD_NO() + ")");
		VCellFonts.setCategory(version, VCellFonts.Category.CAPTION);
		version.setForeground(VCellColors.getDimForeground());
		JPanel titles = new JPanel(new GridLayout(0, 1, 0, 2));
		titles.setOpaque(false);
		titles.add(title);
		titles.add(version);
		JPanel header = new JPanel(new BorderLayout(14, 0));
		header.setOpaque(false);
		header.add(logo, BorderLayout.WEST);
		header.add(titles, BorderLayout.CENTER);
		this.versionNotice.setName("WelcomeVersionNotice");
		this.versionNotice.setIconTextGap(8);
		this.versionNotice.setVisible(false);
		// its wrapping width is in pixels, for the text size: worked out again when that changes (or the theme does),
		// and the window fitted to it, once the change has resized every window
		this.versionNotice.addPropertyChangeListener("font", e -> {
			if (null == this.versionMismatch || this.refitPending) return;
			this.refitPending = true;
			SwingUtilities.invokeLater(() -> {
				this.refitPending = false;
				if (null == this.versionMismatch) return;
				this.renderVersionNotice();
				this.packWindow();
			});
		});
		header.add(this.versionNotice, BorderLayout.EAST);
		return header;
	}

	/**
	 * Shows, beside the version, that this VCell doesn't match the server's (its caution sign pulsing for a few
	 * seconds, then steady), or nothing when it does.
	 */
	public void showVersionMismatch(VersionMismatch mismatch) {
		if (null != this.cautionPulse) this.cautionPulse.stop();
		this.caution.alpha = 1f;
		this.versionMismatch = mismatch;
		this.versionNotice.setVisible(null != mismatch);
		if (null == mismatch) return;
		this.renderVersionNotice();
		long start = System.currentTimeMillis();
		this.cautionPulse = new Timer(40, e -> { // three slow pulses, then steady
			long elapsed = System.currentTimeMillis() - start;
			if (elapsed >= CautionIcon.PULSE_MILLIS * CautionIcon.PULSES) {
				this.caution.alpha = 1f;
				((Timer) e.getSource()).stop();
			} else {
				double phase = 2 * Math.PI * elapsed / CautionIcon.PULSE_MILLIS;
				this.caution.alpha = (float) (0.15 + 0.85 * (0.5 + 0.5 * Math.cos(phase)));
			}
			this.versionNotice.repaint();
		});
		this.cautionPulse.start();
		this.revalidate();
		this.packWindow();
	}

	// The notice's text, wrapping to a few lines at a width for the current text size
	private void renderVersionNotice() {
		VersionMismatch mismatch = this.versionMismatch;
		String server = mismatch.server().getVersionNumber() + " (build " + mismatch.server().getBuildNumber() + ")";
		int width = Math.round(VCellFonts.getSize() * 15);
		this.versionNotice.setText(String.format("""
				<html><body style='width: %dpx'><b>Version mismatch</b><br>
				The latest VCell version is %s; Until you upgrade, some features will be unavailable.</body></html>""",
				width, server));
		this.versionNotice.setToolTipText("<html>Server: " + mismatch.server().getSoftwareVersionString()
				+ "<br>This VCell: " + mismatch.client().getSoftwareVersionString() + "</html>");
	}

	// A caution sign: an amber triangle with an exclamation mark, about twice the text's height, fading as it pulses
	private static final class CautionIcon implements Icon {
		static final int PULSE_MILLIS = 1000, PULSES = 3;
		private static final Color AMBER = new Color(0xF2A900);
		float alpha = 1f;

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.alpha));
			int size = this.getIconWidth();
			float inset = size * 0.06f;
			java.awt.geom.Path2D triangle = new java.awt.geom.Path2D.Float();
			triangle.moveTo(x + size / 2f, y + inset);
			triangle.lineTo(x + size - inset, y + size - inset);
			triangle.lineTo(x + inset, y + size - inset);
			triangle.closePath();
			g2.setColor(AMBER);
			g2.fill(triangle);
			g2.setStroke(new BasicStroke(size / 12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.draw(triangle); // rounds its corners
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(size / 9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.draw(new java.awt.geom.Line2D.Float(x + size / 2f, y + size * 0.38f, x + size / 2f, y + size * 0.66f));
			float dot = size / 8f;
			g2.fill(new java.awt.geom.Ellipse2D.Float(x + (size - dot) / 2f, y + size * 0.76f, dot, dot));
			g2.dispose();
		}

		@Override
		public int getIconWidth() {
			return Math.round(VCellFonts.getSize() * 2.2f);
		}

		@Override
		public int getIconHeight() {
			return this.getIconWidth();
		}
	}

	// The ways to log in when no one is; otherwise who is, with a button to log out
	private JComponent createAccount() {
		this.logInButton.setName("WelcomeLogIn");
		this.guestButton.setName("WelcomeGuest");
		this.notNowButton.setName("WelcomeNotNow");
		this.logInButton.addActionListener(e -> this.manager.logIn(false));
		this.guestButton.addActionListener(e -> this.manager.logIn(true));
		this.notNowButton.addActionListener(e -> {
			this.loginOptionsDismissed = true;
			this.showAccountCard(ACCOUNT_STATUS);
		});
		JPanel options = new JPanel(new GridLayout(1, 3, 6, 0));
		options.setOpaque(false);
		options.add(this.logInButton);
		options.add(this.guestButton);
		options.add(this.notNowButton);

		this.accountStatus.setName("WelcomeAccountStatus");
		this.accountButton.setName("WelcomeAccountButton");
		this.accountButton.addActionListener(e -> {
			if (!this.loggedOut) {
				this.manager.logOut();
			} else { // logged out, the login options put away: bring them back
				this.loginOptionsDismissed = false;
				this.showAccountCard(LOGIN_OPTIONS);
			}
		});
		JPanel status = new JPanel(new BorderLayout(8, 0));
		status.setOpaque(false);
		status.add(this.accountStatus, BorderLayout.CENTER);
		status.add(this.accountButton, BorderLayout.EAST);

		this.account.setName("WelcomeAccount");
		this.account.setOpaque(false);
		this.account.add(options, LOGIN_OPTIONS);
		this.account.add(status, ACCOUNT_STATUS);
		// what logging in does, on a help icon beside the heading
		String about = """
				<html>Logging in opens your browser to sign in to VCell<br>
				securely with Auth0. You stay logged in, even when<br>
				VCell restarts, until you log out.</html>""";
		JButton help = new JButton(new HelpIcon());
		help.setName("WelcomeAccountHelp");
		help.setToolTipText(about);
		help.getAccessibleContext().setAccessibleName("About logging in");
		help.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
		help.setContentAreaFilled(false);
		help.setFocusPainted(false);
		help.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		JLabel text = new JLabel(about);
		text.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
		JPopupMenu popup = new JPopupMenu();
		popup.setName("WelcomeAccountHelpPopup");
		popup.add(text);
		popup.addPopupMenuListener(new PopupMenuListener() {
			@Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
			@Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { WelcomePanel.this.helpHiddenAt = System.currentTimeMillis(); }
			@Override public void popupMenuCanceled(PopupMenuEvent e) {}
		});
		help.addActionListener(e -> { // a click shows it too, for those who don't wait for the tooltip
			// the press of a click on the button while it shows closes it; the click is done then, not to open it again
			if (System.currentTimeMillis() - this.helpHiddenAt < 300) return;
			popup.show(help, 0, help.getHeight());
		});
		JPanel heading = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0)); // no gap before the heading, lined up with the others
		heading.setOpaque(false);
		heading.add(WelcomePanel.heading("Account"));
		heading.add(help);
		JPanel section = new JPanel(new BorderLayout(0, 6));
		section.setOpaque(false);
		section.add(heading, BorderLayout.NORTH);
		section.add(this.account, BorderLayout.CENTER);
		return section;
	}

	// A small circled question mark, about as tall as the text, in the dim text color
	private static final class HelpIcon implements Icon {
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			int size = this.getIconWidth();
			g2.setColor(VCellColors.getDimForeground());
			g2.setStroke(new BasicStroke(Math.max(1f, size / 14f)));
			g2.drawOval(x, y, size - 1, size - 1);
			g2.setFont(c.getFont().deriveFont(Font.BOLD, size * 0.7f));
			FontMetrics metrics = g2.getFontMetrics();
			g2.drawString("?", x + (size - metrics.stringWidth("?")) / 2f, y + (size - metrics.getHeight()) / 2f + metrics.getAscent());
			g2.dispose();
		}

		@Override
		public int getIconWidth() {
			return Math.round(VCellFonts.getSize() * 1.15f);
		}

		@Override
		public int getIconHeight() {
			return this.getIconWidth();
		}
	}

	// The login choices, or who is logged in; the new model buttons only once the choices are put away
	private void showAccountCard(String card) {
		this.accountCards.show(this.account, card);
		this.account.revalidate(); // its height is the card's
		boolean start = !LOGIN_OPTIONS.equals(card);
		if (null != this.startSection && this.startSection.isVisible() != start) {
			this.startSection.setVisible(start);
			this.packWindow();
		}
	}

	// New BioModel, and New MathModel with a menu of its kinds, as File > New > MathModel has
	private JComponent createNewModels() {
		JButton newBioModel = new JButton(NewModel.BIOMODEL.label, VCellIcons.makeIcon("/icons/biomodel.png"));
		newBioModel.setName("Welcome" + NewModel.BIOMODEL.name());
		newBioModel.addActionListener(e -> this.manager.newModel(NewModel.BIOMODEL));

		JPopupMenu mathModels = new JPopupMenu();
		for (NewModel kind : NewModel.values()) {
			if (VCDocumentType.MATHMODEL_DOC != kind.documentType) continue;
			JMenuItem item = new JMenuItem(kind.label);
			item.setName("Welcome" + kind.name());
			item.addActionListener(e -> this.manager.newModel(kind));
			mathModels.add(item);
		}
		JButton newMathModel = new JButton("New MathModel \u25BE", VCellIcons.makeIcon("/icons/mathModel.png"));
		newMathModel.setName("WelcomeNewMathModel");
		newMathModel.addActionListener(e -> mathModels.show(newMathModel, 0, newMathModel.getHeight()));

		JPanel buttons = new JPanel(new GridLayout(1, 2, 6, 0));
		buttons.setOpaque(false);
		buttons.add(newBioModel);
		buttons.add(newMathModel);
		return WelcomePanel.section("Start", buttons);
	}

	// The models the user saved recently, and the database's, as a model window's "VCell DB" tab; takes whatever
	// height is left
	private JComponent createModelTabs() {
		this.recentModels.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		this.modelTabs.setName("WelcomeModelTabs");
		this.modelTabs.addTab("Recent Models", this.recentModels);
		this.modelTabs.addTab("VCell DB", this.databasePanel);
		this.modelTabs.addTab(BioModelsNetPanel.BIO_MODELS_NET, this.bioModelsNetPanel);
		this.modelTabs.addTab("Pathway Comm", this.pathwayCommonsPanel);
		this.modelTabs.setPreferredSize(new Dimension(420, 380));
		this.modelTabs.setVisible(false); // until someone logs in
		return this.modelTabs;
	}

	private JComponent createKeepOpen() {
		this.keepOpen.setName("WelcomeKeepOpen");
		this.keepOpen.setSelected(WelcomeWindowManager.isKeptOpen());
		this.keepOpen.addActionListener(e -> WelcomeWindowManager.setKeptOpen(this.keepOpen.isSelected()));
		return this.keepOpen;
	}

	private static JComponent section(String title, JComponent content) {
		JPanel section = new JPanel(new BorderLayout(0, 6));
		section.setOpaque(false);
		section.add(WelcomePanel.heading(title), BorderLayout.NORTH);
		section.add(content, BorderLayout.CENTER);
		return section;
	}

	// Components one above the other, each its own height and the full width; nested border layouts rather than a
	// box or grid bag, so wrapped text is given the width it has instead of asking for the width of a single line
	private static JComponent stack(JComponent... rows) {
		JPanel stack = new JPanel(new BorderLayout(0, 14));
		stack.setOpaque(false);
		stack.add(rows[0], BorderLayout.NORTH);
		if (rows.length > 1) stack.add(WelcomePanel.stack(Arrays.copyOfRange(rows, 1, rows.length)), BorderLayout.CENTER);
		return stack;
	}

	private static JLabel heading(String text) {
		return GuiUtils.createBoldJLabel(text);
	}
}
