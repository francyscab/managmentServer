package org.uniupo.it.istituto;

import org.uniupo.it.DatabaseConnection;
import org.uniupo.it.util.SQLQuery;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DaoIstitutoImpl implements DaoIstituto {
    @Override
    public List<Istituto> getAllIstituti() throws RuntimeException {
        List<Istituto> istituti = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(SQLQuery.Istituto.GET_ALL_ISTITUTI)) {

            while (rs.next()) {
                Istituto istituto = new Istituto();
                istituto.setId_istituto(rs.getInt("id_istituto"));
                istituto.setNome(rs.getString("nome"));
                istituto.setIndirizzo(rs.getString("indirizzo"));
                istituto.setCitta(rs.getString("citta"));
                istituto.setData_creazione(rs.getTimestamp("data_creazione"));
                istituti.add(istituto);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return istituti;
    }

    @Override
    public void deleteIstituto(int id) throws IllegalStateException {
        Connection conn = null;
        PreparedStatement psCheck = null;
        PreparedStatement psDelete = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Prima controlliamo se ci sono macchinette associate
            psCheck = conn.prepareStatement(SQLQuery.Istituto.CHECK_MACCHINETTE);
            psCheck.setInt(1, id);
            ResultSet rs = psCheck.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                throw new IllegalStateException("Non è possibile eliminare l'istituto perché ha ancora delle macchinette associate");
            }

            // Se non ci sono macchinette, procediamo con l'eliminazione
            psDelete = conn.prepareStatement(SQLQuery.Istituto.DELETE_ISTITUTO);
            psDelete.setInt(1, id);
            int affectedRows = psDelete.executeUpdate();

            if (affectedRows == 0) {
                throw new IllegalStateException("Istituto non trovato");
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (psCheck != null) psCheck.close();
                if (psDelete != null) psDelete.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void addIstituto(Istituto istituto) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(SQLQuery.Istituto.ADD_ISTITUTO);

            ps.setString(1, istituto.getNome());
            ps.setString(2, istituto.getIndirizzo());
            ps.setString(3, istituto.getCitta());

            ps.executeUpdate();
            conn.commit();

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Istituto getIstitutoById(int id) throws IllegalStateException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQLQuery.Istituto.GET_ISTITUTO_BY_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Istituto(
                        rs.getInt("id_istituto"),
                        rs.getString("nome"),
                        rs.getString("indirizzo"),
                        rs.getString("citta"),
                        rs.getTimestamp("data_creazione")
                );
            }
            else {
                throw new IllegalStateException("Istituto non trovato");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
