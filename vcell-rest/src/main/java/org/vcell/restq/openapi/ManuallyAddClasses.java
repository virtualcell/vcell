package org.vcell.restq.openapi;

import io.quarkus.smallrye.openapi.OpenApiFilter;
import io.smallrye.openapi.api.models.media.DiscriminatorImpl;
import io.smallrye.openapi.api.models.media.SchemaImpl;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.media.Discriminator;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.GroupAccessAll;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.GroupAccessSome;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

@OpenApiFilter(OpenApiFilter.RunStage.BUILD)
public class ManuallyAddClasses implements OASFilter {

    @Override
    public void filterOpenAPI(OpenAPI openAPI) {
//        addGroupAccessToSchema(openAPI);
    }

    private void addGroupAccessToSchema(OpenAPI openAPI){
        Map<String, Schema> schemas = openAPI.getComponents().getSchemas();
        Class<?>[] classesToBeAdded = {GroupAccessAll.class, GroupAccessSome.class, GroupAccessNone.class};
        for (Class<?> clas : classesToBeAdded){
            if (!schemas.containsKey(clas.getSimpleName())){
                openAPI.getComponents().addSchema(clas.getSimpleName(), createSchema(clas));
            }
        }

        Schema groupAccessSchema = schemas.get(GroupAccess.class.getSimpleName());
        Discriminator discriminator = new DiscriminatorImpl();
        discriminator.setPropertyName("type");
        discriminator.addMapping("some", String.format("#/components/schemas/%s", GroupAccessSome.class.getSimpleName()));
        discriminator.addMapping("none", String.format("#/components/schemas/%s", GroupAccessNone.class.getSimpleName()));
        discriminator.addMapping("all", String.format("#/components/schemas/%s", GroupAccessAll.class.getSimpleName()));
        groupAccessSchema.discriminator(discriminator);
    }

    private Schema createSchema(Class<?> schemaClass){
        Schema schema = new SchemaImpl();
        for (Field field : schemaClass.getDeclaredFields()){
            Schema addedSchema = new SchemaImpl();
            if (field.getType().isPrimitive()){
                Schema.SchemaType primType = null;
                for (Schema.SchemaType availableType : Schema.SchemaType.values()){
                    if (availableType.toString().equalsIgnoreCase(field.getType().getSimpleName())){
                        primType = availableType;
                    }
                }
                schema.type(primType);
                addedSchema.ref(field.getType().getSimpleName());
                schema.addProperty(field.getName(), addedSchema);
            } else if (field.getType().isArray()) {
                Schema itemSchema = new SchemaImpl();
                String fieldTypeName = field.getType().getSimpleName();
                // Don't want array brackets [], which is why a substring is used
                itemSchema.ref(String.format("#/components/schemas/%s", fieldTypeName.substring(0, fieldTypeName.length() - 2)));

                addedSchema.type(Schema.SchemaType.ARRAY);
                addedSchema.setItems(itemSchema);
                String fieldName = field.getName();
                schema.addProperty(fieldName, addedSchema);
            } else{
                schema.type(Schema.SchemaType.OBJECT);
                addedSchema.ref(String.format("#/components/schemas/%s", field.getType().getSimpleName()));
                schema.addProperty(field.getName(), addedSchema);
            }
        }
        return schema;
    }
}
