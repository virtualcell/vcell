package org.jlibsedml;

/**
 * Simple data object to hold an identifier and a human-readable name.<br/>
 * Equals/hashcode are based on the value of getId().
 * @author radams
 *
 */
public class IdName {
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IdName other = (IdName) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public IdName(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    private final String id,name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
