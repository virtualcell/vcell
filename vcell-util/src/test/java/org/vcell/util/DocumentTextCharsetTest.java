package org.vcell.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Model text (VCML and its annotations) must survive the JVM default charset being windows-1252,
 * as it is for the desktop client on Windows. VCell documents are UTF-8; a conversion that falls
 * back on the platform default either mangles non-ASCII text or, on encode, replaces whatever
 * windows-1252 cannot represent (Greek mu, superscript minus, CJK) with '?'.
 * <p>
 * Only meaningful in a JVM started with {@code -Dfile.encoding=windows-1252}: the
 * {@code windows-1252-default-charset} surefire execution in this module's pom; skipped elsewhere.
 */
@Tag("Fast")
public class DocumentTextCharsetTest {

    static final String TEXT = "typically spanning from 10⁻⁶ to 10⁻³ μm²/s "
            + "(10⁻¹⁸ to 10⁻¹⁵ m²/s) — café, Ångström, 中文";

    @TempDir
    File tempDir;

    @BeforeEach
    void requireWindows1252Default() {
        if (Boolean.getBoolean("vcell.test.windows1252Fork")) {
            assertEquals("windows-1252", Charset.defaultCharset().name(),
                    "the windows-1252 surefire fork did not get -Dfile.encoding=windows-1252");
        } else {
            assumeTrue(Charset.defaultCharset().name().equalsIgnoreCase("windows-1252"),
                    "needs a JVM started with -Dfile.encoding=windows-1252 (see the module pom)");
        }
    }

    /** BigString's compressed wire form is UTF-8, whatever the sender's default charset. */
    @Test
    void bigStringSerializesAsUtf8() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(new BigString(TEXT));
        }
        BigString copy;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))) {
            copy = (BigString) ois.readObject();
        }
        Field f = BigString.class.getDeclaredField("compressedStrBytes");
        f.setAccessible(true);
        byte[] payload = CompressionUtils.uncompress((byte[]) f.get(copy));
        assertArrayEquals(TEXT.getBytes(StandardCharsets.UTF_8), payload, "BigString payload is not UTF-8");
        assertEquals(TEXT, copy.toString());
    }

    @Test
    void readFileToStringReadsUtf8() throws Exception {
        File file = new File(tempDir, "model.vcml");
        Files.write(file.toPath(), TEXT.getBytes(StandardCharsets.UTF_8));
        assertEquals(TEXT, FileUtils.readFileToString(file));
    }
}
