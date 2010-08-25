package cbit.vcell.microscopy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import org.vcell.util.FileUtils;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ProgressDialogListener;
import org.vcell.util.gui.ZEnforcer;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;

/**
 * To play a movie with Java Media Framework. Used in VFrap to show the
 * QuickTime movie of both exp and sim data.
 */
public class JMFPlayer extends JPanel implements ControllerListener {

	/** The player object */
	Player thePlayer = null;

	JDialog parentFrame = null;

	/** The visual component (if any) */
	Component visualComponent = null;

	/** The default control component (if any) */
	Component controlComponent = null;

	// /** The name of this instance's media file. */
	// String mediaName;

	/** The URL representing this media file. */
	URL theURL;

	JButton saveButton2 = null;

	private JPanel playerPanel;

	/** Construct the player object and the GUI. */
	public JMFPlayer(JDialog pf, String mediaName, String fileStr) {
		super();
		parentFrame = pf;
		// mediaName = media;
		setLayout(new BorderLayout());

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		final File movieFile = new File(fileStr);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		//buttonPanel.setBackground(new Color(184, 184, 190));

		JButton saveButton = new JButton("Save Movie As...");
		//saveButton.setBackground(new Color(184, 184, 190));
		//saveButton.setBorderPainted(false);
		GridBagConstraints buttonConstraints = new GridBagConstraints();
		buttonConstraints.gridy = 0;
		buttonConstraints.gridx = 0;
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				save(movieFile);
			}
		});
	
		buttonPanel.add(saveButton, buttonConstraints);
		infoPanel.add(buttonPanel);

		
		Label expDataRangeLabel = new Label("Top: Experimental Data.  Value range [0.01,1.1]");
		infoPanel.add(expDataRangeLabel);
		
		Label simDataRangeLabel = new Label("Bottom: Simulation Data.  Value range [0.01,1.1]");
		infoPanel.add(simDataRangeLabel);
		
		JSeparator separator = new JSeparator();
		infoPanel.add(separator);
		//infoPanel.setBackground(new Color(184, 184, 190));

		add(infoPanel, BorderLayout.NORTH);

		playerPanel = new JPanel();
		
		playerPanel.setLayout(new BorderLayout());
		try {
			theURL = new URL(getClass().getResource("."), mediaName);
			thePlayer = Manager.createPlayer(theURL);
			thePlayer.addControllerListener(this);
		} catch (MalformedURLException e) {
			System.err.println("JMF URL creation error: " + e);
		} catch (Exception e) {
			System.err.println("JMF Player creation error: " + e);
			return;
		}
		add(playerPanel, BorderLayout.CENTER);
		
		// Start the player: this will notify ControllerListener.
		thePlayer.start(); // start playing
	}

	private void save(final File movieFile){
		int choice = VirtualFrapLoader.saveMovieFileChooser.showSaveDialog(parentFrame);
		File outputFile = null;
		if (choice == JFileChooser.APPROVE_OPTION) {
			String outputFileName = VirtualFrapLoader.saveMovieFileChooser
					.getSelectedFile().getPath();
			outputFile = new File(outputFileName);
			if (!VirtualFrapLoader.filter_qt.accept(outputFile)) {
				if (outputFile.getName().indexOf(".") == -1) {
					outputFile = new File(outputFile.getParentFile(),
							outputFile.getName() + "."
									+ VirtualFrapLoader.QT_EXTENSION);
				} else {
					DialogUtils.showErrorDialog(parentFrame,
							"Quick Time movie must have an extension of ."
									+ VirtualFrapLoader.QT_EXTENSION);
					return;
				}
			}
			// copy saved movie file to user specified path.
			if(outputFile != null)
			{
				final File output = outputFile;
				AsynchClientTask saveTask = new AsynchClientTask("Saving movie to " + output.getAbsolutePath(), AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
	    		{
	    			public void run(Hashtable<String, Object> hashTable) throws Exception
	    			{
	    				FileUtils.copyFile(movieFile, output);
	    				
	    			}
	    		};
	    		ClientTaskDispatcher.dispatch(JMFPlayer.this, new Hashtable<String, Object>(), new AsynchClientTask[]{saveTask},true, false, true,null,true);
			}
		} else {
			throw UserCancelException.CANCEL_GENERIC;
		}
	}
	
	/** Called to stop the audio, as from a Stop button or menuitem */
	public void stop() {
		if (thePlayer == null)
			return;
		thePlayer.stop(); // stop playing!
		thePlayer.deallocate(); // free system resources
	}

	/** Called when we are really finished (as from an Exit button). */
	public void destroy() {
		if (thePlayer == null)
			return;
		thePlayer.close();
	}

	/** Called by JMF when the Player has something to tell us about. */
	public synchronized void controllerUpdate(ControllerEvent event) {
		if (event instanceof RealizeCompleteEvent) {
			if ((visualComponent = thePlayer.getVisualComponent()) != null)
				playerPanel.add(BorderLayout.CENTER, visualComponent);
			if ((controlComponent = thePlayer.getControlPanelComponent()) != null)
				playerPanel.add(BorderLayout.SOUTH, controlComponent);
			// re-size the main window
			if (parentFrame != null) {
				try {

					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							parentFrame.pack();
							parentFrame.toFront();
						}
					});
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				parentFrame.setTitle("VFRAP Movie");
			}
		}
	}

	public static void showMovieInDialog(final Dialog parent,
			final String urlStr, final String fileStr) {
		JDialog dialog = new JDialog(parent, "VFRAP Movie");
		dialog.setModal(true);
		dialog.setLayout(new BorderLayout());
		// add info. panel
		// add movie player in the center
		JMFPlayer jp = new JMFPlayer(dialog, urlStr, fileStr);
		dialog.getContentPane().add(jp, BorderLayout.CENTER);
		dialog.setSize(250,500);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		ZEnforcer.showModalDialogOnTop(dialog, parent);
		// frame.addWindowListener(new WindowAdapter(){
		// public void windowClosing(WindowEvent e)
		// {
		// new File(fileStr).deleteOnExit();
		// }
		// });
	}

	public static void main(String[] argv) {
		// JFrame f = new JFrame("JMF Player Test");
		// Container frameCP = f.getContentPane();
		// JMFPlayer p = new JMFPlayer(f,
		// argv.length == 0 ? "file:///C:/VirtualMicroscopy/test.mov"
		// : argv[0]);
		// // p.setSize(350, 500);
		// frameCP.add(BorderLayout.CENTER, p);
		// // f.setSize(360, 550);
		// f.setVisible(true);
		// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		VirtualFrapLoader.saveMovieFileChooser = new JFileChooser();
		VirtualFrapLoader.saveMovieFileChooser
				.addChoosableFileFilter(VirtualFrapLoader.filter_qt);
		VirtualFrapLoader.saveMovieFileChooser
				.setAcceptAllFileFilterUsed(false);
		// VirtualFrapLoader.saveMovieFileChooser.setCurrentDirectory(new
		// File(localWorkspcae.getDefaultWorkspaceDirectory()));
		JDialog dialog = new JDialog();
		dialog.setTitle("VFRAP Movie");
		dialog.setSize(500,500);
		showMovieInDialog(dialog, "file:///C:/VirtualMicroscopy/test.mov",
				"C:/VirtualMicroscopy/test.mov");
	}
}