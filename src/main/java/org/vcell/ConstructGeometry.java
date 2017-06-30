package org.vcell;

import net.imagej.ops.AbstractOp;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

/**
 * Created by kevingaffney on 6/29/17.
 */
@Plugin(type = Command.class)
public class ConstructGeometry<T extends RealType<T>> implements Command {
    @Override
    public void run() {

    }
}
