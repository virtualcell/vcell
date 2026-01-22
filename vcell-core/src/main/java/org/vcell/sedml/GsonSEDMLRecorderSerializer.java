package org.vcell.sedml;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


public class GsonSEDMLRecorderSerializer implements JsonSerializer<SedMLRecorder> {
    @Override
    public JsonElement serialize(SedMLRecorder src, Type typeOfSrc, JsonSerializationContext context){

        JsonObject jsonObj = new JsonObject();
        jsonObj.add("identifier", new JsonPrimitive(src.getIdentifier()));
        jsonObj.add("operation", new JsonPrimitive(String.valueOf(src.getOperationAsString())));
        jsonObj.add("taskLogs", context.serialize(src.getRecords()));
        return jsonObj;
    }
}