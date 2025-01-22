package org.vcell.restq.openapi;

import io.quarkus.smallrye.openapi.OpenApiFilter;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;
import org.jboss.jandex.IndexView;

import java.util.Map;
import java.util.TreeMap;

/**
 * Filter to sort HTTP responses for each operation in the OpenAPI document (fixing the random reordering of responses).
 */
@OpenApiFilter(OpenApiFilter.RunStage.BUILD)
public class SortApiResponsesFilter implements OASFilter {

    public SortApiResponsesFilter(IndexView _view) {
    }

    @Override
    public Operation filterOperation(Operation operation) {
        if (operation.getResponses() != null) {
            APIResponses responses = operation.getResponses();
            Map<String, APIResponse> sortedResponses = new TreeMap<>(responses.getAPIResponses());

            // remove old responses which may not be sorted
            String[] responseCodes = responses.getAPIResponses().keySet().toArray(new String[0]);
            for (String responseCode : responseCodes) {
                responses.removeAPIResponse(responseCode);
            }

            // add back the responses in sorted order
            responses.setAPIResponses(sortedResponses);
        }
        return operation;
    }
}
