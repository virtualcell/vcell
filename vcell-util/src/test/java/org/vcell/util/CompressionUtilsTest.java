package org.vcell.util;

import javax.management.StringValueExp;
import java.io.IOException;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;

import static org.junit.Assert.*;

@Category(Fast.class)
public class CompressionUtilsTest {

    @Test
    public void roundTripCompressionTest() throws IOException{
        assertThrows(NullPointerException.class, () -> CompressionUtils.compress(null));
        assertThrows(NullPointerException.class, () -> CompressionUtils.uncompress(null));

        String message = "Hello VCell!";
        byte[] encodedMessage = message.getBytes();
        byte[] compressedMessage = CompressionUtils.compress(encodedMessage);
        byte[] roundTripMessage = CompressionUtils.uncompress(compressedMessage);
        String returnedMessage = new String(roundTripMessage);
        assertEquals(message, returnedMessage);
    }

    @Test
    public void roundTripCompressionForSerialization() throws IOException, ClassNotFoundException{
        String message = "Hello VCell!";
        StringValueExp messageRepresentation = new StringValueExp(message);
        byte[] compressedRepresentation = CompressionUtils.toCompressedSerialized(messageRepresentation);
        Serializable roundTripSerialization = CompressionUtils.fromCompressedSerialized(compressedRepresentation);
        if(!(roundTripSerialization instanceof StringValueExp roundTripRepresentation))
            throw new AssertionError(roundTripSerialization.getClass().getName()
                    + " does not match up with StringValueExp!");
        assertEquals(message, roundTripRepresentation.getValue());

        // Null Test
        // TODO: Confirm VCell never actually uses this behavior, and put a null check in!
        byte[] compressedNull = CompressionUtils.toCompressedSerialized(null);
        assertNotNull(compressedNull);
        assertThrows(StreamCorruptedException.class, () -> CompressionUtils.fromSerialized(compressedNull));
    }

    @Test
    public void roundTripSerialization() throws IOException, ClassNotFoundException{
        String message = "Hello VCell!";
        StringValueExp messageRepresentation = new StringValueExp(message);
        byte[] compressedRepresentation = CompressionUtils.toSerialized(messageRepresentation);
        Serializable roundTripSerialization = CompressionUtils.fromSerialized(compressedRepresentation);
        if(!(roundTripSerialization instanceof StringValueExp roundTripRepresentation))
            throw new AssertionError(roundTripSerialization.getClass().getName()
                    + " does not match up with StringValueExp!");
        assertEquals(message, roundTripRepresentation.getValue());

        // Null Test
        // TODO: Confirm VCell never actually uses this behavior, and put a null check in!
        byte[] compressedNull = CompressionUtils.toCompressedSerialized(null);
        assertNotNull(compressedNull);
        assertThrows(StreamCorruptedException.class, () -> CompressionUtils.fromSerialized(compressedNull));
    }

    @Test
    public void compressionSizeTest() throws IOException{
        String text = """
                VCell is developed at The Center for Cell Analysis & Modeling, at UConn Health. Established in 1994, CCAM\s
                consists of faculty trained in diverse backgrounds from chemistry, physics, and experimental cell biology\s
                to software engineering. Research at CCAM focuses on the development of new approaches for in vivo\s
                measurements and manipulation of molecular events within the cell, as well as new computational approaches\s
                to organize such data into quantitative models.  CCAM is home to the Microscopy Facility, housing numerous\s
                extensive fluorescent imaging microscopes, and the High Performance Computing facility.
                """;
        StringValueExp messageRepresentation = new StringValueExp(text);
        byte[] compressedSerialization = CompressionUtils.toCompressedSerialized(messageRepresentation);
        byte[] rawBytesSerialization = CompressionUtils.toSerialized(messageRepresentation);
        assertTrue(compressedSerialization.length < rawBytesSerialization.length);
    }
}
