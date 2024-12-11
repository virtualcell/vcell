package org.vcell.util.gui;

import javax.swing.*;
import java.awt.*;

public class CompositeIcon implements Icon
{
    private final Icon icon1;
    private final Icon icon2;
    public CompositeIcon(Icon icon1, Icon icon2) {
        this.icon1 = icon1;
        this.icon2 = icon2;
    }

    @Override public void paintIcon(Component c, Graphics g, int x, int y) {
// Paint the first icon
        icon1.paintIcon(c, g, x, y);

// Paint the second icon next to the first icon
        icon2.paintIcon(c, g, x + icon1.getIconWidth(), y);
    }

    @Override public int getIconWidth() {
// The total width is the sum of both icons' widths
        return icon1.getIconWidth() + icon2.getIconWidth();
    }

    @Override public int getIconHeight() {
// The height is the maximum of both icons' heights
        return Math.max(icon1.getIconHeight(), icon2.getIconHeight());
    }
}