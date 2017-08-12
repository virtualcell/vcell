package cbit.vcell.resource;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
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
