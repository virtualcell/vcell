package cbit.vcell.client.desktop;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

import org.vcell.documentation.HelpContext;
import org.vcell.documentation.HelpIds;

import org.vcell.client.logicalwindow.LWTopFrame;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.client.ModelExplorerWindowManager;
import cbit.vcell.client.laf.AppearanceMenus;

/**
 * The Model Explorer's frame. One of VCell's windows, so the dialogs it opens (comparing models, permissions, deleting)
 * have it as their parent and it's listed in the Window menu; but not a model window: it isn't counted among the open
 * ones (closing the last model window still brings back the welcome window), and closing it only hides it.
 */
@SuppressWarnings("serial")
public class ModelExplorerWindow extends LWTopFrame {
	private final ModelExplorerWindowManager manager;

	public ModelExplorerWindow(ModelExplorerWindowManager manager) {
		this.manager = manager;
		this.setTitle("Model Explorer");
		this.setName("ModelExplorerWindow");
		this.setIconImage(VCellIcons.getJFrameImageIcon());
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.getContentPane().add(manager.getComponent());
		// a menu bar of its own (else macOS shows an empty one): the windows, and VCell Help, which opens at the Model
		// Explorer's page
		HelpContext.setPage((JComponent) this.getContentPane(), HelpIds.MODEL_EXPLORER);
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(LWTopFrame.createWindowMenu(true, AppearanceMenus.create(this)));
		menuBar.add(HelpContext.createHelpMenu());
		this.setJMenuBar(menuBar);
		// most of the screen, up to a comfortable size, centered
		Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		Dimension size = new Dimension(Math.min(1200, screen.width * 9 / 10), Math.min(800, screen.height * 9 / 10));
		this.setSize(size);
		this.setLocation(screen.x + (screen.width - size.width) / 2, screen.y + (screen.height - size.height) / 2);
	}

	public ModelExplorerWindowManager getManager() {
		return this.manager;
	}

	@Override
	public String menuDescription() {
		return "Model Explorer";
	}
}
