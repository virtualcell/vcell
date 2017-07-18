package org.vcell;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import ncsa.hdf.object.Dataset;
import net.imagej.display.OverlayService;
import net.imagej.ops.AbstractOp;
import net.imagej.ops.Op;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;

@Plugin(type = Op.class, name = "compareROIMean")
public class CompareROIMean<T extends RealType<T>> extends AbstractOp {
	
	@Parameter
	private IterableInterval<T> dataA;
	
	@Parameter
	private IterableInterval<T> dataB;
	
	@Parameter
	private OverlayService overlayService;
	
	@Override
	public void run() {
		
		System.out.println(overlayService.getOverlays().size());
		
	}
}
