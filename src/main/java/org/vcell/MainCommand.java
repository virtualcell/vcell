package org.vcell;

import net.imagej.Dataset;
import net.imagej.ImageJ;

import java.io.IOException;

import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import io.scif.services.DatasetIOService;

/**
 * Created by kevingaffney on 6/26/17.
 */
@Plugin(type = Command.class, menuPath = "Plugins>Modeling")
public class MainCommand implements Command {

    @Parameter
    private ImageJ ij;

    @Override
    public void run() {
        MainModel model = new MainModel();
        MainView view = new MainView(model, ij.getContext());
        new MainController(model, view, ij.getContext());
        view.setVisible(true);
    }
}
