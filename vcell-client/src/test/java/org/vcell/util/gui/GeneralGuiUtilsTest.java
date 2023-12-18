package org.vcell.util.gui;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;


@Tag("Fast")
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

    @Disabled
    @Test
    public void ancestorTest(){
        BiFunction<Class<?>, Component, Container> buMethod =
                (clzz, cmpt) -> GeneralGuiUtils.findTypeParentOfComponent(cmpt, clzz);
        aTest(buMethod);
    }
}
