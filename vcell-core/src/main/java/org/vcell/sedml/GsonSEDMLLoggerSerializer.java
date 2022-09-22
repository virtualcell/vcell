package org.vcell.sedml;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


public class GsonSEDMLLoggerSerializer implements JsonSerializer<SEDMLRecorder> {
    @Override
    public JsonElement serialize(SEDMLRecorder src, Type typeOfSrc, JsonSerializationContext context){

        JsonObject jsonObj = new JsonObject();
        jsonObj.add("identifier", new JsonPrimitive(src.getIdentifier()));
        jsonObj.add("operation", new JsonPrimitive(String.valueOf(src.getOperationAsString())));
        jsonObj.add("taskLogs", context.serialize(src.getLogs()));
        return jsonObj;
    }
}