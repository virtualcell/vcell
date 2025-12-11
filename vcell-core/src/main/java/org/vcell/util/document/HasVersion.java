package org.vcell.util.document;

public interface HasVersion {
    void clearVersion();

    String getDescription();

    String getName();

    Version getVersion();

    void setDescription(String description) throws java.beans.PropertyVetoException;

    void setName(String name) throws java.beans.PropertyVetoException;
}
