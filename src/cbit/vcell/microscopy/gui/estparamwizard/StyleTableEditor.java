package cbit.vcell.microscopy.gui.estparamwizard;



import javax.swing.*;
import java.awt.*;


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
