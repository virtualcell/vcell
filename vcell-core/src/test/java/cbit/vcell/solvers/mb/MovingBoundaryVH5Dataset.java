package cbit.vcell.solvers.mb;

import java.util.*;

import io.jhdf.api.Attribute;
import io.jhdf.api.Dataset;
import io.jhdf.object.datatype.CompoundDataType;
import io.jhdf.object.datatype.DataType;
import io.jhdf.object.message.DataLayout;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.vcell.util.CastingUtils;
import org.vcell.util.VCAssert;

import edu.uchc.connjur.wb.ExecutionTrace;

public class MovingBoundaryVH5Dataset {

    public static void info(Dataset dataset){
        try {
            System.out.println(dataset.getName());
            System.out.println(dataset.getDataLayout().name());
            infoChild(dataset);
            DataType dataType = dataset.getDataType();
            if (dataType instanceof CompoundDataType compoundDataType) {
                int count = 0;
                for (CompoundDataType.CompoundDataMember member : compoundDataType.getMembers()) {
                    System.out.println("name["+count+"] " + member.getName());
                    System.out.println("data type["+count+"] " + member.getDataType());
                    System.out.println("offset["+count+"] " + member.getOffset());
                    System.out.println("dimension size["+count+"]  " + Collections.singletonList(member.getDimensionSize()));
                    count++;
                }
            }
            Object obj = dataset.getData();
            analyze(obj);

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void infoChild(Dataset ds) {
        System.out.println(ds.getAttributes().values());
        DataType dataType = ds.getDataType();
        System.out.println(dataType.getJavaType().getName());
        if (ds.isCompound()) {
            CompoundDataType cdt = (CompoundDataType) dataType;
            String[] mn = cdt.getMembers().stream().map(CompoundDataType.CompoundDataMember::getName).toArray(String[]::new);
            System.out.println(ArrayUtils.toString(mn));
            System.out.println(Arrays.asList(mn));
            Map<String, Object> obj = (Map<String, Object>) ds.getData();
            Collection<?> coll = CastingUtils.downcast(Collection.class, obj);
            VCAssert.assertTrue(coll.size() == mn.length, "collection matches names");
            int i = 0;
            for(Object o : coll){
                System.out.println(mn[i++] + " ");
                analyze(o);
            }
        }
    }

    public static void meta(Dataset dataset){
        Map<String, Attribute> meta = dataset.getAttributes();
        for(Attribute attr : meta.values()){
            System.out.println(attr.getName()+" = "+attr.getData());
        }
    }

    private static void analyze(Object o){
        Objects.requireNonNull(o);
        Vector<?> v = CastingUtils.downcast(Vector.class, o);
        if(v != null){
            analyzeVector(v);
            return;
        }
        Class<? extends Object> clzz = o.getClass();
        boolean ia = clzz.isArray();
        if(ia){
            System.out.println("array  of " + clzz.getComponentType().getName());
        }

        boolean prim = clzz.isPrimitive();
        if(prim){
            System.out.println("primitive " + clzz.getTypeName());
            return;
        }
        System.out.println("Object type " + ExecutionTrace.justClassName(o));
    }

    private static void analyzeVector(Vector<?> v){
        Objects.requireNonNull(v);
        for(int i = 0; i < v.size(); i++){
            Object vo = v.get(i);
            System.out.println("Vector index " + i + " object type " + ExecutionTrace.justClassName(vo));
            analyze(vo);
        }
    }


}
