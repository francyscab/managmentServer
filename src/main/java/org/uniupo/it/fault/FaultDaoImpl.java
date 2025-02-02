package org.uniupo.it.fault;

import org.uniupo.it.DatabaseConnection;
import org.uniupo.it.util.SQLQuery;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.uniupo.it.util.SQLQuery.Fault.INSERT_FAULT;

public class FaultDaoImpl implements FaultDao {


    @Override
    public void saveFaults(List<FaultMessage> faults) {
        if (faults == null || faults.isEmpty()) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(INSERT_FAULT)) {
                for (FaultMessage fault : faults) {
                    ps.setString(1, fault.getMachineId());
                    ps.setInt(2, fault.getInstituteId());
                    ps.setString(3, fault.getDescription());
                    ps.setTimestamp(4, fault.getTimestamp());
                    ps.setString(5, fault.getFaultType().name());
                    ps.setObject(6, fault.getIdFault());
                    ps.addBatch();
                }

                ps.executeBatch();
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
    public int markFaultsAsResolved(List<UUID> faultIds) {
        int totalResolved = 0;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(SQLQuery.Fault.MARK_FAULTS_AS_RESOLVED)) {
                for (UUID faultId : faultIds) {
                    System.out.println("UUID type: " + faultId.getClass().getName());
                    ps.setObject(1, faultId, Types.OTHER);
                    int affected = ps.executeUpdate();
                    totalResolved += affected;

                    System.out.println("Resolving fault with ID: " + faultId);
                    if (affected > 0) {
                        System.out.println("Successfully resolved fault: " + faultId);
                    } else {
                        System.out.println("No fault found with ID: " + faultId +
                                " (Query executed but no rows affected)");
                    }
                }

                conn.commit();
                System.out.println("Total faults resolved: " + totalResolved);
                return totalResolved;

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error executing fault resolution query: " + e.getMessage());
                System.err.println("SQL State: " + e.getSQLState());
                throw new RuntimeException("Error during fault resolution", e);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            throw new RuntimeException("Database connection error", e);
        }
    }

}