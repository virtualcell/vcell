package org.vcell.restq.errors.handlers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.vcell.restq.errors.exceptions.UnprocessableContentWebException;

@Provider
public class UnprocessableContentExceptionHandler extends GenericAPIExceptionHandler implements ExceptionMapper<UnprocessableContentWebException>{
    private final static Logger logger = LogManager.getLogger(UnprocessableContentExceptionHandler.class);

    
    @Override
    @APIResponse(responseCode = UnprocessableContentWebException.HTTP_CODE + "", description = UnprocessableContentWebException.DEFAULT_MESSAGE,
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = VCellHTTPError.class))
    )
    public Response toResponse(UnprocessableContentWebException e) {
        return genericExceptionHandler(e, logger);
    }
}
