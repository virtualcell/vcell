package org.vcell.imagej.common.gui;

import java.util.List;

import org.scijava.display.Display;
import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.plugin.PluginService;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import org.scijava.thread.ThreadService;
import org.scijava.ui.UIService;
import org.scijava.ui.swing.viewer.SwingDisplayWindow;
import org.scijava.ui.viewer.DisplayViewer;

import net.imagej.Dataset;
import net.imagej.autoscale.AutoscaleService;
import net.imagej.autoscale.DataRange;
import net.imagej.display.DatasetView;
import net.imagej.display.ImageDisplayService;
import net.imagej.ui.swing.viewer.image.SwingImageDisplayViewer;

@Plugin(type = Service.class)
public class InFrameDisplayService extends AbstractService {
	
	@Parameter
	private DisplayService displayService;
	
	@Parameter
	private ImageDisplayService imageDisplayService;
	
	@Parameter
	private UIService uiService;
	
	@Parameter
	private AutoscaleService autoscaleService;
	
	@Parameter
	private PluginService pluginService;
	
	@Parameter
	private ThreadService threadService;
	
    public void displayDataset(Dataset dataset, SwingDisplayWindow window) {
    	
		Display<?> display = displayService.createDisplayQuietly(dataset);

		final SwingImageDisplayViewer finalViewer = getDisplayViewer(display);
		if (finalViewer == null) return;

		threadService.queue(() -> {
			finalViewer.view(window, display);
			uiService.addDisplayViewer(finalViewer);
			display.update();
			
			autoscale(imageDisplayService.getActiveDatasetView());
		});
    }
    
    private SwingImageDisplayViewer getDisplayViewer(Display<?> display) {
    	
    	if (uiService.getDisplayViewer(display) != null) {
			// display is already being shown
			return null;
		}

		final List<PluginInfo<DisplayViewer<?>>> viewers =
			uiService.getViewerPlugins();

		DisplayViewer<?> displayViewer = null;
		for (final PluginInfo<DisplayViewer<?>> info : viewers) {
			// check that viewer can actually handle the given display
			final DisplayViewer<?> viewer = pluginService.createInstance(info);
			if (!(viewer instanceof SwingImageDisplayViewer)) continue;
			displayViewer = viewer;
			break; // found a suitable viewer; we are done
		}
		
		if (displayViewer == null) {
			System.err.println("For UI '" + getClass().getName() +
				"': no suitable viewer for display: " + display);
			return null;
		}
		
		return (SwingImageDisplayViewer) displayViewer;
    }
    
    /**
     * Scales the brightness and contrast for better display. 
     * Does not change the underlying pixel intensities.
     * @param datasetView
     */
    private void autoscale(DatasetView datasetView) {
		DataRange range = autoscaleService.getDefaultIntervalRange(datasetView.getData());
		datasetView.setChannelRanges(range.getMin(), range.getMax());
		datasetView.getProjector().map();
		datasetView.update();
    }
}
