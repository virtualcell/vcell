package org.vcell.restq.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.db.KeyFactory;
import org.vcell.optimization.CopasiUtils;
import org.vcell.optimization.jtd.OptProblem;
import org.vcell.optimization.jtd.OptProgressReport;
import org.vcell.optimization.jtd.Vcellopt;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.models.OptJobStatus;
import org.vcell.restq.models.OptimizationJobStatus;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;

@ApplicationScoped
public class OptimizationRestService {
    private static final Logger lg = LogManager.getLogger(OptimizationRestService.class);

    private final AgroalConnectionFactory connectionFactory;
    private final KeyFactory keyFactory;
    private final ObjectMapper objectMapper;

    public OptimizationRestService(AgroalConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.keyFactory = connectionFactory.getKeyFactory();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Submit a new optimization job. Writes the OptProblem to the filesystem and creates a database record.
     *
     * @param optProblem the optimization problem to solve
     * @param parestDataDir the directory for optimization data files (e.g. /simdata/parest_data)
     * @param user the authenticated user submitting the job
     * @return the initial job status with the assigned job ID
     */
    public OptimizationJobStatus submitOptimization(OptProblem optProblem, File parestDataDir, User user)
            throws SQLException, IOException {
        Connection con = null;
        try {
            con = connectionFactory.getConnection(this);
            KeyValue jobKey = keyFactory.getNewKey(con);
            String filePrefix = "CopasiParest_" + jobKey;

            File optProblemFile = new File(parestDataDir, filePrefix + "_optProblem.json");
            File optOutputFile = new File(parestDataDir, filePrefix + "_optRun.json");
            File optReportFile = new File(parestDataDir, filePrefix + "_optReport.txt");

            // Write the OptProblem to the filesystem
            if (!parestDataDir.exists()) {
                parestDataDir.mkdirs();
            }
            CopasiUtils.writeOptProblem(optProblemFile, optProblem);

            // Insert the database record
            Instant now = Instant.now();
            insertOptJob(con, jobKey, user, OptJobStatus.SUBMITTED,
                    optProblemFile.getAbsolutePath(), optOutputFile.getAbsolutePath(), optReportFile.getAbsolutePath(),
                    null, null, now);
            con.commit();

            return new OptimizationJobStatus(jobKey, OptJobStatus.SUBMITTED, null, null, null, null);
        } catch (SQLException | IOException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) connectionFactory.release(con, this);
        }
    }

    /**
     * Get the current status of an optimization job, including progress or results if available.
     */
    public OptimizationJobStatus getOptimizationStatus(KeyValue jobKey, User user)
            throws SQLException, DataAccessException, IOException {
        OptJobRecord record = getOptJobRecord(jobKey);
        if (record == null) {
            throw new DataAccessException("Optimization job not found: " + jobKey);
        }
        if (!record.ownerKey.equals(user.getID())) {
            throw new DataAccessException("Not authorized to access optimization job: " + jobKey);
        }

        OptProgressReport progressReport = null;
        Vcellopt results = null;

        switch (record.status) {
            case RUNNING:
            case QUEUED:
                // Try to read progress from the report file
                progressReport = readProgressReport(record.optReportFile);
                // Check if the output file has appeared (solver completed)
                results = readResults(record.optOutputFile);
                if (results != null) {
                    updateOptJobStatus(jobKey, OptJobStatus.COMPLETE, null);
                    return new OptimizationJobStatus(jobKey, OptJobStatus.COMPLETE, null, record.htcJobId, progressReport, results);
                }
                break;
            case COMPLETE:
                progressReport = readProgressReport(record.optReportFile);
                results = readResults(record.optOutputFile);
                break;
            case STOPPED:
                // After stop, the report file has progress up to the kill point
                progressReport = readProgressReport(record.optReportFile);
                break;
            case FAILED:
            case SUBMITTED:
                // No progress or results expected
                break;
        }

        return new OptimizationJobStatus(jobKey, record.status, record.statusMessage, record.htcJobId, progressReport, results);
    }

