package org.vcell.util.gui;

import javax.swing.*;
import java.awt.*;

// see also VCellIcon
// Usage:
// Icon colorIconFirst = new ColorIcon(10, 10, colorFirst, true);
// Icon colorIconSecond = new ColorIcon(10, 10, colorSecond, true);
// Icon compositeIcon = new CompositeIcon(colorIconFirst, colorIconSecond);
public class CompositeIcon implements Icon
{
    private final Icon icon1;
    private final Icon icon2;

    public CompositeIcon(Icon icon1, Icon icon2) {
        this.icon1 = icon1;
        this.icon2 = icon2;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        icon1.paintIcon(c, g, x, y);                            // paint the first icon
        icon2.paintIcon(c, g, x + icon1.getIconWidth(), y);  // paint the second icon next to the first icon
    }

    @Override
    public int getIconWidth() {
        return icon1.getIconWidth() + icon2.getIconWidth();     // the total width is the sum of both icons' widths
    }

    @Override
    public int getIconHeight() {
        return Math.max(icon1.getIconHeight(), icon2.getIconHeight());  // height is the maximum of both icons' heights
    }
}