/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;


@SuppressWarnings("serial")
public class DropTest2 extends JFrame implements DropTargetListener {

	DropTarget dt;
	JTextArea ta;

	public DropTest2() {
		super("Drop Test");
		setSize(300,300);
		addWindowListener(new WindowAdapter() { });

		getContentPane().add(
				new JLabel("Drop a list from your file chooser here:"),
				BorderLayout.NORTH);
		ta = new JTextArea();
		ta.setBackground(Color.white);
		getContentPane().add(ta, BorderLayout.CENTER);

		// Set up our text area to recieve drops...
		// This class will handle drop events
		dt = new DropTarget(ta, this);
		setVisible(true);
	}

	public void dragEnter(DropTargetDragEvent dtde) {
		System.out.println("Drag Enter");
	}

	public void dragExit(DropTargetEvent dte) {
		System.out.println("Drag Exit");
	}

	public void dragOver(DropTargetDragEvent dtde) {
		System.out.println("Drag Over");
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
		System.out.println("Drop Action Changed");
	}

	public void drop(DropTargetDropEvent dtde) {
		try {
			// Ok, get the dropped object and try to figure out what it is
			Transferable tr = dtde.getTransferable();
			DataFlavor[] flavors = tr.getTransferDataFlavors();
			for (int i = 0; i < flavors.length; i++) {
				System.out.println("Possible flavor: " + flavors[i].getMimeType());
				// Check for file lists specifically
				if (flavors[i].isFlavorJavaFileListType()) {
					// Great!  Accept copy drops...
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					ta.setText("Successful file list drop.\n\n");

					// And add the list of file names to our text area
					@SuppressWarnings("unchecked")
					List<File> list = (List<File>)tr.getTransferData(flavors[i]);
					for (int j = 0; j < list.size(); j++) {
						ta.append(list.get(j) + "\n");
					}

					// If we made it this far, everything worked.
					dtde.dropComplete(true);
					return;
				}
				// Ok, is it another Java object?
				else if (flavors[i].isFlavorSerializedObjectType()) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					ta.setText("Successful text drop.\n\n");
					Object o = tr.getTransferData(flavors[i]);
					ta.append("Object: " + o);
					dtde.dropComplete(true);
					return;
				}
				// How about an input stream?
				else if (flavors[i].isRepresentationClassInputStream()) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					ta.setText("Successful text drop.\n\n");
					ta.read(new InputStreamReader(
							(InputStream)tr.getTransferData(flavors[i])),
							"from system clipboard");
					dtde.dropComplete(true);
					return;
				}
			}
			// Hmm, the user must not have dropped a file list
			System.out.println("Drop failed: " + dtde);
			dtde.rejectDrop();
		} catch (Exception e) {
			e.printStackTrace();
			dtde.rejectDrop();
		}
	}

	public static void main(String args[]) {
		new DropTest2();
	}
} 
