package org.vcell.core;

import cbit.image.GIFImage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression guard for a serialization NPE in the REST /api/v1/vcInfoContainer response.
 * <p>
 * {@link GIFImage#getSize()} lazily decoded the gif via {@code ImageIO.read()} on every call and
 * dereferenced the result. {@code ImageIO.read()} returns {@code null} when no reader can decode the
 * bytes (an unusual/blank thumbnail), so {@code getSize()} threw an NPE. Because Jackson serializes
 * the {@code preview} (GIFImage) as part of a large streamed response, that NPE aborted the whole
 * vcInfoContainer body mid-stream, and the desktop client saw truncated/garbled JSON. getSize() now
 * returns {@code null} for an undecodable gif instead of throwing.
 */
@Tag("Fast")
public class GIFImageSerializationTest {

    @Test
    public void undecodableGifSizeSerializesWithoutNpe() throws Exception {
        // bytes that no ImageIO reader can decode -> getJavaImage() returns null
        GIFImage gif = new GIFImage("this-is-not-a-real-gif".getBytes());

        assertNull(gif.getSize(), "getSize() must return null for an undecodable gif rather than NPE");

        String json = new ObjectMapper().writeValueAsString(gif);
        assertTrue(json.contains("\"size\":null"),
                "GIFImage must serialize with size:null for an undecodable gif (regression: NPE aborted the stream): " + json);
    }
}
