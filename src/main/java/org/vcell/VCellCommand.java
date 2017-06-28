package org.vcell;

import net.imagej.ImageJ;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Created by kevingaffney on 6/26/17.
 */
@Plugin(type = Command.class, menuPath = "Plugins>Modeling")
public class VCellCommand implements Command {

    @Parameter
    private ImageJ ij;

    @Override
    public void run() {
        VCellModel model = new VCellModel();
        MainView view = new MainView(model, ij.getContext());
        new MasterController(model, view, ij.getContext());
        view.setVisible(true);
    }
}
