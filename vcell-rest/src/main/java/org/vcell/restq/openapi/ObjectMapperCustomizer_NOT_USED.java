package org.vcell.restq.openapi;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class ObjectMapperCustomizer_NOT_USED implements io.quarkus.jackson.ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper objectMapper) {
//        SimpleModule myModule = new SimpleModule();
//        myModule.addSerializer(Date.class, new LocalDateTimeFromDateSerializer());
//        objectMapper.registerModule(myModule);
    }

    private static class LocalDateTimeFromDateSerializer extends StdSerializer<Date> {

        public LocalDateTimeFromDateSerializer() {
            super(Date.class);
        }

        @Override
        public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            LocalDate localDate = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(date));
            jsonGenerator.writeString(localDate.toString()); // Or use a DateTimeFormatter for custom formatting
        }
    }
}
