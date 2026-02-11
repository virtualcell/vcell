package org.vcell.restq.openapi;

import io.quarkus.smallrye.openapi.OpenApiFilter;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;
import org.jboss.jandex.IndexView;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.lang.reflect.Field;

/**
 * Filter to sort HTTP responses for each operation in the OpenAPI document (fixing the random reordering of responses).
 */
@OpenApiFilter(OpenApiFilter.RunStage.BUILD)
public class SortApiResponsesFilter implements OASFilter {

    public SortApiResponsesFilter(IndexView _view) {
    }

    @Override
    public Schema filterSchema(Schema schema) {
        if (schema == null || schema.getProperties() == null || schema.getType() == null) {
            return schema;
        }

        try {
            Class<?> modelClass = Class.forName(schema.getType().name());
            Map<String, Schema> properties = schema.getProperties();

            Iterator<Map.Entry<String, Schema>> iterator = properties.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Schema> entry = iterator.next();
                String propertyName = entry.getKey();

                Field field = findField(modelClass, propertyName);
                if (field != null && java.lang.reflect.Modifier.isPrivate(field.getModifiers())) {
                    iterator.remove();
                }
            }

        } catch (ClassNotFoundException ignored) {
            // Model class not found — skip filtering
        }

        return schema;
    }

    private Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
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
