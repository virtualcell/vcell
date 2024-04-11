package cbit.vcell.message.server.bootstrap.client;

import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory.RemoteProxyException;
import cbit.vcell.server.VCellConnection;
import org.junit.jupiter.api.*;
import org.vcell.api.client.VCellApiClient;
import org.vcell.api.client.query.BioModelsQuerySpec;
import org.vcell.api.common.BiomodelRepresentation;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;
import org.vcell.util.document.VCInfoContainer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@Disabled
@Tag("Fast")
public class RemoteProxyVCellConnectionFactoryTest {
	
	private RemoteProxyVCellConnectionFactory factory = null;
	private VCellConnection vcConn = null;
	private VCellApiClient apiClient = null;

	@BeforeEach
	public void setUp() throws Exception {
		UserLoginInfo userLoginInfo = new UserLoginInfo("schaff",new DigestedPassword("xxxxxxx"));
		factory = new RemoteProxyVCellConnectionFactory("localhost", 8099, "");
		apiClient = factory.getVCellApiClient();
		vcConn = factory.createVCellConnection(userLoginInfo);
	}

	@AfterEach
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException {
		BiomodelRepresentation[] biomodelReps = apiClient.getBioModels(new BioModelsQuerySpec());
		assertNotNull(biomodelReps);
		VCInfoContainer vcInfoContainer = null;
		try {
			vcInfoContainer = vcConn.getUserMetaDbServer().getVCInfoContainer();
		} catch (DataAccessException | RemoteProxyException e) {
			e.printStackTrace();
            fail("exception");
		}
		assertNotNull(vcInfoContainer);
	}
	
}
