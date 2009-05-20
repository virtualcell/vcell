package cbit.vcell.microscopy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.vcell.util.FileUtils;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;


/**
 * To play a movie with Java Media Framework.
 * Used in VFrap to show the QuickTime movie of both exp and sim data.
 */
public class JMFPlayer extends JPanel implements ControllerListener {

  /** The player object */
  Player thePlayer = null;
  
  /** Our contentpane */
  Container cp;

  JFrame parentFrame=null;
  
  /** The visual component (if any) */
  Component visualComponent = null;

  /** The default control component (if any) */
  Component controlComponent = null;

  /** The name of this instance's media file. */
  String mediaName;

  /** The URL representing this media file. */
  URL theURL;

  JButton saveButton2 = null;
  /** Construct the player object and the GUI. */
  public JMFPlayer(JFrame pf, String media) {
	super();
    parentFrame = pf;
	mediaName = media;
    
    cp = this;
    cp.setLayout(new BorderLayout());
//    cp.setSize(350,500);
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
//    System.out.println("theURL = " + theURL);
     
//    saveButton = new JButton(new ImageIcon(getClass().getResource("/images/save.gif")));
//    saveButton.setBackground(new Color(184,184,190));
//    saveButton.setBorder(null);
    // Start the player: this will notify ControllerListener.
    thePlayer.start(); // start playing
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
            cp.add(BorderLayout.CENTER, visualComponent);
        if ((controlComponent = thePlayer.getControlPanelComponent()) != null)
            cp.add(BorderLayout.SOUTH, controlComponent);
//         re-size the main window
        if (parentFrame != null) {
        	try{
				
	        	SwingUtilities.invokeAndWait(new Runnable(){public void run(){
	        		parentFrame.pack();
	        		parentFrame.toFront();
				}});
        	}catch(Exception e2){
				e2.printStackTrace();
			}
            parentFrame.setTitle("VFRAP Movie");
        }
    }
  }

  public static void showMovieInFrame(final String urlStr, final String fileStr)
  {
	  JFrame frame = new JFrame("VFRAP Movie");
	  frame.setLayout(new BorderLayout());
	  //add info. panel
	  JPanel infoPanel = new JPanel(new GridLayout(0,1));
	  final File movieFile = new File(fileStr);
	  
	  
	  JButton saveButton2 = new JButton("  Save  Movie  ");
	  saveButton2.setBackground(new Color(184,184,190));
	  saveButton2.setBorderPainted(false);
	  
	  JPanel panel = new JPanel();
	  panel.setBackground(new Color(184,184,190));
	  panel.add(saveButton2);
	  infoPanel.add(panel);
	  
	  
	  infoPanel.add(new Label("Top: Experimental Data.  Value range [0.01,1.1]"));
	  infoPanel.add(new Label("Bottom: Simulation Data.  Value range [0.01,1.1]"));
	  infoPanel.setBackground(new Color(184,184,190));
	  frame.getContentPane().add(infoPanel,BorderLayout.NORTH);
	  //add movie player in the center
	  JMFPlayer jp = new JMFPlayer(frame, urlStr);
	  frame.getContentPane().add(jp,BorderLayout.CENTER);
	  frame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width-200)/2,
		    			(Toolkit.getDefaultToolkit().getScreenSize().height-220)/2);
	  frame.setVisible(true);
	  frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//	  frame.addWindowListener(new WindowAdapter(){
//		  public void windowClosing(WindowEvent e)
//	      {
//		  	 new File(fileStr).deleteOnExit();
//		  }
//	  });
	  final JFrame frameCopy = frame;
	  saveButton2.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent arg0) {
			int choice = VirtualFrapLoader.saveMovieFileChooser.showSaveDialog(frameCopy);
			File outputFile = null;
			if (choice == JFileChooser.APPROVE_OPTION){
				String outputFileName = VirtualFrapLoader.saveMovieFileChooser.getSelectedFile().getPath();
				outputFile = new File(outputFileName);
				if(!VirtualFrapLoader.filter_qt.accept(outputFile)){
					if(outputFile.getName().indexOf(".") == -1){
						outputFile = new File(outputFile.getParentFile(),outputFile.getName()+"."+VirtualFrapLoader.QT_EXTENSION);
					}else{
						DialogUtils.showErrorDialog("Quick Time movie must have an extension of ."+VirtualFrapLoader.QT_EXTENSION);
						return;
					}
				}
				//copy saved movie file to user specified path.
				try {
					FileUtils.copyFile(movieFile, outputFile);
				} catch (IOException e) {
					DialogUtils.showErrorDialog("Fail to save movie to file: "+ outputFile.getPath()+"."+ e.getMessage());
					return;
				}
			}else{
				throw UserCancelException.CANCEL_GENERIC;
			}			
		}
	  });
  }
  
  public static void main(String[] argv) {
//    JFrame f = new JFrame("JMF Player Test");
//    Container frameCP = f.getContentPane();
//    JMFPlayer p = new JMFPlayer(f,
//        argv.length == 0 ? "file:///C:/VirtualMicroscopy/test.mov"
//            : argv[0]);
////    p.setSize(350, 500);
//    frameCP.add(BorderLayout.CENTER, p);
////    f.setSize(360, 550);
//    f.setVisible(true);
//    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  VirtualFrapLoader.saveMovieFileChooser = new JFileChooser();
	  VirtualFrapLoader.saveMovieFileChooser.addChoosableFileFilter(VirtualFrapLoader.filter_qt);
	  VirtualFrapLoader.saveMovieFileChooser.setAcceptAllFileFilterUsed(false);
//	  VirtualFrapLoader.saveMovieFileChooser.setCurrentDirectory(new File(localWorkspcae.getDefaultWorkspaceDirectory()));
	  showMovieInFrame("file:///C:/VirtualMicroscopy/test.mov", "C:/VirtualMicroscopy/test.mov");
  }
}