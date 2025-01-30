package org.uniupo.it.transazione;

import org.uniupo.it.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.uniupo.it.util.SQLQuery.Transazione.*;

public class DaoTransazioneImpl implements DaoTransazione {


    @Override
    public void addTransazione(TransactionMessage transazione) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_TRANSACTION)) {
            ps.setString(1, transazione.getMachineId());
            ps.setString(2, transazione.getInstituteId());
            ps.setString(3, transazione.getDrinkCode());
            ps.setInt(4, transazione.getSugarLevel());
            ps.setTimestamp(5, transazione.getTimestamp());
            ps.setInt(6, transazione.getTransactionId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'inserimento della transazione", e);
        }
    }

    @Override
    public List<TransactionMessage> getTransazioniByMacchinetta(String idMacchinetta, String idIstituto) {
        List<TransactionMessage> transazioni = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_BY_MACHINE)) {
            ps.setString(1, idMacchinetta);
            ps.setString(2, idIstituto);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transazioni.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero delle transazioni", e);
        }
        return transazioni;
    }

    @Override
    public List<TransactionMessage> getTransazioniByIstituto(String idIstituto) {
        List<TransactionMessage> transazioni = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_BY_INSTITUTE)) {
            ps.setString(1, idIstituto);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transazioni.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero delle transazioni", e);
        }
        return transazioni;
    }

    @Override
    public TransactionMessage getTransazioneById(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_BY_ID)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractTransactionFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero della transazione", e);
        }
        return null;
    }

    @Override
    public List<TransactionMessage> getAllTransazioni() {
        List<TransactionMessage> transazioni = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(GET_ALL)) {

            while (rs.next()) {
                transazioni.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero delle transazioni", e);
        }
        return transazioni;
    }

    private TransactionMessage extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        return new TransactionMessage(
                rs.getString("id_macchinetta"),
                rs.getString("id_istituto"),
                rs.getString("codice_bevanda"),
                rs.getInt("livello_zucchero"),
                rs.getInt("transaction_id"),
                rs.getTimestamp("timestamp")
        );
    }
}