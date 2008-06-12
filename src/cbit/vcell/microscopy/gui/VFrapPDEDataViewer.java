package cbit.vcell.microscopy.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JFrame;

import cbit.vcell.client.data.PDEDataViewer;

public class VFrapPDEDataViewer extends PDEDataViewer {
	public VFrapPDEDataViewer()
	{
		super();
	}

	@Override
	protected void showComponentInFrame(Component comp, String title) {
		final JFrame frame = new JFrame(title);
		frame.setLocation(new Point(300,300));
		frame.getContentPane().add(comp);
		frame.setPreferredSize(new Dimension(700,550));
		frame.pack();
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
	}
}
