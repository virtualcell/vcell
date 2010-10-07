package cbit.vcell.model.gui;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.awt.BorderLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.vcell.util.gui.JInternalFrameEnhanced;

import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.graph.ReactionSlicesCartoonEditorPanel;
import cbit.vcell.graph.StructureCartoonTool;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public class ReactionSlicesCartoonEditorDialog extends JInternalFrameEnhanced {
	private JPanel contentsPane = null;
	private ReactionSlicesCartoonEditorPanel reactionCartoonEditorPanel = null;
	private StructureCartoonTool structureCartoonTool = null;

	public ReactionSlicesCartoonEditorDialog(StructureCartoonTool sct) {
		super();
		structureCartoonTool = sct;
		initialize();
	}

	public void cleanupOnClose() {

		getReactionCartoonEditorPanel().cleanupOnClose();
	}

	private JPanel getContentsPane() {
		if (contentsPane == null) {
			try {
				contentsPane = new JPanel();
				contentsPane.setName("ContentsPane");
				contentsPane.setLayout(new BorderLayout());
				getContentsPane().add(getReactionCartoonEditorPanel(), "Center");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return contentsPane;
	}

	private ReactionSlicesCartoonEditorPanel getReactionCartoonEditorPanel() {
		if (reactionCartoonEditorPanel == null) {
			try {
				reactionCartoonEditorPanel = new ReactionSlicesCartoonEditorPanel(structureCartoonTool);
				reactionCartoonEditorPanel.setName("ReactionCartoonEditorPanel");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return reactionCartoonEditorPanel;
	}

	private void handleException(Throwable exception) {
		 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		 exception.printStackTrace(System.out);
	}

	public void init(Model model, Structure structure, DocumentManager documentManager) {
		getReactionCartoonEditorPanel().setModel(model);
		getReactionCartoonEditorPanel().setStructure(structure);
		getReactionCartoonEditorPanel().setDocumentManager(documentManager);
		setTitle("Reactions for "+structure.getName());
	}

	private void initialize() {
		try {
			setName("ReactionCartoonEditorDialog");
			setFrameIcon(new ImageIcon(getClass().getResource("/images/stepSlices.gif")));
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setClosable(true);
			setSize(600, 495);
			setResizable(true);
			setContentPane(getContentsPane());
		} catch (Throwable throwable) {
			handleException(throwable);
		}
	}

	public static void main(String[] args) {
		try {
			JFrame frame = new JFrame();
			ReactionSlicesCartoonEditorDialog aReactionCartoonEditorDialog;
			aReactionCartoonEditorDialog = new ReactionSlicesCartoonEditorDialog(null);
			frame.setContentPane(aReactionCartoonEditorDialog);
			frame.setSize(aReactionCartoonEditorDialog.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			Insets insets = frame.getInsets();
			frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JInternalFrame");
			exception.printStackTrace(System.out);
		}
	}
}
