package org.uniupo.it.ricavo;

import org.uniupo.it.DatabaseConnection;
import org.uniupo.it.util.SQLQuery;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DaoRicavoImpl implements DaoRicavo {

    @Override
    public List<Ricavo> getAllRicavi() {
        List<Ricavo> ricavi = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SQLQuery.Ricavo.GET_ALL_RICAVI)) {

            while (rs.next()) {
                ricavi.add(extractRicavoFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ricavi;
    }

    @Override
    public List<Ricavo> getRicaviByMacchinetta(String id_macchinetta,int id_istituto) {
        List<Ricavo> ricavi = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQLQuery.Ricavo.GET_RICAVI_BY_MACCHINETTA)) {

            ps.setString(1, id_macchinetta);
            ps.setInt(2, id_istituto);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ricavi.add(extractRicavoFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return ricavi;
    }

    @Override
    public List<Ricavo> getRicaviByIstituto(int id_istituto) {
        List<Ricavo> ricavi = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQLQuery.Ricavo.GET_RICAVI_BY_ISTITUTO)) {

            ps.setInt(1, id_istituto);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ricavi.add(extractRicavoFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ricavi;
    }

    @Override
    public void addRicavo(Ricavo ricavo) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQLQuery.Ricavo.ADD_RICAVO)) {

            ps.setString(1, ricavo.getId_macchinetta());
            ps.setInt(2, ricavo.getId_istituto());
            ps.setBigDecimal(3, ricavo.getSomma_ricavo());
            ps.setString(4, ricavo.getRaccolto_da());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getTotaleRicavi() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SQLQuery.Ricavo.GET_TOTALE_RICAVI)) {

            if (rs.next()) {
                return rs.getDouble("totale");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0.0;
    }

    @Override
    public double getTotaleRicaviByMacchinetta(String id_macchinetta,int id_istituto) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQLQuery.Ricavo.GET_TOTALE_RICAVI_BY_MACCHINETTA)) {

            ps.setString(1, id_macchinetta);
            ps.setInt(2, id_istituto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("totale");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0.0;
    }

    @Override
    public double getTotaleRicaviByIstituto(int id_istituto) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQLQuery.Ricavo.GET_TOTALE_RICAVI_BY_ISTITUTO)) {

            ps.setInt(1, id_istituto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("totale");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0.0;
    }

    private Ricavo extractRicavoFromResultSet(ResultSet rs) throws SQLException {
        return new Ricavo(
                rs.getInt("id_ricavo"),
                rs.getString("id_macchinetta"),
                rs.getBigDecimal("somma_ricavo"),
                rs.getInt("id_istituto"),
                rs.getTimestamp("data_ricavo"),
                rs.getString("raccolto_da")
        );
    }
}