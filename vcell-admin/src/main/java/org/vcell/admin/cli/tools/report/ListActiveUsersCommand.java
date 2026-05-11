package org.vcell.admin.cli.tools.report;

import cbit.vcell.modeldb.AdminDBTopLevel.ActiveUser;
import org.vcell.admin.cli.CLIDatabaseService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * CSV of distinct users who submitted at least one simulation job during {@code [start, end]}.
 * Columns: {@code userid,email,company,country,tld_extension}.
 *
 * <p>{@code userid} is the VCell login id from {@code vc_userinfo.userid}. {@code company} comes
 * from {@code vc_userinfo.companyname}, {@code country} from {@code vc_userinfo.country} (both
 * may be blank). {@code tld_extension} is derived from the email's domain — the substring after
 * the last {@code .}, lowercased — and is blank if the email has no dot.
 *
 * <p>Output: CSV with header on stdout, or to {@code -o &lt;file&gt;}.
 *
 * <p>Same cascade-delete caveat as {@link CountSimJobsInDbCommand}: a user whose only jobs in
 * the period have since had their parent simulation removed will not appear here.
 *
 * <p>See {@code .claude/commands/admin-report.md}.
 */
@Command(name = "list-active-users",
        description = "CSV of users who submitted a sim job in [start, end] "
                + "(userid, email, company, country, tld_extension).")
public class ListActiveUsersCommand implements Callable<Integer> {

    @Option(names = "--start", required = true,
            description = "Period start date (yyyy-MM-dd, inclusive).")
    private LocalDate start;

    @Option(names = "--end", required = true,
            description = "Period end date (yyyy-MM-dd, inclusive).")
    private LocalDate end;

    @Option(names = {"-o", "--output-file"},
            description = "Write CSV to this file instead of stdout.")
    private File outputFile;

    @Override
    public Integer call() throws Exception {
        try (CLIDatabaseService db = new CLIDatabaseService()) {
            List<ActiveUser> users = db.listActiveUsersInPeriod(start, end);
            try (Writer w = outputFile != null
                    ? new FileWriter(outputFile)
                    : new java.io.OutputStreamWriter(System.out);
                 PrintWriter pw = new PrintWriter(w)) {
                pw.println("userid,email,company,country,tld_extension");
                for (ActiveUser u : users) {
                    pw.println(csv(u.userid()) + "," + csv(u.email()) + "," + csv(u.company())
                            + "," + csv(u.country()) + "," + csv(tldOf(u.email())));
                }
                pw.flush();
            }
            System.err.println("Active users in [" + start + ", " + end + "]: " + users.size());
        }
        return 0;
    }

    /** Last dot-segment of an email's domain, lowercased; empty string if none. */
    static String tldOf(String email) {
        if (email == null) return "";
        int at = email.lastIndexOf('@');
        if (at < 0 || at == email.length() - 1) return "";
        String domain = email.substring(at + 1);
        int dot = domain.lastIndexOf('.');
        if (dot < 0 || dot == domain.length() - 1) return "";
        return domain.substring(dot + 1).toLowerCase();
    }

    /** RFC-4180-ish CSV escaping: quote when the value contains comma, quote, CR, or LF. */
    static String csv(String s) {
        if (s == null) return "";
        boolean needsQuote = s.indexOf(',') >= 0 || s.indexOf('"') >= 0
                || s.indexOf('\n') >= 0 || s.indexOf('\r') >= 0;
        if (!needsQuote) return s;
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
}
