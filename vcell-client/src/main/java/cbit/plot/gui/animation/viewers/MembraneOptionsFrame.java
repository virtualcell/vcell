/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cbit.plot.gui.animation.viewers;

import cbit.plot.gui.animation.helpersetup.Colors;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MembraneOptionsFrame extends JFrame implements ActionListener {
    
    private JButton okButton;
    
    public MembraneOptionsFrame(Membrane membrane){
        super("Edit Membrane");
        
        Container c = this.getContentPane();
        c.add(buildBottomPanel(), "South");
        c.add(buildCenterPanel(membrane), "Center");
        
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
    private JPanel buildCenterPanel(Membrane membrane){
        MembraneOptionsTableModel model = new MembraneOptionsTableModel(membrane);
        
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        
        JComboBox box = new JComboBox(Colors.COLORNAMES);
        TableColumn colorColumn = table.getColumnModel().getColumn(1);
        colorColumn.setCellEditor(new DefaultCellEditor(box));
        
        JScrollPane pane = new JScrollPane(table);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(pane);
        
        return p;
    }
    
    /* ************ ACTION LISTENER ***************************************/
    @Override
    public void actionPerformed(ActionEvent event){
        this.setVisible(false);
        this.dispose();
    }
    
}
