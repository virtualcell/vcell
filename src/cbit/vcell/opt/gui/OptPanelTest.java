package cbit.vcell.opt.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class OptPanelTest extends cbit.vcell.client.test.ClientTester {
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		//cbit.vcell.server.VCellConnection vcConn = VCellConnectionFactoryInit(args,"OptPanelTest").createVCellConnection();

		java.awt.Frame frame = new java.awt.Frame();
		OptPanel aOptPanel;
		aOptPanel = new OptPanel();
		frame.add("Center", aOptPanel);
		frame.setSize(aOptPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});

		cbit.vcell.opt.OptimizationSpec optSpec = null;
		if (args.length==0){
			optSpec = cbit.vcell.opt.OptimizationSpecTest.getOdeExample();
		}else{
			//
			// read optSpec from a file
			//
			optSpec = new cbit.vcell.opt.OptimizationSpec();
			java.io.File optSpecFile = new java.io.File(args[0]);
			java.io.FileReader reader = new java.io.FileReader(optSpecFile);
			StringBuffer strBuf = new StringBuffer(1000);
			int ichar = 0;
			while ((ichar = reader.read())!=-1){
				char singleChar = (char)ichar;
				strBuf.append(singleChar);
			}	
			cbit.vcell.math.CommentStringTokenizer tokens = new cbit.vcell.math.CommentStringTokenizer(strBuf.toString());
			optSpec.read(tokens);
		}
			
		aOptPanel.setOptimizationSpec(optSpec);
		aOptPanel.setOptimizationService(new cbit.vcell.opt.solvers.LocalOptimizationService());

		frame.setVisible(true);

	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of OptPanelTest");
		exception.printStackTrace(System.out);
	}
}
}
