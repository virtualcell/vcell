package cbit.vcell.client.constants;

import org.junit.Assert;
import org.junit.Test;

public class VCellCodeVersionTest {
	
	@Test
	public void testValues( ) {
		VCellCodeVersion minorLess = gen(VCellCodeVersion.CURRENT_MAJOR,VCellCodeVersion.CURRENT_MINOR - 1);
		VCellCodeVersion minorMore = gen(VCellCodeVersion.CURRENT_MAJOR,VCellCodeVersion.CURRENT_MINOR + 1);
		VCellCodeVersion majorLess = gen(VCellCodeVersion.CURRENT_MAJOR - 1,VCellCodeVersion.CURRENT_MINOR);
		VCellCodeVersion majorMore= gen(VCellCodeVersion.CURRENT_MAJOR + 1,VCellCodeVersion.CURRENT_MINOR);
		VCellCodeVersion self= gen(VCellCodeVersion.CURRENT_MAJOR,VCellCodeVersion.CURRENT_MINOR);
		Assert.assertTrue(VCellCodeVersion.CURRENT.compareTo(minorMore) < 0);
		Assert.assertTrue(VCellCodeVersion.CURRENT.compareTo(majorMore) < 0);
		Assert.assertTrue(VCellCodeVersion.CURRENT.compareTo(minorLess) > 0);
		Assert.assertTrue(VCellCodeVersion.CURRENT.compareTo(majorLess) > 0);
		Assert.assertTrue(VCellCodeVersion.CURRENT.compareTo(self) == 0);
		Assert.assertTrue(VCellCodeVersion.CURRENT.compare(5,4) < 0);
	}
	
	private VCellCodeVersion gen(int maj, int min) {
		return new VCellCodeVersion(maj, min);
	}
	

}
