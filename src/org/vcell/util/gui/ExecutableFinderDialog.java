package org.vcell.util.gui;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import org.vcell.util.UserCancelException;

import cbit.vcell.resource.ResourceUtil.ExecutableFinder;

public class ExecutableFinderDialog implements ExecutableFinder {
	public static final String FIND = "Find...";
	final String userMessage;
	final Component parent;
	
	/**
	 * @param parent graphical component for dialog, if required. May not be null
	 * @param userMessage Message to display to user with file dialog 
	 * @throws IllegalArgumentException if parent null
	 */
	public ExecutableFinderDialog(Component parent,String userMessage) {
		this.parent = parent;
		this.userMessage = userMessage;
		if (parent == null) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public File find(String executableName) {
		String retcode = DialogUtils.showWarningDialog(parent, executableName + " not found",userMessage, new String[] {FIND,"Cancel"}, FIND);
		if (retcode != null && !retcode.equals(FIND)){
			throw UserCancelException.CANCEL_GENERIC;
		}
		//
		// ask user for file location
		//
		VCFileChooser fileChooser = new ExeChooser(new File(System.getProperty("user.home")));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setDialogTitle("Choose " + executableName + "  executable file");
		if (fileChooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
			// user didn't choose save
			throw UserCancelException.CANCEL_FILE_SELECTION;
		} else {
			File selectedFile = fileChooser.getSelectedFile();
			if (selectedFile.canExecute()) {
				return selectedFile;
			}
		}
		return null;
	}
	
	@SuppressWarnings("serial")
	private static class ExeChooser extends VCFileChooser {

		ExeChooser(File currentDirectory) {
			super(currentDirectory);
		}

		@Override
		public boolean accept(File f) {
			return f.canExecute(); 
		}
		
	}

}
