package cbit.vcell.field;

import org.vcell.util.document.User;

public class FieldDataDBEntry {
    public User owner;
    public String name;
    public String annotation;

    public FieldDataDBEntry(){}

    public FieldDataDBEntry(User owner, String name, String annotation){
        this.owner = owner; this.name = name; this.annotation = annotation;
    }

}
