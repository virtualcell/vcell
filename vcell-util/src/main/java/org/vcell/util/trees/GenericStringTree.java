package org.vcell.util.trees;

public class GenericStringTree extends GenericTree<String> {
    @Override
    protected boolean isInvalidType(Object o) {
        return !(o instanceof String);
    }
}
