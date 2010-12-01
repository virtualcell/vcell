package cbit.vcell.model.gui;

/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.vcell.util.gui.JInternalFrameEnhanced;

import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.graph.ReactionCartoonEditorPanel;
import cbit.vcell.graph.structures.StructureSuite;
import cbit.vcell.model.Model;

@SuppressWarnings("serial")
public class ReactionCartoonEditorDialog extends JInternalFrameEnhanced {
	private ReactionCartoonEditorPanel reactionCartoonEditorPanel = null;

	public ReactionCartoonEditorDialog() {
		super();
		initialize();
	}

	public void cleanupOnClose() {
		getReactionCartoonEditorPanel().cleanupOnClose();
	}

	private ReactionCartoonEditorPanel getReactionCartoonEditorPanel() {
		if (reactionCartoonEditorPanel == null) {
			try {
				reactionCartoonEditorPanel = new ReactionCartoonEditorPanel();
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

	public void init(Model model, StructureSuite structureSuite, DocumentManager documentManager) {
		getReactionCartoonEditorPanel().setModel(model);
		getReactionCartoonEditorPanel().setStructureSuite(structureSuite);
		getReactionCartoonEditorPanel().setDocumentManager(documentManager);
		setTitle(structureSuite.getTitle());
	}

	private void initialize() {
		try {
			setFrameIcon(new ImageIcon(getClass().getResource("/images/step.gif")));
			setName("ReactionCartoonEditorDialog");
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setClosable(true);
			setSize(600, 495);
			setResizable(true);
			add(getReactionCartoonEditorPanel(), BorderLayout.CENTER);
		} catch (Throwable throwable) {
			handleException(throwable);
		}
	}

	public static void main(String[] args) {
		try {
			JFrame frame = new JFrame();
			ReactionCartoonEditorDialog aReactionCartoonEditorDialog;
			aReactionCartoonEditorDialog = new ReactionCartoonEditorDialog();
			frame.setContentPane(aReactionCartoonEditorDialog);
			frame.setSize(aReactionCartoonEditorDialog.getSize());
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
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
