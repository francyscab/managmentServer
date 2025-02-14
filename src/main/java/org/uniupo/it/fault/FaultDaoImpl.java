package org.uniupo.it.fault;

import org.uniupo.it.DatabaseConnection;
import org.uniupo.it.util.SQLQuery;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.uniupo.it.util.SQLQuery.Fault.INSERT_FAULT;
import static org.uniupo.it.util.SQLQuery.Fault.UPDATE_MACHINE_FAULT_STATUS;

public class FaultDaoImpl implements FaultDao {


    @Override
    public void saveFaults(List<FaultMessage> faults) {
        if (faults == null || faults.isEmpty()) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(INSERT_FAULT);
                 PreparedStatement psUpdate = conn.prepareStatement(UPDATE_MACHINE_FAULT_STATUS)) {
                for (FaultMessage fault : faults) {
                    ps.setString(1, fault.getMachineId());
                    ps.setInt(2, fault.getInstituteId());
                    ps.setString(3, fault.getDescription());
                    ps.setTimestamp(4, fault.getTimestamp());
                    ps.setString(5, fault.getFaultType().name());
                    ps.setObject(6, fault.getIdFault());
                    ps.addBatch();

                    psUpdate.setString(1, fault.getMachineId());
                    psUpdate.setInt(2, fault.getInstituteId());
                    psUpdate.addBatch();

                }

                ps.executeBatch();
                psUpdate.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Error executing batch insert" + e.getMessage());
                throw new RuntimeException("Error executing batch insert", e);
            }
        } catch (SQLException e) {
            System.out.println("Database connection error" + e.getMessage());
            throw new RuntimeException("Database connection error", e);
        }
    }

    @Override
    public List<Fault> getFaultsByMachine(String machineId, int idIstituto) {
        List<Fault> faults = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQLQuery.Fault.GET_FAULTS_BY_MACHINE)) {

            ps.setString(1, machineId);
            ps.setInt(2, idIstituto);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                faults.add(extractFaultFromResultSet(rs));
            }
            System.out.println("Faults retrieved: " + faults);
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving faults", e);
        }

        return faults;
    }

    private Fault extractFaultFromResultSet(ResultSet rs) throws SQLException {
        return new Fault(
                (UUID) rs.getObject("id_fault"),
                rs.getString("id_macchinetta"),
                rs.getString("descrizione"),
                rs.getTimestamp("data_segnalazione"),
                FaultType.valueOf(rs.getString("tipo_guasto")),
                rs.getInt("id_istituto"),
                rs.getBoolean("risolto")
        );
    }

    @Override
    public void markFaultsAsResolved(List<UUID> faultIds) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                String machineId;
                int instituteId;
                try (PreparedStatement psGetMachine = conn.prepareStatement(SQLQuery.Fault.GET_MACHINE_FOR_FAULT)) {
                    psGetMachine.setObject(1, faultIds.getFirst());
                    ResultSet rs = psGetMachine.executeQuery();
                    if (rs.next()) {
                        machineId = rs.getString("id_macchinetta");
                        instituteId = rs.getInt("id_istituto");
                    } else {
                        throw new RuntimeException("Could not find machine info for fault");
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(SQLQuery.Fault.MARK_FAULTS_AS_RESOLVED)) {
                    for (UUID faultId : faultIds) {
                        ps.setObject(1, faultId, Types.OTHER);
                        ps.executeUpdate();
                    }
                }

                try (PreparedStatement psUpdate = conn.prepareStatement(SQLQuery.Fault.UPDATE_MACHINE_NO_FAULT)) {
                    psUpdate.setString(1, machineId);
                    psUpdate.setInt(2, instituteId);
                    psUpdate.executeUpdate();
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error during fault resolution", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database connection error", e);
        }
    }

}