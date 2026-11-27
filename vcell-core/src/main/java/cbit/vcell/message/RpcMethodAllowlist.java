package cbit.vcell.message;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * The methods each RPC queue may invoke on its service implementation.
 *
 * {@link VCRpcRequest#rpc(Object)} looks a method up by name among the implementation's public methods,
 * and those classes have many public methods that exist for in-process callers only. This list is
 * the remote API: every name a client proxy (RpcDataServerProxy, RpcDbServerProxy, RpcSimServerProxy,
 * in vcell-core and vcell-api, at any version) has sent. It keeps older desktop clients working;
 * a name no client sends is refused before any lookup.
 *
 * Adding a remote method means adding its name here as well as to the proxy.
 */
public final class RpcMethodAllowlist {

	private RpcMethodAllowlist() { }

	public static final Set<String> DATA = names(
			"addFunction",
			"addFunctions",
			"doDataOperation",
			"fieldDataFileOperation",
			"getDataIdentifiers",
			"getDataProcessingOutput",
			"getDataSetMetadata",
			"getDataSetTimes",
			"getDataSetTimeSeries",
			"getEmptyVtuMeshFiles",
			"getFenicsBundleFile",
			"getFenicsBundleFiles",
			"getFenicsBundleLocatedSamples",
			"getFenicsBundleSamples",
			"getFunctions",
			"getIsODEData",
			"getLangevinBatchData",
			"getLangevinBatchResultSet",
			"getLangevinTrajectory",
			"getLineScan",
			"getMesh",
			"getNFSimMolecularConfigurations",
			"getODEData",
			"getParticleDataBlock",
			"getParticleDataExists",
			"getSimDataBlock",
			"getSupportedExportColorModes",
			"getTimeSeriesValues",
			"getVtuMeshData",
			"getVtuMeshFiles",
			"getVtuTimes",
			"getVtuVarInfos",
			"makeRemoteFile",
			"removeFunction"
	);

	public static final Set<String> DB = names(
			"curate",
			"deleteBioModel",
			"deleteGeometry",
			"deleteMathModel",
			"deleteResultSetExport",
			"deleteVCImage",
			"doTestSuiteOP",
			"fieldDataDBOperation",
			"findReferences",
			"getAllReferences",
			"getBioModelInfo",
			"getBioModelInfos",
			"getBioModelMetaData",
			"getBioModelMetaDatas",
			"getBioModelXML",
			"getBoundSpecies",
			"getDatabaseSpecies",
			"getDictionaryReactions",
			"getExportLog",
			"getExportLogs",
			"getExternalDataIdentifiers",
			"getFieldDataIdentifiers",
			"getGeometryInfo",
			"getGeometryInfos",
			"getGeometryXML",
			"getMathModelInfo",
			"getMathModelInfos",
			"getMathModelMetaData",
			"getMathModelMetaDatas",
			"getMathModelXML",
			"getPreferences",
			"getReactionStep",
			"getReactionStepAsModel",
			"getReactionStepInfos",
			"getResultSetInfos",
			"getSimulationStatus",
			"getSimulationXML",
			"getSpecialUsers",
			"getTestSuite",
			"getTestSuiteInfos",
			"getUserReactionDescriptions",
			"getVCImageInfo",
			"getVCImageXML",
			"getVCInfoContainer",
			"groupAddUser",
			"groupRemoveUser",
			"groupSetPrivate",
			"groupSetPublic",
			"replacePreferences",
			"saveBioModel",
			"saveBioModelAs",
			"saveExternalDataIdentifier",
			"saveGeometry",
			"saveGeometryAs",
			"saveMathModel",
			"saveMathModelAs",
			"saveSimulation",
			"saveVCImage",
			"saveVCImageAs",
			"userRegistrationOP"
	);

	public static final Set<String> DISPATCH = names(
			"getSimpleJobStatus",
			"getSimulationStatus",
			"startSimulation",
			"stopSimulation"
	);

	private static final Map<VCellQueue, Set<String>> BY_QUEUE = Map.of(
			VCellQueue.DataRequestQueue, DATA,
			VCellQueue.DbRequestQueue, DB,
			VCellQueue.SimReqQueue, DISPATCH);

	/**
	 * The allowlist for one of the service queues.
	 * @throws IllegalArgumentException for any other queue: its handler must be given its own list
	 */
	public static Set<String> forQueue(VCellQueue queue) {
		Set<String> allowed = BY_QUEUE.get(queue);
		if (allowed == null) {
			throw new IllegalArgumentException("no RPC method allowlist for queue " + (queue == null ? null : queue.getName())
					+ "; pass the allowed method names to VCRpcMessageHandler explicitly");
		}
		return allowed;
	}

	private static Set<String> names(String... names) {
		return Collections.unmodifiableSet(new TreeSet<>(java.util.Arrays.asList(names)));
	}
}
