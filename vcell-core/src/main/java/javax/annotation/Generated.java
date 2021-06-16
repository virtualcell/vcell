package javax.annotation;

public @interface Generated {
    /**
     * The value element MUST have the name of the code generator. The
     * name is the fully qualified name of the code generator.
     *
     * @return The name of the code generator
     */
    String[] value();

    /**
     * Date when the source was generated. The date element must follow the ISO
     * 8601 standard. For example the date element would have the following
     * value 2017-07-04T12:08:56.235-0700 which represents 2017-07-04 12:08:56
     * local time in the U.S. Pacific Time time zone.
     *
     * @return The date the source was generated
     */
    String date() default "";

    /**
     * A place holder for any comments that the code generator may want to
     * include in the generated code.
     *
     * @return Comments that the code generated included
     */
    String comments() default "";
}
