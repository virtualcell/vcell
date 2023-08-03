/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cbit.plot.gui.animation.viewers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProgressWindow extends JFrame implements ActionListener {
    
    private final JProgressBar progressBar = new JProgressBar();
    private final JButton cancel = new JButton("Cancel Image Loading");
    private final JLabel progressLabel;
    
    private boolean progressCanceled = false;
    
    private int loaded = 0;
    private final int total;
    
    
    public ProgressWindow(int total){
        super("");
        this.total = total;
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Container c = this.getContentPane();
        c.setLayout(new FlowLayout());
        
        JLabel loadingLabel = new JLabel("Loading Image Files", JLabel.CENTER);
        loadingLabel.setPreferredSize(new Dimension(250,30));
        loadingLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        c.add(loadingLabel);
        
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(250,20));
        progressBar.setForeground(Color.GREEN);
        progressBar.setStringPainted(true);
        c.add(progressBar);
        
        progressLabel = new JLabel("Loaded " + loaded + " of " + total + " files.", JLabel.CENTER);
        progressLabel.setPreferredSize(new Dimension(250, 30));
        c.add(progressLabel);
        
        c.add(cancel);
        
        cancel.addActionListener(this);
        
        this.setLocationRelativeTo(null);
        this.setSize(new Dimension(300,200));
        this.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        progressCanceled = true;
        this.setVisible(false);
        this.dispose();
    }
    
    public void setProgress(int value, int filesLoaded){
        progressBar.setValue(value);
        loaded = filesLoaded;
//        System.out.println("Tried to set the progress bar to " + value);
//        System.out.println(progressBar.getValue());
        progressLabel.setText("Loaded " + loaded + " of " + total + " files.");
    }
    
    public boolean wasCanceled(){
        return progressCanceled;
    }
    
    public void finishedProgress(){
//        try{
//            Thread.sleep(4000);
//        }catch(InterruptedException ex){
//            ex.printStackTrace();
//        }
        progressCanceled = false;
        this.setVisible(false);
        this.dispose();
    }
    

}
