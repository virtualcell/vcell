package cbit.vcell.field;

import org.vcell.util.document.User;

public class FieldDataExternalDataIDEntry {
    public final User owner;
    public String name;
    public String annotation;

    public FieldDataExternalDataIDEntry(User owner){this.owner = owner;}

    public FieldDataExternalDataIDEntry(User owner, String name, String annotation){
        this.owner = owner; this.name = name; this.annotation = annotation;
    }

}
