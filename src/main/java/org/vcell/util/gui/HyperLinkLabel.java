/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;

/*
 * The HyperLinkLabel is made with a label and an underline.
 * Together with the mouse event, it functions like a hyperlink in web.
 * author: Tracy Li
 * version: 1.0
 */
@SuppressWarnings({ "serial" })
public class HyperLinkLabel extends JLabel implements MouseListener {
    private Color color = Color.blue;
    private ActionListener listener;
    private int id;
    private String command;


    public HyperLinkLabel() {
        this("", null, 0);
    }

    /**
     * Constructor
     * @param text text to disaplay
     * @param listener  listener to be invoke
     * @param id    id will pass when invoking listener
     */
    public HyperLinkLabel(String text, ActionListener listener, int id) {
        super(text);
        setForeground(color);
//        setBackground(Color.white);
        setOpaque(false);
        this.listener = listener;
        this.id = id;
        addMouseListener(this);
    }

    public void mouseClicked(MouseEvent e) {
        if (isEnabled()) {
            HyperLinkLabel.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (listener != null) {
                String cmd = command;
                if (cmd == null) {
                    cmd = getText();
                }
                listener.actionPerformed(new ActionEvent(HyperLinkLabel.this, id, cmd));
            }
        }
    }
    public void mouseEntered(MouseEvent e) {
        if (isEnabled()) {
            HyperLinkLabel.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    public void mouseExited(MouseEvent e) {
        HyperLinkLabel.this.setCursor(Cursor.getDefaultCursor());
    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void setColor(Color color)
    {
    	this.color = color;
    	setForeground(color);
    }
    /**
     * Paint under line text
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Font font = getFont();
        Graphics2D g2d = ( Graphics2D) g;
        Rectangle2D box = font.getStringBounds(getText(), g2d.getFontRenderContext());
        int startX = 0;
        if (getHorizontalAlignment() == JLabel.RIGHT) 
            startX = getSize().width - box.getBounds().width;
        Point startPoint = new Point(startX, box.getBounds().height - 3);
        Point endPoint = new Point(startPoint);
        endPoint.translate(box.getBounds().width, 0);
        g.setColor(color);
        g.drawLine( startPoint.x, startPoint.y, endPoint.x, endPoint.y);
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }


}
