package org.vcell.util.xml;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class XmlCharsTest {

    @Test
    public void plainAscii_isValid() {
        assertEquals(-1, XmlChars.firstInvalidIndex("Reaction_1", true));
        assertEquals(-1, XmlChars.firstInvalidIndex("Reaction_1", false));
    }

    @Test
    public void unicodeLetters_areValid() {
        // greek mu, en-dash; legitimate in scientific names
        assertEquals(-1, XmlChars.firstInvalidIndex("μ-prot", true));
        assertEquals(-1, XmlChars.firstInvalidIndex("k_14–3–3", true));
    }

    @Test
    public void supplementaryPlane_isValid() {
        // U+1F600 (surrogate pair) — not whitespace, valid in name mode
        String emoji = new String(Character.toChars(0x1F600));
        assertEquals(-1, XmlChars.firstInvalidIndex(emoji, true));
    }

    @Test
    public void tabLfCr_validInAttributes_invalidInNames() {
        assertEquals(-1, XmlChars.firstInvalidIndex("a\tb", false));
        assertEquals(-1, XmlChars.firstInvalidIndex("a\nb", false));
        assertEquals(-1, XmlChars.firstInvalidIndex("a\rb", false));
        assertEquals(1, XmlChars.firstInvalidIndex("a\tb", true));
        assertEquals(1, XmlChars.firstInvalidIndex("a\nb", true));
        assertEquals(1, XmlChars.firstInvalidIndex("a\rb", true));
    }

    @Test
    public void spaceRejectedInName_acceptedInAttribute() {
        assertEquals(-1, XmlChars.firstInvalidIndex("a b", false));
        assertEquals(1, XmlChars.firstInvalidIndex("a b", true));
    }

    @Test
    public void controlChar_0x13_rejected() {
        // observed in biomodel 311226221
        String bad = "k_reid_3";
        assertEquals(4, XmlChars.firstInvalidIndex(bad, true));
        assertEquals(4, XmlChars.firstInvalidIndex(bad, false));
    }

    @Test
    public void controlChar_0x1C_rejected() {
        // observed in biomodel 311875206 (paired with U+FFFD)
        String bad = "namesuffix";
        assertEquals(4, XmlChars.firstInvalidIndex(bad, true));
    }

    @Test
    public void replacementChar_rejectedByPolicy() {
        // U+FFFD — XML 1.0 technically allows it; project policy rejects
        String bad = "name�suffix";
        assertFalse(XmlChars.isValidXml10Char(0xFFFD));
        assertEquals(4, XmlChars.firstInvalidIndex(bad, true));
        assertEquals(4, XmlChars.firstInvalidIndex(bad, false));
    }

    @Test
    public void realWorldPattern_replacementPlus0x1C_rejected() {
        // exact pattern observed in cached VCML of biomodel 311875206
        String bad = "rxn�end";
        // U+FFFD comes first
        assertEquals(3, XmlChars.firstInvalidIndex(bad, true));
    }

    @Test
    public void unpairedSurrogate_rejected() {
        assertFalse(XmlChars.isValidXml10Char(0xD800));
        assertFalse(XmlChars.isValidXml10Char(0xDFFF));
        // A lone high surrogate as a single char
        String lone = "x" + (char) 0xD800 + "y";
        assertEquals(1, XmlChars.firstInvalidIndex(lone, false));
    }

    @Test
    public void nonCharacterCodepoints_rejected() {
        assertFalse(XmlChars.isValidXml10Char(0xFDD0));
        assertFalse(XmlChars.isValidXml10Char(0xFDEF));
        assertFalse(XmlChars.isValidXml10Char(0xFFFE));
        assertFalse(XmlChars.isValidXml10Char(0xFFFF));
        assertFalse(XmlChars.isValidXml10Char(0x1FFFE));
        assertFalse(XmlChars.isValidXml10Char(0x10FFFF));
    }

    @Test
    public void nullInput_isHandled() {
        assertEquals(-1, XmlChars.firstInvalidIndex(null, true));
        XmlChars.requireValidName(null, "any");
        XmlChars.requireValidAttributeContent(null, "any");
    }

    @Test
    public void emptyString_isValid() {
        assertEquals(-1, XmlChars.firstInvalidIndex("", true));
        XmlChars.requireValidName("", "any");
    }

    @Test
    public void requireValidName_throwsWithFieldOffsetCodepoint() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> XmlChars.requireValidName("rxnend", "Reaction.name"));
        String msg = ex.getMessage();
        assertNotNull(msg);
        assertTrue(msg.contains("Reaction.name"), msg);
        assertTrue(msg.contains("index 3"), msg);
        assertTrue(msg.contains("0x0013"), msg);
        assertTrue(msg.contains("\\x13"), msg);
    }

    @Test
    public void requireValidAttributeContent_acceptsTabAndNewline() {
        XmlChars.requireValidAttributeContent("a\tb\nc", "some.attr");
    }

    @Test
    public void requireValidAttributeContent_rejectsReplacementChar() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> XmlChars.requireValidAttributeContent("a�b", "some.attr"));
        assertTrue(ex.getMessage().contains("0xFFFD"), ex.getMessage());
        assertTrue(ex.getMessage().contains("<U+FFFD>"), ex.getMessage());
    }

    @Test
    public void firstInvalidIndex_returnsUtf16Index_notCodepointIndex() {
        // surrogate pair (length 2) followed by bad char at char index 2
        String emoji = new String(Character.toChars(0x1F600));
        String s = emoji + "";
        assertEquals(emoji.length(), XmlChars.firstInvalidIndex(s, false));
        assertEquals(2, XmlChars.firstInvalidIndex(s, false));
    }
}
