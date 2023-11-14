package org.vcell.util;

public class CastingUtils {

    /**
     * downcast to object or return null
     *
     * @param clzz return type, not null
     * @param obj  may be null
     * @return obj as T or null if obj is null or not of type T
     */
    public static <T> T downcast(Class<? extends T> clzz, Object obj){
        if(clzz == null || !clzz.isInstance(obj)) return null;
        return clzz.cast(obj);
    }

    /**
     * attempt to class object to specified type
     *
     * @param clzz desired type, not null
     * @param obj  object to cast; if null returns !isGood( ) CastInfo<T>
     * @return CastInfo<T> object describing results
     */
    public static <T> CastInfo<T> attemptCast(Class<T> clzz, Object obj){
        final String requiredTypeName = clzz.getName();
        if(obj == null) return new FailInfo<>(requiredTypeName, "null");
        final String actualTypeName = obj.getClass().getName();
        T result = CastingUtils.downcast(clzz, obj);
        if(result == null) return new FailInfo<>(requiredTypeName, actualTypeName);
        return new SucceedInfo<>(requiredTypeName, actualTypeName, result);
    }

    public interface CastInfo<T> {
        /**
         * was cast successful?
         */
        boolean isGood();

        /**
         * return type converted object
         *
         * @return non null pointer
         * @throws ProgrammingException if {@link #isGood()} returns false
         */
        T get();

        /**
         * @return name of desired class if {@link #isGood()} returns false
         */
        String requiredName();

        /**
         * @return name of provided object if {@link #isGood()} returns false
         */
        String actualName();

        /**
         * @return message explaining cast
         */
        String castMessage();
    }

    private abstract static class CiBase<T> implements CastInfo<T> {
        final String requiredTypeName;
        final String actualTypeName;

        protected CiBase(String requiredTypeName, String actualTypeName){
            this.requiredTypeName = requiredTypeName;
            this.actualTypeName = actualTypeName;
        }

        public String requiredName(){
            return requiredTypeName;
        }

        public String actualName(){
            return actualTypeName;
        }

        public String castMessage(){
            return "cast from " + actualTypeName + " to " + requiredTypeName;
        }
    }

    private static class FailInfo<T> extends CiBase<T> {

        FailInfo(String requiredTypeName, String actualTypeName){
            super(requiredTypeName, actualTypeName);
        }

        public boolean isGood(){
            return false;
        }

        public T get(){
            String msg = "Programming exception, " + castMessage() + " failed";
            throw new ProgrammingException(msg);
        }
    }

    private static class SucceedInfo<T> extends CiBase<T> {
        final T obj;

        SucceedInfo(String requiredTypeName, String actualTypeName, T obj){
            super(requiredTypeName, actualTypeName);
            this.obj = obj;
        }

        public boolean isGood(){
            return true;
        }

        public T get(){
            return obj;
        }
    }
}
