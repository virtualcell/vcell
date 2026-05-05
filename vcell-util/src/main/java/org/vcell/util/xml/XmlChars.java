package org.vcell.util.xml;

/**
 * XML character validation helper. Used to keep invalid-XML chars out of
 * model strings before they reach a CLOB or VCML attribute. See PR drafted
 * after observing two stored BioModels (311226221, 311875206) whose cached
 * VCML contained C0 control characters in reaction-name attributes and
 * therefore failed to load.
 *
 * <p>What we forbid (in addition to XML 1.0's own rules):
 * <ul>
 *   <li>{@code U+FFFD REPLACEMENT CHARACTER} — almost always evidence of
 *       upstream charset corruption, never legitimate in a model identifier
 *       or attribute.</li>
 * </ul>
 *
 * <p>Two contexts are distinguished:
 * <ul>
 *   <li><b>Name mode</b> — for entity names ({@code Reaction.name},
 *       {@code Species.name}, etc.). Forbids whitespace as well.</li>
 *   <li><b>Attribute-content mode</b> — for general XML attribute values.
 *       Allows TAB/LF/CR.</li>
 * </ul>
 *
 * <p>All methods are stateless and thread-safe.
 */
public final class XmlChars {

    private XmlChars() {}

    /**
     * @param cp Unicode codepoint
     * @return true if {@code cp} is allowed inside an XML 1.0 attribute value
     *         (and additionally not {@code U+FFFD}, not an unpaired surrogate,
     *         not a non-character codepoint).
     */
    public static boolean isValidXml10Char(int cp) {
        if (cp < 0x20) {
            return cp == 0x09 || cp == 0x0A || cp == 0x0D;
        }
        if (cp >= 0xD800 && cp <= 0xDFFF) return false;            // unpaired surrogates
        if (cp == 0xFFFD) return false;                             // replacement char (project policy)
        if (cp >= 0xFDD0 && cp <= 0xFDEF) return false;             // non-characters
        if ((cp & 0xFFFE) == 0xFFFE) return false;                  // U+nFFFE / U+nFFFF
        return cp <= 0x10FFFF;
    }

    /**
     * Same as {@link #isValidXml10Char(int)} but additionally forbids any
     * whitespace codepoint. Use for entity names.
     */
    public static boolean isValidNameChar(int cp) {
        if (!isValidXml10Char(cp)) return false;
        return !Character.isWhitespace(cp);
    }

    /**
     * Scans {@code s} for the first invalid char.
     *
     * @param nameMode if true, applies {@link #isValidNameChar(int)};
     *                 else applies {@link #isValidXml10Char(int)}.
     * @return the {@code char} index (UTF-16 unit) of the first invalid
     *         char, or {@code -1} if none.
     */
    public static int firstInvalidIndex(CharSequence s, boolean nameMode) {
        if (s == null) return -1;
        int i = 0;
        while (i < s.length()) {
            int cp = Character.codePointAt(s, i);
            boolean ok = nameMode ? isValidNameChar(cp) : isValidXml10Char(cp);
            if (!ok) return i;
            i += Character.charCount(cp);
        }
        return -1;
    }

    /**
     * Validates {@code value} as an entity name. Throws if any char is
     * forbidden. The exception message identifies the field, the offset,
     * the offending codepoint, and a short snippet for context.
     */
    public static void requireValidName(String value, String fieldDesc) {
        require(value, fieldDesc, true);
    }

    /**
     * Validates {@code value} as XML attribute content. Throws if any char
     * is forbidden.
     */
    public static void requireValidAttributeContent(String value, String fieldDesc) {
        require(value, fieldDesc, false);
    }

    private static void require(String value, String fieldDesc, boolean nameMode) {
        if (value == null) return;
        int idx = firstInvalidIndex(value, nameMode);
        if (idx < 0) return;
        int cp = Character.codePointAt(value, idx);
        throw new IllegalArgumentException(format(value, fieldDesc, idx, cp));
    }

    private static String format(String value, String fieldDesc, int idx, int cp) {
        StringBuilder snippet = new StringBuilder();
        int from = Math.max(0, idx - 10);
        int to = Math.min(value.length(), idx + 10);
        for (int i = from; i < to; i++) {
            char c = value.charAt(i);
            if (c < 0x20 && c != 0x09) {
                snippet.append(String.format("\\x%02x", (int) c));
            } else if (c == 0xFFFD) {
                snippet.append("<U+FFFD>");
            } else {
                snippet.append(c);
            }
        }
        return String.format(
                "invalid character in %s at index %d (codepoint 0x%04X): \"%s\"",
                fieldDesc, idx, cp, snippet.toString());
    }
}
