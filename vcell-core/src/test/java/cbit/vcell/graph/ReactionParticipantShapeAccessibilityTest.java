package cbit.vcell.graph;

import cbit.gui.graph.EdgeShape;
import cbit.gui.graph.GraphModel;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Model;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class ReactionParticipantShapeAccessibilityTest {

	@Test
	public void selectedStrokeIsWider_8_3_f() throws Exception {
		DiagramFixture fixture = new DiagramFixture();

		assertEquals(1f, fixture.reactant.curveStroke().getLineWidth(), 0f);

		fixture.cartoon.selectShape(fixture.reactant);
		assertTrue(fixture.reactant.curveStroke().getLineWidth() >= 2.5f);
		fixture.cartoon.clearSelection();

		fixture.cartoon.selectShape(fixture.speciesShape);
		assertTrue(fixture.reactant.curveStroke().getLineWidth() >= 2.5f,
				"selecting the start species must widen the edge");
		assertEquals(1f, fixture.otherReactant.curveStroke().getLineWidth(), 0f);
		fixture.cartoon.clearSelection();

		BasicStroke unselectedDash = fixture.catalyst.curveStroke();
		assertEquals(1f, unselectedDash.getLineWidth(), 0f);
		assertEquals(EdgeShape.LINE_STYLE_DASHED, fixture.catalyst.getLineStyle());
		fixture.cartoon.selectShape(fixture.catalystSpecies);
		BasicStroke selectedDash = fixture.catalyst.curveStroke();
		assertTrue(selectedDash.getLineWidth() >= 2.5f);
		assertArrayEquals(unselectedDash.getDashArray(), selectedDash.getDashArray());
	}

	@Test
	public void plainEdgeWidensOnlyWhenTheEdgeIsSelected() throws Exception {
		GraphModel model = new GraphModel() {
			@Override
			public void refreshAll() {
			}
		};
		StubEdge edge = new StubEdge(model);
		model.addShape(edge);
		assertEquals(1f, edge.curveStroke().getLineWidth(), 0f);
		model.selectShape(edge);
		assertTrue(edge.curveStroke().getLineWidth() >= 2.5f);
	}

	private static final class DiagramFixture {
		final ReactionCartoonFull cartoon = new ReactionCartoonFull();
		final SpeciesContextShape speciesShape;
		final SpeciesContextShape catalystSpecies;
		final ReactantShape reactant;
		final ReactantShape otherReactant;
		final CatalystShape catalyst;

		DiagramFixture() throws Exception {
			Model model = new Model("accessibility");
			model.addSpecies(new Species("A", "A"));
			model.addSpecies(new Species("B", "B"));
			model.addSpecies(new Species("C", "C"));
			model.addFeature("cyt");
			Feature cyt = (Feature) model.getStructure("cyt");
			SpeciesContext a = model.addSpeciesContext(model.getSpecies("A"), cyt);
			SpeciesContext b = model.addSpeciesContext(model.getSpecies("B"), cyt);
			SpeciesContext c = model.addSpeciesContext(model.getSpecies("C"), cyt);
			SimpleReaction reaction = new SimpleReaction(model, cyt, "r", false);
			reaction.addReactant(a, 1);
			reaction.addReactant(b, 1);
			reaction.addCatalyst(c);
			model.addReactionStep(reaction);
			Reactant reactantA = reaction.getReactant(0);
			Reactant reactantB = reaction.getReactant(1);
			Catalyst catalystC = reaction.getCatalyst(0);

			speciesShape = new SpeciesContextShape(a, cartoon);
			SpeciesContextShape otherSpecies = new SpeciesContextShape(b, cartoon);
			catalystSpecies = new SpeciesContextShape(c, cartoon);
			SimpleReactionShape reactionShape = new SimpleReactionShape(reaction, cartoon);
			reactant = new ReactantShape(reactantA, reactionShape, speciesShape, cartoon);
			otherReactant = new ReactantShape(reactantB, reactionShape, otherSpecies, cartoon);
			catalyst = new CatalystShape(catalystC, reactionShape, catalystSpecies, cartoon);
			cartoon.addShape(speciesShape);
			cartoon.addShape(otherSpecies);
			cartoon.addShape(catalystSpecies);
			cartoon.addShape(reactionShape);
			cartoon.addShape(reactant);
			cartoon.addShape(otherReactant);
			cartoon.addShape(catalyst);
		}
	}

	private static final class StubEdge extends EdgeShape {
		StubEdge(GraphModel graphModel) {
			super(new Point(0, 0), new Point(40, 0), graphModel);
		}

		@Override
		public Object getModelObject() {
			return "edge";
		}

		@Override
		public void refreshLayoutSelf() {
		}

		@Override
		public void refreshLabel() {
		}

		@Override
		public Dimension getPreferedSizeSelf(Graphics2D g) {
			return new Dimension(1, 1);
		}
	}
}
