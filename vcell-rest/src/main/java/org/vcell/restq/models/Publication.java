package org.vcell.restq.models;

import cbit.vcell.modeldb.BioModelReferenceRep;
import cbit.vcell.modeldb.MathModelReferenceRep;
import cbit.vcell.modeldb.PublicationRep;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.vcell.util.document.KeyValue;

import java.util.Arrays;
import java.util.Date;

public record Publication(
        Long pubKey,
        String title,
        String[] authors,
        Integer year,
        String citation,
        String pubmedid,
        String doi,
        Integer endnoteid,
        String url,
        Integer wittid,
        BiomodelRef[] biomodelRefs,
        MathmodelRef[] mathmodelRefs,
        @JsonFormat(pattern="yyyy-MM-dd")
        Date date) {

    public static Publication fromPublicationRef(PublicationRep publicationRep) {
        BiomodelRef[] bioModelRefs = Arrays.stream(publicationRep.getBiomodelReferenceReps()).map(BiomodelRef::fromBioModelReferenceRep).toArray(BiomodelRef[]::new);
        MathmodelRef[] mathModelRefs = Arrays.stream(publicationRep.getMathmodelReferenceReps()).map(MathmodelRef::fromMathModelReferenceRep).toArray(MathmodelRef[]::new);
        return new Publication(
                Long.parseLong(publicationRep.getPubKey().toString()),
                publicationRep.getTitle(),
                publicationRep.getAuthors(),
                publicationRep.getYear(),
                publicationRep.getCitation(),
                publicationRep.getPubmedid(),
                publicationRep.getDoi(),
                Integer.parseInt(publicationRep.getEndnoteid()),
                publicationRep.getUrl(),
                Integer.parseInt(publicationRep.getWittid()),
                bioModelRefs,
                mathModelRefs,
                publicationRep.getDate()
        );
    }

    public PublicationRep toPublicationRep() {
        BioModelReferenceRep[] bioModelReferenceReps = Arrays.stream(this.biomodelRefs).map(b -> b.toBioModelReferenceRep()).toArray(BioModelReferenceRep[]::new);
        MathModelReferenceRep[] mathModelReferenceReps = Arrays.stream(this.mathmodelRefs).map(m -> m.toMathModelReferenceRep()).toArray(MathModelReferenceRep[]::new);
        return new PublicationRep(
                pubKey!=null ? new KeyValue(Long.toString(pubKey)) : null,
                title,
                authors,
                year,
                citation,
                pubmedid,
                doi,
                Integer.toString(endnoteid),
                url,
                bioModelReferenceReps,
                mathModelReferenceReps,
                Integer.toString(wittid),
                date
        );

    }
}

