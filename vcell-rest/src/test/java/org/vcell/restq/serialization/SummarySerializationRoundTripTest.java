package org.vcell.restq.serialization;

import cbit.image.GIFImage;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.restclient.utils.DtoModelTransforms;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.GroupAccessSome;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Database-free, both-ways round-trip guard for the {@code /api/v1} summary DTOs
 * (see {@code org.vcell.restq.handlers.dto}). It exercises the full production pipeline without a
 * database or a running server:
 *
 * <pre>
 *   native POJO --(server transform)--&gt; server DTO --(Jackson JSON)--&gt; client DTO
 *               --(DtoModelTransforms)--&gt; native POJO
 * </pre>
 *
 * The old summary endpoints Jackson-serialized core domain objects directly, and the core objects'
 * getter-driven serialization diverged from the OpenAPI schema and the generated client — producing
 * a run of client-breaking bugs on real data (missing GroupAccess {@code type} discriminator;
 * {@code User.key} as a nested object; a schema/wire {@code hiddenMembers} mismatch that NPE'd the
 * client; a preview decode NPE that aborted the stream). Dedicated DTOs (fields == getters ==
 * schema) fix the class of bug; this test locks it down by reconstructing the native object and
 * asserting it survives the trip.
 */
@Tag("Fast")
public class SummarySerializationRoundTripTest {

    private final ObjectMapper server = new ObjectMapper();
    private final ObjectMapper client = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /** native VersionRep JSON must deserialize into the client model and back to a native Version. */
    private Version roundTrip(Version version) throws Exception {
        org.vcell.restq.handlers.dto.VersionRep serverDto = org.vcell.restq.handlers.dto.VersionRep.fromVersion(version);
        String json = server.writeValueAsString(serverDto);
        org.vcell.restclient.model.VersionRep clientDto =
                client.readValue(json, org.vcell.restclient.model.VersionRep.class);
        return DtoModelTransforms.versionDTOToVersion(clientDto);
    }

    @Test
    public void sharedModel_groupAccessSome_roundTrips() throws Exception {
        User owner = new User("alice", new KeyValue("1"));
        User member = new User("bob", new KeyValue("2"));
        boolean[] hidden = new boolean[]{ false };
        BigDecimal hash = GroupAccess.calculateHash(new KeyValue[]{ member.getID() }, hidden);
        GroupAccessSome some = new GroupAccessSome(new BigDecimal(100), hash, new User[]{ member }, hidden);
        Version original = new Version(new KeyValue("11"), "SharedModel", owner, some,
                null, new BigDecimal(1), new Date(1_700_000_000_000L), VersionFlag.Current, "annotation");

        Version rt = roundTrip(original);

        assertEquals("SharedModel", rt.getName());
        assertEquals("alice", rt.getOwner().getName());
        assertEquals("1", rt.getOwner().getID().toString()); // User.key survives as a string, not {"value":1}
        assertEquals(1_700_000_000_000L, rt.getDate().getTime());
        assertEquals(VersionFlag.Current.getIntValue(), rt.getFlag().getIntValue());

        GroupAccessSome rtGroup = assertInstanceOf(GroupAccessSome.class, rt.getGroupAccess(),
                "shared access must round-trip to GroupAccessSome (no InvalidTypeIdException)");
        assertEquals(new BigDecimal(100), rtGroup.getGroupid());
        // getNormalGroupMembers() / getHiddenGroupMembers() must not NPE (the hiddenMembers-null bug)
        assertEquals("bob", rtGroup.getNormalGroupMembers()[0].getName());
        assertNull(rtGroup.getHiddenGroupMembers());
    }

    @Test
    public void undecodableGifPreview_roundTripsWithNullSize() throws Exception {
        GIFImage gif = new GIFImage("this-is-not-a-real-gif".getBytes());

        org.vcell.restq.handlers.dto.PreviewRep serverDto = org.vcell.restq.handlers.dto.PreviewRep.fromGIFImage(gif);
        assertNull(serverDto.width(), "an undecodable gif has no decoded size");
        String json = server.writeValueAsString(serverDto);
        org.vcell.restclient.model.PreviewRep clientDto =
                client.readValue(json, org.vcell.restclient.model.PreviewRep.class);

        // the client rebuilds the GIFImage from the base64 bytes; the bytes survive and getSize() is null, not NPE
        GIFImage rt = new GIFImage(Base64.getDecoder().decode(clientDto.getGifBase64()));
        assertArrayEquals("this-is-not-a-real-gif".getBytes(), rt.getGifEncodedData());
        assertNull(rt.getSize(), "an undecodable preview must round-trip with a null size, not crash");
    }
}
