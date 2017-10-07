package org.vcell.imagej.app;

import javax.swing.SwingUtilities;

import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.ServiceHelper;
import org.vcell.imagej.common.gui.InFrameDisplayService;
import org.vcell.imagej.common.vcell.VCellService;

import net.imagej.ImageJ;

/**
 * Created by kevingaffney on 6/26/17.
 */
@Plugin(type = Command.class, menuPath = "Plugins>Modeling")
public class MainCommand implements Command {

    @Parameter
    private ImageJ ij;

    @Override
    public void run() {
    	loadServices();
        MainModel model = new MainModel();
        MainView view = new MainView(model, ij.getContext());
        new MainController(model, view, ij.getContext());
        SwingUtilities.invokeLater(() -> {
        	view.setVisible(true);
        });
    }
    
    private void loadServices() {
        ServiceHelper helper = new ServiceHelper(ij.getContext());
        helper.loadService(InFrameDisplayService.class);
        helper.loadService(VCellService.class);
    }
}
