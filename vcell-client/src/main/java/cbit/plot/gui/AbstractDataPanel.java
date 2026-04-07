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
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public abstract class AbstractDataPanel extends JPanel {

    protected static final Logger LG = LogManager.getLogger(AbstractDataPanel.class);

    protected ScrollTable scrollPaneTable;
    protected NonEditableDefaultTableModel nonEditableDefaultTableModel = null;

    protected JPopupMenu popupMenu = null;
    protected JMenuItem miCopyAll = null;
    protected JMenuItem miCopyHDF5 = null;

    protected enum CopyAction { copy, copyrow, copyall }

    protected final AbstractDataPanel.IvjEventHandler ivjEventHandler = new AbstractDataPanel.IvjEventHandler();

    // ------------------------------------------------------------
    // Event handler (shared)
    // ------------------------------------------------------------
    protected class IvjEventHandler implements ActionListener, MouseListener, PropertyChangeListener, ChangeListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() == getScrollPaneTable()) {
                int row = getScrollPaneTable().rowAtPoint(e.getPoint());
                int col = getScrollPaneTable().columnAtPoint(e.getPoint());
                LG.debug(getClass().getSimpleName() + ": clicked row=" + row + " col=" + col);

                if (SwingUtilities.isRightMouseButton(e)) {
                    getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                }

                onMouseClick(row, col, e);
            }
        }

        @Override public void mousePressed(MouseEvent e) {}
        @Override public void mouseReleased(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}

        @Override
        public void actionPerformed(ActionEvent e) {
            // Reserved for future shared actions
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            onPropertyChange(evt);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            onStateChanged(e);
        }
    }

    // ------------------------------------------------------------
    // Header renderer wrapper (shared)
    // ------------------------------------------------------------
    protected class GenericHeaderRenderer extends DefaultTableCellRenderer {

        private final TableCellRenderer base;

        public GenericHeaderRenderer(TableCellRenderer base) {
            this.base = base;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = base.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!(c instanceof JLabel)) {
                return c;
            }
            return c;
        }
    }

    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------
    public AbstractDataPanel() {
        super();
        initialize();
    }

    // ------------------------------------------------------------
    // Initialization
    // ------------------------------------------------------------
    private void initialize() {
        try {
            setLayout(new BorderLayout());
            add(getScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);

            JLabel footer = new JLabel(getFooterLabelText());
            add(footer, BorderLayout.SOUTH);

            initConnections();
        } catch (Throwable exc) {
            handleException(exc);
        }
    }

    protected String getFooterLabelText() {
        return "<html>To <b>Copy</b> table data or <b>Export</b> as HDF5, select rows/cells and use the right mouse button menu.</html>";
    }

    protected void initConnections() throws Exception {
        this.addPropertyChangeListener(ivjEventHandler);
        getScrollPaneTable().addMouseListener(ivjEventHandler);

        getScrollPaneTable().setModel(getNonEditableDefaultTableModel());
        getScrollPaneTable().createDefaultColumnsFromModel();

        TableCellRenderer baseHeaderRenderer = getScrollPaneTable().getTableHeader().getDefaultRenderer();
        getScrollPaneTable().getTableHeader().setDefaultRenderer(new GenericHeaderRenderer(baseHeaderRenderer));
    }

    protected void handleException(Throwable exception) {
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    // ------------------------------------------------------------
    // Lazy getters
    // ------------------------------------------------------------
    protected ScrollTable getScrollPaneTable() {
        if (scrollPaneTable == null) {
            try {
                scrollPaneTable = new ScrollTable();
                scrollPaneTable.setCellSelectionEnabled(true);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return scrollPaneTable;
    }

    protected NonEditableDefaultTableModel getNonEditableDefaultTableModel() {
        if (nonEditableDefaultTableModel == null) {
            try {
                nonEditableDefaultTableModel = new NonEditableDefaultTableModel();
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return nonEditableDefaultTableModel;
    }

    protected JPopupMenu getPopupMenu() {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();

            miCopyAll = new JMenuItem("Copy All");
            miCopyAll.addActionListener(e -> onCopyCells(false));
            popupMenu.add(miCopyAll);

            miCopyHDF5 = new JMenuItem("Copy to HDF5");
            miCopyHDF5.setEnabled(false);
            miCopyHDF5.addActionListener(e -> onCopyCells(true));
            popupMenu.add(miCopyHDF5);
        }
        return popupMenu;
    }

    // ------------------------------------------------------------
    // Hooks for subclasses
    // ------------------------------------------------------------
    protected void onCopyCells(boolean isHDF5) {
        // Subclasses override if needed
    }

    protected void onMouseClick(int row, int col, MouseEvent e) {
        // Subclasses override if needed
    }

    protected void onPropertyChange(PropertyChangeEvent evt) {
        // Subclasses override if needed
    }

    protected void onStateChanged(ChangeEvent e) {
        // Subclasses override if needed
    }
}