package cbit.vcell.model.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class DBReactionWizardTest extends cbit.vcell.client.test.ClientTester {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {

	if(args.length < 4 || (!args[args.length-1].equalsIgnoreCase("feature") && !args[args.length-1].equalsIgnoreCase("membrane"))){
		System.out.println("Must specify 'feature' || 'membrane' as last argument");
		System.exit(0);
	}
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DBReactionWizardPanel aDBReactionWizardPanel;
		aDBReactionWizardPanel = new DBReactionWizardPanel();
		//javax.swing.JDialog frame = aDBReactionWizardDialog;
		frame.setContentPane(aDBReactionWizardPanel);
		frame.setSize(aDBReactionWizardPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		//
		Class structClass = null;
		if(args[args.length-1].equalsIgnoreCase("feature")){
			structClass = Class.forName("cbit.vcell.model.Feature");
		}else if(args[args.length-1].equalsIgnoreCase("membrane")){
			structClass = Class.forName("cbit.vcell.model.Membrane");
		}else{
			throw new IllegalArgumentException("unknown test type="+args[args.length-1]);
		}
		String[] args2 = new String[args.length-1];
		System.arraycopy(args,0,args2,0,args.length-1);
		cbit.vcell.client.server.ClientServerManager managerManager = mainInit(args2,"CreateSpeciesDialogTest",new javax.swing.JFrame());
		cbit.vcell.clientdb.DocumentManager docManager = managerManager.getDocumentManager();
        aDBReactionWizardPanel.setDocumentManager(docManager);
        //
		cbit.vcell.model.Model model = cbit.vcell.model.ModelTest.getExample_Bound();
        aDBReactionWizardPanel.setModel(model);
        //
        cbit.vcell.model.Structure struct = null;
        for(int i = 0;i < model.getNumStructures();i+= 1){
	        if(model.getStructure(i).getClass().equals(structClass)){
		        struct = model.getStructure(i);
		        break;
	        }
        }
        if(struct == null){
	        throw new RuntimeException("Couldn't find suitable structure");
        }
        aDBReactionWizardPanel.setStructure(struct);

		aDBReactionWizardPanel.setVisible(true);
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
		aDBReactionWizardPanel.setVisible(true);
		frame.pack();
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of cbit.vcell.model.CreateSpeciesDialog");
		exception.printStackTrace(System.out);
	}
}
}