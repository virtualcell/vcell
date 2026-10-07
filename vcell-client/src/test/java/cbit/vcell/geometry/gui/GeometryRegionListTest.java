package cbit.vcell.geometry.gui;

import cbit.image.gui.ImagePlaneManagerPanel;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.parser.Expression;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.JList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class GeometryRegionListTest {

	@Test
	public void geometryViewerListsEverySubvolumeName() throws Exception {
		Geometry geometry = new Geometry("regions", 3);
		geometry.getGeometrySpec().addSubVolume(new AnalyticSubVolume("extracellular", new Expression(1.0)));
		geometry.getGeometrySpec().addSubVolume(new AnalyticSubVolume("cytosol", new Expression("x^2+y^2+z^2<0.2;")));
		geometry.getGeometrySpec().addSubVolume(new AnalyticSubVolume("nucleus", new Expression("x^2+y^2+z^2<0.05;")));

		GeometryViewer viewer = new GeometryViewer();
		viewer.setGeometry(geometry);

		ImagePlaneManagerPanel slice = viewer.geometrySlicePanel();
		JList<String> regions = slice.getRegionList();
		assertEquals(3, regions.getModel().getSize());
		for (int i = 0; i < regions.getModel().getSize(); i++) {
			regions.setSelectedIndex(i);
			String name = regions.getModel().getElementAt(i);
			assertTrue(slice.regionReadout().contains(name), slice.regionReadout());
		}
	}
}
