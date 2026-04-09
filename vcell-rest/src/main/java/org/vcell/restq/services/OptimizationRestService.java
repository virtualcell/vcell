package org.vcell.restq.services;

import cbit.vcell.modeldb.OptJobTable;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.db.KeyFactory;
import org.vcell.optimization.CopasiUtils;
import org.vcell.optimization.OptJobRecord;
import org.vcell.optimization.OptJobStatus;
import org.vcell.optimization.jtd.OptProblem;
import org.vcell.optimization.jtd.OptProgressReport;
import org.vcell.optimization.jtd.Vcellopt;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.models.OptimizationJobStatus;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.File;
import java.io.IOException;
import java.sql.*;

@ApplicationScoped
public class OptimizationRestService {
    private static final Logger lg = LogManager.getLogger(OptimizationRestService.class);

    private static final OptJobTable optJobTable = OptJobTable.table;

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
            String sql = "INSERT INTO " + optJobTable.getTableName() +
                    " " + optJobTable.getSQLColumnList() +
                    " VALUES " + optJobTable.getSQLValueList(jobKey, user.getID(), OptJobStatus.SUBMITTED,
                    optProblemFile.getAbsolutePath(), optOutputFile.getAbsolutePath(), optReportFile.getAbsolutePath(),
                    null, null);
            executeUpdate(con, sql);
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
        if (!record.ownerKey().equals(user.getID())) {
            throw new DataAccessException("Not authorized to access optimization job: " + jobKey);
        }

        OptProgressReport progressReport = null;
        Vcellopt results = null;

        switch (record.status()) {
            case RUNNING:
            case QUEUED:
                // Try to read progress from the report file
                progressReport = readProgressReport(record.optReportFile());
                // Check if the output file has appeared (solver completed)
                results = readResults(record.optOutputFile());
                if (results != null) {
                    updateOptJobStatus(jobKey, OptJobStatus.COMPLETE, null);
                    return new OptimizationJobStatus(jobKey, OptJobStatus.COMPLETE, null, record.htcJobId(), progressReport, results);
                }
                break;
            case COMPLETE:
                progressReport = readProgressReport(record.optReportFile());
                results = readResults(record.optOutputFile());
                break;
            case STOPPED:
                // After stop, the report file has progress up to the kill point
                progressReport = readProgressReport(record.optReportFile());
                break;
            case FAILED:
            case SUBMITTED:
                // No progress or results expected
                break;
        }

        return new OptimizationJobStatus(jobKey, record.status(), record.statusMessage(), record.htcJobId(), progressReport, results);
    }

    /**
     * Update the status of an optimization job (e.g. when vcell-submit reports back).
     */
    public void updateOptJobStatus(KeyValue jobKey, OptJobStatus status, String statusMessage)
            throws SQLException {
        Connection con = null;
        try {
            con = connectionFactory.getConnection(this);
            String sql = "UPDATE " + optJobTable.getTableName() +
                    " SET " + optJobTable.getSQLUpdateList(status, statusMessage) +
                    " WHERE " + optJobTable.id + "=" + jobKey;
            executeUpdate(con, sql);
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
            String sql = "UPDATE " + optJobTable.getTableName() +
                    " SET " + optJobTable.getSQLUpdateListHtcJobId(htcJobId, OptJobStatus.QUEUED) +
                    " WHERE " + optJobTable.id + "=" + jobKey;
            executeUpdate(con, sql);
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
        return record.htcJobId();
    }

    /**
     * List optimization jobs for a user, most recent first. Returns lightweight status (no progress/results).
     */
    public OptimizationJobStatus[] listOptimizationJobs(User user) throws SQLException {
        Connection con = null;
        try {
            con = connectionFactory.getConnection(this);
            String sql = "SELECT " + optJobTable.id + "," +
                    optJobTable.status + "," +
                    optJobTable.htcJobId + "," +
                    optJobTable.statusMessage + "," +
                    optJobTable.insertDate + "," +
                    optJobTable.updateDate +
                    " FROM " + optJobTable.getTableName() +
                    " WHERE " + optJobTable.ownerRef + "=" + user.getID() +
                    " ORDER BY " + optJobTable.insertDate + " DESC";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            java.util.List<OptimizationJobStatus> jobs = new java.util.ArrayList<>();
            while (rs.next()) {
                jobs.add(new OptimizationJobStatus(
                        new KeyValue(rs.getBigDecimal(optJobTable.id.toString())),
                        OptJobStatus.valueOf(rs.getString(optJobTable.status.toString())),
                        rs.getString(optJobTable.statusMessage.toString()),
                        rs.getString(optJobTable.htcJobId.toString()),
                        null,
                        null
                ));
            }
            return jobs.toArray(new OptimizationJobStatus[0]);
        } finally {
            if (con != null) connectionFactory.release(con, this);
        }
    }

    // --- Private helpers ---

    private OptJobRecord getOptJobRecord(KeyValue jobKey) throws SQLException {
        Connection con = null;
        try {
            con = connectionFactory.getConnection(this);
            String sql = "SELECT * FROM " + optJobTable.getTableName() +
                    " WHERE " + optJobTable.id + "=" + jobKey;
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return optJobTable.getOptJobRecord(rs);
            }
            return null;
        } finally {
            if (con != null) connectionFactory.release(con, this);
        }
    }

    private void executeUpdate(Connection con, String sql) throws SQLException {
        if (lg.isDebugEnabled()) lg.debug(sql);
        Statement stmt = con.createStatement();
        try {
            int rowCount = stmt.executeUpdate(sql);
            if (rowCount != 1) {
                throw new SQLException("Expected 1 row, got " + rowCount);
            }
        } finally {
            stmt.close();
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
}
