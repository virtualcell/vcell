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
                (publicationRep.getEndnoteid()!=null) ? Integer.parseInt(publicationRep.getEndnoteid()) : null,
                publicationRep.getUrl(),
                (publicationRep.getWittid()!=null) ? Integer.parseInt(publicationRep.getWittid()) : null,
                bioModelRefs,
                mathModelRefs,
                publicationRep.getDate()
        );
    }

    public PublicationRep toPublicationRep() {
        BioModelReferenceRep[] bioModelReferenceReps = new BioModelReferenceRep[0];
        if (this.biomodelRefs!=null) {
            bioModelReferenceReps = Arrays.stream(this.biomodelRefs).map(b -> b.toBioModelReferenceRep()).toArray(BioModelReferenceRep[]::new);
        }
        MathModelReferenceRep[] mathModelReferenceReps = new MathModelReferenceRep[0];
        if (this.mathmodelRefs!=null){
            mathModelReferenceReps = Arrays.stream(this.mathmodelRefs).map(m -> m.toMathModelReferenceRep()).toArray(MathModelReferenceRep[]::new);
        }
        return new PublicationRep(
                pubKey!=null ? new KeyValue(Long.toString(pubKey)) : null,
                title,
                authors,
                year,
                citation,
                pubmedid,
                doi,
                (endnoteid!=null) ? Integer.toString(endnoteid) : null,
                url,
                bioModelReferenceReps,
                mathModelReferenceReps,
                (wittid!=null) ? Integer.toString(wittid) : null,
                date
        );

    }
}

