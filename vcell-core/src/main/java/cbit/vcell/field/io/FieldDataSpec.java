package cbit.vcell.field.io;

import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.document.User;

import java.io.File;
import java.util.List;

public class FieldDataSpec {
    public final static int JOBINDEX_DEFAULT = 0;

    public File file;
    public String fileName;
    public Extent extent;
    public ISize iSize;

    /**
    Can also be recognized as variable names.
     */
    public List<String> channelNames;
    public List<Double> times;
    public String annotation;
    public Origin origin;
    public User owner;
}
