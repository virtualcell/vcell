package cbit.vcell.client.task;

import org.vcell.util.ProgrammingException;
import org.vcell.util.VCAssert;

import java.util.Hashtable;

public class ResultsHashUtils {
    // TODO: Change this class to `ResultsHash extends Hashtable<String, Object>`,
    //  and implement everywhere the hash is already used
    /**
     * fetch and type convert object from hash
     * @param hashTable not null
     * @param key name (not null)
     * @param clzz required type, not null
     * @param required throw exception if required and not present
     * @return object cast to correct type or possibly null if !required
     * @throws ProgrammingException if key not of required type
     * @throws ProgrammingException required is true and key not present
     */

    public static <T> T fetch(Hashtable<String, Object> hashTable, String key, Class<T> clzz, boolean required) {
        if (null == hashTable) throw new IllegalArgumentException("hashTable may not be null");
        if (null == key) throw new IllegalArgumentException("key may not be null");
        if (null == clzz) throw new IllegalArgumentException("clzz may not be null");

        if (required && !hashTable.containsKey(key)) throw new ProgrammingException("key " + key + " not found in async hashtable");

        Object obj = hashTable.get(key);
        if (obj == null) return null;

        Class<?> classOfObj = obj.getClass();
        if (clzz.isAssignableFrom(classOfObj)) return clzz.cast(obj);

        String exceptionFormat = "object `%s` of type `%s` is not an instance of `%s`";
        String exceptionMessage = String.format(exceptionFormat, obj, classOfObj.getName(), clzz.getName());
        throw new ProgrammingException(exceptionMessage);
    }

    /**
     * typesafe (verified) fetch of object from hash
     * @param hashTable non null
     * @param keyInfo non null
     * @return object of correct type
     */
    static protected Object fetch(Hashtable<String, Object> hashTable, AsynchClientTask.KeyInfo keyInfo) {
        Object obj = hashTable.get(keyInfo.name);
        VCAssert.assertTrue(obj != null, "ClientTaskDispatcher failed to verify object");
        VCAssert.assertTrue(keyInfo.clzz.isAssignableFrom(obj.getClass()),"ClientTaskDispatcher failed to verify type");
        return obj;
    }
}
