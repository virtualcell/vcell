package org.vcell.restclient;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class CustomObjectMapper extends ObjectMapper {
    public CustomObjectMapper(){
        super();
    }

    public CustomObjectMapper(CustomObjectMapper src){
        super(src);
    }

    public CustomObjectMapper(JsonFactory jsonFactory){
        super(jsonFactory);
    }

    @Override
    public <T> T readValue(InputStream src, Class<T> valueType) throws IOException {
        if (valueType == String.class){
            return valueType.cast(new String(src.readAllBytes()));
        }
        return super.readValue(src, valueType);
    }

    @Override
    public <T> T readValue(InputStream src, TypeReference<T> valueTypeRef) throws IOException {
        if (valueTypeRef.getType() == String.class){
            return (T) new String(src.readAllBytes());
        }
        return super.readValue(src, valueTypeRef);
    }

    @Override
    public ObjectMapper copy() {
        return new CustomObjectMapper(this);
    }

    @Override
    public ObjectMapper copyWith(JsonFactory factory) {
        return new CustomObjectMapper(factory);
    }
}
