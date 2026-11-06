package org.vcell.client.debug;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Keyboard use of the GUI, for the debug bridge: a window's tab order, audited (controls Tab never reaches, steps out of
 * reading order, stops on hidden components, stops with nothing to read out); and keys sent to a component.
 * <p>
 * Neither needs the window to have the operating system's focus, so both work on a client behind other windows. The tab
 * order is worked out from the window's {@link FocusTraversalPolicy}, the same one Swing follows when Tab is pressed.
 * Keys are dispatched as events inside the client, never as real keystrokes (which would go to whatever application is
 * in front): key bindings (Enter on a default button, Escape, arrows in a list, Space on a button) all run. Tab itself
 * moves real focus only while the window has it; {@code /focusOrder} answers where Tab goes either way.
 */
final class FocusInspector {
	private static final int MAX_STOPS = 1000; // a policy that never cycles back is a bug of its own; stop there

	private FocusInspector() {}

	/** The window's tab order, from its first stop around to the last, with any problems found in it. */
	static String focusOrderJson(String windowSelector) {
		return FocusInspector.onEdt(() -> {
			Window window = FocusInspector.findWindow(windowSelector);
			if (null == window) return "{\"error\":\"no showing window matches '" + FocusInspector.escape(String.valueOf(windowSelector)) + "'\"}";
			List<Component> stops = FocusInspector.tabOrder(window);
			List<String> issues = new ArrayList<>();
			FocusInspector.findUnreachable(window, stops, issues);
			FocusInspector.findOutOfOrder(window, stops, issues);
			for (int index = 0; index < stops.size(); index++) {
				Component stop = stops.get(index);
				if (!stop.isShowing()) issues.add(FocusInspector.issue("hidden", stop, window, "Tab stops on it, but it isn't showing"));
				if (FocusInspector.label(stop).isEmpty()) issues.add(FocusInspector.issue("unnamed", stop, window, "nothing to read out: no text, accessible name or tooltip"));
			}
			StringBuilder json = new StringBuilder("{\"window\":\"").append(FocusInspector.escape(FocusInspector.title(window))).append("\",\"stops\":[");
			for (int index = 0; index < stops.size(); index++) {
				if (index > 0) json.append(',');
				json.append(FocusInspector.node(stops.get(index), window, index));
			}
			json.append("],\"issues\":[").append(String.join(",", issues)).append("],\"summary\":\"")
					.append(stops.size()).append(" stops, ").append(issues.size()).append(issues.size() == 1 ? " issue" : " issues").append("\"}");
			return json.toString();
		});
	}

	/**
	 * Sends a key, as {@link KeyStroke#getKeyStroke(String)} names it ("ENTER", "shift TAB", "ctrl F", "SPACE"), to the
	 * component (or, for none or "focus", to whatever has focus): pressed, typed when it types a character, released.
	 */
	static String sendKeyJson(String selector, String keySpec) {
		KeyStroke keyStroke = null == keySpec ? null : KeyStroke.getKeyStroke(keySpec.trim());
		if (null == keyStroke || KeyEvent.VK_UNDEFINED == keyStroke.getKeyCode()) {
			return "{\"error\":\"unknown key '" + FocusInspector.escape(String.valueOf(keySpec)) + "': name it as KeyStroke does, e.g. ENTER, shift TAB, ctrl F\"}";
		}
		return FocusInspector.onEdt(() -> {
			boolean toFocus = null == selector || selector.isEmpty() || "focus".equals(selector);
			Component target = toFocus ? KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() : SwingInspector.findByPath(selector);
			if (null == target) {
				return toFocus ? "{\"error\":\"nothing has focus (the client isn't the active application): name the component to send the key to\"}"
						: "{\"error\":\"no component matches '" + FocusInspector.escape(selector) + "'\"}";
			}
			int modifiers = keyStroke.getModifiers();
			long now = System.currentTimeMillis();
			char typed = FocusInspector.typedChar(keyStroke.getKeyCode());
			target.dispatchEvent(new KeyEvent(target, KeyEvent.KEY_PRESSED, now, modifiers, keyStroke.getKeyCode(), KeyEvent.CHAR_UNDEFINED));
			if (KeyEvent.CHAR_UNDEFINED != typed) {
				target.dispatchEvent(new KeyEvent(target, KeyEvent.KEY_TYPED, now, modifiers, KeyEvent.VK_UNDEFINED, typed));
			}
			target.dispatchEvent(new KeyEvent(target, KeyEvent.KEY_RELEASED, now, modifiers, keyStroke.getKeyCode(), KeyEvent.CHAR_UNDEFINED));
			Component focus = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
			Window window = SwingUtilities.getWindowAncestor(target);
			return "{\"sent\":\"" + FocusInspector.escape(keyStroke.toString()) + "\",\"to\":\"" + SwingInspector.idFor(target)
					+ "\",\"focusOwner\":" + (null == focus ? "null" : "\"" + SwingInspector.idFor(focus) + "\"")
					+ ",\"windowFocused\":" + (null != window && window.isFocused()) + "}";
		});
	}

