package org.vcell.util;

import java.io.Serializable;

public class Triplet<One, Two, Three> implements Serializable {
    public final One one;
    public final Two two;
    public final Three three;

    public Triplet(One one, Two two, Three three) {
        this.one = one;
        this.two = two;
        this.three = three;
    }

    public String toString() {
        return "<" + (this.one == null ? "null" : this.one.toString()) + ", " +
                (this.two == null ? "null" : this.two.toString()) + ">";
    }

    public int hashCode() {
        int h = 13;
        h += h *37 + (this.one == null ? 0 : this.one.hashCode());
        h += h *37 + (this.two == null ? 0 : this.two.hashCode());
        return h;
    }

    public boolean equals( Object o ) {
        if (this == o) return true;
        if (null == o) return false;
        if (!(o instanceof Triplet<?, ?, ?> otherTriplet)) return false; // each individual member's equal will handle the generic types
        return this.equals(otherTriplet);
    }

    public boolean equals(Triplet <?, ?, ?> o) {
        return this.one.equals(o.one) && this.two.equals(o.two) && this.three.equals(o.three);
    }
}
