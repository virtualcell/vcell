package org.vcell.admin.cli.db;

import org.vcell.admin.cli.CLIDatabaseService;
import org.vcell.db.ConnectionFactory;
import org.vcell.util.xml.XmlChars;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.Callable;

@Command(name = "scan-xml-control-chars",
        description = "scan biomodel and mathmodel CLOBs for invalid XML chars (read-only)")
public class XmlControlCharScanCommand implements Callable<Integer> {

    @Option(names = "--output", description = "TSV output path (default: xml-bad-chars.tsv)")
    private Path output = Path.of("xml-bad-chars.tsv");

    @Option(names = "--limit", description = "max rows to scan per kind (0 = no limit)")
    private int limit = 0;

    @Option(names = "--skip-biomodels", description = "skip vc_biomodelxml")
    private boolean skipBiomodels = false;

    @Option(names = "--skip-mathmodels", description = "skip vc_mathmodelxml")
    private boolean skipMathmodels = false;

    @Option(names = "--all-occurrences",
            description = "report every bad codepoint (default: only the first per row)")
    private boolean allOccurrences = false;

    @Option(names = "--progress-every", description = "print progress every N rows (default 500)")
    private int progressEvery = 500;

    @Option(names = "--snippet-radius", description = "chars of context around the bad codepoint (default 20)")
    private int snippetRadius = 20;

    @Option(names = "--max-clob-mb", description = "skip CLOBs larger than this many MB (default 64)")
    private int maxClobMB = 64;

    private static final String BIOMODEL_QUERY =
            "SELECT b.id AS model_id, u.userid, x.bmxml AS xml " +
            "FROM vc_biomodelxml x " +
            "JOIN vc_biomodel b ON x.biomodelref = b.id " +
            "JOIN vc_userinfo u ON b.ownerref = u.id";

    private static final String MATHMODEL_QUERY =
            "SELECT m.id AS model_id, u.userid, x.mmxml AS xml " +
            "FROM vc_mathmodelxml x " +
            "JOIN vc_mathmodel m ON x.mathmodelref = m.id " +
            "JOIN vc_userinfo u ON m.ownerref = u.id";

    public Integer call() {
        long t0 = System.nanoTime();
        System.err.println("scan-xml-control-chars: starting (output=" + output.toAbsolutePath() + ")");
        try (CLIDatabaseService cliDb = new CLIDatabaseService();
             Writer fileWriter = Files.newBufferedWriter(output, StandardCharsets.UTF_8);
             PrintWriter out = new PrintWriter(new BufferedWriter(fileWriter), true /*autoFlush*/)) {
            ConnectionFactory cf = cliDb.getConnectionFactory();
            out.println("kind\tmodel_id\tuserid\toffset\tcp_hex\tsnippet");

            int badRows = 0;
            int totalScanned = 0;
            if (!skipBiomodels) {
                int[] r = scan(cf, "biomodel", BIOMODEL_QUERY, out);
                totalScanned += r[0];
                badRows += r[1];
            }
            if (!skipMathmodels) {
                int[] r = scan(cf, "mathmodel", MATHMODEL_QUERY, out);
                totalScanned += r[0];
                badRows += r[1];
            }

            long ms = (System.nanoTime() - t0) / 1_000_000L;
            System.err.println("scan-xml-control-chars: done. scanned=" + totalScanned
                    + " bad_rows=" + badRows + " elapsed_ms=" + ms);
            return 0;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return 1;
        }
    }

