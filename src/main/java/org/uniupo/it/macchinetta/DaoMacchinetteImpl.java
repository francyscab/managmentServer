// DaoMacchinetteImpl.java
package org.uniupo.it.macchinetta;

import org.eclipse.jetty.util.log.Log;
import org.uniupo.it.DatabaseConnection;
import org.uniupo.it.util.SQLQuery;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DaoMacchinetteImpl implements DaoMacchinetta {

    @Override
    public List<Macchinetta> getAllMacchinette() {
        List<Macchinetta> macchinette = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SQLQuery.Macchinetta.GET_ALL_MACCHINETTE)) {

            while (rs.next()) {
                macchinette.add(extractMacchinettaFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return macchinette;
    }

    @Override
    public List<Macchinetta> getMacchinetteByIstituto(int idIstituto) {
        List<Macchinetta> macchinette = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQLQuery.Macchinetta.GET_MACCHINETTE_BY_ISTITUTO)) {

            ps.setInt(1, idIstituto);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                macchinette.add(extractMacchinettaFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return macchinette;
    }

    @Override
    public Macchinetta getMacchinettaById(String id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQLQuery.Macchinetta.GET_MACCHINETTA_BY_ID)) {

            ps.setString(1, id);  // Prima impostiamo il parametro

            try (ResultSet rs = ps.executeQuery()) {  // Poi eseguiamo la query
                if (rs.next()) {
                    return extractMacchinettaFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore nel recupero della macchinetta: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void addMacchinetta(Macchinetta macchinetta) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQLQuery.Macchinetta.ADD_MACCHINETTA)) {

            ps.setString(1, macchinetta.getId_macchinetta());
            ps.setInt(2, macchinetta.getId_istituto());
            ps.setString(3, macchinetta.getPiano());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteMacchinetta(String id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQLQuery.Macchinetta.DELETE_MACCHINETTA)) {

            ps.setString(1, id);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new IllegalStateException("Macchinetta non trovata");
            }
        } catch (SQLException e) {
            System.out.println("Errore nell'eliminazione della macchinetta: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Macchinetta extractMacchinettaFromResultSet(ResultSet rs) throws SQLException {
        return new Macchinetta(
                rs.getString("id_macchinetta"),
                rs.getInt("id_istituto"),
                StatusMacchinetta.valueOf(rs.getString("stato_corrente")),
                rs.getTimestamp("data_ultima_manutenzione"),
                rs.getTimestamp("data_ultimo_refill"),
                rs.getTimestamp("data_ultimo_svuotamento_cassa"),
                rs.getTimestamp("data_installazione"),
                rs.getString("piano")
        );
    }
}