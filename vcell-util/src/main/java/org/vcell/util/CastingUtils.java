package org.vcell.util;

public class CastingUtils {

    /**
     * downcast to object or return null
     * @param clzz return type, not null
     * @param obj may be null
     * @return obj as T or null if obj is null or not of type T
     */
    public static <T> T downcast(Class<T> clzz, Object obj) {
        if (obj != null && clzz.isAssignableFrom(obj.getClass())) {
            @SuppressWarnings("unchecked")
            T rval = (T) obj;
            return rval;
        }
        return null;
    }

    /**
     * attempt to class object to specified type
     * @param clzz desired type, not null
     * @param obj object to cast; if null returns !isGood( ) CastInfo<T>
     * @return CastInfo<T> object describing results
     */
    public static <T> CastInfo<T> attemptCast(Class<T> clzz, Object obj) {
        final String rname = clzz.getName();
        if (obj != null) {
            final String aname = obj.getClass().getName();
            T result = downcast(clzz, obj);
            if (result == null) {
                return new FailInfo<>(rname, aname);
            }
            return new SucceedInfo<>(rname,aname,result);
        }
        return new FailInfo<>(rname, "null");
    }

    public interface CastInfo <T> {
        /**
         * was cast successful?
         */
        boolean isGood( );
        /**
         * return type converted object
         * @return non null pointer
         * @throws ProgrammingException if {@link #isGood()} returns false
         */
        T get( );
        /**
         * @return name of desired class if {@link #isGood()} returns false
         */
        String requiredName( );
        /**
         * @return name of provided object if {@link #isGood()} returns false
         */
        String actualName( );

        /**
         * @return message explaining cast
         */
        String castMessage( );
    }

    private abstract static class CiBase<T> implements CastInfo<T> {
        final String rname;
        final String aname;
        protected CiBase(String rname, String aname) {
            this.rname = rname;
            this.aname = aname;
        }
        public String requiredName() { return rname; }
        public String actualName() { return aname; }
        public String castMessage( ) {
            return "cast from " + aname + " to " + rname;
        }
    }

    private static class FailInfo<T> extends CiBase<T> {

        FailInfo(String rname, String aname) {
            super(rname,aname);
        }
        public boolean isGood() {
            return false;
        }
        public T get() {
            String msg = "Programming exception, " + castMessage() + " failed";
            throw new ProgrammingException(msg);
        }
    }

    private static class SucceedInfo<T> extends CiBase<T> {
        final T obj;

        SucceedInfo(String rname,String aname, T obj) {
            super(rname,aname);
            this.obj = obj;
        }
        public boolean isGood() {
            return true;
        }
        public T get() {
            return obj;
        }
    }
}
