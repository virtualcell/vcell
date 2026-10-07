package cbit.vcell.client;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.ObservablePropertiesPanel;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.Structure;
import org.vcell.documentation.VcellHelpViewer;
import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;

import javax.help.JHelp;
import javax.imageio.ImageIO;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;

/**
 * Opens the Observables topic in the client's JavaHelp viewer, then opens the
 * observables properties editor the page describes.
 */
public final class HelpCloseout {
	private HelpCloseout() {
	}

	public static void main(String[] args) throws Exception {
		File dir = new File("docs/accessibility/evidence/2026-10-c10-help");
		if (!dir.isDirectory() && !dir.mkdirs()) {
			throw new IllegalStateException("cannot create " + dir);
		}
		Robot robot = new Robot();
		JFrame[] frames = new JFrame[1];
		String[] helpText = new String[1];
		SwingUtilities.invokeAndWait(() -> openHelp(frames));
		pause();
		SwingUtilities.invokeAndWait(() -> {
			helpText[0] = readHelp(frames[0]);
			scrollHelpTo(frames[0], "question mark");
		});
		String text = helpText[0];
		System.out.println("help id text has question mark: " + text.contains("question mark"));
		System.out.println("help id text has state name: " + text.contains("state's name"));
		System.out.println("help id text has shown in green: " + text.contains("shown in green"));
		System.out.println("help id text has shown in yellow: " + text.contains("shown in yellow"));
		capture(robot, frames[0], new File(dir, "javahelp-observables.png"));
		SwingUtilities.invokeAndWait(() -> frames[0].dispose());

		SwingUtilities.invokeAndWait(() -> openProperties(frames));
		pause();
		capture(robot, frames[0], new File(dir, "observables-properties.png"));
		SwingUtilities.invokeAndWait(() -> frames[0].dispose());
	}

	private static void openHelp(JFrame[] frames) {
		try {
			VcellHelpViewer viewer = new VcellHelpViewer(VcellHelpViewer.VCELL_DOC_URL);
			JFrame frame = new JFrame("Virtual Cell Help");
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.getContentPane().add(viewer);
			frame.setSize(980, 760);
			frame.setLocation(40, 40);
			frame.setVisible(true);
			find(viewer, JHelp.class).setCurrentID("Observables");
			frame.toFront();
			frames[0] = frame;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private static void scrollHelpTo(JFrame frame, String phrase) {
		JEditorPane page = find(frame.getContentPane(), JEditorPane.class);
		if (page == null || page.getDocument() == null) {
			return;
		}
		try {
			String plain = page.getDocument().getText(0, page.getDocument().getLength());
			int index = plain.toLowerCase().indexOf(phrase.toLowerCase());
			if (index < 0) {
				return;
			}
			Rectangle place = page.modelToView(index);
			if (place != null) {
				page.scrollRectToVisible(place);
			}
		} catch (Exception ignored) {
			// The page text is already recorded if scrolling the view fails.
		}
	}

	private static String readHelp(JFrame frame) {
		JEditorPane page = find(frame.getContentPane(), JEditorPane.class);
		if (page == null) {
			return "";
		}
		System.out.println("help title: " + frame.getTitle());
		return page.getText() == null ? "" : page.getText();
	}

	private static void openProperties(JFrame[] frames) {
		try {
			BioModel bioModel = new BioModel(null);
			Model model = bioModel.getModel();
			Structure compartment = model.createFeature();
			RbmModelContainer rules = model.getRbmModelContainer();
			MolecularType receptor = rules.createMolecularType();
			MolecularComponent tyrosine = new MolecularComponent("Y");
			ComponentStateDefinition phosphorylated = new ComponentStateDefinition("p");
			tyrosine.addComponentStateDefinition(phosphorylated);
			tyrosine.addComponentStateDefinition(new ComponentStateDefinition("u"));
			receptor.addMolecularComponent(tyrosine);
			MolecularComponent ligand = new MolecularComponent("l");
			ligand.addComponentStateDefinition(new ComponentStateDefinition("bound"));
			receptor.addMolecularComponent(ligand);
			rules.addMolecularType(receptor, false);

			RbmObservable observable = rules.createObservable(RbmObservable.ObservableType.Molecules, receptor, compartment);
			MolecularTypePattern pattern = new MolecularTypePattern(receptor);
			MolecularComponentPattern tyrosinePattern = pattern.getMolecularComponentPattern(tyrosine);
			tyrosinePattern.setComponentStatePattern(new ComponentStatePattern(phosphorylated));
			MolecularComponentPattern ligandPattern = pattern.getMolecularComponentPattern(ligand);
			ligandPattern.setComponentStatePattern(new ComponentStatePattern());
			SpeciesPattern speciesPattern = new SpeciesPattern();
			speciesPattern.addMolecularTypePattern(pattern);
			observable.addSpeciesPattern(speciesPattern);
			rules.addObservable(observable);

			ObservablePropertiesPanel panel = new ObservablePropertiesPanel();
			panel.setBioModel(bioModel);
			Method setObservable = ObservablePropertiesPanel.class.getDeclaredMethod("setObservable", RbmObservable.class);
			setObservable.setAccessible(true);
			setObservable.invoke(panel, observable);

			JFrame frame = new JFrame("Observables properties");
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.getContentPane().add(panel);
			frame.setSize(980, 520);
			frame.setLocation(40, 40);
			frame.setVisible(true);
			frame.toFront();
			panel.revalidate();
			panel.repaint();
			frames[0] = frame;
			System.out.println("properties observable: " + observable.getName());
			System.out.println("tyrosine state pattern: " + tyrosinePattern.getComponentStatePattern().getComponentStateDefinition().getDisplayName());
			System.out.println("ligand state pattern any: " + ligandPattern.getComponentStatePattern().isAny());
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private static void pause() throws InterruptedException {
		Thread.sleep(900);
	}

	private static void capture(Robot robot, Window window, File file) throws Exception {
		Rectangle bounds = window.getBounds();
		BufferedImage image = robot.createScreenCapture(bounds);
		ImageIO.write(image, "png", file);
		System.out.println("wrote " + file.getPath() + " " + image.getWidth() + "x" + image.getHeight());
	}

	private static <T> T find(Component component, Class<T> type) {
		if (type.isInstance(component)) {
			return type.cast(component);
		}
		if (component instanceof Container) {
			for (Component child : ((Container) component).getComponents()) {
				T found = find(child, type);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}
}
