package org.vcell.rest.n5data;

import cbit.vcell.math.MathException;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.janelia.saalfeldlab.n5.Bzip2Compression;
import org.janelia.saalfeldlab.n5.Compression;
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
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

/*
Handling receiving data

Logging, tracking
 */

public final class N5ExportRestlet extends Restlet {
	private final static Logger lg = LogManager.getLogger(N5ExportRestlet.class);

	public enum CompressionType {
		raw,
		gzip,
		bzip2
	};


	public N5ExportRestlet(Context context) {
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

				//TODO: Potential for abuse, I'm taking an object from the routes path and turning it into a string
				String simID = request.getAttributes().get(VCellApiApplication.N5_SIMID).toString();


				String[] species = form.getValuesArray(VCellApiApplication.N5_EXPORT_SPECIES, true);
				String compresionLevel = form.getFirstValue(VCellApiApplication.N5_COMPRESSION, true);


				CompressionType compresionType = null;

				if (compresionLevel != null){
					if (compresionLevel.equalsIgnoreCase(VCellApiApplication.N5_EXPORT_COMPRESSION_RAW)){
						compresionType = CompressionType.raw;
					} else if (compresionLevel.equalsIgnoreCase(VCellApiApplication.N5_EXPORT_COMPRESSION_BZIP)) {
						compresionType = CompressionType.gzip;
					} else if (compresionLevel.equalsIgnoreCase(VCellApiApplication.N5_EXPORT_COMPRESSION_GZIP)) {
						compresionType = CompressionType.bzip2;
					}
				}

				if (simID != null && species != null){
					N5Service n5Service = new N5Service(simID, user);

					Consumer<Compression> exporation = (compression) -> {
						try {
							n5Service.exportToN5(species, compression);
							response.setStatus(Status.SUCCESS_OK);
							response.setEntity("exported "+ simID + " successfully", MediaType.TEXT_PLAIN);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					};
					switch (compresionType) {
						case raw:
							exporation.accept(new RawCompression());
							return;
						case gzip:
							exporation.accept(new GzipCompression());
							return;
						case bzip2:
							exporation.accept(new Bzip2Compression());
							return;
						default:
							lg.info("failed to specify compression type desired");
							response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
							response.setEntity("failed to specify desired compression type", MediaType.TEXT_PLAIN);
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