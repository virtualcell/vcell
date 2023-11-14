package org.vcell.util;

import org.junit.Ignore;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiFunction;

import static org.junit.Assert.*;

public class GeneralGuiUtilsTest {

    private void aTest(BiFunction<Class<?>, Component, Container> method){
        JFrame jf = new JFrame();
        JPanel jp = new JPanel();
        jf.add(jp);
        JButton btn = new JButton();
        jf.add(btn);
        assertSame(method.apply(Frame.class, jf), jf);
        assertSame(method.apply(Frame.class, jp), jf);
        assertSame(method.apply(Window.class, jp), jf);
        assertNull(method.apply(JDialog.class, jp));
        assertNull(method.apply(Frame.class, null));
        assertSame(method.apply(Frame.class, btn), jf);
        assertSame(method.apply(Window.class, btn), jf);
        assertNull(method.apply(JDialog.class, btn));
    }

    @Ignore
    @Test
    public void ancestorTest(){
        BiFunction<Class<?>, Component, Container> buMethod =
                (clzz, cmpt) -> GeneralGuiUtils.findTypeParentOfComponent(cmpt, clzz);
        aTest(buMethod);
    }
}
