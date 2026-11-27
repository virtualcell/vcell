package cbit.vcell.solver.stoch;

import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.XmlHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ucar.ma2.ArrayChar;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The Hy3S input file (NetCDF) names the messaging endpoint the way the vcell-hy3s solvers read it: the REST
 * bridge's {@code host:restport}, as SolverFileWriter writes for the other solvers - not the legacy AMQP
 * {@code failover:(tcp://host:port)}.
 */
@Tag("Fast")
public class NetCDFWriterJmsBrokerTest {

	private static final String SIMTASK_RESOURCE = "SimID_274641698_0__0.simtask.xml"; // Hybrid (Gibson + Milstein)

	private final Map<String, String> savedProperties = new HashMap<>();

	@TempDir
	File tempDir;

	private void setProperty(String key, String value) {
		savedProperties.putIfAbsent(key, System.getProperty(key));
		System.setProperty(key, value);
	}

	@BeforeEach
	public void setup() {
		setProperty(PropertyLoader.jmsSimHostExternal, "broker.example.org");
		setProperty(PropertyLoader.jmsSimRestPortExternal, "30163");
		setProperty(PropertyLoader.jmsSimPortExternal, "31618");
		setProperty(PropertyLoader.jmsUser, "clientUser");
		setProperty(PropertyLoader.jmsPasswordValue, "not-a-secret");
	}

	@AfterEach
	public void teardown() {
		savedProperties.forEach((key, value) -> {
			if (value == null) System.clearProperty(key); else System.setProperty(key, value);
		});
		savedProperties.clear();
	}

	@Test
	public void brokerUrlIsRestHostAndPort() throws Exception {
		assertEquals("broker.example.org:30163", NetCDFWriter.jmsBrokerUrl());
	}

	@Test
	public void hybridInputFileCarriesRestBroker() throws Exception {
		SimulationTask simTask;
		try (InputStream in = getClass().getResourceAsStream(SIMTASK_RESOURCE)) {
			assertNotNull(in, SIMTASK_RESOURCE);
			simTask = XmlHelper.XMLToSimTask(new String(in.readAllBytes(), StandardCharsets.UTF_8));
		}
		File ncFile = new File(tempDir, "hybrid.nc");
		new NetCDFWriter(simTask, ncFile.getAbsolutePath(), true).writeHybridInputFile();

		NetcdfFile nc = NetcdfFile.open(ncFile.getAbsolutePath()); // this netcdf-java predates AutoCloseable
		try {
			Variable broker = nc.findVariable("JMS_BROKER");
			assertNotNull(broker, "messaging on: JMS_BROKER is written");
			assertEquals("broker.example.org:30163", ((ArrayChar) broker.read()).getString());
		} finally {
			nc.close();
		}
	}
}
