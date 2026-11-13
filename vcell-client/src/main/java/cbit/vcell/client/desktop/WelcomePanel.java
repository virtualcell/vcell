package cbit.vcell.client.desktop;

import cbit.vcell.client.WelcomeWindowManager;
import cbit.vcell.client.WelcomeWindowManager.NewModel;
import cbit.vcell.client.desktop.biomodel.BioModelsNetPanel;
import cbit.vcell.client.laf.VCellColors;
import cbit.vcell.client.laf.VCellFonts;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.DatabaseModelsPanel;
import cbit.vcell.desktop.RecentModelsPanel;
import cbit.vcell.desktop.SearchOptions;
import org.vcell.api.server.ClientServerManager.VersionMismatch;
import org.vcell.api.server.ConnectionStatus;
import org.vcell.documentation.VcellHelpViewer;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.LoadingWheel;
import org.vcell.util.gui.VCellIcons;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;

/**
 * What the welcome window shows. Before the user picks how to log in (or not to): VCell's logo and a welcome, centered,
 * with a prompt and the ways to log in under them, and any version mismatch below those. After: the logo at the left,
 * beside it who is logged in (with a button to log out, or to go back to the ways to log in), and at the right buttons
 * for new models; any version mismatch below those; whether the window stays open after a model opens; and, once
 * logged in, tabs of the models the user saved most recently and of the databases' models. VCell's version is in the
 * window's title.
 */
public class WelcomePanel extends JPanel {
	private static final String LOGIN_OPTIONS = "options", ACCOUNT_STATUS = "status";

