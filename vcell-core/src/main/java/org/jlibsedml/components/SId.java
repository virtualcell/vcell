package org.jlibsedml.components;

import javax.annotation.Nonnull;

public record SId(String string) {

    public SId {
        if (!isValidSId(string)) {
            throw new IllegalArgumentException("Invalid SId: " + string);
        }
    }

    public static String unwrap(SId sid) {
        return sid == null ? null : sid.string();
    }

    public int length() {
        return this.string.length();
    }

    /*
     *      letter ::= 'a'..'z','A'..'Z'
     *      digit  ::= '0'..'9'
     *      idChar ::= letter | digit | '_'
     *      SId    ::= ( letter | '_' ) idChar*
     */
    public static boolean isValidSId(String string) {
        if (string == null) return false;
        return string.matches("[a-zA-Z_]\\w*");
    }

    @Override
    @Nonnull
    public String toString() {
        return "SId [" + this.string() + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (!(other instanceof SId sid)) return false;
        return this.string().equals(sid.string());
    }

    @Override
    public int hashCode() {
        return this.string().hashCode();
    }
}