	// From the window's first stop, each next one, until back at the first
	private static List<Component> tabOrder(Window window) {
		FocusTraversalPolicy policy = window.getFocusTraversalPolicy();
		List<Component> stops = new ArrayList<>();
		if (null == policy) return stops;
		Component first = policy.getFirstComponent(window);
		Set<Component> seen = Collections.newSetFromMap(new IdentityHashMap<>());
		for (Component stop = first; null != stop && seen.add(stop) && stops.size() < MAX_STOPS; stop = policy.getComponentAfter(window, stop)) {
			stops.add(stop);
		}
		return stops;
	}

	// Controls a mouse user can work, showing in the window, that Tab never reaches
	private static void findUnreachable(Window window, List<Component> stops, List<String> issues) {
		Set<Component> reached = Collections.newSetFromMap(new IdentityHashMap<>());
		reached.addAll(stops);
		Deque<Component> toVisit = new ArrayDeque<>(List.of(window));
		while (!toVisit.isEmpty()) {
			Component component = toVisit.pop();
			if (component instanceof CellRendererPane || component instanceof JPopupMenu || component instanceof JScrollBar) continue;
			if (FocusInspector.isControl(component) && component.isShowing() && component.isEnabled() && !FocusInspector.isReached(component, reached)) {
				issues.add(FocusInspector.issue("unreachable", component, window, "a control Tab never reaches"));
			}
			if (FocusInspector.isControl(component) && !(component instanceof JTabbedPane)) continue; // its parts are its own
			if (component instanceof Container container) for (Component child : container.getComponents()) toVisit.push(child);
		}
	}

	// Reached itself, or (a combo box or spinner, say) through a part of it that takes the focus for it
	private static boolean isReached(Component component, Set<Component> reached) {
		for (Component stop : reached) {
			if (stop == component || SwingUtilities.isDescendingFrom(stop, component)) return true;
		}
		return false;
	}

	private static boolean isControl(Component component) {
		if (component instanceof JMenuItem || component instanceof JMenuBar) return false; // reached through the menu bar
		if (component instanceof JTextComponent text) return text.isEditable();
		return component instanceof AbstractButton || component instanceof JComboBox || component instanceof JList
				|| component instanceof JTree || component instanceof JTable || component instanceof JTabbedPane
				|| component instanceof JSlider || component instanceof JSpinner;
	}

	// A step to somewhere wholly above the last stop, or wholly left of it on the same line: not how a page is read
	private static void findOutOfOrder(Window window, List<Component> stops, List<String> issues) {
		for (int index = 1; index < stops.size(); index++) {
			Rectangle from = FocusInspector.boundsIn(stops.get(index - 1), window), to = FocusInspector.boundsIn(stops.get(index), window);
			if (null == from || null == to) continue;
			boolean up = to.y + to.height <= from.y;
			boolean sameLine = to.y < from.y + from.height && from.y < to.y + to.height;
			boolean left = sameLine && to.x + to.width <= from.x;
			if (!up && !left) continue;
			issues.add("{\"kind\":\"out-of-order\",\"from\":" + (index - 1) + ",\"to\":" + index + ",\"detail\":\"Tab goes "
					+ (up ? "back up" : "left") + " from " + FocusInspector.escape(FocusInspector.describe(stops.get(index - 1))) + " to "
					+ FocusInspector.escape(FocusInspector.describe(stops.get(index))) + "\"}");
		}
	}

	private static String issue(String kind, Component component, Window window, String detail) {
		return "{\"kind\":\"" + kind + "\",\"id\":\"" + SwingInspector.idFor(component) + "\",\"component\":\""
				+ FocusInspector.escape(FocusInspector.describe(component)) + "\",\"detail\":\"" + FocusInspector.escape(detail) + "\"}";
	}

	private static String node(Component component, Window window, int index) {
		Rectangle bounds = FocusInspector.boundsIn(component, window);
		return "{\"index\":" + index + ",\"id\":\"" + SwingInspector.idFor(component) + "\",\"class\":\"" + FocusInspector.className(component)
				+ "\",\"name\":" + (null == component.getName() ? "null" : "\"" + FocusInspector.escape(component.getName()) + "\"")
				+ ",\"label\":\"" + FocusInspector.escape(FocusInspector.label(component)) + "\",\"showing\":" + component.isShowing()
				+ (null == bounds ? "" : ",\"x\":" + bounds.x + ",\"y\":" + bounds.y + ",\"w\":" + bounds.width + ",\"h\":" + bounds.height) + "}";
	}

