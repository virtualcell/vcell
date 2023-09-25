package org.vcell.rest.n5data;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.janelia.saalfeldlab.n5.Bzip2Compression;
import org.janelia.saalfeldlab.n5.GzipCompression;
import org.janelia.saalfeldlab.n5.RawCompression;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.engine.adapter.HttpRequest;
import org.vcell.rest.VCellApiApplication;
import org.vcell.util.document.User;

import java.util.ArrayList;

/*
Handling receiving data

Logging, tracking
 */

public final class N5Restlet extends Restlet {
	private final static Logger lg = LogManager.getLogger(N5Restlet.class);

	public enum CompressionType {
		raw,
		gzip,
		bzip2
	};


	public N5Restlet(Context context) {
		super(context);
	}

	@Override
	public void handle(Request req, Response response) {
		if (req.getMethod().equals(Method.GET)){
			try {
				VCellApiApplication application = ((VCellApiApplication)getApplication());
				User user = application.getVCellUser(req.getChallengeResponse(), VCellApiApplication.AuthenticationPolicy.ignoreInvalidCredentials);
				HttpRequest request = (HttpRequest)req;
				Form form = request.getResourceRef().getQueryAsForm();

				String simID = form.getFirstValue(VCellApiApplication.N5_SIMID, true);

				String compresionLevel = form.getFirstValue(VCellApiApplication.N5_COMPRESSION, true);
				CompressionType compresionType = null;

				String[] species = form.getValuesArray(VCellApiApplication.N5_EXPORT_SPECIES, true);
				System.out.println(simID);

				String supportedSpecies = form.getFirstValue(VCellApiApplication.N5_SUPPORTED_SPECIES, true);

				N5Service n5Service = new N5Service(simID, user);

				if(supportedSpecies != null){
					ArrayList<String> listOfSupportedSpecies = n5Service.supportedSpecies();
					Gson gson = new Gson();
					response.setEntity(gson.toJson(listOfSupportedSpecies), MediaType.APPLICATION_JSON);
					return;
				}


				if (compresionLevel != null){
					if (compresionLevel.equalsIgnoreCase(VCellApiApplication.N5_COMPRESSION_RAW)){
						compresionType = CompressionType.raw;
					} else if (compresionLevel.equalsIgnoreCase(VCellApiApplication.N5_COMPRESSION_BZIP)) {
						compresionType = CompressionType.gzip;
					} else if (compresionLevel.equalsIgnoreCase(VCellApiApplication.N5_COMPRESSION_GZIP)) {
						compresionType = CompressionType.bzip2;
					}
				}

				if (simID != null && species != null){
					switch (compresionType) {
						case raw:
							n5Service.exportToN5(species, new RawCompression());
						case gzip:
							n5Service.exportToN5(species, new GzipCompression());
						case bzip2:
							n5Service.exportToN5(species, new Bzip2Compression());
						default:
							lg.info("failed to specify compression type desired");
							response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
							response.setEntity("failed to specify desired export type", MediaType.TEXT_PLAIN);
					}
				}

			} catch (Exception e) {
				lg.error(e.getMessage(), e);
				response.setStatus(Status.SERVER_ERROR_INTERNAL);
				response.setEntity("failed to export sim data to N5 format: "+e.getMessage(), MediaType.TEXT_PLAIN);
			}
		}
	}
}