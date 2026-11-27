package org.vcell.client.viz;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Proxy;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.vis.vcell.SubdomainInfo;

import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.DataSetController;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.LocalDataSetController;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

/**
 * The field viewer's server over ONE finite-volume run on disk, for looking at (and timing) a real run by hand:
 * a local quick run, or a run's files copied from the data server. It reads the run as the desktop reads a local
 * quick run ({@code ClientSimManager}: a {@link DataSetControllerImpl} with no cache), or with {@code --cache} as
 * the data server reads one (a cache table, as {@code FieldViewerServerFvTest} sets up).
 * <p>
 * The run's files ({@code SimID_<key>_<job>_*}) must lie in {@code <dataRoot>/<user>/}. Prints the viewer's URL
 * and runs until killed. Timings of each stage of a request go to the {@code org.vcell.client.viz} logger at
 * debug level ({@link StageTimer}).
 * <p>
 * {@code --latency=<ms>} stands in for a run read from the data server: every data-manager call waits that long
 * and its result is copied through Java serialization, as a remote call's is, and each call is logged with its
 * time and size. It models the remote seam's cost per call; the data server's own work is still done here.
 *
 * <pre>
 * java -cp &lt;vcell-client test classpath&gt; org.vcell.client.viz.FieldViewerRunServer \
 *     &lt;webapp-viewer dir&gt; &lt;dataRoot&gt; &lt;user&gt; &lt;simKey&gt; [job] [--cache] [--latency=&lt;ms&gt;]
 * </pre>
 */
public final class FieldViewerRunServer {

	private FieldViewerRunServer() {
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 4) {
			throw new IllegalArgumentException("usage: FieldViewerRunServer <webapp-viewer dir> <dataRoot> <user> <simKey> [job] [--cache] [--latency=<ms>]");
		}
		File webapp = new File(args[0]).getAbsoluteFile();
		File root = new File(args[1]).getAbsoluteFile();
		User owner = new User(args[2], new KeyValue("1"));
		String simKey = args[3];
		int job = args.length > 4 && !args[4].startsWith("--") ? Integer.parseInt(args[4]) : 0;
		boolean cache = java.util.Arrays.asList(args).contains("--cache");
		long latency = java.util.Arrays.stream(args).filter(a -> a.startsWith("--latency=")).findFirst()
				.map(a -> Long.parseLong(a.substring("--latency=".length()))).orElse(-1L);
		PropertyLoader.setProperty(PropertyLoader.fieldViewerStaticDir, webapp.getPath());
		PropertyLoader.setProperty(PropertyLoader.fieldViewerPort, "0");

		DataSetControllerImpl controller = new DataSetControllerImpl(
				cache ? new Cachetable(10 * Cachetable.minute, 1_000_000_000L) : null, root, cache ? root : null);
		DataSetController local = new LocalDataSetController(null, controller, null, owner);
		DataSetController reader = latency < 0 ? local : remote(local, latency);
		VCDataManager dataManager = new VCDataManager(() -> reader);
		VCSimulationDataIdentifier vcdID = new VCSimulationDataIdentifier(
				new VCSimulationIdentifier(new KeyValue(simKey), owner), job);
		SubdomainInfo subdomains = SubdomainInfo.read(
				new File(new File(root, owner.getName()), "SimID_" + simKey + "_" + job + "_.subdomains"));
		FieldViewerServer.register(vcdID, dataManager, subdomains, "run " + simKey);
		int port = FieldViewerServer.start();
		System.out.println("RUN http://127.0.0.1:" + port + "/?sim=" + simKey + "&job=" + job);
		System.out.flush();
		Thread.currentThread().join();
	}

	/** {@code local} as if over the remote seam: each call waits {@code latency} ms, and its result is serialized. */
	private static DataSetController remote(DataSetController local, long latency) {
		org.apache.logging.log4j.Logger lg = org.apache.logging.log4j.LogManager.getLogger(FieldViewerRunServer.class);
		return (DataSetController) Proxy.newProxyInstance(DataSetController.class.getClassLoader(),
				new Class<?>[] { DataSetController.class }, (proxy, method, methodArgs) -> {
					long start = System.nanoTime();
					Thread.sleep(latency);
					Object result;
					try {
						result = method.invoke(local, methodArgs);
					} catch (java.lang.reflect.InvocationTargetException e) {
						throw e.getCause();
					}
					int bytes = 0;
					if (result instanceof Serializable) {
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						try (ObjectOutputStream out = new ObjectOutputStream(buffer)) {
							out.writeObject(result);
						}
						bytes = buffer.size();
						try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()))) {
							result = in.readObject();
						}
					}
					lg.debug("remote call {} {} ms, {} bytes", method.getName(), Math.round((System.nanoTime() - start) / 1e6),
							bytes);
					return result;
				});
	}
}
