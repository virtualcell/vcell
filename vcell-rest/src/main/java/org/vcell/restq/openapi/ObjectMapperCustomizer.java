package org.vcell.restq.openapi;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import jakarta.ws.rs.ext.Provider;
import org.vcell.util.document.KeyValue;

import java.io.IOException;

@Provider
public class ObjectMapperCustomizer implements io.quarkus.jackson.ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper objectMapper) {
        SimpleModule myModule = new SimpleModule();
        myModule.addSerializer(KeyValue.class, new KeyValueSerializer());
        objectMapper.registerModule(myModule);
    }

    private static class KeyValueSerializer extends StdSerializer<KeyValue> {

        public KeyValueSerializer() {
            super(KeyValue.class);
        }

        @Override
        public void serialize(KeyValue kv, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(kv.toString());
        }
    }
}
