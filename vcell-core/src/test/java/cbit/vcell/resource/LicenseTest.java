package cbit.vcell.resource;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Disabled
@Tag("Fast")
public class LicenseTest {
	
	@Test
	public void getMit( ) {
		String mitL = ResourceUtil.resourceToString("/org/vcell/licenses/mit.txt");
		System.out.println(mitL);
	}
	
	@Test
	public void getThirdParty( ) {
		String lic = ResourceUtil.resourceToString("/org/vcell/licenses/thirdpartylicenses.txt");
		System.out.println(lic);
	}
	
	

}
