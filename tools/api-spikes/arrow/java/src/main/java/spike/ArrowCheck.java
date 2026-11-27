package spike;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.Float8Vector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.ipc.ArrowStreamReader;
import org.apache.arrow.vector.ipc.ArrowStreamWriter;
import org.apache.arrow.vector.types.FloatingPointPrecision;
import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.types.pojo.FieldType;
import org.apache.arrow.vector.types.pojo.Schema;

/** Arrow part of the spike (style guide 2.3/2.4). java ArrowCheck read <file> | write <file> */
public class ArrowCheck {
    static final Double[] VALUES = { 1.25, null, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 2.0 };
    static int fails = 0;
    static void check(String what, boolean ok, Object detail) {
        System.out.println((ok ? "PASS " : "FAIL ") + what + (ok ? "" : "   <- " + detail));
        if (!ok) fails++;
    }

    public static void main(String[] args) throws Exception {
        try (RootAllocator alloc = new RootAllocator()) {
            if (args[0].equals("write")) {
                ArrowType f64 = new ArrowType.FloatingPoint(FloatingPointPrecision.DOUBLE);
                Schema schema = new Schema(List.of(
                        new Field("time", FieldType.notNullable(f64), null),
                        new Field("p0", FieldType.nullable(f64), null)), Map.of("variable", "Ran_cyt", "points", "[[1.0,2.5]]"));
                try (VectorSchemaRoot root = VectorSchemaRoot.create(schema, alloc);
                     FileOutputStream out = new FileOutputStream(args[1]);
                     ArrowStreamWriter w = new ArrowStreamWriter(root, null, out.getChannel())) {
                    Float8Vector time = (Float8Vector) root.getVector("time");
                    Float8Vector p0 = (Float8Vector) root.getVector("p0");
                    for (int i = 0; i < VALUES.length; i++) {
                        time.setSafe(i, 0.5 * i);
                        if (VALUES[i] == null) p0.setNull(i); else p0.setSafe(i, VALUES[i]);
                    }
                    root.setRowCount(VALUES.length);
                    w.start(); w.writeBatch(); w.end();
                }
                System.out.println("wrote " + args[1]);
            } else {
                try (FileInputStream in = new FileInputStream(args[1]);
                     ArrowStreamReader r = new ArrowStreamReader(in.getChannel(), alloc)) {
                    r.loadNextBatch();
                    VectorSchemaRoot root = r.getVectorSchemaRoot();
                    Float8Vector p0 = (Float8Vector) root.getVector("p0");
                    check("null stays null (validity bitmap), distinct from NaN", p0.isNull(1) && p0.getNullCount() == 1, p0);
                    check("NaN stays NaN (valid, non-null)", !p0.isNull(2) && Double.isNaN(p0.get(2)), p0);
                    check("+Infinity / -Infinity survive", p0.get(3) == Double.POSITIVE_INFINITY && p0.get(4) == Double.NEGATIVE_INFINITY, p0);
                    check("finite values bit-exact", p0.get(0) == 1.25 && p0.get(5) == 2.0, p0);
                    check("schema metadata carries the request echo", "Ran_cyt".equals(root.getSchema().getCustomMetadata().get("variable")), root.getSchema());
                    check("native byte order is little-endian (this platform)", ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN, ByteOrder.nativeOrder());
                }
                System.out.println(fails == 0 ? "ALL PASSED" : fails + " FAILED");
                System.exit(fails == 0 ? 0 : 1);
            }
        }
    }
}
