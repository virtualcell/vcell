/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package org.vcell.imagej.plugin;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/*import org.eclipse.swt.widgets.Text; */
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.DialogPrompt.MessageType;
import org.scijava.ui.UIService;
import org.vcell.imagej.helper.VCellHelper;
import org.vcell.imagej.helper.VCellHelper.BasicStackDimensions;
import org.vcell.imagej.helper.VCellHelper.IJDataList;
import org.vcell.imagej.helper.VCellHelper.VCellModelSearchResults;

import net.imagej.ImageJ;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.numeric.real.DoubleType;




/**
 * This example illustrates how to create an ImageJ {@link ContextCommand} plugin that uses VCellHelper.
 * <p>
 * You should replace the parameter fields with your own inputs and outputs,
 * and replace the {@link run} method implementation with your own logic.
 * </p>
 * <p>
 * To add VCellHelper to this project,
 * rt-click on topmost tree element
 * "imagej-plugin2"->Properties->Libraries tab->Add External Jars...->
 * File Dialog->{EclipseVCellWorkspaceRootDir}/vcell/vcell-imagej-helper/target/vcell-imagej-helper-0.0.1-SNAPSHOT.jar.
 * </p>
 * <p>
 * Once vcell-imagej-helper-0.0.1-SNAPSHOT.jar has been added to the Libraries tab open
 * the small arrow to the left and select "Source Attachment"->Add/Edit->External Location->
 * External File Dialog->{EclipseVCellWorkspaceRootDir}/vcell/vcell-imagej-helper/target/vcell-imagej-helper-0.0.1-SNAPSHOT-sources.jar.
 * </p>
  */
@Plugin(type = ContextCommand.class, menuPath = "Plugins>VCellPluginTest")
public class VCellPluginTest extends ContextCommand {
    //
    // Feel free to add more parameters here...
    //
	
	//"colreeze\",\"Monkeyflower_pigmentation_v2\",\"Pattern_formation\",\"WT\",null,null\n"+e.getMessage(), "Search failed", MessageType.ERROR_MESSAGE);
	//displayProgressBar(false, null, null);
	
	@Parameter
	private UIService uiService;

  	@Parameter
	private VCellHelper vcellHelper;
  	
  	
	@Parameter
	private String vCellUser = "colreeze";
	
	@Parameter
	private String  vCellModel = "Monkeyflower_pigmentation_v2";
	
	@Parameter
	private String application = "Pattern_formation";
	
	@Parameter
	private String simulation = "WT";
	
	@Parameter
	private String variable = "A";
	
	@Parameter
	private int  timePoint = 500;
	
	/* @Parameter
	private String  imageName; */
	
	/* @Parameter */
	/*private String size; */

