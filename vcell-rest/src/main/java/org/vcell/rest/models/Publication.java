package org.vcell.rest.models;

import cbit.vcell.modeldb.BioModelReferenceRep;
import cbit.vcell.modeldb.MathModelReferenceRep;
import cbit.vcell.modeldb.PublicationRep;
import cbit.vcell.parser.ExpressionException;
import org.vcell.util.document.KeyValue;

import java.util.Arrays;
import java.util.Date;

public record Publication(
        String pubKey,
        String title,
        String[] authors,
        Integer year,
        String citation,
        String pubmedid,
        String doi,
        String endnoteid,
        String url,
        String wittid,
        BiomodelRef[] biomodelRefs,
        MathmodelRef[] mathmodelRefs,
        Date date) {

    public static Publication fromPublicationRef(PublicationRep publicationRep) {
        BiomodelRef[] bioModelRefs = Arrays.stream(publicationRep.getBiomodelReferenceReps()).map(BiomodelRef::fromBioModelReferenceRep).toArray(BiomodelRef[]::new);
        MathmodelRef[] mathModelRefs = Arrays.stream(publicationRep.getMathmodelReferenceReps()).map(MathmodelRef::fromMathModelReferenceRep).toArray(MathmodelRef[]::new);
        return new Publication(
                publicationRep.getPubKey().toString(),
                publicationRep.getTitle(),
                publicationRep.getAuthors(),
                publicationRep.getYear(),
                publicationRep.getCitation(),
                publicationRep.getPubmedid(),
                publicationRep.getDoi(),
                publicationRep.getEndnoteid(),
                publicationRep.getUrl(),
                publicationRep.getWittid(),
                bioModelRefs,
                mathModelRefs,
                publicationRep.getDate()
        );
    }

    public PublicationRep toPublicationRep() {
        BioModelReferenceRep[] bioModelReferenceReps = Arrays.stream(this.biomodelRefs).map(b -> b.toBioModelReferenceRep()).toArray(BioModelReferenceRep[]::new);
        MathModelReferenceRep[] mathModelReferenceReps = Arrays.stream(this.mathmodelRefs).map(m -> m.toMathModelReferenceRep()).toArray(MathModelReferenceRep[]::new);
        return new PublicationRep(
                new KeyValue(pubKey),
                title,
                authors,
                year,
                citation,
                pubmedid,
                doi,
                endnoteid,
                url,
                bioModelReferenceReps,
                mathModelReferenceReps,
                wittid,
                date
        );

    }
}