	// What a screen reader would say for it: its text, else its accessible name, else its tooltip
	private static String label(Component component) {
		String text = component instanceof AbstractButton button ? button.getText()
				: component instanceof JLabel label ? label.getText() : null;
		if (null != text && !text.isBlank()) return text.strip();
		String accessible = null == component.getAccessibleContext() ? null : component.getAccessibleContext().getAccessibleName();
		if (null != accessible && !accessible.isBlank()) return accessible.strip();
		String tip = component instanceof JComponent jComponent ? jComponent.getToolTipText() : null;
		if (null != tip && !tip.isBlank()) return tip.replaceAll("<[^>]*>", " ").strip();
		// a list, tree, table or tab strip is read out by its content; its developer name just says which, here. A text
		// field or drop-down isn't: it needs an accessible name, which a JLabel gives it through setLabelFor.
		if (component instanceof JList || component instanceof JTree || component instanceof JTable || component instanceof JTabbedPane) {
			return null == component.getName() ? "" : component.getName();
		}
		return "";
	}

	private static String describe(Component component) {
		String label = FocusInspector.label(component);
		return FocusInspector.className(component) + (label.isEmpty() ? "" : " '" + label + "'")
				+ (null == component.getName() ? "" : " (" + component.getName() + ")");
	}

	// Its class's name, or (an anonymous subclass, which has none) the nearest named one's
	private static String className(Component component) {
		Class<?> type = component.getClass();
		while (type.getSimpleName().isEmpty()) type = type.getSuperclass();
		return type.getSimpleName();
	}

	private static Rectangle boundsIn(Component component, Window window) {
		if (!component.isShowing()) return null;
		return SwingUtilities.convertRectangle(component.getParent(), component.getBounds(), window);
	}

	private static char typedChar(int keyCode) {
		return switch (keyCode) {
			case KeyEvent.VK_ENTER -> '\n';
			case KeyEvent.VK_SPACE -> ' ';
			case KeyEvent.VK_ESCAPE -> (char) 27;
			case KeyEvent.VK_TAB -> '\t';
			case KeyEvent.VK_BACK_SPACE -> '\b';
			default -> keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z ? Character.toLowerCase((char) keyCode)
					: keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9 ? (char) keyCode : KeyEvent.CHAR_UNDEFINED;
		};
	}

	// The window named so (getName()), else titled so (containing it), else the focused one, else the frontmost showing
	private static Window findWindow(String selector) {
		List<Window> showing = Arrays.stream(Window.getWindows()).filter(Window::isShowing).toList();
		if (null != selector && !selector.isEmpty()) {
			for (Window window : showing) if (selector.equals(window.getName())) return window;
			for (Window window : showing) if (FocusInspector.title(window).contains(selector)) return window;
			return null;
		}
		for (Window window : showing) if (window.isFocused()) return window;
		return showing.isEmpty() ? null : showing.get(showing.size() - 1);
	}

	private static String title(Window window) {
		if (window instanceof Frame frame && null != frame.getTitle()) return frame.getTitle();
		if (window instanceof Dialog dialog && null != dialog.getTitle()) return dialog.getTitle();
		return null == window.getName() ? "" : window.getName();
	}

	private static String escape(String text) {
		StringBuilder escaped = new StringBuilder();
		for (char c : text.toCharArray()) {
			switch (c) {
				case '"' -> escaped.append("\\\"");
				case '\\' -> escaped.append("\\\\");
				case '\n' -> escaped.append("\\n");
				case '\r' -> escaped.append("\\r");
				case '\t' -> escaped.append("\\t");
				default -> {
					if (c < 0x20) escaped.append(String.format("\\u%04x", (int) c));
					else escaped.append(c);
				}
			}
		}
		return escaped.toString();
	}

	private static <T> T onEdt(Supplier<T> supplier) {
		if (SwingUtilities.isEventDispatchThread()) return supplier.get();
		AtomicReference<T> result = new AtomicReference<>();
		AtomicReference<Throwable> error = new AtomicReference<>();
		try {
			SwingUtilities.invokeAndWait(() -> {
				try {
					result.set(supplier.get());
				} catch (Throwable t) {
					error.set(t);
				}
			});
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("interrupted waiting on the Swing thread", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getCause());
		}
		if (null != error.get()) throw new RuntimeException(error.get());
		return result.get();
	}
}