    /**
     * Update the status of an optimization job (e.g. when vcell-submit reports back).
     */
    public void updateOptJobStatus(KeyValue jobKey, OptJobStatus status, String statusMessage)
            throws SQLException {
        Connection con = null;
        try {
            con = connectionFactory.getConnection(this);
            PreparedStatement stmt = con.prepareStatement(
                    "UPDATE vc_optjob SET status = ?, statusMessage = ?, updateDate = ? WHERE id = ?");
            stmt.setString(1, status.name());
            stmt.setString(2, statusMessage);
            stmt.setTimestamp(3, Timestamp.from(Instant.now()));
            stmt.setLong(4, Long.parseLong(jobKey.toString()));
            stmt.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) connectionFactory.release(con, this);
        }
    }

    /**
     * Update the SLURM job ID after vcell-submit confirms submission.
     */
    public void updateHtcJobId(KeyValue jobKey, String htcJobId) throws SQLException {
        Connection con = null;
        try {
            con = connectionFactory.getConnection(this);
            PreparedStatement stmt = con.prepareStatement(
                    "UPDATE vc_optjob SET htcJobId = ?, status = ?, updateDate = ? WHERE id = ?");
            stmt.setString(1, htcJobId);
            stmt.setString(2, OptJobStatus.QUEUED.name());
            stmt.setTimestamp(3, Timestamp.from(Instant.now()));
            stmt.setLong(4, Long.parseLong(jobKey.toString()));
            stmt.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) connectionFactory.release(con, this);
        }
    }

    /**
     * Get the SLURM job ID for a given optimization job (needed for stop).
     */
    public String getHtcJobId(KeyValue jobKey) throws SQLException, DataAccessException {
        OptJobRecord record = getOptJobRecord(jobKey);
        if (record == null) {
            throw new DataAccessException("Optimization job not found: " + jobKey);
        }
        return record.htcJobId;
    }

    // --- Private helpers ---

    private void insertOptJob(Connection con, KeyValue jobKey, User user, OptJobStatus status,
                              String optProblemFile, String optOutputFile, String optReportFile,
                              String htcJobId, String statusMessage, Instant now) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(
                "INSERT INTO vc_optjob (id, ownerRef, status, optProblemFile, optOutputFile, optReportFile, " +
                        "htcJobId, statusMessage, insertDate, updateDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        stmt.setLong(1, Long.parseLong(jobKey.toString()));
        stmt.setLong(2, Long.parseLong(user.getID().toString()));
        stmt.setString(3, status.name());
        stmt.setString(4, optProblemFile);
        stmt.setString(5, optOutputFile);
        stmt.setString(6, optReportFile);
        stmt.setString(7, htcJobId);
        stmt.setString(8, statusMessage);
        stmt.setTimestamp(9, Timestamp.from(now));
        stmt.setTimestamp(10, Timestamp.from(now));
        stmt.executeUpdate();
    }

    private OptJobRecord getOptJobRecord(KeyValue jobKey) throws SQLException {
        Connection con = null;
        try {
            con = connectionFactory.getConnection(this);
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT j.id, j.ownerRef, j.status, j.optProblemFile, j.optOutputFile, j.optReportFile, " +
                            "j.htcJobId, j.statusMessage, j.insertDate, j.updateDate " +
                            "FROM vc_optjob j WHERE j.id = ?");
            stmt.setLong(1, Long.parseLong(jobKey.toString()));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new OptJobRecord(
                        new KeyValue(rs.getBigDecimal("id")),
                        new KeyValue(rs.getBigDecimal("ownerRef")),
                        OptJobStatus.valueOf(rs.getString("status")),
                        rs.getString("optProblemFile"),
                        rs.getString("optOutputFile"),
                        rs.getString("optReportFile"),
                        rs.getString("htcJobId"),
                        rs.getString("statusMessage"),
                        rs.getTimestamp("insertDate").toInstant(),
                        rs.getTimestamp("updateDate").toInstant()
                );
            }
            return null;
        } finally {
            if (con != null) connectionFactory.release(con, this);
        }
    }

    private OptProgressReport readProgressReport(String reportFilePath) {
        try {
            File f = new File(reportFilePath);
            if (f.exists()) {
                return CopasiUtils.readProgressReportFromCSV(f);
            }
        } catch (IOException e) {
            lg.warn("Failed to read progress report from {}: {}", reportFilePath, e.getMessage());
        }
        return null;
    }

    private Vcellopt readResults(String outputFilePath) {
        try {
            File f = new File(outputFilePath);
            if (f.exists()) {
                return objectMapper.readValue(f, Vcellopt.class);
            }
        } catch (IOException e) {
            lg.warn("Failed to read optimization results from {}: {}", outputFilePath, e.getMessage());
        }
        return null;
    }

    /**
     * Internal record for database row data.
     */
    record OptJobRecord(
            KeyValue id,
            KeyValue ownerKey,
            OptJobStatus status,
            String optProblemFile,
            String optOutputFile,
            String optReportFile,
            String htcJobId,
            String statusMessage,
            Instant insertDate,
            Instant updateDate
    ) {}
}
