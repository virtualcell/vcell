package org.vcell.util.recording;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import com.google.gson.reflect.TypeToken;


public class GsonExceptionSerializer implements JsonSerializer<Exception> {
    @Override
    public JsonElement serialize(Exception src, Type typeOfSrc, JsonSerializationContext context){
        JsonSerializer<StackTraceElement> steSerializer = new GsonStackTraceElementSerializer();
        JsonArray steArray = new JsonArray(src.getStackTrace().length);
        JsonObject jsonObj = new JsonObject();
        jsonObj.add("name", new JsonPrimitive(src.getClass().getCanonicalName()));
        jsonObj.add("cause", new JsonPrimitive(String.valueOf(src.getCause())));
        jsonObj.add("message", new JsonPrimitive(src.getMessage()));
        for (StackTraceElement ste : src.getStackTrace()){
            steArray.add(steSerializer.serialize(ste, (new TypeToken<StackTraceElement>(){}).getType(), context));
        }
        jsonObj.add("stackTrace", steArray);
        return jsonObj;
    }
}
