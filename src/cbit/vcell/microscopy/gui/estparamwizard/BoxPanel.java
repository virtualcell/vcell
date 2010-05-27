package cbit.vcell.microscopy.gui.estparamwizard;

import javax.swing.*;
import javax.swing.plaf.metal.MetalInternalFrameUI;
import javax.swing.border.Border;

import cbit.vcell.microscopy.gui.VirtualFrapLoader;

import java.awt.*;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

/**
 * Place holder panel for sheet view report. <p>
 * This has minimizable, maximizable, closable button focus hightlight border.
 */
public abstract class BoxPanel extends JPanel {

    /**
     * Title
     */
    protected String title;

    /**
     * Panel for button
     */
    protected JPanel gluePanel;

    /**
     * Content panel
     */
    protected JPanel contentPane;

    private boolean isSelected = false;

    public BoxPanel(String title) {
        this.title = title;
        setOpaque(false);
        setBorder(new BoxBorder());
        JPanel titlePanel = new JPanel(new BorderLayout());
        gluePanel = new JPanel();
        gluePanel.setLayout(new BoxLayout(gluePanel, BoxLayout.X_AXIS));
        gluePanel.add(Box.createHorizontalGlue());
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 13));

        JPanel linePanel = new LinePanel();

        titlePanel.add(lblTitle, BorderLayout.WEST);
        titlePanel.add(gluePanel, BorderLayout.CENTER);
        titlePanel.add(linePanel, BorderLayout.SOUTH);

        BorderFactory.createEmptyBorder(1,1,1,1);

        setLayout(new BorderLayout());
        contentPane = new JPanel();
        super.add(titlePanel, BorderLayout.NORTH);
        super.add(contentPane, BorderLayout.CENTER);
    }

    private class LinePanel extends JPanel {
        public LinePanel() {
            setBorder(new Border() {
                public boolean isBorderOpaque() {
                    return true;
                }

                public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                    g.setColor(Color.red);
                    g.fillRect(0, 2, width, 1);
                }

                public Insets getBorderInsets(Component c) {
                    return lineBorderInset;
                }
            });
        }
    }
    private static Insets lineBorderInset = new Insets(0,0,1,0);

    public Component add(Component comp) {
        throw new InternalError("Use getContentPane.add(comp) instead.");
    }

    public JPanel getContentPane() {
        return contentPane;
    }

    public void setContentPane(JPanel contentPane) {
        this.contentPane = contentPane;
    }

    public class BoxBorder implements Border {


        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            if (isSelected) {
                g.setColor(borderColor);
                g.drawRect(2, 2, width-5, height-5);
            }
        }

        public Insets getBorderInsets(Component c) {
            return borderInset;
        }

    }
    final static private Insets borderInset = new Insets(5, 5, 5, 5);
    final static private Color borderColor = new Color(166, 166, 255);
}
