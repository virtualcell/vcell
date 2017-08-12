/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui.estparamwizard;



import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class StyleTableEditor extends JTextField {

    public StyleTableEditor() {
        setBorder(BorderFactory.createEmptyBorder(1, 5, 1,1));
    }

    public Component getListCellRendererComponent(JList list,
                Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.toString());
        if (isSelected)
            setBackground(Color.lightGray);
        else
            setBackground(Color.white);
        return this;
    }

    public Component getEditorComponent() {
        return this;
    }

    public Object getItem() {
        return getText();
    }

    public void setItem(Object anObject) {
        setText(anObject.toString());
    }
}
