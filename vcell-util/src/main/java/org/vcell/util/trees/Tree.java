package org.vcell.util.trees;

import java.util.ArrayList;
import java.util.List;

public interface Tree <T>{
    boolean hasElement(T ... path);
    List<T> getChildren(T ... path);
    boolean addElement(T ... path);
    boolean addElement(boolean makeParents, T ... path);
    T swapElements(T replacement, T ... path);
    T swapElements(boolean removeChildren, T replacement,  T ... path);
    T removeElement(T ... path);
    T removeElement(boolean removeChildren, T ... path);



    class Node<T>{
        private T data;
        private Node<T> parent;
        private List<Node<T>> children;

        Node(T data){
            this(data, new ArrayList<>());
        }

        Node(T data, List<Node<T>> children){
            this.data = data;
            this.parent = null;
            this.children = children;
        }

        T getData(){
            return data;
        }

        Node<T> getParent(){
            return parent;
        }

        List<Node<T>> getChildren(){
            return children;
        }

        void setData(T data){
            this.data = data;
        }

        void setChildren(List<Node<T>> children){
            this.children = children;
        }

        private void setParent(Node<T> parent){
            this.parent = parent;
        }

        boolean hasChild(Node<T> childToFind){
            for (Node<T> child : this.children){
                if (child.equals(childToFind)) return true;
            }
            return false;
        }

        void addChild(Node<T> childToAdd){
            if (this.hasChild(childToAdd)) return;
            this.children.add(childToAdd);
            childToAdd.setParent(this);
        }

        void removeChild(Node<T> childToRemove){
            if (this.hasChild(childToRemove)) this.children.remove(childToRemove);
        }
    }
}
