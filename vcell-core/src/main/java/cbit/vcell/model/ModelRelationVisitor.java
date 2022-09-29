package cbit.vcell.model;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionUtils;
import cbit.vcell.units.VCUnitDefinition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Compare;
import org.vcell.util.Relatable;
import org.vcell.util.RelationVisitor;
import org.vcell.util.document.KeyValue;

public class ModelRelationVisitor implements RelationVisitor {

    Logger logger = LogManager.getLogger(ModelRelationVisitor.class);

    private final boolean bStrict;

    public ModelRelationVisitor(boolean bStrict) {
        this.bStrict = bStrict;
    }

    @Override
    public boolean relate(int int1, int int2) {
        return int1 == int2;
    }

    @Override
    public boolean relate(boolean bool1, boolean bool2) {
        return bool1 == bool2;
    }

    @Override
    public boolean relate(String str1, String str2) {
        return (str1 != null) && (str2 != null) && str1.equals(str2);
    }

    @Override
    public boolean relate(Expression exp1, Expression exp2) {
        if (bStrict){
            return Compare.isEqual(exp1, exp2);
        }else{
            return ExpressionUtils.functionallyEquivalent(exp1, exp2, false);
        }
    }

    @Override
    public boolean relate(Relatable relatable1, Relatable relatable2) {
        logger.info("relate() not yet implemented for "+relatable1+", defering to visitor again");
        if (relatable1==null || relatable2==null){
            return false;
        }
        return relatable1.relate(relatable2,this);
    }

     @Override
    public <T extends Relatable> boolean relateOrNull(T relatable1, T relatable2) {
        if (relatable1==null && relatable2==null){
            return true;
        }
        return relate(relatable1, relatable2);
    }

    @Override
    public <T extends Relatable> boolean relateOrNull(T[] relatable1, T[] relatable2) {
        if (relatable1==null && relatable2==null){
            return true;
        }
        return relate(relatable1, relatable2);
    }

    @Override
    public boolean relateOrNull(String str1, String str2) {
        if (str1==null && str2==null){
            return true;
        }
        return relate(str1, str2);
    }

    @Override
    public boolean relate(KeyValue key1, KeyValue key2) {
        return key1.compareEqual(key2);
    }

    @Override
    public boolean relate(VCUnitDefinition unitDefinition, VCUnitDefinition unitDefinition1) {
        return unitDefinition.compareEqual(unitDefinition1);
    }

    @Override
    public boolean relate(SpeciesPattern speciesPattern, SpeciesPattern speciesPattern1) {
        return Compare.isEqual(speciesPattern, speciesPattern1);
    }

    @Override
    public boolean relateStrict(Diagram[] fieldDiagrams, Diagram[] fieldDiagrams1) {
        return Compare.isEqualStrict(fieldDiagrams, fieldDiagrams1);
    }

    @Override
    public boolean relate(ModelUnitSystem unitSystem, ModelUnitSystem unitSystem1) {
        return Compare.isEqual(unitSystem, unitSystem1);
    }

    @Override
    public boolean relate(Model.StructureTopology structureTopology, Model.StructureTopology structureTopology1) {
        return Compare.isEqual(structureTopology, structureTopology1);
    }

    @Override
    public boolean relate(Model.ElectricalTopology electricalTopology, Model.ElectricalTopology electricalTopology1) {
        return Compare.isEqual(electricalTopology, electricalTopology1);
    }

    @Override
    public boolean relate(Model.RbmModelContainer rbmModelContainer, Model.RbmModelContainer rbmModelContainer1) {
        return Compare.isEqual(rbmModelContainer, rbmModelContainer1);
    }

    @Override
    public <T extends Relatable> boolean relate(T[] v1, T[] v2) {
        if (v1==null || v2==null){
            return false;
        }
        if (v1.length != v2.length){
            return false;
        }

        int arrayLen = v1.length;
        //
        // check that every element v1[i] == v2[i]
        //
        boolean bSame = true;
        int ii = 0;
        for (ii=0;ii<arrayLen;ii++){
            if (!relate(v1[ii],v2[ii])){
                bSame = false;
                break;
            }
        }
        if (bSame) {
            return true;
        }

        //
        // check that every element in v1 is in v2
        //
        for (int i=ii;i<arrayLen;i++){
            T c1 = v1[i];
            boolean bFound = false;
            for (int j=ii;j<arrayLen;j++){
                T c2 = v2[j];
                if (relate(c2,c1)){
                    bFound = true;
                    break;
                }
            }
            if (!bFound){
                return false;
            }
        }
        //
        // check that every element in v2 is in v1
        //
        for (int i=ii;i<arrayLen;i++){
            T c2 = v2[i];
            boolean bFound = false;
            for (int j=ii;j<arrayLen;j++){
                T c1 = v1[j];
                if (relate(c1,c2)){
                    bFound = true;
                    break;
                }
            }
            if (!bFound){
                return false;
            }
        }
        return true;
    }

    @Override
    public <T extends Relatable> boolean relateStrict(T[] v1, T[] v2) {
        if (v1==null || v2==null){
            return false;
        }
        if (v1.length != v2.length){
            return false;
        }

        int arrayLen = v1.length;
        //
        // check that every element v1[i] == v2[i]
        //
        boolean bSame = true;
        int ii = 0;
        for (ii=0;ii<arrayLen;ii++){
            if (!relate(v1[ii],v2[ii])){
                bSame = false;
                break;
            }
        }
        return bSame;
    }



}
