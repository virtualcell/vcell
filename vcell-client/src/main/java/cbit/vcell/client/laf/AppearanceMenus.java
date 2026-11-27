package cbit.vcell.client.laf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * The menu items for VCell's look, for a window's menu bar: the theme, the text size, and the font dialog. Each window
 * makes its own (a menu item belongs to one menu bar); the choices apply to all of VCell.
 */
public final class AppearanceMenus {
	private static final Logger lg = LogManager.getLogger(AppearanceMenus.class);

	private AppearanceMenus() {}

	/** The theme menu, the text size menu and the font dialog's item, acting for {@code requester}'s window. */
	public static List<JMenuItem> create(Component requester) {
		return List.of(AppearanceMenus.createThemeMenu(requester), AppearanceMenus.createTextSizeMenu(), AppearanceMenus.createFontMenuItem(requester));
	}

	/** Follow System, which forgets a theme picked so VCell follows the system's light / dark mode, then each theme. */
	private static JMenu createThemeMenu(Component requester) {
		JMenu menu = new JMenu("Set VCell Theme");
		menu.setName("LookAndFeelMenu");
		JMenuItem followSystem = new JMenuItem("Follow System");
		followSystem.setName("FollowSystemTheme");
		followSystem.addActionListener(e -> VCellLookAndFeel.followSystemLookAndFeel(requester));
		menu.add(followSystem);
		menu.addSeparator();
		try {
			UIManager.LookAndFeelInfo[] installed = UIManager.getInstalledLookAndFeels();
			for (UIManager.LookAndFeelInfo info : Arrays.stream(installed).sorted(new VCellLookAndFeel.LookAndFeelComparator()).toList()) {
				JMenuItem item = new JMenuItem(info.getName());
				item.setName(info.getName());
				item.addActionListener(e -> VCellLookAndFeel.changeVCellLookAndFeel(requester, info));
				menu.add(item);
			}
		} catch (Exception e) {
			lg.error("Could not list the installed themes", e);
		}
		return menu;
	}

	/** The size of VCell's text, in the same steps the font dialog offers; marks the current one each time it opens. */
	private static JMenu createTextSizeMenu() {
		JMenu menu = new JMenu("Set Font Size");
		menu.setName("TextSizeMenu");
		ButtonGroup sizes = new ButtonGroup();
		for (float size : VCellFonts.getSizeChoices()) {
			String label = Math.round(size) + " pt" + (size == VCellFonts.DEFAULT_SIZE ? " (Default)" : "");
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(label);
			item.setName("TextSize" + Math.round(size));
			item.addActionListener(e -> VCellFonts.chooseSize(size));
			sizes.add(item);
			menu.add(item);
		}
		menu.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(MenuEvent e) {
				for (Component item : menu.getMenuComponents()) {
					if (item instanceof JRadioButtonMenuItem sizeItem) sizeItem.setSelected(sizeItem.getName().equals("TextSize" + Math.round(VCellFonts.getSize())));
				}
			}
			@Override public void menuDeselected(MenuEvent e) {}
			@Override public void menuCanceled(MenuEvent e) {}
		});
		return menu;
	}

	/** Opens the font dialog: family and size, with a sample. */
	private static JMenuItem createFontMenuItem(Component requester) {
		JMenuItem item = new JMenuItem("Font...");
		item.setName("FontMenuItem");
		item.addActionListener(e -> FontChooser.show(requester));
		return item;
	}
}
