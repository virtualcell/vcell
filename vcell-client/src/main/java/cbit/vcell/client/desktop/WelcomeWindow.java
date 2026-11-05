package cbit.vcell.client.desktop;

import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.client.WelcomeWindowManager;
import cbit.vcell.client.WelcomeWindowManager.NewModel;
import cbit.vcell.client.laf.AppearanceMenus;
import org.vcell.api.server.ConnectionStatus;
import org.vcell.client.logicalwindow.LWTopFrame;
import org.vcell.util.gui.VCellIcons;

import javax.swing.*;

/** The welcome window's frame: the welcome panel, a File menu for new models, and the Window menu. */
@SuppressWarnings("serial")
public class WelcomeWindow extends LWTopFrame implements TopLevelWindow {
	private final WelcomeWindowManager manager;
	private final ChildWindowManager childWindowManager;

	public WelcomeWindow(WelcomeWindowManager manager) {
		super();
		this.manager = manager;
		this.setName("WelcomeWindow");
		this.setTitle("Welcome to Virtual Cell \u2014 Version " + DocumentWindowAboutBox.getVERSION_NO() + " (build " + DocumentWindowAboutBox.getBUILD_NO() + ")");
		this.setIconImage(VCellIcons.getJFrameImageIcon());
		// closing goes to the request manager, which hides it, or quits VCell if no model window is open
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.getContentPane().add(manager.getComponent());
		this.setJMenuBar(this.createMenuBar());
		this.childWindowManager = new ChildWindowManager(this);
	}

	private JMenuBar createMenuBar() {
		JMenu file = new JMenu("File");
		file.setName("WelcomeFileMenu");
		JMenu newMathModel = new JMenu("New MathModel");
		for (NewModel kind : NewModel.values()) {
			JMenuItem item = new JMenuItem(kind.label);
			item.addActionListener(e -> this.manager.newModel(kind));
			if (NewModel.BIOMODEL == kind) file.add(item);
			else newMathModel.add(item);
		}
		file.add(newMathModel);
		file.addSeparator();
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(e -> this.manager.getRequestManager().exitApplication());
		file.add(exit);
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(file);
		menuBar.add(createWindowMenu(true, AppearanceMenus.create(this))); // the theme and font, as a model window has
		return menuBar;
	}

	@Override
	public String menuDescription() {
		return "Welcome";
	}

	@Override
	public TopLevelWindowManager getTopLevelWindowManager() {
		return this.manager;
	}

	@Override
	public void updateConnectionStatus(ConnectionStatus connectionStatus) {
		this.manager.updateConnectionStatus(connectionStatus);
	}

	@Override
	public void updateMemoryStatus(long freeBytes, long totalBytes) {}

	@Override
	public void updateWhileInitializing(int i) {}

	@Override
	public ChildWindowManager getChildWindowManager() {
		return this.childWindowManager;
	}
}
