package org.vcell;

import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;

/**
 * Created by kevingaffney on 6/22/17.
 */
@Plugin(type = Command.class)
public class VirtualTIRF<T extends RealType<T>> implements Command {

    @Parameter
    private File file;

    @Override
    public void run() {

    }
}
