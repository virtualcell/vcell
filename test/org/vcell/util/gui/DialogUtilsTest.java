package org.vcell.util.gui;

import javax.swing.JFrame;

public class DialogUtilsTest {
	public static final String IPSUM_LOREM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

	public static void main(String[] args) {
		JFrame parent = new JFrame("Message parent");
		DialogUtils.showOKCancelWarningDialog(parent, "Test" ,IPSUM_LOREM);
		DialogUtils.showOKCancelWarningDialog(parent, "Long list" ,longList());
		parent.dispose();
	}
	
	private static String longList( ) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0 ; i < 60 ; ++i) {
			sb.append(i);
			sb.append("\n");
		}
		return sb.toString();
	}

}
