package cbit.vcell.export.server;

import cbit.image.DisplayAdapterService;
import cbit.image.DisplayPreferences;
import cbit.vcell.message.VCRpcRequest;
import org.vcell.util.DataAccessException;
import org.vcell.util.Range;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.gui.DialogUtils;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Local 8.8-b: the server's RPC dispatcher against an upgraded target and an old target,
 * then the export renderer that those answers select.
 */
public class ExportServerCloseout {

	public static void main(String[] args) throws Exception {
		File out = new File("docs/accessibility/evidence/2026-10-c9-export");
		out.mkdirs();
		StringBuilder log = new StringBuilder();
		User user = new User("closeout", new KeyValue("1"));

		String[] upgraded = (String[]) new VCRpcRequest(
				user, VCRpcRequest.RpcServiceType.DATA, "getSupportedExportColorModes", new Object[] { user })
				.rpc(new UpgradedExportServer());
		log.append("upgraded modes");
		for (String mode : upgraded) {
			log.append(' ').append(mode);
		}
		log.append('\n');

		DisplayPreferences cividisRequest = preferences(DisplayAdapterService.CIVIDIS);
		DisplayPreferences cividisWritten = ExportColorModeNegotiation.preferencesForServer(cividisRequest, upgraded);
		log.append("upgraded cividis request object ").append(cividisWritten.getColorMode()).append('\n');
		paint(out, "cividis-export.png", cividisWritten, "Cividis", log);

		DisplayPreferences blueRedRequest = preferences(DisplayAdapterService.BLUERED);
		DisplayPreferences blueRedWritten = ExportColorModeNegotiation.preferencesForServer(blueRedRequest, upgraded);
		log.append("upgraded bluered request object ").append(blueRedWritten.getColorMode()).append('\n');
		paint(out, "bluered-export.png", blueRedWritten, "BlueRed", log);
		int[] retained = DisplayAdapterService.createBlueRedColorModel();
		log.append(String.format("retained BlueRed data ends %s .. %s%n",
				hex(retained[0]), hex(retained[248 - 1])));

		DataAccessException oldError = null;
		try {
			new VCRpcRequest(
					user, VCRpcRequest.RpcServiceType.DATA, "getSupportedExportColorModes", new Object[] { user })
					.rpc(new OldExportServer());
		} catch (DataAccessException e) {
			oldError = e;
		}
		log.append("old server error: ").append(oldError == null ? "none" : oldError.getMessage()).append('\n');
		boolean old = ExportColorModeNegotiation.isUnsupportedColorModeQuery(oldError);
		String[] legacy = old ? ExportColorModeNegotiation.legacyModes() : new String[0];
		log.append("old treated as legacy ").append(old).append('\n');
		DisplayPreferences requested = preferences(DisplayAdapterService.CIVIDIS);
		DisplayPreferences written = ExportColorModeNegotiation.preferencesForServer(requested, legacy);
		String notice = ExportColorModeNegotiation.notice(requested.getColorMode(), written.getColorMode());
		log.append("old request object ").append(written.getColorMode()).append('\n');
		log.append("notice: ").append(notice).append('\n');
		paint(out, "old-server-bluered.png", written, written.getColorMode(), log);

		JFrame frame = new JFrame("Export");
		JLabel label = new JLabel(notice);
		frame.add(label);
		frame.setSize(640, 160);
		frame.setLocation(80, 80);
		frame.setAlwaysOnTop(true);
		SwingUtilities.invokeAndWait(() -> frame.setVisible(true));
		Thread.sleep(400);
		SwingUtilities.invokeLater(() -> DialogUtils.showWarningDialog(frame, notice));
		Thread.sleep(800);
		java.awt.Window dialog = null;
		for (java.awt.Window window : java.awt.Window.getWindows()) {
			if (window instanceof java.awt.Dialog && window.isShowing()
					&& "Warning".equals(((java.awt.Dialog) window).getTitle())) {
				dialog = window;
			}
		}
		log.append("warning dialog showing ").append(dialog != null).append('\n');
		if (dialog != null) {
			java.awt.Window warning = dialog;
			java.awt.Rectangle bounds = new java.awt.Rectangle(warning.getLocationOnScreen(), warning.getSize());
			ImageIO.write(new java.awt.Robot().createScreenCapture(bounds), "png", new File(out, "c9-old-server-notice.png"));
			SwingUtilities.invokeAndWait(() -> {
				for (java.awt.Component component : ((java.awt.Container) warning).getComponents()) {
					clickOk(component);
				}
				warning.dispose();
			});
		}
		SwingUtilities.invokeAndWait(frame::dispose);
		try (PrintWriter writer = new PrintWriter(new FileWriter(new File(out, "record.txt")))) {
			writer.print(log);
		}
		System.out.print(log);
	}

	private static void paint(File out, String name, DisplayPreferences preferences, String label, StringBuilder log) throws Exception {
		DisplayAdapterService service = new DisplayAdapterService();
		DisplayAdapterService.addStandardColorModels(service);
		Range range = new Range(0, 10);
		ExportSpecs.setupDisplayAdapterService(preferences, service, range);
		log.append(name).append(" active ").append(service.getActiveColorModelID()).append('\n');
		int low = service.getColorFromValue(0);
		int high = service.getColorFromValue(10);
		log.append(String.format("  value 0 %s  value 10 %s%n", hex(low), hex(high)));
		BufferedImage image = new BufferedImage(420, 80, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 420, 80);
		g.setColor(Color.BLACK);
		g.drawString(label, 8, 16);
		for (int i = 0; i < 248; i++) {
			double value = range.getMin() + (range.getMax() - range.getMin()) * i / 247.0;
			g.setColor(new Color(service.getColorFromValue(value)));
			g.fillRect(8 + i, 28, 1, 40);
		}
		g.dispose();
		ImageIO.write(image, "png", new File(out, name));
		if (!label.equals(service.getActiveColorModelID())) {
			throw new IllegalStateException(name + " label " + label + " active " + service.getActiveColorModelID());
		}
	}

	private static DisplayPreferences preferences(String mode) {
		int[] specials = DisplayAdapterService.CIVIDIS.equals(mode)
				? DisplayAdapterService.createCividisSpecialColors()
				: DisplayAdapterService.createBlueRedSpecialColors();
		return new DisplayPreferences(mode, new Range(0, 10), specials, false, false);
	}

	private static String hex(int rgb) {
		Color color = new Color(rgb);
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

	private static void clickOk(java.awt.Component component) {
		if (component instanceof javax.swing.AbstractButton
				&& "OK".equals(((javax.swing.AbstractButton) component).getText())) {
			((javax.swing.AbstractButton) component).doClick();
		}
		if (component instanceof java.awt.Container) {
			for (java.awt.Component child : ((java.awt.Container) component).getComponents()) {
				clickOk(child);
			}
		}
	}

	/** The current server method: Gray, BlueRed, and Cividis. */
	public static final class UpgradedExportServer {
		public String[] getSupportedExportColorModes(User user) {
			return ExportColorModeNegotiation.currentModes();
		}
	}

	/** An old server object. It has no colormap query, so the dispatcher reports the missing method. */
	public static final class OldExportServer {
		public String getServerVersion(User user) {
			return "old";
		}
	}
}
