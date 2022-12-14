package org.vcell.util.recording;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonStackTraceElementSerializer implements JsonSerializer<StackTraceElement> {
    @Override
    public JsonElement serialize(StackTraceElement src, Type typeOfSrc, JsonSerializationContext context){
        JsonObject jsonObj = new JsonObject();
        String className, fileName, methodName;

        jsonObj.add("className", new JsonPrimitive((className = src.getClassName()) == null ? "<none>" : className));
        jsonObj.add("fileName", new JsonPrimitive((fileName = src.getFileName()) == null ? "<none" : fileName));
        jsonObj.add("lineNumber", new JsonPrimitive(src.getLineNumber()));
        jsonObj.add("methodName", new JsonPrimitive((methodName = src.getMethodName()) == null ? "<none>" : methodName));
        jsonObj.add("isNativeMethod", new JsonPrimitive(src.isNativeMethod()));
        return jsonObj;
    }
}
