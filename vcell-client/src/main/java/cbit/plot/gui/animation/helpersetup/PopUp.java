/**
 *                      CLASS POPUP
 *  The JOptionPane messages require 3 or 4 inputs and take up a lot of space
 *  in my code, but for the most part only the message changes.  I'm going
 *  to provide my own implementations which only take a string as the 
 *  parameter.
 */

package cbit.plot.gui.animation.helpersetup;

import javax.swing.*;

public class PopUp {
    
    public static void error(String message){
        JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void warning(String message){
        JOptionPane.showMessageDialog(null, message, "WARNING", JOptionPane.WARNING_MESSAGE);
    }
    
    public static void information(String message){
        JOptionPane.showMessageDialog(null, message, "INFO MESSAGE", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static int errorWithOption(String message){
        return JOptionPane.showConfirmDialog(null, message, "ERROR", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
    }
    
    public static int doubleCheck(String message){
        return JOptionPane.showConfirmDialog(null, message, "CONFIRM SELECTION", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }
    
    public static String input(String message){
        return JOptionPane.showInputDialog(null, message, "INPUT", JOptionPane.QUESTION_MESSAGE);
    }
    
    public static String input(String message, String initialValue){
        return JOptionPane.showInputDialog(message, initialValue);
    }
    
}
