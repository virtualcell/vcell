package cbit.vcell.microscopy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.print.attribute.standard.MediaName;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import cbit.gui.ZEnforcer;

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

  /** Construct the player object and the GUI. */
  public JMFPlayer(JFrame pf, String media) {
	super();
    parentFrame = pf;
	mediaName = media;
    
    cp = this;
    cp.setLayout(new BorderLayout());
    cp.setSize(300,520);
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
    System.out.println("theURL = " + theURL);

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

  public static void showMovieInFrame(String fileStr)
  {
	  JFrame frame = new JFrame("VFRAP Movie");
	  frame.setLayout(new BorderLayout());
	  //add info. panel
	  JPanel infoPanel = new JPanel(new GridLayout(0,1));
	  File movieFile = new File(fileStr);
	  infoPanel.add(new Label("File saved to: " + ((movieFile==null)?"":movieFile.getParent())));
	  infoPanel.add(new Label("File name: " + ((movieFile==null)?"":movieFile.getName())));
	  infoPanel.add(new Label("Top: Experimental Data.  Value range [0.01,1.1]"));
	  infoPanel.add(new Label("Bottom: Simulation Data.  Value range [0.01,1.1]"));
	  infoPanel.setBackground(new Color(184,184,190));
	  frame.getContentPane().add(infoPanel,BorderLayout.NORTH);
	  //add movie player in the center
	  JMFPlayer jp = new JMFPlayer(frame, fileStr);
	  frame.getContentPane().add(jp,BorderLayout.CENTER);
	  frame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width-200)/2,
		    			(Toolkit.getDefaultToolkit().getScreenSize().height-220)/2);
	  frame.setVisible(true);
	  frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//	  frame.setSize(300, 350);
//	  frame.pack();
  }
  
  public static void main(String[] argv) {
    JFrame f = new JFrame("JMF Player Test");
    Container frameCP = f.getContentPane();
    JMFPlayer p = new JMFPlayer(f,
        argv.length == 0 ? "file:///C:/VirtualMicroscopy/test.mov"
            : argv[0]);
    frameCP.add(BorderLayout.CENTER, p);
    f.setSize(200, 200);
    f.setVisible(true);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}