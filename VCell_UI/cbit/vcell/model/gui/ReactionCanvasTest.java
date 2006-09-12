package cbit.vcell.model.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.dictionary.ReactionCanvasDisplaySpec;
import cbit.vcell.model.*;
/**
 * This type was created in VisualAge.
 */
public class ReactionCanvasTest {
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ReactionCanvas aReactionCanvas;
		aReactionCanvas = new ReactionCanvas();
		frame.setContentPane(aReactionCanvas);
		frame.setSize(aReactionCanvas.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setSize(400,400);
		frame.setVisible(true);

		//
		// test ReactionCanvas using direct specification of display (see ReactionCanvasDisplaySpec)
		//
		ReactionCanvasDisplaySpec spec = new ReactionCanvasDisplaySpec("left","right","top","bottom",ReactionCanvasDisplaySpec.ARROW_RIGHT);
		aReactionCanvas.setReactionCanvasDisplaySpec(spec);

		try { Thread.sleep(2000); }catch (InterruptedException e){}

		spec = new ReactionCanvasDisplaySpec("left","right","top","bottom",ReactionCanvasDisplaySpec.ARROW_BOTH);
		aReactionCanvas.setReactionCanvasDisplaySpec(spec);

		try { Thread.sleep(2000); }catch (InterruptedException e){}

		spec = new ReactionCanvasDisplaySpec("left",null,"top",null,ReactionCanvasDisplaySpec.ARROW_RIGHT);
		aReactionCanvas.setReactionCanvasDisplaySpec(spec);
		
		try { Thread.sleep(2000); }catch (InterruptedException e){}

		//
		// clear screen
		//
		spec = null;
		aReactionCanvas.setReactionCanvasDisplaySpec(spec);
	
		try { Thread.sleep(2000); }catch (InterruptedException e){}
		
		//
		// test ReactionCanvas using actual Model reactions
		//
		Model model = ModelTest.getExample();
		while (true){
			for (int i = 0; i < model.getReactionSteps().length; i++){
				aReactionCanvas.setReactionStep(model.getReactionSteps(i));
				Thread.sleep(2000);
			}
		}

		
		
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.ReactionCanvas");
		exception.printStackTrace(System.out);
	}
}
}
