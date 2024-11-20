package org.vcell.util.trees;

import java.util.Arrays;
import java.util.List;

public abstract class GenericTree <T> implements Tree<T> {
    private final Node<T> root;

    public GenericTree(){
        this(null);
    }

    private GenericTree(T rootValue){
        this.root = new Node<>(rootValue);
    }


    @Override
    @SafeVarargs
    final public boolean hasElement(T... path) {
        try {
            Node<T> targetNode = getTargetNode(path);
            return targetNode.getData().equals(path[path.length - 1]);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @SafeVarargs
    final public List<T> getChildren(T... path) {
        Node<T> targetNode = getTargetNode(path);
        return targetNode.getChildren().stream().map(Node::getData).toList();
    }

    @Override
    @SafeVarargs
    final public boolean addElement(T ... path){
        return this.addElement(false, path);
    }

    @Override
    @SafeVarargs
    final public boolean addElement(boolean makeParents, T ... path){
        T[] parentPath = Arrays.copyOf(path, path.length - 1);
        Node<T> targetNode = this.getTargetNode(makeParents, parentPath);
        for (Node<T> child : targetNode.getChildren()) if (child.getData().equals(path[parentPath.length])) return false;
        targetNode.addChild(new Node<>(path[parentPath.length]));
        return true;
    }

    @Override
    @SafeVarargs
    final public T swapElements(T replacement, T ... path){
        return this.swapElements(false, replacement, path);
    }

    @Override
    @SafeVarargs
    final public T swapElements(boolean removeChildren, T replacement, T ... path){
        Node<T> targetNode = getTargetNode(path);
        T oldValue = targetNode.getData();
        if (!targetNode.getChildren().isEmpty() && removeChildren){
            Node<T> parent = targetNode.getParent();
            parent.removeChild(targetNode);
            parent.addChild(new Node<>(replacement));
        } else {
            targetNode.setData(replacement);
        }
        return oldValue;
    }

    @Override
    @SafeVarargs
    final public T removeElement(T... path) {
        return this.removeElement(false, path);
    }

    @Override
    @SafeVarargs
    final public T removeElement(boolean removeChildren, T... path) {
        Node<T> targetNode = getTargetNode(path);
        if (!targetNode.getChildren().isEmpty() && !removeChildren)
            throw new IllegalArgumentException("Path element to remove has children; set flag to remove if desired behavior.");

        T oldValue = targetNode.getData();
        targetNode.getParent().removeChild(targetNode);
        return oldValue;
    }

    protected abstract boolean isInvalidType(Object o);

    protected Node<T> getChild(Node<T> node, T value){
        for (Node<T> child : node.getChildren()) {
            if (child.getData().equals(value)) return child;
        }
        return null;
    }

    @SafeVarargs
    private Node<T> getTargetNode(T ... path) {
        return this.getTargetNode(false, path);
    }

    @SafeVarargs
    private Node<T> getTargetNode(boolean makeParents, T ... path){
        Node<T> currentNode = this.root;
        for (int i = 0; i < path.length; i++) { // Need index for exception statement
            T element = path[i];
            if (isInvalidType(element)) throw new IllegalArgumentException(element.toString() + " -> is not the correct type for this tree!");
            Node<T> childNode = getChild(currentNode, element);
            if (childNode == null){
                if (!makeParents) throw new IllegalArgumentException("Path element `" + element + "`  at index (" + i + ") not found");
                childNode =  new Node<>(element);
                currentNode.addChild(childNode);
            }
            currentNode = childNode;
        }
        return currentNode;
    }
}
