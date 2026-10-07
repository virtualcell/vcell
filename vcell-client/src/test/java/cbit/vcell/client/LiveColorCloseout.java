package cbit.vcell.client;

import cbit.image.DisplayAdapterService;
import cbit.image.SourceDataInfo;
import cbit.image.gui.ImagePaneView;
import cbit.image.gui.ImagePlaneManagerPanel;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.gui.GeometryViewer;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.gui.MeshSpecificationPanel;
import cbit.vcell.parser.Expression;
import cbit.vcell.simdata.gui.PDEDataContextPanel;
import cbit.vcell.solver.Simulation;
import org.vcell.util.Extent;
import org.vcell.util.Origin;
import org.vcell.util.Range;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Opens the results slice, the geometry viewer, and the mesh panel on screen and drives them
 * the way C4–C6 describe. Not a unit test: it needs a display. Run the main method, then read
 * the record it writes under docs/accessibility/evidence/2026-10-c4-c6/.
 */
public class LiveColorCloseout {

	private static final File OUT = new File("docs/accessibility/evidence/2026-10-c4-c6");
	private static final StringBuilder LOG = new StringBuilder();
	private static Robot robot;
	private static final AtomicBoolean autoDismissErrors = new AtomicBoolean(true);

	public static void main(String[] args) throws Exception {
		OUT.mkdirs();
		robot = new Robot();
		robot.setAutoDelay(30);
		Thread watcher = new Thread(LiveColorCloseout::watchErrorDialogs, "error-dialog-watcher");
		watcher.setDaemon(true);
		watcher.start();
		try {
			runC4();
			runC5();
			runC6();
		} catch (Throwable t) {
			log("FAILED " + t);
			t.printStackTrace(new PrintWriter(new StringBuilderWriter(LOG)));
		} finally {
			try (PrintWriter writer = new PrintWriter(new FileWriter(new File(OUT, "measurements.txt")))) {
				writer.print(LOG);
			}
			System.out.println(LOG);
		}
	}