    /**
     * This main function serves for development purposes.
     * It allows you to run the plugin immediately out of
     * your integrated development environment (IDE).
     *
     * @param args whatever, it's ignored
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
    	
    	
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
    }


	private JDialog progressDialog = null;
	private final Dimension dim = new Dimension(200,25);
	private final JProgressBar jProgressBar = new JProgressBar(0,100) {
		@Override
		public Dimension getPreferredSize() {
			// TODO Auto-generated method stub
			return dim;
		}
		@Override
		public Dimension getSize(Dimension rv) {
			// TODO Auto-generated method stub
			return dim;
		}
	};
    private void displayProgressBar(boolean bShow,String message,String title) {
    	if(progressDialog == null) {
			JFrame applicationFrame = (JFrame)uiService.getDefaultUI().getApplicationFrame();
			progressDialog = new JDialog(applicationFrame,"Checking for VCell Client",false);
			progressDialog.getContentPane().add(jProgressBar);
			jProgressBar.setStringPainted(true);
			progressDialog.pack();
    	}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(!bShow) {
					progressDialog.setVisible(false);
					return;
				}
				jProgressBar.setValue(0);
				progressDialog.setTitle(title);
				jProgressBar.setString(message);
				progressDialog.setVisible(true);
			}
		});
    }

    private Hashtable<String,Thread> threadHash = new Hashtable<String,Thread>();
    private void startJProgressThread0(String lastName,String newName) {
    	if(lastName != null && threadHash.get(lastName) != null) {
	    	threadHash.get(lastName).interrupt();
	    	while(threadHash.get(lastName) != null) {
	    		try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
	    	}
    	}
    	if(newName == null) {
    		return;
    	}
    	final Thread progressThread = new Thread(new Runnable(){
			@Override
			public void run() {
				final int[] progress = new int[] {1};
				while(progressDialog.isVisible()) {
					if(Thread.currentThread().isInterrupted()) {
						break;
					}
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							jProgressBar.setValue(progress[0]);
						}});
					progress[0]++;
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						break;
					}
				}
				threadHash.remove(Thread.currentThread().getName());
			}});
    	threadHash.put(newName, progressThread);
		progressThread.setName(newName);
		progressThread.setDaemon(true);//So not block JVM exit
		progressThread.start();
    }
    
	@Override
	public void run() {
		
		displayProgressBar(true, "Checking listening ports...", "Checking for VCell Client");
		startJProgressThread0(null,"Check");
		
      try {
    	  //Find the port that a separately running VCell client is listening on
    	  //
			System.out.println("vcell service port="+vcellHelper.findVCellApiServerPort());
			//uiService.getDisplayViewer(textDisplay).dispose();
			displayProgressBar(false, null, null);
		} catch (Exception e) {
			//e.printStackTrace();
			displayProgressBar(false, null, null);
			//uiService.getDisplayViewer(textDisplay).dispose();
			uiService.showDialog("Activate VCell client ImageJ service\nTools->'Start Fiji (ImageJ) service'\n"+e.getMessage(), "Couldn't contact VCell client", MessageType.ERROR_MESSAGE);
			return;
		}
      
      
		displayProgressBar(true, "Searching...", "Searching VCell Models");
		startJProgressThread0("Check","Search");

		String theCacheKey = null;
      VCellHelper.VCellModelSearch vcms = new VCellHelper.VCellModelSearch(VCellHelper.ModelType.bm,vCellUser,vCellModel,application,simulation,null,null);
      try {
		ArrayList<VCellModelSearchResults> vcmsr = vcellHelper.getSearchedModelSimCacheKey(false,vcms,null);
		if(vcmsr.size() == 0) {
			throw new Exception("No Results for search found");
		}
		theCacheKey = vcmsr.get(0).getCacheKey();
		System.out.println("theCacheKey="+theCacheKey);
		displayProgressBar(false, null, null);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		uiService.showDialog("VCellHelper.ModelType.bm,vCellUser,vCellModel,application,simulation,null,null\n"+e.getMessage(), "Search failed", MessageType.ERROR_MESSAGE);
		displayProgressBar(false, null, null);
	}
      
      displayProgressBar(true, "Loading Data...", "Loading Data");
      startJProgressThread0("Search","getTimePointData");

      try {
    	  String var = variable;
    	  int[] time = new int[] {timePoint};
    	  IJDataList tpd = vcellHelper.getTimePointData(theCacheKey,var,VCellHelper.VARTYPE_POSTPROC.NotPostProcess,time,0);
    	  double[] data = tpd.ijData[0].getDoubleData();
    	  BasicStackDimensions bsd = tpd.ijData[0].stackInfo;
    	  System.out.println(bsd.xsize+" "+bsd.ysize);
    	  ArrayImg<DoubleType, DoubleArray> testimg = ArrayImgs.doubles( data, bsd.xsize,bsd.ysize);
    	  uiService.show(testimg);
    	  
    	  displayProgressBar(false, null, null);
	} catch (Exception e) {
		// TODO Auto-generated catch block
//		e.printStackTrace();
		uiService.showDialog("theCacheKey,var,VCellHelper.VARTYPE_POSTPROC.NotPostProcess,time,0\n"+e.getMessage(), "getTimePoint(...) failed", MessageType.ERROR_MESSAGE);
		displayProgressBar(false, null, null);

	}
      startJProgressThread0("getTimePointData",null);
	}
}
