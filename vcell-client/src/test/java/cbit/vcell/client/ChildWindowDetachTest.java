package cbit.vcell.client;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.vcell.client.logicalwindow.LWTopFrame;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A detached child window must become an ordinary un-owned top level window - one the OS will
 * give a taskbar button and let the user minimise - while carrying its contents across intact.
 *
 * Background: modeless child windows were made OWNED dialogs in ec0ed8478a to fix them hiding
 * behind the document window, since an un-owned frame can only be raised with a best-effort
 * toFront() that macOS 13.3+ and Windows foreground-lock refuse. The accepted price was no
 * taskbar button and no way to get the window out of the way, which is what users hit on a
 * small screen. Detaching pays that price back on demand.
 *
 * What matters and is asserted here:
 *   - attached   => a Dialog, owned (cannot be minimised; OS keeps it above its owner)
 *   - detached   => a Frame, un-owned (minimisable, independently stackable)
 *   - the caller's content pane is the SAME component afterwards, not a rebuilt one
 *   - position and size survive, so the window does not jump out from under the mouse
 */
@Tag("Fast")
@DisabledIf("isHeadless")
public class ChildWindowDetachTest {

    static boolean isHeadless() {
        return GraphicsEnvironment.isHeadless();
    }

    @SuppressWarnings("serial")
    private static class TestTopFrame extends LWTopFrame {
        @Override
        public String menuDescription() {
            return "test top frame";
        }
    }

    /** runs the body on the EDT and rethrows whatever it threw */
    private static void onEdt(ThrowingRunnable body) throws Exception {
        AtomicReference<Exception> failure = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            try {
                body.run();
            } catch (Exception e) {
                failure.set(e);
            }
        });
        if (failure.get() != null) {
            throw failure.get();
        }
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    @Test
    public void detachGivesAnUnownedFrameAndKeepsTheContents() throws Exception {
        onEdt(() -> {
            TestTopFrame top = new TestTopFrame();
            top.setBounds(50, 50, 400, 300);
            ChildWindowManager cwm = new ChildWindowManager(top);

            JPanel viewer = new JPanel();
            viewer.add(new JLabel("pretend simulation results"));
            ChildWindowManager.ChildWindow child = cwm.addChildWindow(viewer, new Object(), "Results");
            child.setSize(320, 240);
            child.show();

            // ---- attached: an owned dialog, which is why it cannot be minimised
            Window attached = SwingUtilities.getWindowAncestor(viewer);
            assertNotNull(attached, "child window should be realized");
            assertTrue(attached instanceof Dialog,
                    "attached child should be a Dialog, was " + attached.getClass().getName());
            assertSame(top, attached.getOwner(), "attached child should be owned by the document window");
            assertFalse(child.isDetached());

            attached.setBounds(new Rectangle(120, 140, 360, 260));
            Rectangle before = attached.getBounds();

            // ---- detach
            child.setDetached(true);
            assertTrue(child.isDetached());

            Window detached = SwingUtilities.getWindowAncestor(viewer);
            assertNotNull(detached, "detached child should still be realized");
            assertTrue(detached instanceof Frame,
                    "detached child must be a Frame so the OS gives it a taskbar button and lets "
                            + "the user minimise it, was " + detached.getClass().getName());
            assertEquals(null, detached.getOwner(),
                    "detached child must be un-owned, otherwise the OS still pins it above the "
                            + "document window - the whole point of detaching");

            // the viewer itself was carried across, not rebuilt
            assertSame(viewer, findLabelHolder(detached),
                    "the caller's content pane must be the same component after detaching");
            assertEquals(before, detached.getBounds(),
                    "detaching must not move or resize the window");

            // ---- and back again
            child.setDetached(false);
            assertFalse(child.isDetached());
            Window reattached = SwingUtilities.getWindowAncestor(viewer);
            assertTrue(reattached instanceof Dialog, "reattaching should restore an owned dialog");
            assertSame(top, reattached.getOwner());
            assertEquals(before, reattached.getBounds(), "reattaching must not move the window either");

            child.close();
            top.dispose();
        });
    }

    private static java.awt.Component findLabelHolder(Window w) {
        return find(w, JPanel.class);
    }

    private static java.awt.Component find(java.awt.Container c, Class<?> type) {
        for (java.awt.Component comp : c.getComponents()) {
            if (type.isInstance(comp) && comp instanceof JPanel
                    && ((JPanel) comp).getComponentCount() == 1
                    && ((JPanel) comp).getComponent(0) instanceof JLabel) {
                return comp;
            }
            if (comp instanceof java.awt.Container) {
                java.awt.Component found = find((java.awt.Container) comp, type);
                if (found != null) return found;
            }
        }
        return null;
    }
}
