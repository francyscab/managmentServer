package org.uniupo.it.istituto;

import org.uniupo.it.DatabaseConnection;
import org.uniupo.it.util.SQLQuery;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DaoIstitutoImpl implements DaoIstituto {
    @Override
    public List<Istituto> getAllIstituti() {
        List<Istituto> istituti = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(SQLQuery.Istituto.GET_ALL_ISTITUTI)) {

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
    public void addIstituto(Istituto istituto) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
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
    public Istituto getIstitutoById(int id) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