	private final WelcomeWindowManager manager;
	private final RecentModelsPanel recentModels = new RecentModelsPanel();
	private final DatabaseModelsPanel databaseModels = new DatabaseModelsPanel(); // the database's models, as its tree's folders sort them
	private final BioModelsNetPanel bioModelsNetPanel = new BioModelsNetPanel(); // the BioModels Database browser, which model windows no longer carry
	private final JTabbedPane modelTabs = new JTabbedPane(); // recent models, VCell's database and the BioModels Database
	private final JPanel modelsArea = new JPanel(new BorderLayout(0, 8)); // one search, over all the tabs, and the tabs; only when logged in
	private final SearchField searchField = new SearchField("Search models");
	private final Timer searchPause = new Timer(150, e -> this.search()); // searching once typing pauses, not at each key
	private final SearchOptionsPanel searchOptions = new SearchOptionsPanel(this.searchPause::restart); // the advanced search
	private final JToggleButton filtersButton = new JToggleButton(); // shows the search options, in the header's place
	private final JPanel headerArea = new JPanel(new BorderLayout()); // the header, or the search options while they show
	private JComponent headerSection = null; // the logo, account and new model buttons, and any version mismatch
	private int optionsGrowth = 0, optionsRise = 0; // how much taller the window grew, and how far up it moved, for the search options
	private JComponent startSection = null; // the new model buttons, in a row under the logo and account; once the user has chosen how to log in, or not to
	private final JPanel besideLogo = new JPanel(new GridBagLayout()); // the account: centered while choosing; after, any version mismatch, then the account at the right
	private JComponent accountSection = null;
	private String accountCard = LOGIN_OPTIONS; // the one showing
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
	private final JButton logInButton = new JButton("Log In / Sign Up");
	private final JButton guestButton = new JButton("Try as Guest");
	private final JButton notNowButton = new JButton("Use Offline");
	private final JLabel accountStatus = new JLabel();
	private final JButton accountButton = new JButton(); // Log Out; Go Back, to the login choices, for a guest or offline
	private final JButton signUpButton = new JButton("Sign Up"); // for a guest
	private final Component signUpGap = Box.createHorizontalStrut(6);
	private final HelpButton accountHelp = new HelpButton("WelcomeAccountHelp", "About logging in"); // beside the heading, or who is logged in
	private final JPanel headingRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); // over the login choices: a prompt to pick one
	private final JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.TRAILING, 0, 0)); // who is logged in
	private final JCheckBox keepOpen = new JCheckBox("Keep this window open after opening a model");
	private DocumentManager documentManager = null;
	private int state = ConnectionStatus.NOT_CONNECTED;
	private boolean loggedOut = true; // not connected, or a login that failed to connect
	private boolean guest = false; // logged in as the guest user
	private boolean loginOptionsDismissed = false; // "Use Offline": a "Go Back" button in their place
	private final CautionIcon caution = new CautionIcon();
	private final JLabel versionNotice = new JLabel("Version mismatch", this.caution, SwingConstants.LEADING);
	private final HelpButton versionHelp = new HelpButton("WelcomeVersionHelp", "About the version mismatch");
	private final JPanel versionMismatchPanel = new JPanel(new GridBagLayout()); // when this VCell isn't the server's
	private final JPanel header = new JPanel(new BorderLayout(14, 0));
	private final FlowLayout versionMismatchLayout = new FlowLayout(FlowLayout.CENTER, 0, 0);
	private final JPanel versionMismatchBelow = new JPanel(this.versionMismatchLayout); // below the login choices, or the account
	private final Icon logoIcon = new ImageIcon(VCellIcons.getJFrameImageIcon());
	private final JLabel logo = new JLabel(this.logoIcon, SwingConstants.CENTER);
	private final LoadingWheel loadingWheel = new LoadingWheel(this.logoIcon.getIconWidth()); // in the logo's place while logging in
	private final Timer loadingWheelTurns = this.loadingWheel.animate(this.logo);
	private final JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0)); // over the login choices: the logo and a welcome
	private int fittedWidth = -1; // the window's width as last fitted here: a different one, its user gave it
	private Boolean arrangedForChoosing = null; // how the header is laid out: for the login choices, or after them
	private Timer cautionPulse = null;

	public WelcomePanel(WelcomeWindowManager manager) {
		super(new BorderLayout(0, 14));
		this.manager = manager;
		this.databaseModels.setOpener(manager::openModel);
		this.databaseModels.setMenuMaker(manager::createModelMenu);
		this.bioModelsNetPanel.setDocumentWindowManager(manager);
		this.setName("WelcomePanel");
		this.logo.setName("WelcomeLogo");
		this.logo.getAccessibleContext().setAccessibleName("VCell");
		this.setBorder(BorderFactory.createEmptyBorder(18, 22, 16, 22));
		this.startSection = this.createNewModels();
		this.versionMismatchBelow.setOpaque(false);
		this.versionMismatchBelow.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		this.startSection.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
		JPanel belowHeader = new JPanel(new BorderLayout());
		belowHeader.setOpaque(false);
		belowHeader.add(this.versionMismatchBelow, BorderLayout.NORTH);
		belowHeader.add(this.startSection, BorderLayout.CENTER);
		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(false);
		top.add(this.createHeader(), BorderLayout.NORTH);
		top.add(belowHeader, BorderLayout.CENTER);
		this.headerSection = top;
		this.headerArea.setOpaque(false);
		this.headerArea.add(top, BorderLayout.CENTER);
		this.add(this.headerArea, BorderLayout.NORTH);
		this.add(this.createModelTabs(), BorderLayout.CENTER);
		this.add(this.createFooter(), BorderLayout.SOUTH);
		this.recentModels.setOpener(manager::openModel);
		this.recentModels.setMenuMaker(manager::createModelMenu);
		this.updateConnectionStatus(null, null);
	}

	/**
	 * Once the user has picked how to log in, as wide as when logged in even while the model tabs are hidden (using VCell
	 * offline), so the window keeps one width whichever was picked.
	 */
	@Override
	public Dimension getPreferredSize() {
		Dimension size = super.getPreferredSize();
		if (null == this.startSection || !this.startSection.isVisible() || this.modelsArea.isVisible()) return size;
		Insets insets = this.getInsets();
		size.width = Math.max(size.width, this.modelTabs.getPreferredSize().width + insets.left + insets.right);
		return size;
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
		if (ConnectionStatus.CONNECTED == state) this.guest = User.isGuest(status.getUserName()); // and while reconnecting
		if (this.loggedOut) this.guest = false;
		this.accountButton.setText(this.loggedOut || this.guest ? "Go Back" : "Log Out");
		this.accountButton.setEnabled(ConnectionStatus.INITIALIZING != state);
		this.showLoading(ConnectionStatus.INITIALIZING == state);
		this.signUpButton.setVisible(this.guest && !this.loggedOut);
		this.signUpGap.setVisible(this.signUpButton.isVisible());
		this.showAccountCard(this.loggedOut && !this.loginOptionsDismissed ? LOGIN_OPTIONS : ACCOUNT_STATUS);
		DocumentManager current = ConnectionStatus.CONNECTED == state ? documentManager : reconnecting ? this.documentManager : null;
		if (current != this.documentManager) { // status updates come often; only a new login changes the lists
			this.documentManager = current;
			this.recentModels.setDocumentManager(current);
			this.databaseModels.setDocumentManager(current);
			if (null != current) this.chooseModelTabs();
		}
		if (this.modelsArea.isVisible() != (null != current)) {
			if (null == current) this.showSearchOptions(false); // logged out: the header, with the ways to log in
			this.modelsArea.setVisible(null != current);
			this.packWindow();
		}
	}

	// While logging in (or reconnecting), the loading wheel turns in the logo's place
	private void showLoading(boolean loading) {
		if (loading == (this.logo.getIcon() == this.loadingWheel)) return;
		this.logo.setIcon(loading ? this.loadingWheel : this.logoIcon);
		this.logo.getAccessibleContext().setAccessibleName(loading ? "Logging in" : "VCell");
		if (loading) this.loadingWheelTurns.start();
		else this.loadingWheelTurns.stop();
	}

	// On logging in: Recent Models, but not for a guest (guests can't save models); and the tab to start at, the
	// database's models if there are no recent ones (a guest, or a new user)
	private void chooseModelTabs() {
		int recentIndex = this.modelTabs.indexOfComponent(this.recentModels);
		if (this.guest && recentIndex >= 0) this.modelTabs.removeTabAt(recentIndex);
		if (!this.guest && recentIndex < 0) this.modelTabs.insertTab("Recent Models", null, this.recentModels, null, 0);
		this.modelTabs.setSelectedComponent(this.guest || !this.recentModels.hasModels() ? this.databaseModels : this.recentModels);
		this.searchField.setText(""); // a new login starts with every model
		this.searchOptions.reset();
		this.showSearchOptions(false);
		this.search();
	}

	/**
	 * Fits the window to what it shows, centered on the screen as it is once logged in: logging in grows it downward,
	 * into the middle of the screen, rather than past the bottom.
	 */
	public void packAndCenter() {
		Window window = SwingUtilities.getWindowAncestor(this);
		boolean tabsShown = this.modelsArea.isVisible(), chosenShown = this.startSection.isVisible();
		String card = this.accountCard;
		// laid out as when logged in
		this.modelsArea.setVisible(true);
		this.startSection.setVisible(true);
		this.keepOpen.setVisible(true);
		this.accountCards.show(this.account, ACCOUNT_STATUS);
		this.accountCard = ACCOUNT_STATUS;
		this.arrangeForAccountCard();
		window.pack();
		WelcomePanel.fitToScreen(window);
		Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds(); // below the menu bar
		window.setLocation(screen.x + (screen.width - window.getWidth()) / 2, screen.y + (screen.height - window.getHeight()) / 2);
		this.modelsArea.setVisible(tabsShown);
		this.startSection.setVisible(chosenShown);
		this.keepOpen.setVisible(chosenShown);
		this.accountCards.show(this.account, card);
		this.accountCard = card;
		this.arrangeForAccountCard();
		window.pack();
		WelcomePanel.fitToScreen(window);
		this.fittedWidth = window.getWidth();
	}

	// Fits the window to what it now shows: the model tabs come and go with the login. Growing, it keeps a width its user
	// gave it, if wider; otherwise it fits snugly. Hidden (logging out from a model window), it is fitted all the same, to
	// come back at its new size.
	private void packWindow() {
		Window window = SwingUtilities.getWindowAncestor(this);
		if (null == window || !window.isDisplayable()) return; // not yet shown: packAndCenter() fits it then
		int width = window.getWidth(), height = window.getHeight();
		boolean resizedByUser = width != this.fittedWidth;
		window.pack();
		if (resizedByUser && window.getHeight() > height) window.setSize(Math.max(width, window.getWidth()), window.getHeight());
		WelcomePanel.fitToScreen(window);
		this.fittedWidth = window.getWidth();
	}

	// No taller than the screen, and moved up if it now runs past the bottom (the model tabs make do with less room)
	private static void fitToScreen(Window window) {
		Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		if (window.getHeight() > screen.height) window.setSize(window.getWidth(), screen.height);
		int overBottom = window.getY() + window.getHeight() - (screen.y + screen.height);
		if (overBottom > 0) window.setLocation(window.getX(), Math.max(screen.y, window.getY() - overBottom));
	}

	// The logo; beside it, the account (and below them both, any version mismatch)
	private JComponent createHeader() {
		this.besideLogo.setOpaque(false);
		this.accountSection = this.createAccount();
		this.besideLogo.add(this.accountSection, WelcomePanel.accountBesideLogo(true));

		this.versionNotice.setName("WelcomeVersionNotice");
		this.versionNotice.setIconTextGap(6);
		VCellFonts.setCategory(this.versionNotice, VCellFonts.Category.HEADING);
		this.versionMismatchPanel.setOpaque(false);
		this.versionMismatchPanel.add(this.versionNotice);
		this.versionMismatchPanel.add(this.versionHelp);
		this.versionMismatchPanel.setVisible(false);

		JLabel welcome = new JLabel("Welcome to Virtual Cell");
		VCellFonts.setCategory(welcome, VCellFonts.Category.TITLE);
		this.titleRow.setOpaque(false);
		this.titleRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		this.titleRow.add(welcome);

		this.header.setOpaque(false);
		this.header.add(this.logo, BorderLayout.WEST); // until arranged for the login choices
		this.header.add(this.besideLogo, BorderLayout.CENTER);
		return this.header;
	}

	/**
	 * Shows, beside the account, that this VCell doesn't match the server's (its caution sign pulsing for a few
	 * seconds, then steady, with a "?" that explains), or nothing when it does.
	 */
	public void showVersionMismatch(VersionMismatch mismatch) {
		if (null != this.cautionPulse) this.cautionPulse.stop();
		this.caution.alpha = 1f;
		this.versionMismatchPanel.setVisible(null != mismatch);
		this.arrangeForAccountCard();
		if (null == mismatch) return;
		String server = mismatch.server().getVersionNumber() + " (build " + mismatch.server().getBuildNumber() + ")";
		String client = mismatch.client().getVersionNumber() + " (build " + mismatch.client().getBuildNumber() + ")";
		this.versionHelp.setHelp("<html>The latest VCell version is " + server + "; this is " + client + ".<br>"
				+ "Until you upgrade, some features will be unavailable.</html>");
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
		// side by side beside the logo; a version mismatch goes below them, rather than beside
		JPanel options = new JPanel(new GridLayout(1, 3, 6, 0)); // each as wide as the widest, centered under the welcome
		options.setOpaque(false);
		options.add(this.logInButton);
		options.add(this.guestButton);
		options.add(this.notNowButton);

		this.accountStatus.setName("WelcomeAccountStatus");
		this.accountButton.setName("WelcomeAccountButton");
		this.accountButton.addActionListener(e -> {
			if (!this.loggedOut) {
				this.manager.logOut(); // a guest's Go Back too: ending the guest session brings back the login choices
			} else { // offline: bring back the login choices
				this.loginOptionsDismissed = false;
				this.showAccountCard(LOGIN_OPTIONS);
			}
		});
		this.signUpButton.setName("WelcomeSignUp");
		this.signUpButton.addActionListener(e -> this.manager.signUp());
		this.signUpButton.setVisible(false);
		this.signUpGap.setVisible(false);
		// who is logged in (with what logging in does, on a help icon beside it), and the buttons below, their own
		// widths, all at the right
		this.accountHelp.setHelp("<html>You stay logged in, even when VCell restarts, until you log out.</html>");
		this.statusRow.setOpaque(false);
		this.statusRow.add(this.accountStatus);
		JPanel accountButtonHolder = new JPanel();
		accountButtonHolder.setLayout(new BoxLayout(accountButtonHolder, BoxLayout.X_AXIS));
		accountButtonHolder.setOpaque(false);
		accountButtonHolder.add(Box.createHorizontalGlue());
		accountButtonHolder.add(this.accountButton);
		accountButtonHolder.add(this.signUpGap);
		accountButtonHolder.add(this.signUpButton);
		JPanel status = new JPanel(new BorderLayout(0, 4));
		status.setOpaque(false);
		status.add(this.statusRow, BorderLayout.NORTH);
		status.add(accountButtonHolder, BorderLayout.SOUTH);

		this.account.setName("WelcomeAccount");
		this.account.setOpaque(false);
		JPanel centeredOptions = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		centeredOptions.setOpaque(false);
		centeredOptions.add(options);
		this.account.add(centeredOptions, LOGIN_OPTIONS);
		this.account.add(status, ACCOUNT_STATUS);
		this.headingRow.setOpaque(false);
		this.headingRow.add(WelcomePanel.heading("Please select a login option"));
		JPanel section = new JPanel(new BorderLayout(0, 6));
		section.setOpaque(false);
		section.add(this.headingRow, BorderLayout.NORTH);
		section.add(this.account, BorderLayout.CENTER);
		return section;
	}

	/**
	 * A small "?" that shows its help (HTML) as its tooltip, and in a popup under it when clicked; clicking it again, or
	 * anywhere else, closes the popup.
	 */
	private static final class HelpButton extends JButton {
		private final JLabel text = new JLabel();
		private final JPopupMenu popup = new JPopupMenu();
		private long hiddenAt = 0; // when the popup last closed

		HelpButton(String name, String accessibleName) {
			super(new HelpIcon());
			this.setName(name);
			this.getAccessibleContext().setAccessibleName(accessibleName);
			this.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
			this.setContentAreaFilled(false);
			this.setFocusPainted(false);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			this.text.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
			this.popup.setName(name + "Popup");
			this.popup.add(this.text);
			this.popup.addPopupMenuListener(new PopupMenuListener() {
				@Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
				@Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { HelpButton.this.hiddenAt = System.currentTimeMillis(); }
				@Override public void popupMenuCanceled(PopupMenuEvent e) {}
			});
			this.addActionListener(e -> { // a click shows it too, for those who don't wait for the tooltip
				// the press of a click on the button while it shows closes it; the click is done then, not to open it again
				if (System.currentTimeMillis() - this.hiddenAt < 300) return;
				this.popup.show(this, 0, this.getHeight());
			});
		}

		void setHelp(String html) {
			this.text.setText(html);
			this.setToolTipText(html);
		}
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

	// The login choices, or who is logged in; the new model buttons and whether the window stays open only once the
	// choices are put away
	private void showAccountCard(String card) {
		this.accountCard = card;
		this.accountCards.show(this.account, card);
		this.account.revalidate(); // its height is the card's
		boolean chosen = !LOGIN_OPTIONS.equals(card); // logged in, or chose not to
		boolean changed = this.arrangeForAccountCard();
		if (this.startSection.isVisible() != chosen) {
			this.startSection.setVisible(chosen);
			this.keepOpen.setVisible(chosen);
			changed = true;
		}
		if (changed) this.packWindow();
	}

	// Before the login choice: the logo and a welcome centered on their own line, a prompt to pick a way to log in and the
	// choices centered under them, and a version mismatch centered below those. After it: the logo at the top left, who is
	// logged in at the top right, a mismatch between them, and the new model buttons in a row below. Says whether anything
	// moved.
	private boolean arrangeForAccountCard() {
		boolean choosing = LOGIN_OPTIONS.equals(this.accountCard);
		this.versionMismatchBelow.setVisible(choosing && this.versionMismatchPanel.isVisible());
		this.suggestNextStep();
		if (Boolean.valueOf(choosing).equals(this.arrangedForChoosing)) return false;
		this.arrangedForChoosing = choosing;
		if (choosing) this.versionMismatchBelow.add(this.versionMismatchPanel);
		else this.besideLogo.add(this.versionMismatchPanel, WelcomePanel.mismatchBesideLogo());
		if (choosing) {
			this.titleRow.add(this.logo, 0);
			this.header.add(this.titleRow, BorderLayout.NORTH);
		} else {
			this.header.remove(this.titleRow);
			this.header.add(this.logo, BorderLayout.WEST);
		}
		this.headingRow.setVisible(choosing);
		(choosing ? this.headingRow : this.statusRow).add(this.accountHelp); // beside the prompt, or who is logged in
		// choosing, the account takes the whole width, the choices centered in it; after, its own width, at the right
		((GridBagLayout) this.besideLogo.getLayout()).setConstraints(this.accountSection, WelcomePanel.accountBesideLogo(choosing));
		this.versionMismatchLayout.setAlignment(choosing ? FlowLayout.CENTER : FlowLayout.LEADING);
		this.revalidate();
		return true;
	}

	// The button the user is most likely after, as the window's default (drawn by the theme as the one to press, and
	// pressed by Enter): logging in or signing up, while choosing; signing up, for a guest; none, otherwise
	private void suggestNextStep() {
		JRootPane rootPane = SwingUtilities.getRootPane(this);
		if (null == rootPane) return; // not in its window yet: addNotify() sets it then
		JButton suggested = LOGIN_OPTIONS.equals(this.accountCard) ? this.logInButton : this.signUpButton.isVisible() ? this.signUpButton : null;
		if (rootPane.getDefaultButton() != suggested) rootPane.setDefaultButton(suggested);
	}

	@Override
	public void addNotify() {
		super.addNotify();
		this.suggestNextStep();
	}

	// The account beside the logo, its top level with the logo's: the whole width, while choosing; otherwise its own,
	// at the right
	private static GridBagConstraints accountBesideLogo(boolean wholeWidth) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.anchor = wholeWidth ? GridBagConstraints.FIRST_LINE_START : GridBagConstraints.FIRST_LINE_END;
		constraints.fill = wholeWidth ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
		// after, the room a version mismatch leaves (it takes nearly all of it, to be centered in it); all of it without one,
		// to stay at the right (a grid with no weight centers what it holds)
		constraints.weightx = wholeWidth ? 1 : 0.001;
		return constraints;
	}

	// A version mismatch, after the login choice: centered in the room between the logo and the account, its top level
	// with theirs (so Tab reaches its "?" before the account's, reading the row left to right)
	private static GridBagConstraints mismatchBesideLogo() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.weightx = 1;
		constraints.insets = new Insets(0, 8, 0, 8);
		return constraints;
	}

	// New BioModel, New MathModel with a menu of its kinds (as File > New > MathModel has), and Import from File, side by
	// side
	private JComponent createNewModels() {
		// short labels, under the section's heading, to keep the row narrow; the tooltips say the rest
		JButton newBioModel = new JButton("BioModel", VCellIcons.makeIcon("/icons/biomodel.png"));
		newBioModel.setName("Welcome" + NewModel.BIOMODEL.name());
		newBioModel.setToolTipText(NewModel.BIOMODEL.label);
		newBioModel.addActionListener(e -> this.manager.newModel(NewModel.BIOMODEL));

		JPopupMenu mathModels = new JPopupMenu();
		for (NewModel kind : NewModel.values()) {
			if (VCDocumentType.MATHMODEL_DOC != kind.documentType) continue;
			JMenuItem item = new JMenuItem(kind.label);
			item.setName("Welcome" + kind.name());
			item.addActionListener(e -> this.manager.newModel(kind));
			mathModels.add(item);
		}
		JButton newMathModel = new JButton("MathModel \u25BE", VCellIcons.makeIcon("/icons/mathModel.png"));
		newMathModel.setName("WelcomeNewMathModel");
		newMathModel.setToolTipText("New MathModel: choose its kind");
		newMathModel.addActionListener(e -> mathModels.show(newMathModel, 0, newMathModel.getHeight()));

		JButton importModel = new JButton("Import File", VCellIcons.makeIcon("/icons/File.gif"));
		importModel.setName("WelcomeImport");
		importModel.setToolTipText("""
				<html>Opens a model from a file: VCML (.vcml), SBML (.sbml, .xml), COMBINE archive (.omex),<br>
				SED-ML (.sedml), BioNetGen (.bngl), Virtual FRAP (.vfrap) or SpringSaLaD (.ssld)</html>""");
		importModel.addActionListener(e -> this.manager.importModel());

		JPanel buttons = new JPanel(new GridLayout(1, 0, 6, 0)); // side by side, each as wide as the widest
		buttons.setOpaque(false);
		buttons.add(newBioModel);
		buttons.add(newMathModel);
		buttons.add(importModel);
		JPanel row = new JPanel(new BorderLayout());
		row.setOpaque(false);
		row.add(WelcomePanel.section("Start a new Model", buttons), BorderLayout.CENTER);
		return row;
	}

	// The models the user saved recently, the VCell database's (in tabs as its tree's folders sort them), and the
	// BioModels Database (EBI's) as a BioModel window's "BMDB" tab has it; takes whatever height is left
	private JComponent createModelTabs() {
		this.recentModels.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		this.modelTabs.setName("WelcomeModelTabs");
		this.modelTabs.addTab("Recent Models", this.recentModels);
		this.modelTabs.addTab("VCell Database", this.databaseModels);
		this.modelTabs.addTab("BioModels Database", this.bioModelsNetPanel);
		this.modelTabs.setPreferredSize(new Dimension(420, 380));

		// one search for every tab, rather than one in each; typing searches once it pauses, Escape clears it
		this.bioModelsNetPanel.hideSearch();
		this.searchField.setName("WelcomeSearch");
		this.searchField.setToolTipText("Finds models by name, owner or description, best matches first; Filters narrows the search");
		this.searchField.putClientProperty("JTextField.variant", "search");
		this.searchPause.setRepeats(false);
		this.searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			@Override public void insertUpdate(javax.swing.event.DocumentEvent e) { WelcomePanel.this.searchPause.restart(); }
			@Override public void removeUpdate(javax.swing.event.DocumentEvent e) { WelcomePanel.this.searchPause.restart(); }
			@Override public void changedUpdate(javax.swing.event.DocumentEvent e) { WelcomePanel.this.searchPause.restart(); }
		});
		this.searchField.getInputMap().put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "clearSearch");
		this.searchField.getActionMap().put("clearSearch", new AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				WelcomePanel.this.searchField.setText("");
			}
		});

		// Filters opens the search options upward, over the header: someone searching is past logging in or starting a
		// model, and the lists keep their room
		this.filtersButton.setName("WelcomeFilters");
		this.filtersButton.setToolTipText("More ways to search: where to look, which models, and their order");
		this.filtersButton.addActionListener(e -> this.showSearchOptions(this.filtersButton.isSelected()));
		this.showFiltersChanged(SearchOptions.defaults());
		JPanel searchRow = new JPanel(new BorderLayout(6, 0));
		searchRow.setOpaque(false);
		searchRow.add(this.searchField, BorderLayout.CENTER);
		searchRow.add(this.filtersButton, BorderLayout.EAST);

		this.modelsArea.setOpaque(false);
		this.modelsArea.add(WelcomePanel.section("Open existing Model", searchRow), BorderLayout.NORTH); // headed as Start is
		this.modelsArea.add(this.modelTabs, BorderLayout.CENTER);
		this.modelsArea.setVisible(false); // until someone logs in
		return this.modelsArea;
	}

	// Each tab shows only the models the search (and its options) matches
	private void search() {
		this.searchPause.stop();
		String text = this.searchField.getText();
		SearchOptions options = this.searchOptions.getOptions();
		this.recentModels.setSearch(text, options);
		this.databaseModels.setSearch(text, options);
		this.bioModelsNetPanel.setSearchText(text);
		this.showFiltersChanged(options);
	}

	// The search options in the header's place, or the header back. They're taller than the header: the window grows by
	// the difference at its top, so the options open upward and the lists keep their room; it shrinks back after.
	private void showSearchOptions(boolean shown) {
		if (shown == (this.searchOptions.getParent() == this.headerArea)) return;
		this.filtersButton.setSelected(shown);
		Window window = SwingUtilities.getWindowAncestor(this);
		int extra = shown ? Math.max(0, this.searchOptions.getPreferredSize().height - this.headerSection.getHeight()) : 0;
		this.headerArea.removeAll();
		this.headerArea.add(shown ? this.searchOptions : this.headerSection, BorderLayout.CENTER);
		this.showFiltersChanged(this.searchOptions.getOptions());
		if (null != window && window.isShowing() && shown && 0 != extra) {
			Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
			int top = Math.max(screen.y, window.getY() - extra); // no room above: it grows down instead
			this.optionsRise = window.getY() - top;
			int heightBefore = window.getHeight();
			window.setBounds(window.getX(), top, window.getWidth(), window.getHeight() + extra);
			WelcomePanel.fitToScreen(window);
			this.optionsGrowth = window.getHeight() - heightBefore;
		} else if (null != window && window.isShowing() && !shown && 0 != this.optionsGrowth) { // undo just that
			window.setBounds(window.getX(), window.getY() + this.optionsRise, window.getWidth(), window.getHeight() - this.optionsGrowth);
			this.optionsGrowth = 0;
			this.optionsRise = 0;
		}
		this.headerArea.revalidate();
		this.headerArea.repaint();
	}

	// The Filters button says how many options narrow the search, so a narrowed search is never invisible; and which
	// way it opens (the options open upward)
	private void showFiltersChanged(SearchOptions options) {
		int changed = options.changedCount();
		this.filtersButton.setText("Filters" + (changed > 0 ? " (" + changed + ")" : "") + (this.filtersButton.isSelected() ? " \u25BE" : " \u25B4"));
	}

	/** A text field that says what it searches while it's empty. */
	static final class SearchField extends JTextField { // also the model picker's
		private final String hint;

		SearchField(String hint) {
			this.hint = hint;
			this.getAccessibleContext().setAccessibleName(hint);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (!this.getText().isEmpty()) return;
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setColor(VCellColors.getDimForeground());
			g2.setFont(this.getFont());
			Insets insets = this.getInsets();
			FontMetrics metrics = g2.getFontMetrics();
			int baseline = insets.top + (this.getHeight() - insets.top - insets.bottom - metrics.getHeight()) / 2 + metrics.getAscent();
			g2.drawString(this.hint, insets.left + 2, baseline);
			g2.dispose();
		}
	}

	// Whether the window stays open after a model opens (once a way to log in is picked), and, always, under it, VCell's help
	private JComponent createFooter() {
		this.keepOpen.setName("WelcomeKeepOpen");
		this.keepOpen.setSelected(WelcomeWindowManager.isKeptOpen());
		this.keepOpen.addActionListener(e -> WelcomeWindowManager.setKeptOpen(this.keepOpen.isSelected()));

		JButton learn = new JButton("Learn VCell");
		learn.setName("WelcomeLearn");
		learn.setToolTipText("Opens VCell's help, as Help > VCell Help does");
		learn.addActionListener(e -> VcellHelpViewer.showStandaloneViewer()); // as Help > VCell Help does

		// the help on a line of its own, at the right, under the checkbox: beside it, the two widen the window
		JPanel learnRow = new JPanel(new BorderLayout());
		learnRow.setOpaque(false);
		learnRow.add(learn, BorderLayout.EAST);
		JPanel footer = new JPanel(new BorderLayout(0, 6));
		footer.setOpaque(false);
		footer.add(this.keepOpen, BorderLayout.NORTH);
		footer.add(learnRow, BorderLayout.SOUTH);
		return footer;
	}

	private static JComponent section(String title, JComponent content) {
		JPanel section = new JPanel(new BorderLayout(0, 6));
		section.setOpaque(false);
		section.add(WelcomePanel.heading(title), BorderLayout.NORTH);
		section.add(content, BorderLayout.CENTER);
		return section;
	}

	private static JLabel heading(String text) {
		return GuiUtils.createBoldJLabel(text);
	}
}
