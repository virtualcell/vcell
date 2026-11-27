package org.vcell.util.gui;

import cbit.vcell.client.laf.VCellLookAndFeel;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.vcell.client.logicalwindow.LWContainerHandle;
import org.vcell.util.CastingUtils;

@SuppressWarnings("serial")

public class IndefiniteProgressDialog extends ProgressDialog implements ActionListener {
    private final static int INITIAL_UPDATE_TIME_MILLIS = 40;    // update time, milliseconds
    private final static int SHOW_DELAY_TIME_MILLIS = 1000;

    private JLabel lblMessage;
    private javax.swing.Timer displayTimer;
    private javax.swing.Timer workingPanelTimer;

    public IndefiniteProgressDialog(LWContainerHandle owner){
        super(owner);
        setLocationRelativeTo(null);
        JPanel contentPane = new JPanel();
        contentPane.setBackground(VCellLookAndFeel.getContentBackground()); // as the message's, and behind the wheel
        setContentPane(contentPane);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

        displayTimer = new Timer(SHOW_DELAY_TIME_MILLIS, this);
        workingPanelTimer = new Timer(INITIAL_UPDATE_TIME_MILLIS, null);
        JPanel graphicPanel = new WorkingPanel(workingPanelTimer);
        Dimension ps = new Dimension(GRAPHIC_SIZE, GRAPHIC_SIZE);
        graphicPanel.setPreferredSize(ps);
        getContentPane().add(graphicPanel);                        // left

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        ps = new Dimension(DialogWidth, GRAPHIC_SIZE);
        rightPanel.setPreferredSize(ps);
        getContentPane().add(rightPanel);

        JPanel msgPanel = new JPanel();
        msgPanel.setBackground(VCellLookAndFeel.getContentBackground());
        rightPanel.add(msgPanel);

        JPanel cancelPanel = new JPanel();
        cancelPanel.setBackground(VCellLookAndFeel.getContentBackground());
        rightPanel.add(cancelPanel);

        lblMessage = new JLabel("Message");
        msgPanel.add(lblMessage);
        lblMessage.setHorizontalAlignment(SwingConstants.LEFT);
        JButton cButton = super.getCancelButton();
        cancelPanel.add(cButton);

        pack();
    }

    @Override
    public void setToVisible(){
        if(!isVisible()){
            displayTimer.start();
        }
    }

    @Override
    public void setVisible(boolean b){
        displayTimer.stop();
        super.setVisible(b);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(!isVisible()){
            displayTimer.stop();
            super.setVisible(true);
        }
    }

    @Override
    public void dispose(){
        workingPanelTimer.stop();
        super.dispose();
        if(LG.isDebugEnabled()){
            LG.debug(getClass().getName() + " " + message + " disposed");
        }

    }

    /**
     * @throws UnsupportedOperationException (always)
     */
    @Override
    public void setProgress(int progress){
        throw new UnsupportedOperationException(getClass().getName() + " does not support setProgress");
    }

    @Override
    void setProgressBarString(String progressString){
        setTitle(progressString);
    }

    @Override
    public void setMessageImpl(String s){

//		DocumentWindow dw = getMainFrame();
//		if(dw != null) {
//			Runnable r = new StatusBarMessageThread(dw, s);
//			new Thread(r).start();
//		}

        int len = s.length();
        if(len > MaxLen){
            s = s.substring(0, TruncHeaderLen) + ".." + s.substring(len - TruncTailLen, len);
        }
        lblMessage.setText(s);
    }

    // The loading wheel, turning a step at each tick of its timer while no other modal dialog is up; on a clear
    // background, so the dialog's shows through it whatever the theme
    private class WorkingPanel extends JPanel implements ComponentListener, ActionListener {
        final Timer swingTimer;
        final LoadingWheel wheel = new LoadingWheel(GRAPHIC_SIZE);

        WorkingPanel(Timer t){
            swingTimer = t;
            swingTimer.addActionListener(this);
            swingTimer.setRepeats(true);
            swingTimer.start();
            this.setOpaque(false);
            this.addComponentListener(this);
        }

        @Override
        protected void paintComponent(Graphics g){
            this.wheel.paintIcon(this, g, (getWidth() - GRAPHIC_SIZE) / 2, (getHeight() - GRAPHIC_SIZE) / 2);
        }

        @Override
        public void actionPerformed(ActionEvent arg0){
            if(!isModelDialogPresent()){
                this.wheel.turn();
                repaint();
            }
        }

        /**
         * @return true if visible dialog is modal
         */
        private boolean isModelDialogPresent(){
            for(Window w : Window.getWindows()){
                if(w.isShowing()){
                    Dialog d = CastingUtils.downcast(Dialog.class, w);
                    if(d != null && d != IndefiniteProgressDialog.this){
                        switch(d.getModalityType()){
                            case MODELESS:
                                continue;
                            default:
                                return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void componentHidden(ComponentEvent arg0){
            swingTimer.stop();
        }

        @Override
        public void componentMoved(ComponentEvent arg0){
        }

        @Override
        public void componentResized(ComponentEvent arg0){
        }

        @Override
        public void componentShown(ComponentEvent arg0){
            swingTimer.start();
        }
    }
}