	private static void runC4() throws Exception {
		log("C4 results slice");
		AtomicReference<PDEDataContextPanel> panelRef = new AtomicReference<>();
		AtomicReference<JFrame> frameRef = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() -> {
			PDEDataContextPanel panel = new PDEDataContextPanel();
			DisplayAdapterService service = panel.getdisplayAdapterService1();
			service.setValueDomain(new Range(0, 10));
			service.setActiveScaleRange(new Range(0, 10));
			int xSize = 16;
			int ySize = 16;
			int zSize = 4;
			double[] values = new double[xSize * ySize * zSize];
			for (int i = 0; i < values.length; i++) {
				values[i] = 4.25;
			}
			SourceDataInfo info = new SourceDataInfo(SourceDataInfo.RAW_VALUE_TYPE, values,
					new Extent(xSize, ySize, zSize), new Origin(0, 0, 0), new Range(0, 10),
					0, xSize, 1, ySize, xSize, zSize, xSize * ySize);
			ImagePlaneManagerPanel slice = (ImagePlaneManagerPanel) findNamed(panel, "ImagePlaneManagerPanel");
			slice.setSourceDataInfo(info);
			JFrame frame = show("Simulation results — BlueRed", panel, 1040, 720);
			panelRef.set(panel);
			frameRef.set(frame);
		});
		pause(600);
		JFrame frame = frameRef.get();
		PDEDataContextPanel panel = panelRef.get();
		SwingUtilities.invokeAndWait(() -> {
			frame.setSize(1040, 800);
			panel.revalidate();
			log("frame size " + frame.getWidth() + "x" + frame.getHeight());
		});
		pause(400);
		activate(frame);
		SwingUtilities.invokeAndWait(() -> {
			log("colormap " + panel.getdisplayAdapterService1().getActiveColorModelID());
			for (JRadioButton button : findAll(panel, JRadioButton.class)) {
				log("color button '" + button.getText() + "' border " + borderOf(button)
						+ " showing " + button.isShowing() + " bounds " + button.getBounds());
			}
			List<Color> swatches = new ArrayList<>();
			for (JLabel label : findAll(panel, JLabel.class)) {
				String text = label.getText();
				if (text == null || text.isEmpty()) {
					continue;
				}
				if (text.startsWith("BM ") || text.startsWith("AM ") || text.startsWith("NN ")
						|| text.startsWith("ND ") || text.startsWith("NR ")
						|| text.equals("Min") || text.equals("Max") || text.equals("0.0") || text.equals("10.0")) {
					Color paper = label.isOpaque() ? label.getBackground() : parentPaper(label);
					double ratio = DisplayAdapterService.contrastRatio(label.getForeground().getRGB(), paper.getRGB());
					log(String.format("label '%s' ink %s paper %s %.2f:1 showing %s",
							text, hex(label.getForeground()), hex(paper), ratio, label.isShowing()));
					if (text.startsWith("BM ") || text.startsWith("AM ") || text.startsWith("NN ")
							|| text.startsWith("ND ") || text.startsWith("NR ")) {
						Color gap = parentPaper(label);
						double swatch = DisplayAdapterService.contrastRatio(label.getBackground().getRGB(), gap.getRGB());
						log(String.format("  swatch %s on gap %s %.2f:1", hex(label.getBackground()), hex(gap), swatch));
						swatches.add(label.getBackground());
					}
				}
			}
			for (int i = 1; i < swatches.size(); i++) {
				double beside = DisplayAdapterService.contrastRatio(swatches.get(i - 1).getRGB(), swatches.get(i).getRGB());
				log(String.format("  neighbor %s / %s %.2f:1", hex(swatches.get(i - 1)), hex(swatches.get(i)), beside));
			}
			for (JTextField field : findAll(panel, JTextField.class)) {
				Color ink = field.isEnabled() ? field.getForeground() : field.getDisabledTextColor();
				double ratio = DisplayAdapterService.contrastRatio(ink.getRGB(), field.getBackground().getRGB());
				log(String.format("field '%s' ink %s paper %s %.2f:1 enabled %s",
						field.getText(), hex(ink), hex(field.getBackground()), ratio, field.isEnabled()));
			}
			JRadioButton xy = (JRadioButton) findNamed(panel, "ZAxisCheckbox");
			log("slice axis '" + xy.getText() + "' border " + borderOf(xy) + " showing " + xy.isShowing());
			JLabel slice = (JLabel) findNamed(panel, "SliceLabel");
			double sliceRatio = DisplayAdapterService.contrastRatio(slice.getForeground().getRGB(), slice.getBackground().getRGB());
			log(String.format("slice label '%s' %.2f:1 showing %s", slice.getText(), sliceRatio, slice.isShowing()));
		});
		Component view = findByClassName(panel, "ImagePaneView");
		SwingUtilities.invokeAndWait(() -> view.requestFocusInWindow());
		pause(150);
		robot.setAutoDelay(0);
		robot.keyPress(KeyEvent.VK_RIGHT);
		robot.keyRelease(KeyEvent.VK_RIGHT);
		pause(200);
		AtomicReference<String> keyboard = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() -> keyboard.set(((JLabel) findNamed(panel, "InfoJlabel")).getText()));
		log("keyboard readout '" + keyboard.get() + "'");
		Point imagePoint = pointOnDataImage(panel);
		moveCursor(imagePoint.x - 40, imagePoint.y + 8);
		pause(100);
		for (int step = 0; step <= 40; step += 8) {
			moveCursor(imagePoint.x - 40 + step, imagePoint.y + 8);
			pause(40);
		}
		pause(200);
		AtomicReference<String> pointer = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() -> {
			pointer.set(((JLabel) findNamed(panel, "InfoJlabel")).getText());
			Point cursor = java.awt.MouseInfo.getPointerInfo().getLocation();
			Point frameAt = frame.getLocationOnScreen();
			Component hit = SwingUtilities.getDeepestComponentAt(frame, cursor.x - frameAt.x, cursor.y - frameAt.y);
			log("cursor " + cursor.x + "," + cursor.y + " frame " + frameAt.x + "," + frameAt.y
					+ " " + frame.getWidth() + "x" + frame.getHeight()
					+ " hit " + (hit == null ? "none" : hit.getClass().getSimpleName()));
		});
		log("pointer readout '" + pointer.get() + "' at " + imagePoint.x + "," + imagePoint.y);
		if (pointer.get() == null || !pointer.get().contains("Value =")) {
			SwingUtilities.invokeAndWait(() -> {
				ImagePaneView image = (ImagePaneView) view;
				Point origin = image.getLocationOnScreen();
				int x = imagePoint.x - origin.x;
				int y = imagePoint.y - origin.y;
				image.dispatchEvent(new java.awt.event.MouseEvent(
						image, java.awt.event.MouseEvent.MOUSE_MOVED,
						System.currentTimeMillis(), 0, x, y, 0, false));
				pointer.set(((JLabel) findNamed(panel, "InfoJlabel")).getText());
			});
			log("pointer readout after delivered move '" + pointer.get() + "'");
		}
		JSlider slider = (JSlider) findNamed(panel, "SliceSlider");
		SwingUtilities.invokeAndWait(() -> slider.requestFocusInWindow());
		pause(200);
		SwingUtilities.invokeAndWait(() -> log("slider focus border " + borderOf(slider) + " focused " + slider.isFocusOwner()));
		capture(frame, "c4-results-bluered.png");
		SwingUtilities.invokeAndWait(() -> frame.dispose());
	}

	private static void runC5() throws Exception {
		log("C5 geometry");
		AtomicReference<GeometryViewer> viewerRef = new AtomicReference<>();
		AtomicReference<JFrame> frameRef = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() -> {
			Geometry geometry = new Geometry("regions", 3);
			try {
				geometry.getGeometrySpec().addSubVolume(new AnalyticSubVolume("extracellular", new Expression(1.0)));
				geometry.getGeometrySpec().addSubVolume(new AnalyticSubVolume("cytosol", new Expression("x^2+y^2+z^2<0.2;")));
				geometry.getGeometrySpec().addSubVolume(new AnalyticSubVolume("nucleus", new Expression("x^2+y^2+z^2<0.05;")));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			GeometryViewer viewer = new GeometryViewer();
			viewer.setGeometry(geometry);
			JTabbedPane tabs = findFirst(viewer, JTabbedPane.class);
			if (tabs != null) {
				for (int i = 0; i < tabs.getTabCount(); i++) {
					if ("Slice View".equals(tabs.getTitleAt(i))) {
						tabs.setSelectedIndex(i);
					}
				}
			}
			JFrame frame = show("Geometry — regions", viewer, 1040, 720);
			viewerRef.set(viewer);
			frameRef.set(frame);
		});
		pause(500);
		GeometryViewer viewer = viewerRef.get();
		JFrame frame = frameRef.get();
		activate(frame);
		JList<?> list = (JList<?>) findNamed(viewer, "RegionList");
		SwingUtilities.invokeAndWait(() -> {
			log("region count " + list.getModel().getSize());
			for (int i = 0; i < list.getModel().getSize(); i++) {
				log("region row " + list.getModel().getElementAt(i));
			}
			list.requestFocusInWindow();
			log("readout '" + readout(viewer) + "' focus " + list.isFocusOwner());
		});
		pause(200);
		for (int step = 0; step < 2; step++) {
			robot.keyPress(KeyEvent.VK_DOWN);
			robot.keyRelease(KeyEvent.VK_DOWN);
			pause(200);
			AtomicReference<String> text = new AtomicReference<>();
			SwingUtilities.invokeAndWait(() -> text.set(readout(viewer) + " selected=" + list.getSelectedValue()));
			log("after down " + text.get());
		}
		capture(frame, "c5-geometry-regions.png");
		SwingUtilities.invokeAndWait(() -> frame.dispose());
	}

	private static void runC6() throws Exception {
		log("C6 mesh validation");
		autoDismissErrors.set(false);
		AtomicReference<MeshSpecificationPanel> panelRef = new AtomicReference<>();
		AtomicReference<JFrame> frameRef = new AtomicReference<>();
		AtomicReference<JTextField> fieldRef = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() -> {
			Geometry geometry = new Geometry("mesh", 3);
			MathDescription math = new MathDescription("mesh");
			try {
				math.setGeometry(geometry);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			Simulation simulation = new Simulation(math, null);
			MeshSpecificationPanel panel = new MeshSpecificationPanel();
			panel.setSimulation(simulation);
			panel.setEnabled(true);
			enableTree(panel);
			JCheckBox auto = findFirst(panel, JCheckBox.class);
			if (auto != null) {
				auto.setSelected(false);
			}
			JTextField field = (JTextField) findNamed(panel, "XTextField");
			JFrame frame = show("Mesh specification", panel, 520, 360);
			panelRef.set(panel);
			frameRef.set(frame);
			fieldRef.set(field);
		});
		pause(400);
		JFrame frame = frameRef.get();
		JTextField field = fieldRef.get();
		activate(frame);
		Point fieldPoint = new Point();
		SwingUtilities.invokeAndWait(() -> {
			Point origin = field.getLocationOnScreen();
			fieldPoint.x = origin.x + field.getWidth() / 2;
			fieldPoint.y = origin.y + field.getHeight() / 2;
		});
		robot.mouseMove(fieldPoint.x, fieldPoint.y);
		robot.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
		pause(200);
		replaceField("abc");
		pause(200);
		AtomicReference<String> typed = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() -> typed.set(field.getText()));
		log("field after typing '" + typed.get() + "'");
		robot.keyPress(KeyEvent.VK_TAB);
		robot.keyRelease(KeyEvent.VK_TAB);
		Dialog dialog = waitForDialog("Error", 4000);
		log("dialog showing " + (dialog != null));
		if (dialog != null) {
			capture(dialog, "c6-error-dialog.png");
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			pause(300);
			if (dialog.isShowing()) {
				clickOk(dialog);
			}
		}
		pause(400);
		AtomicReference<String> afterDialog = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() -> afterDialog.set(messageText(panelRef.get())));
		log("sentence after dialog '" + afterDialog.get() + "'");
		capture(frame, "c6-sentence-remains.png");
		replaceField("10");
		pause(150);
		robot.keyPress(KeyEvent.VK_TAB);
		robot.keyRelease(KeyEvent.VK_TAB);
		pause(400);
		AtomicReference<String> cleared = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() -> cleared.set(messageText(panelRef.get()) + " field='" + field.getText() + "'"));
		log("sentence after correction '" + cleared.get() + "'");
		capture(frame, "c6-sentence-cleared.png");
		SwingUtilities.invokeAndWait(() -> frame.dispose());
		autoDismissErrors.set(true);
	}

	/** Robot.mouseMove is ignored in this session. CoreGraphics moves the cursor. */
	private static void moveCursor(int x, int y) throws Exception {
		Process process = new ProcessBuilder("/tmp/movemouse", Integer.toString(x), Integer.toString(y))
				.redirectErrorStream(true)
				.start();
		if (process.waitFor() != 0) {
			throw new IllegalStateException("movemouse " + x + "," + y);
		}
	}

	/** Click the title bar so later keystrokes go to this window. */
	private static void activate(Window window) throws Exception {
		AtomicReference<Point> title = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() -> {
			window.toFront();
			Point origin = window.getLocationOnScreen();
			title.set(new Point(origin.x + 120, origin.y + 12));
		});
		Point point = title.get();
		robot.mouseMove(point.x, point.y);
		pause(80);
		robot.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
		pause(250);
	}

	/** A screen point that lands on data pixels, not the ruler or the black margin. */
	private static Point pointOnDataImage(Container panel) throws Exception {
		AtomicReference<Point> point = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() -> {
			ImagePaneView view = null;
			for (ImagePaneView candidate : findAll(panel, ImagePaneView.class)) {
				log("image view candidate " + candidate.getWidth() + "x" + candidate.getHeight()
						+ " showing " + candidate.isShowing());
				if (candidate.isShowing()) {
					view = candidate;
				}
			}
			if (view == null) {
				throw new IllegalStateException("no showing image view");
			}
			Point origin = view.getLocationOnScreen();
			Point onImage = null;
			for (int y = 0; y < view.getHeight() && onImage == null; y += 2) {
				for (int x = 0; x < view.getWidth(); x += 2) {
					if (x >= 4 && y >= 4 && view.isPointOnImage(new Point(x, y))) {
						onImage = new Point(x, y);
						break;
					}
				}
			}
			if (onImage == null) {
				onImage = new Point(8, 8);
				log("no on-image point; using " + onImage.x + "," + onImage.y);
			}
			point.set(new Point(origin.x + onImage.x + 1, origin.y + onImage.y + 1));
			log("image view " + view.getWidth() + "x" + view.getHeight() + " at " + origin.x + "," + origin.y
					+ " on-image " + onImage.x + "," + onImage.y);
		});
		return point.get();
	}

	private static void replaceField(String text) throws InterruptedException {
		robot.keyPress(KeyEvent.VK_META);
		robot.keyPress(KeyEvent.VK_A);
		robot.keyRelease(KeyEvent.VK_A);
		robot.keyRelease(KeyEvent.VK_META);
		pause(80);
		type(text);
	}

	private static String readout(GeometryViewer viewer) {
		JLabel info = (JLabel) findNamed(viewer, "InfoJlabel");
		return info == null ? null : info.getText();
	}

	private static String messageText(Container parent) {
		for (JLabel label : findAll(parent, JLabel.class)) {
			String text = label.getText();
			if (text != null && (text.contains("Wrong number") || text.equals(" "))) {
				return text;
			}
		}
		return null;
	}

	private static void type(String text) {
		for (int i = 0; i < text.length(); i++) {
			int code = KeyEvent.getExtendedKeyCodeForChar(text.charAt(i));
			robot.keyPress(code);
			robot.keyRelease(code);
		}
	}

	private static JFrame show(String title, JComponent content, int width, int height) {
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(content);
		frame.setSize(width, height);
		frame.setLocation(60, 60);
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
		frame.toFront();
		return frame;
	}

	private static void capture(Window window, String name) throws Exception {
		pause(250);
		AtomicReference<Rectangle> bounds = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() -> {
			window.toFront();
			bounds.set(new Rectangle(window.getLocationOnScreen(), window.getSize()));
		});
		Rectangle rect = bounds.get();
		BufferedImage shot = robot.createScreenCapture(rect);
		ImageIO.write(shot, "png", new File(OUT, name));
		log("wrote " + name + " " + rect.width + "x" + rect.height);
	}

	private static void watchErrorDialogs() {
		while (true) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				return;
			}
			if (!autoDismissErrors.get()) {
				continue;
			}
			Dialog dialog = findDialog("Error");
			if (dialog != null) {
				log("auto-dismissed Error dialog");
				clickOk(dialog);
			}
		}
	}

	private static Dialog waitForDialog(String title, long timeoutMs) throws InterruptedException {
		long end = System.currentTimeMillis() + timeoutMs;
		while (System.currentTimeMillis() < end) {
			Dialog dialog = findDialog(title);
			if (dialog != null) {
				return dialog;
			}
			Thread.sleep(50);
		}
		return null;
	}

	private static Dialog findDialog(String title) {
		for (Window window : Window.getWindows()) {
			if (window instanceof Dialog && window.isShowing() && title.equals(((Dialog) window).getTitle())) {
				return (Dialog) window;
			}
		}
		return null;
	}

	private static void clickOk(Dialog dialog) {
		try {
			SwingUtilities.invokeAndWait(() -> {
				JComponent ok = findButton(dialog, "OK");
				if (ok instanceof AbstractButton) {
					((AbstractButton) ok).doClick();
				} else {
					dialog.dispose();
				}
			});
		} catch (Exception e) {
			log("could not dismiss dialog " + e);
		}
	}

	private static JComponent findButton(Container parent, String text) {
		for (Component child : parent.getComponents()) {
			if (child instanceof AbstractButton && text.equals(((AbstractButton) child).getText())) {
				return (JComponent) child;
			}
			if (child instanceof Container) {
				JComponent found = findButton((Container) child, text);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}

	private static void enableTree(Component component) {
		component.setEnabled(true);
		if (component instanceof Container) {
			for (Component child : ((Container) component).getComponents()) {
				enableTree(child);
			}
		}
	}

	private static String borderOf(JComponent component) {
		if (component.getBorder() instanceof LineBorder) {
			LineBorder border = (LineBorder) component.getBorder();
			return hex(border.getLineColor()) + " x" + border.getThickness();
		}
		return String.valueOf(component.getBorder());
	}

	private static Color parentPaper(Component component) {
		Component parent = component.getParent();
		while (parent != null) {
			if (parent.isBackgroundSet() && parent.getBackground() != null) {
				return parent.getBackground();
			}
			parent = parent.getParent();
		}
		return Color.WHITE;
	}

	private static String hex(Color color) {
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

	private static Component findNamed(Container parent, String name) {
		for (Component child : parent.getComponents()) {
			if (name.equals(child.getName())) {
				return child;
			}
			if (child instanceof Container) {
				Component found = findNamed((Container) child, name);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}

	private static Component findByClassName(Container parent, String simpleName) {
		if (parent.getClass().getSimpleName().equals(simpleName)) {
			return parent;
		}
		for (Component child : parent.getComponents()) {
			if (child.getClass().getSimpleName().equals(simpleName)) {
				return child;
			}
			if (child instanceof Container) {
				Component found = findByClassName((Container) child, simpleName);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}

	private static <T> T findFirst(Container parent, Class<T> type) {
		List<T> all = findAll(parent, type);
		return all.isEmpty() ? null : all.get(0);
	}

	private static <T> List<T> findAll(Container parent, Class<T> type) {
		List<T> found = new ArrayList<>();
		collect(parent, type, found);
		return found;
	}

	@SuppressWarnings("unchecked")
	private static <T> void collect(Container parent, Class<T> type, List<T> found) {
		for (Component child : parent.getComponents()) {
			if (type.isInstance(child)) {
				found.add((T) child);
			}
			if (child instanceof Container) {
				collect((Container) child, type, found);
			}
		}
	}

	private static void pause(long ms) throws InterruptedException {
		Thread.sleep(ms);
	}

	private static void log(String line) {
		LOG.append(line).append('\n');
		System.out.println(line);
	}

	private static final class StringBuilderWriter extends java.io.Writer {
		private final StringBuilder target;

		StringBuilderWriter(StringBuilder target) {
			this.target = target;
		}

		@Override
		public void write(char[] cbuf, int off, int len) {
			target.append(cbuf, off, len);
		}

		@Override
		public void flush() {
		}

		@Override
		public void close() {
		}
	}
}
