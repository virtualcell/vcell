
package cbit.plot.gui.animation.viewers;

import cbit.plot.gui.animation.helpersetup.Colors;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AxesOptionsFrame extends JFrame implements ActionListener {
    
    private JButton okButton;
    
    public AxesOptionsFrame(Axes axes){
        super("Edit Axes Properties");
        Container c = this.getContentPane();
        
        c.add(buildBottomPanel(), "South");
        c.add(buildCenterPanel(axes), "Center");
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }
    
    /* ************  BUILD THE BOTTOM PANEL ****************************/
    private JPanel buildBottomPanel(){
        okButton = new JButton("Finished");
        okButton.addActionListener(this);
        JPanel p = new JPanel();
        p.add(okButton);
        return p;
    }
    
    /* ************* BUILD THE MIDDLE PANEL ****************************/
    private JPanel buildCenterPanel(Axes axes){
        AxesOptionsTableModel model = new AxesOptionsTableModel(axes);
        
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        
        JComboBox box = new JComboBox(Colors.COLORNAMES);
        TableColumn colorColumn = table.getColumnModel().getColumn(5);
        colorColumn.setCellEditor(new DefaultCellEditor(box));
        
        JScrollPane pane = new JScrollPane(table);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(pane);
        
        return p;
    }
    
    @Override
    public void actionPerformed(ActionEvent event){
        this.setVisible(false);
        this.dispose();
    }
}
