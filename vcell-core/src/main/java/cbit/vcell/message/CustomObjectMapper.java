package cbit.vcell.message;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CustomObjectMapper extends ObjectMapper {
    public CustomObjectMapper(){
        super();
        setUp();
    }

    public CustomObjectMapper(CustomObjectMapper src){
        super(src);
        setUp();
    }

    public CustomObjectMapper(JsonFactory jsonFactory){
        super(jsonFactory);
        setUp();
    }

    private void setUp(){
        registerModule(new JavaTimeModule());
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public <T> T readValue(InputStream src, Class<T> valueType) throws IOException {
        if (valueType == String.class){
            return valueType.cast(new String(src.readAllBytes()));
        } else if (valueType == File.class) {
            File randomTmpFile = File.createTempFile("tmp-file-" + RandomStringUtils.randomAlphabetic(5), ".tmp");
            FileUtils.copyInputStreamToFile(src, randomTmpFile);
            return (T) randomTmpFile;
        }
        return super.readValue(src, valueType);
    }

    @Override
    public <T> T readValue(InputStream src, TypeReference<T> valueTypeRef) throws IOException {
        if (valueTypeRef.getType() == String.class){
            return (T) new String(src.readAllBytes());
        } else if (valueTypeRef.getType() == File.class) {
            File randomTmpFile = File.createTempFile("tmp-file-" + RandomStringUtils.randomAlphabetic(5), ".tmp");
            FileUtils.copyInputStreamToFile(src, randomTmpFile);
            return (T) randomTmpFile;
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
