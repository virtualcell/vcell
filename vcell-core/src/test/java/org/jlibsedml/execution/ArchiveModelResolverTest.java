package org.jlibsedml.execution;

import java.net.URI;
import java.util.Collections;

import org.jlibsedml.ArchiveComponents;
import org.jlibsedml.IModelContent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Tag("Fast")
class ArchiveModelResolverTest {
    private static final String VCML = "<vcml><BioModel Name=\"Lee\"/></vcml>";
    private static final String APPLICATION =
            "#/vcml:vcml/vcml:BioModel/vcml:SimulationSpec[@Name='-sorafenib']";

    private ArchiveModelResolver resolverFor(String filename) {
        IModelContent content = new IModelContent() {
            @Override
            public String getName() { return filename; }
            @Override
            public String getContents() { return VCML; }
        };
        return new ArchiveModelResolver(new ArchiveComponents(
                Collections.singletonList(content), Collections.emptyList()));
    }

    @Test
    void resolvesPlainFilename() {
        assertEquals(VCML, resolverFor("model.vcml").getModelXMLFor(URI.create("model.vcml")));
    }

    @Test
    void resolvesApplicationFragmentWithoutChangingSource() {
        // Reproduce the Lee export's filename-plus-XPath source, rather than
        // substituting a simpler anchor that could hide URI parsing problems.
        URI source = URI.create("Lee_2026_sedml.vcml" + APPLICATION);
        assertEquals(VCML, resolverFor("Lee_2026_sedml.vcml").getModelXMLFor(source));
        assertEquals(APPLICATION.substring(1), source.getFragment());
    }

    @Test
    void resolvesRelativePathWithApplicationFragment() {
        ArchiveModelResolver resolver = resolverFor("models/Lee.vcml");
        resolver.setSedmlPath("experiments/");
        assertEquals(VCML, resolver.getModelXMLFor(URI.create("../models/Lee.vcml" + APPLICATION)));
    }

    @Test
    void resolvesEscapedSpacesWithApplicationFragment() {
        assertEquals(VCML, resolverFor("Lee model.vcml")
                .getModelXMLFor(URI.create("Lee%20model.vcml" + APPLICATION)));
    }

    @Test
    void preservesEncodedHashInArchiveEntryName() {
        // The existing resolver decodes %20 only. Preserve its treatment of
        // other escapes; do not truncate %23 as though it were a literal '#'.
        assertEquals(VCML, resolverFor("Lee%23model.vcml")
                .getModelXMLFor(URI.create("Lee%23model.vcml" + APPLICATION)));
    }

    @Test
    void missingEntryWithFragmentStillReturnsNull() {
        assertNull(resolverFor("model.vcml")
                .getModelXMLFor(URI.create("missing.vcml" + APPLICATION)));
    }
}
