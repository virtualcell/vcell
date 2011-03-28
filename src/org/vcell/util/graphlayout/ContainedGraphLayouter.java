package org.vcell.util.graphlayout;

import java.util.Arrays;
import java.util.List;

import org.vcell.util.graphlayout.energybased.ShootAndCutLayouter;


public interface ContainedGraphLayouter {

	public static class DefaultLayouters {
		public static final List<String> NAMES = 
			Arrays.asList(RandomLayouter.LAYOUT_NAME, EdgeTugLayouter.LAYOUT_NAME, 
					ShootAndCutLayouter.LAYOUT_NAME, SimpleElipticalLayouter.LAYOUT_NAME);		
	}
	
	public String getLayoutName();
	public void layout(ContainedGraph graph);
	
}
