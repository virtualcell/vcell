package cbit.vcell.constraints.gui;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * ConstraintPanel is constructed only from its own main method. Nothing else in the
 * source tree creates one, so the panel is not a shipped client screen.
 */
@Tag("Fast")
public class ConstraintPanelReachabilityTest {

	@Test
	public void onlyItsOwnMainConstructsConstraintPanel() throws IOException {
		Path here = Path.of("").toAbsolutePath();
		Path root = Files.isDirectory(here.resolve("vcell-client")) ? here : here.getParent();
		List<String> hits = new ArrayList<>();
		Files.walkFileTree(root, new SimpleFileVisitor<>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				String name = dir.getFileName().toString();
				if (name.equals("target") || name.equals("node_modules") || name.equals(".git")
						|| name.equals("localsolvers") || name.equals(".idea")) {
					return FileVisitResult.SKIP_SUBTREE;
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				String name = file.getFileName().toString();
				if (name.endsWith(".java") && Files.readString(file).contains("new ConstraintPanel(")) {
					hits.add(root.relativize(file).toString());
				}
				return FileVisitResult.CONTINUE;
			}
		});
		assertEquals(List.of("vcell-client/src/main/java/cbit/vcell/constraints/gui/ConstraintPanel.java"), hits);
	}
}
