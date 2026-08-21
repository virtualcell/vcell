package org.vcell.util.document;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The four-part version is carried as "<EDITION>_Version_<MAJOR.MINOR.PATCH>_build_<BUILD>".
 * PATCH was read from the MINOR position for four years (issue #2011) without anything
 * noticing, because its only consumer compared it against a value that was equally wrong.
 */
@Tag("Fast")
public class VCellSoftwareVersionTest {

	@Test
	public void parsesAllFourParts() {
		VCellSoftwareVersion v = VCellSoftwareVersion.fromString("Alpha_Version_8.0.28_build_01");
		assertEquals(VCellSoftwareVersion.VCellSite.alpha, v.getSite());
		assertEquals(8, v.getMajorVersion());
		assertEquals(0, v.getMinorVersion());
		assertEquals(28, v.getPatchVersion());
		assertEquals("01", v.getBuildNumber());
	}

	@Test
	public void patchIsNotAliasedToMinor() {
		// 8.1.0 and 8.0.1 differ only by which of the two middle numbers is set, so a
		// parser that reads PATCH from the MINOR position cannot tell them apart.
		VCellSoftwareVersion a = VCellSoftwareVersion.fromString("Rel_Version_8.1.0_build_01");
		assertEquals(1, a.getMinorVersion());
		assertEquals(0, a.getPatchVersion());

		VCellSoftwareVersion b = VCellSoftwareVersion.fromString("Rel_Version_8.0.1_build_01");
		assertEquals(0, b.getMinorVersion());
		assertEquals(1, b.getPatchVersion());
	}

	@Test
	public void unparseableVersionIsUnknownRatherThanThrowing() {
		VCellSoftwareVersion v = VCellSoftwareVersion.fromString("nonsense");
		assertEquals(VCellSoftwareVersion.VCellSite.unknown, v.getSite());
	}
}
