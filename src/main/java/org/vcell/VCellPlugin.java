package org.vcell;

import net.imagej.ImageJ;

import java.io.IOException;

/**
 * Created by kevingaffney on 6/23/17.
 */
public class VCellPlugin {
    public static void main(String... args) throws IOException {
        ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ij.command().run(MainCommand.class, true);
    }
}
