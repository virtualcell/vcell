package org.jlibsedml.components.output;

import org.jlibsedml.*;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedGeneralClass;
import org.jlibsedml.components.dataGenerator.DataGenerator;

import java.util.List;
import java.util.Set;

/**
 * Base class for any kind of SED-ML output - e.g., a plot or report.
 *
 * @author anu/radams
 *
 */
public abstract class Output extends SedBase {

    /**
     *
     * @param id   - non null or non-empty String.
     * @param name - optional, can be null
     * @throws IllegalArgumentException if <code>id</code> is <code>null</code>.
     */
    public Output(SId id, String name) {
        super(id, name);
        if (SedMLElementFactory.getInstance().isStrictCreation()) {
            SedGeneralClass.checkNoNullArgs(id);
        }
    }

    /**
     * Gets the type of this output (Plot2D, Plot3D, Report)
     *
     * @return A <code>non-null String</code>.
     */
    public abstract String getKind();

    /**
     * Boolean test for whether this output is a Plot2d description.
     *
     * @return <code>true</code> if this is a Plot2d description, <code>false</code> otherwise.
     */
    public boolean isPlot2d() {
        return this.getKind().equals(SedMLTags.PLOT2D_KIND);
    }

    /**
     * Boolean test for whether this output is a Plot3d description.
     *
     * @return <code>true</code> if this is a Plot3d description, <code>false</code> otherwise.
     */
    public boolean isPlot3d() {
        return this.getKind().equals(SedMLTags.PLOT3D_KIND);
    }


    /**
     * Boolean test for whether this output is a report description.
     *
     * @return <code>true</code> if this is a report description, <code>false</code> otherwise.
     */
    public boolean isReport() {
        return this.getKind().equals(SedMLTags.REPORT_KIND);
    }

    /**
     * Gets a {@link List} of all {@link DataGenerator} identifiers used in this output.<br/>
     * This list will contain only unique entries; the same {@link DataGenerator} id will not appear
     * twice in this output.
     *
     * @return A possibly empty but non-null {@link List} of {@link DataGenerator} id values.
     */
    public abstract Set<SId> getAllDataGeneratorReferences();
}
