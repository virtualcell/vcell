/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.graphlayout;

import java.util.Arrays;
import java.util.List;

import org.vcell.util.graphlayout.energybased.ShootAndCutLayouter;
import org.vcell.util.graphlayout.energybased.WerewolfLayouter;

import cbit.gui.graph.GraphModel;
import cbit.gui.graph.GraphView;


public interface GraphLayouter {

	public static interface Client {
		GraphModel getGraphModel();
		String getLayoutName();
		boolean isRequestingStop();
		public int getWidth();
		public int getHeight();
		
		public static class Default implements Client {
			protected final String layoutName;
			protected final GraphView graphView;
			
			public Default(GraphView graphView, String layoutName) { 
				this.graphView = graphView;
				this.layoutName = layoutName; 
			}
			
			public String getLayoutName() { return layoutName; }
			public boolean isRequestingStop() { return false; }
			public GraphModel getGraphModel() { return graphView.getGraphModel(); }
			public void repaint() { graphView.repaint(); }
			public int getWidth() { return graphView.getWidth(); }
			public int getHeight() { return graphView.getHeight(); }
		}
		
	}
	
	public static class DefaultLayouters {
		public static final List<String> NAMES = 
			Arrays.asList(RandomLayouter.LAYOUT_NAME, EdgeTugLayouter.LAYOUT_NAME, 
					ShootAndCutLayouter.LAYOUT_NAME, SimpleElipticalLayouter.LAYOUT_NAME,
					GenericLogicGraphLayouter.LAYOUT_NAME, WerewolfLayouter.LAYOUT_NAME, ShrinkCanvasLayouter.LAYOUT_NAME,
					ExpandCanvasLayouter.LAYOUT_NAME);		
	}
	
	public String getLayoutName();
	public void layout(Client client);
	public void layout(ContainedGraph graph);

}
