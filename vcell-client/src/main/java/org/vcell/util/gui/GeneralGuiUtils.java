package org.vcell.util.gui;

import com.sun.istack.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.KeyStroke;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class GeneralGuiUtils {
    private static final Logger lg = LogManager.getLogger(GeneralGuiUtils.class);

    public static final KeyStroke CLOSE_WINDOW_KEY_STROKE;

    static{ //allow initialization in headless environment
        KeyStroke ks;
        try {   // TODO: Try with getMenuShortcutKeyMaskEx()
            ks = KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        } catch(HeadlessException he){
            ks = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK);
        }
        CLOSE_WINDOW_KEY_STROKE = ks;
    }

    public static void addCloseWindowKeyboardAction(JComponent jComponent){
        //@SuppressWarnings("serial")
        Action winCloseAction = new AbstractAction() {
            @Override
            public synchronized void actionPerformed(ActionEvent e){
                Window window;
                if(e.getSource() == null) return;
                if(e.getSource() instanceof Window win){
                    window = win;
                } else if(e.getSource() instanceof Component comp){
                    window = (Window) GeneralGuiUtils.findTypeParentOfComponent(comp, Window.class);
                } else {
                    window = null;
                }

                if(window != null){
                    window.dispatchEvent(new WindowEvent(window, java.awt.event.WindowEvent.WINDOW_CLOSING));
                }
            }
        };

        final String winCloseInputMapAction = "winCloseAction";
        InputMap inputMap = jComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        if(inputMap.get(CLOSE_WINDOW_KEY_STROKE) != null) return;
        inputMap.put(CLOSE_WINDOW_KEY_STROKE, winCloseInputMapAction);
        jComponent.getActionMap().put(winCloseInputMapAction, winCloseAction);
    }

    public static void attemptMaximize(Frame frame){
        Class<? extends Frame> clazz = frame.getClass();
        Method method = GeneralGuiUtils.getMethod(clazz);
        if(method == null) return;
        Field field = GeneralGuiUtils.getField(clazz);
        if(field == null) return;
        try {
            method.invoke(frame, field.get(frame));
        } catch(Exception e){
            lg.error(e.getMessage(), e);
        }
    }

    private static Method getMethod(Class<? extends Frame> clazz){
        for(Method method : clazz.getMethods()){
            if(!method.getName().equals("setExtendedState")) continue;
            return method;
        }
        return null;
    }

    private static Field getField(Class<? extends Frame> clazz){
        // TODO: Check if this can be returned in-loop
        Field result = null;
        for(Field field : clazz.getFields()){
            if(!field.getName().equals("MAXIMIZED_BOTH")) continue;
            result = field;
        }
        return result;
    }

    public static void centerOnComponent(Window window, Component reference){
        if(window == null) return;

        if(reference == null){
            if(lg.isWarnEnabled()) lg.warn("reference=null");
            window.setLocation(0, 0);
            return;
        }

        try {
            Point pR = reference.getLocationOnScreen();
            pR.x += Math.max((reference.getWidth() - window.getWidth()) / 2, 0);
            pR.y += Math.max((reference.getHeight() - window.getHeight()) / 2, 0);
            window.setLocation(pR);
        } catch(Exception e){
            centerOnScreen(window);
        }
    }

    public static void centerOnScreen(Window window){
        if(window == null) return;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final Rectangle maxBounds = ge.getMaximumWindowBounds();
        Dimension screenSize = maxBounds.getSize();
        Dimension size = window.getSize();
        if(size.height > screenSize.height) size.height = screenSize.height;
        if(size.width > screenSize.width) size.width = screenSize.width;
        window.setLocation((screenSize.width - size.width) / 2, (screenSize.height - size.height) / 2);
    }

    public static Line2D clipLine(Line2D line, Rectangle2D rectangle){
        if(line == null || rectangle == null || !line.intersects(rectangle)) return null;
        // Question: Does this code work with a rectangle that is rotated such that is it not orthogonal to the axes?
        // All comments assume non-rotated rectangle
        Point2D startOfLine = line.getP1();
        Point2D endOfLine = line.getP2();
        Line2D bottomBorder = new Line2D.Double(rectangle.getMinX(), rectangle.getMinY(), rectangle.getMaxX(), rectangle.getMinY());
        Line2D rightBorder = new Line2D.Double(rectangle.getMaxX(), rectangle.getMinY(), rectangle.getMaxX(), rectangle.getMaxY());
        Line2D topBorder = new Line2D.Double(rectangle.getMaxX(), rectangle.getMaxY(), rectangle.getMinX(), rectangle.getMaxY());
        Line2D leftBorder = new Line2D.Double(rectangle.getMinX(), rectangle.getMaxY(), rectangle.getMinX(), rectangle.getMinY());
        // we'll do them counter-clockwise, starting with 'origin' in bottom left
        Line2D[] sides = new Line2D[]{bottomBorder, rightBorder, topBorder, leftBorder};

        // Let's bind some common calculations for multiple uses and readability


        if(startOfLine.getX() >= rectangle.getX() &&
                startOfLine.getY() >= rectangle.getY() &&
                startOfLine.getX() <= rectangle.getX() + rectangle.getWidth() &&
                startOfLine.getY() <= rectangle.getY() + rectangle.getHeight() &&
                endOfLine.getX() >= rectangle.getX() &&
                endOfLine.getY() >= rectangle.getY() &&
                endOfLine.getX() <= rectangle.getX() + rectangle.getWidth() &&
                endOfLine.getY() <= rectangle.getY() + rectangle.getHeight()){
            return line;
        }

        if(startOfLine.getX() != endOfLine.getX() && startOfLine.getY() != endOfLine.getY()){
            // lines parallel to the rectangle will get separate treatment
            if(startOfLine.getX() >= rectangle.getX() &&
                    startOfLine.getY() >= rectangle.getY() &&
                    startOfLine.getX() <= rectangle.getX() + rectangle.getWidth() &&
                    startOfLine.getY() <= rectangle.getY() + rectangle.getHeight()){
                // clip from startOfLine to intersection with one border
                for(Line2D border : sides){
                    if(!line.intersectsLine(border)) continue;
                    line.setLine(startOfLine, GeneralGuiUtils.lineIntersection(line, border));
                }
                return line;
            } else if(endOfLine.getX() >= rectangle.getX() &&
                    endOfLine.getY() >= rectangle.getY() &&
                    endOfLine.getX() <= rectangle.getX() + rectangle.getWidth() &&
                    endOfLine.getY() <= rectangle.getY() + rectangle.getHeight()){
                // do it again in reverse
                line.setLine(endOfLine, startOfLine);
                return GeneralGuiUtils.clipLine(line, rectangle);
            } else {
                // we must intersect at least 2 sides (3 or 4 if we go through corner(s))
                // TODO: Evaluate if returns could improve this else-scoped section
                Point2D p01 = null;
                Point2D p02 = null;
                for(Line2D border : sides){
                    if(!line.intersectsLine(border)) continue;
                    if(p01 == null){
                        p01 = GeneralGuiUtils.lineIntersection(line, border);
                    } else if(p02 == null || p02.equals(p01)){
                        // If we don't have p02 yet, or p02 is p01, we have to keep searching. Otherwise,
                        // it's the one we want, and we keep it. We check for p01 equality so don't risk overwriting
                        // p02 (again) with p01 if it turns out we're just finding p01 again as a corner
                        p02 = GeneralGuiUtils.lineIntersection(line, border);
                    }
                }
                line.setLine(p01, p02);
                return line;
            }
        } else if(startOfLine.getX() == endOfLine.getX()){
            // vertical line with one point within rectangle
            if(startOfLine.getX() >= rectangle.getX() &&
                    startOfLine.getY() >= rectangle.getY() &&
                    startOfLine.getX() <= rectangle.getX() + rectangle.getWidth() &&
                    startOfLine.getY() <= rectangle.getY() + rectangle.getHeight()){
                if(endOfLine.getY() < rectangle.getMinY()){
                    // endOfLine is above rectangle
                    endOfLine.setLocation(endOfLine.getX(), rectangle.getMinY());
                } else {
                    // endOfLine is below rectangle
                    endOfLine.setLocation(endOfLine.getX(), rectangle.getMaxY());
                }
                line.setLine(startOfLine, endOfLine);
                return line;
            } else {
                // must contain endOfLine
                // do it again in reverse
                line.setLine(endOfLine, startOfLine);
                return GeneralGuiUtils.clipLine(line, rectangle);
            }
        } else if(startOfLine.getX() == endOfLine.getX() && startOfLine.getY() == endOfLine.getY()){
            // single point within or on border of rectangle
            return line;
        } else {
            // horizontal line with one point within rectangle
            if(startOfLine.getX() >= rectangle.getX() &&
                    startOfLine.getY() >= rectangle.getY() &&
                    startOfLine.getX() <= rectangle.getX() + rectangle.getWidth() &&
                    startOfLine.getY() <= rectangle.getY() + rectangle.getHeight()){
                if(endOfLine.getX() < rectangle.getMinX()){
                    // endOfLine is to the left of rectangle
                    endOfLine.setLocation(rectangle.getMinX(), endOfLine.getY());
                } else {
                    // endOfLine is to the right of rectangle
                    endOfLine.setLocation(rectangle.getMaxX(), endOfLine.getY());
                }
                line.setLine(startOfLine, endOfLine);
                return line;
            } else {
                // must contain endOfLine
                // do it again in reverse
                line.setLine(endOfLine, startOfLine);
                return GeneralGuiUtils.clipLine(line, rectangle);
            }
        }
    }

    public static Point2D lineIntersection(Line2D l1, Line2D l2){
        if(l1 == null || l2 == null || !l1.intersectsLine(l2)) return null;
        double a1 = (l1.getY2() - l1.getY1()) / (l1.getX2() - l1.getX1());
        double b1 = (l1.getX2() * l1.getY1() - l1.getX1() * l1.getY2()) / (l1.getX2() - l1.getX1());
        double a2 = (l2.getY2() - l2.getY1()) / (l2.getX2() - l2.getX1());
        double b2 = (l2.getX2() * l2.getY1() - l2.getX1() * l2.getY2()) / (l2.getX2() - l2.getX1());
        if(l1.getX1() == l1.getX2() && l2.getX1() == l2.getX2()){
            return new Point2D.Double(Double.NaN, Double.NaN);
        } else if(l1.getX1() == l1.getX2()){
            return new Point2D.Double(l1.getX1(), a2 * l1.getX1() + b2);
        } else if(l2.getX1() == l2.getX2()){
            return new Point2D.Double(l2.getX2(), a1 * l2.getX2() + b1);
        } else {
            return new Point2D.Double((b1 - b2) / (a2 - a1), a1 * (b1 - b2) / (a2 - a1) + b1);
        }
    }

    /**
     * @param disposableChild the child of the parent to dispose of.
     * @deprecated should not have to do this
     */
    public static void disposeParentWindow(Component disposableChild){
        Container container;
        if((container = GeneralGuiUtils.findTypeParentOfComponent(disposableChild, JDialog.class)) != null){
            ((JDialog) container).dispose();
        } else if((container = GeneralGuiUtils.findTypeParentOfComponent(disposableChild, JFrame.class)) != null){
            ((JFrame) container).dispose();
        } else {
            String messageFormat = "%s.disposeParentWindow(Component) only handles JFrame parents";
            throw new IllegalArgumentException(String.format(messageFormat, GeneralGuiUtils.class.getName()));
        }
    }

    /**
     * return Container of specified type that is either the input component, or a AWT parent
     * of the input component
     *
     * @param component, may be null
     * @param parentType not null
     * @return component, ancestor of component, or null
     */
    // TODO: see if we can make this generic so it returns the type it looks for?
    public static Container findTypeParentOfComponent(Component component, @NotNull Class<?> parentType){
        Container comp = (component == null || component instanceof Container) ? (Container) component : component.getParent();
        for(Container p = comp; p != null; p = p.getParent()){
            if(!parentType.isAssignableFrom(p.getClass())) continue;
            return p;
        }
        return null;
    }

    public static void enableComponents(Container container, boolean b){
        for(Component component : GeneralGuiUtils.getComponent(container)){
            component.setEnabled(b);
            if(component instanceof Container cont){
                GeneralGuiUtils.enableComponents(cont, b);
            }
        }
    }

    public static void printComponentInfo(Container container){
        for(Component component : GeneralGuiUtils.getComponent(container)){
            StringBuilder message = new StringBuilder("\n");
            message.append(component).append("\n\t");
            message.append("Preferred Size: ").append(component.getPreferredSize()).append("\n\t");
            message.append("Actual Size: ").append(component.getSize()).append("\n\t");
            if(lg.isInfoEnabled()) lg.info(message.toString());
            if(component instanceof Container cont){
                GeneralGuiUtils.printComponentInfo(cont);
            }
        }
    }

    //TODO: Change to `List`
    //TODO: see if we can make `Class<? extends Component>` happen
    public static void findComponent(Container start, Class<?> findThis, ArrayList<Component> componentList){
        for(Component component : GeneralGuiUtils.getComponent(start)){
            if(component.getClass() == findThis) componentList.add(component);
            if(component instanceof Container cont) GeneralGuiUtils.findComponent(cont, findThis, componentList);
        }
    }

    private static Component[] getComponent(Container container){
        if(container instanceof JMenu menu) return menu.getMenuComponents();
        return container.getComponents();
    }

    // TODO: Determine if this is necessary
    public static void setCursorThroughout(final Container container, final Cursor cursor){
        if(container == null) return;
        container.setCursor(cursor);
    }
}

    
