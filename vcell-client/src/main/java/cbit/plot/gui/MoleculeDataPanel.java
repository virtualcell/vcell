package cbit.plot.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.gui.NonEditableDefaultTableModel;
import org.vcell.util.gui.ScrollTable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MoleculeDataPanel extends JPanel {

    private static final Logger LG = LogManager.getLogger(MoleculeDataPanel.class);

    private ScrollTable scrollPaneTable;
    private NonEditableDefaultTableModel nonEditableDefaultTableModel = null;

    private JPopupMenu popupMenu = null;
    private JMenuItem miCopyAll = null;
    private JMenuItem miCopyHDF5 = null;
    private static enum CopyAction {copy,copyrow,copyall};

    private final MoleculeDataPanel.IvjEventHandler ivjEventHandler = new MoleculeDataPanel.IvjEventHandler();



    class IvjEventHandler implements ActionListener, MouseListener, PropertyChangeListener, ChangeListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() == getScrollPaneTable()) {
                int row = getScrollPaneTable().rowAtPoint(e.getPoint());
                int col = getScrollPaneTable().columnAtPoint(e.getPoint());
                System.out.println("MoleculeDataPanel: clicked row=" + row + " col=" + col);
                if (SwingUtilities.isRightMouseButton(e)) {
                    getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }

        @Override public void mousePressed(MouseEvent e) {}
        @Override public void mouseReleased(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}

        @Override
        public void actionPerformed(ActionEvent e) {
            // reserved for future
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            // reserved for future dynamic formatting
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            // reserved for future slider/spinner interactions
        }
    }

    private class MoleculeHeaderRenderer extends DefaultTableCellRenderer {

        private final TableCellRenderer base;

        public MoleculeHeaderRenderer(TableCellRenderer base) {
            this.base = base;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            // First let ScrollTable’s renderer do its work
            Component c = base.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!(c instanceof JLabel)) {
                return c;   // safety
            }
            return c;
        }
    }

    public MoleculeDataPanel() {
        super();
        initialize();
    }
    private void initialize() {
        try {
            setName("MoleculeDataPanel");
            setLayout(new java.awt.BorderLayout());
            setSize(541, 348);
            add(getScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);
            JLabel lblNewLabel = new JLabel("<html>To <b>Copy</b> table data or <b>Export</b> as HDF5, select rows/cells and use the right mouse button menu.</html>");
            add(lblNewLabel, BorderLayout.SOUTH);
            initConnections();
//            controlKeys();
        } catch (Throwable exc) {
            handleException(exc);
        }
    }

    private void initConnections() throws java.lang.Exception {
        this.addPropertyChangeListener(ivjEventHandler);
        getScrollPaneTable().addMouseListener(ivjEventHandler);
        getScrollPaneTable().setModel(getNonEditableDefaultTableModel());
        getScrollPaneTable().createDefaultColumnsFromModel();
        TableCellRenderer baseHeaderRenderer = getScrollPaneTable().getTableHeader().getDefaultRenderer();
        getScrollPaneTable().getTableHeader().setDefaultRenderer(new MoleculeDataPanel.MoleculeHeaderRenderer(baseHeaderRenderer));

    }
    private void handleException(java.lang.Throwable exception) {
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    // -----------------------------------------------------------
    private ScrollTable getScrollPaneTable() {
        if (scrollPaneTable == null) {
            try {
                scrollPaneTable = new ScrollTable();
                scrollPaneTable.setName("ScrollPaneTable");
                scrollPaneTable.setCellSelectionEnabled(true);
                scrollPaneTable.setBounds(0, 0, 200, 200);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return scrollPaneTable;
    }

    private JPopupMenu getPopupMenu() {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();

            miCopyAll = new JMenuItem("Copy All");
            miCopyAll.addActionListener(e -> copyCells(this, false));
            popupMenu.add(miCopyAll);

            miCopyHDF5 = new JMenuItem("Copy to HDF5");
            miCopyHDF5.setEnabled(false);       // export to HDF5 code is not working
            miCopyHDF5.addActionListener(e -> copyCells(this,true));
            popupMenu.add(miCopyHDF5);
        }
        return popupMenu;
    }

    private static synchronized void copyCells(MoleculeDataPanel cdp, boolean isHDF5) {

    }
    private NonEditableDefaultTableModel getNonEditableDefaultTableModel() {
        if (nonEditableDefaultTableModel == null) {
            try {
                nonEditableDefaultTableModel = new NonEditableDefaultTableModel();
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return nonEditableDefaultTableModel;
    }

}
