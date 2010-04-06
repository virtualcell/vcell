package org.vcell.sybil.init;

/*   SybilApplication  --- by Oliver Ruebenacker, UCHC --- April 2007 to March 2010
 *   A standalone application
 */

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import org.vcell.sybil.gui.GUIManager;
import org.vcell.sybil.gui.space.GUIFrameSpace;
import org.vcell.sybil.gui.space.GUIJFrameSpace;
import cbit.vcell.biomodel.BioModel;

public class SybilApplication implements Runnable {

	protected GUIFrameSpace frameSpace;
	protected MainGraphInit initGraph;
	protected MainInit init;

	public SybilApplication(GUIFrameSpace frameSpaceNew, BioModel bioModel) { 
		this(bioModel, frameSpaceNew);
		if(bioModel != null) { 
			init.coreManager().fileManager().setBox(bioModel.getVCMetaData().getSBbox()); 
		} 
	}

	public SybilApplication() { 
		this(null, new GUIJFrameSpace(new JFrame())); 
	}
	
	public SybilApplication(BioModel bioModel, GUIFrameSpace frameSpaceNew) { 
		frameSpace = frameSpaceNew;
		initGraph = new MainGraphInit();
		init = new MainInit(bioModel, initGraph);
	}
	
	public void run() {
		GUIManager gui = new GUIManager(frameSpace, init.coreManager(), init.actionMap());
		JMenuBar menuBar = gui.createMenuBar();
		gui.frameSpace().setMenuBar(menuBar);
		initGraph.setUI(gui);
 		init.setUI(gui);
		System.out.println("Starting GUI");
		SwingUtilities.invokeLater(gui);      				
	}
	
	public void runVCell() {
		GUIManager gui = new GUIManager(frameSpace, init.coreManager(), init.actionMap());
		JMenuBar menuBar = gui.createMenuBar();
		gui.frameSpace().setMenuBar(menuBar);
		initGraph.setUI(gui);
 		init.setUI(gui);
		System.out.println("Starting Sybil GUI");
		gui.run();      				
	}
	
}