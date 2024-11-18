package org.vcell.util;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.vcell.util.trees.GenericStringTree;
import org.vcell.util.trees.GenericTree;
import org.vcell.util.trees.Tree;

import java.util.List;

@Tag("Fast")
public class GenericTreeTest {
    @Test
    public void exerciseStringTree(){
        Tree<String> testTree = new GenericStringTree();

        testTree.addElement(true, "hello", "world");
        Assertions.assertTrue(testTree.hasElement( "hello", "world"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testTree.addElement("hello", "my", "baby"));
        Assertions.assertFalse(testTree.hasElement("hello", "my"));
        testTree.addElement("hello", "my");
        testTree.addElement( "hello", "my", "baby");
        Assertions.assertTrue(testTree.hasElement("hello", "my", "baby"));

        List<String> children = testTree.getChildren("hello");
        Assertions.assertEquals(2, children.size());
        Assertions.assertTrue(children.contains("world"));
        Assertions.assertTrue(children.contains("my"));

        testTree.swapElements("honey", "hello", "my", "baby");
        Assertions.assertTrue(testTree.hasElement("hello", "my", "honey"));
        testTree.addElement("hello", "my", "honey", "gal");
        testTree.swapElements("ragtime", "hello", "my", "honey");
        Assertions.assertTrue(testTree.hasElement("hello", "my", "ragtime", "gal"));
        testTree.swapElements(true, "baby", "hello", "my", "ragtime");
        Assertions.assertFalse(testTree.hasElement("hello", "my", "ragtime", "gal"));
        Assertions.assertFalse(testTree.hasElement("hello", "my", "ragtime"));
        Assertions.assertTrue(testTree.hasElement("hello", "my", "baby"));

        testTree.swapElements("ragtime", "hello", "my", "baby");
        testTree.addElement("hello", "my", "ragtime", "gal");
        Assertions.assertThrows(IllegalArgumentException.class, () -> testTree.removeElement("hello", "my", "baby"));
        testTree.removeElement("hello", "my", "ragtime", "gal");
        Assertions.assertFalse(testTree.hasElement("hello", "my", "ragtime", "gal"));
        Assertions.assertTrue(testTree.hasElement("hello", "my", "ragtime"));
        testTree.removeElement(true, "hello", "my");
        Assertions.assertFalse(testTree.hasElement("hello", "my", "ragtime"));
        Assertions.assertFalse(testTree.hasElement("hello", "my"));
        Assertions.assertTrue(testTree.hasElement("hello"));
    }
}
