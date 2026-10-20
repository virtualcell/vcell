package org.vcell.restq.openapi;

import io.quarkus.smallrye.openapi.OpenApiFilter;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.media.MediaType;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.jboss.jandex.IndexView;

/**
 * Filter to drop response content whose schemas are all empty ({@code schema: {}}).
 * <p>
 * Newer smallrye-openapi (Quarkus 3.40) describes endpoints returning a bare {@code jakarta.ws.rs.core.Response}
 * as having an {@code application/json} body of unknown shape. Those endpoints return no entity, and the
 * generated clients would then try to parse the empty body as JSON (a {@code void} method becomes one
 * returning {@code Object}). Removing the content restores the "no body" description.
 */
@OpenApiFilter(OpenApiFilter.RunStage.BUILD)
public class DropEmptyResponseContentFilter implements OASFilter {

    public DropEmptyResponseContentFilter(IndexView _view) {
    }

    @Override
    public APIResponse filterAPIResponse(APIResponse apiResponse) {
        Content content = apiResponse.getContent();
        if (content != null && content.getMediaTypes() != null && !content.getMediaTypes().isEmpty()
                && content.getMediaTypes().values().stream().allMatch(DropEmptyResponseContentFilter::hasEmptySchema)) {
            apiResponse.setContent(null);
        }
        return apiResponse;
    }

    private static boolean hasEmptySchema(MediaType mediaType) {
        Schema schema = mediaType.getSchema();
        return schema == null || schema.getAll() == null || schema.getAll().isEmpty();
    }
}