    private int[] scan(ConnectionFactory cf, String kind, String sql, PrintWriter out) throws Exception {
        Object lock = new Object();
        Connection con = cf.getConnection(lock);
        int scanned = 0;
        int badRows = 0;
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = executeStreaming(ps)) {
            while (rs.next()) {
                if (limit > 0 && scanned >= limit) break;
                scanned++;
                long modelId = rs.getLong("model_id");
                String userid = rs.getString("userid");
                Clob clob = rs.getClob("xml");
                if (clob == null) continue;
                int hits = scanClob(kind, modelId, userid, clob, out);
                if (hits > 0) badRows++;
                if (scanned % progressEvery == 0) {
                    System.err.println("[" + kind + "] scanned=" + scanned + " bad_rows=" + badRows);
                }
            }
        } finally {
            cf.release(con, lock);
        }
        System.err.println("[" + kind + "] complete: scanned=" + scanned + " bad_rows=" + badRows);
        return new int[] { scanned, badRows };
    }

    private static ResultSet executeStreaming(PreparedStatement ps) throws Exception {
        // Encourage the driver not to materialize the full result set in memory.
        try {
            ps.setFetchSize(50);
        } catch (Exception ignore) { /* not all drivers support */ }
        return ps.executeQuery();
    }

    /**
     * Scan a single CLOB. Returns the number of bad codepoints reported.
     */
    private int scanClob(String kind, long modelId, String userid, Clob clob, PrintWriter out)
            throws Exception {
        long lengthChars;
        try {
            lengthChars = clob.length();
        } catch (Exception e) {
            // some drivers/CLOBs don't support length(); fall back to streaming read with a size cap
            lengthChars = -1;
        }
        long maxChars = (long) maxClobMB * 1024L * 1024L / 2L; // chars are 2 bytes
        if (lengthChars > maxChars) {
            System.err.println("[" + kind + "/" + modelId + "/" + userid
                    + "] skipping: CLOB length " + lengthChars + " chars > cap");
            return 0;
        }
        StringBuilder doc = new StringBuilder(lengthChars > 0 ? (int) lengthChars : 16384);
        try (Reader r = clob.getCharacterStream()) {
            char[] buf = new char[8192];
            int n;
            while ((n = r.read(buf)) != -1) {
                doc.append(buf, 0, n);
                if (doc.length() > maxChars) {
                    System.err.println("[" + kind + "/" + modelId + "/" + userid
                            + "] skipping: CLOB length > cap");
                    return 0;
                }
            }
        }
        // doc is now a CharSequence; XmlChars.firstInvalidIndex walks codepoints with proper
        // surrogate handling. For all-occurrences we loop ourselves.
        int hits = 0;
        int from = 0;
        while (true) {
            int idx = firstInvalidIndexFrom(doc, from);
            if (idx < 0) break;
            int cp = Character.codePointAt(doc, idx);
            String snippet = renderSnippet(doc, idx, cp);
            out.printf("%s\t%d\t%s\t%d\t0x%04X\t%s%n",
                    kind, modelId, userid, idx, cp, snippet);
            hits++;
            if (!allOccurrences) break;
            from = idx + Character.charCount(cp);
        }
        return hits;
    }

    private static int firstInvalidIndexFrom(CharSequence s, int from) {
        int i = from;
        int len = s.length();
        while (i < len) {
            int cp = Character.codePointAt(s, i);
            if (!XmlChars.isValidXml10Char(cp)) return i;
            i += Character.charCount(cp);
        }
        return -1;
    }

    private String renderSnippet(CharSequence doc, int idx, int cp) {
        int radius = Math.max(snippetRadius, 0);
        int from = Math.max(0, idx - radius);
        int charLen = Character.charCount(cp);
        int to = Math.min(doc.length(), idx + charLen + radius);
        StringBuilder sb = new StringBuilder();
        for (int i = from; i < idx; i++) appendDisplay(sb, doc.charAt(i));
        sb.append('[').append(String.format("U+%04X", cp)).append(']');
        for (int i = idx + charLen; i < to; i++) appendDisplay(sb, doc.charAt(i));
        // strip TSV-breaking whitespace
        return sb.toString().replace('\t', ' ').replace('\r', ' ').replace('\n', ' ');
    }

    private static void appendDisplay(StringBuilder sb, char c) {
        if (c == 0xFFFD) {
            sb.append("<U+FFFD>");
        } else if (c < 0x20 && c != 0x09) {
            sb.append(String.format("\\x%02X", (int) c));
        } else {
            sb.append(c);
        }
    }
}
