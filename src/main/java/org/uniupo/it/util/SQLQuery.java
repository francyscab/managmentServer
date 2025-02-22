package org.uniupo.it.util;

public class SQLQuery {

    public static class Istituto {
        public static final String GET_ALL_ISTITUTI = """
                SELECT * FROM management_schema.istituti
                ORDER BY id_istituto ASC
                """;

        public static final String ADD_ISTITUTO = """
                 INSERT INTO management_schema.istituti\s
                 (nome, indirizzo, citta)
                 VALUES (?, ?, ?)
                \s""";
        public static final String GET_ISTITUTO_BY_ID = """
                SELECT * FROM management_schema.istituti
                WHERE id_istituto = ?
                """;
        public static final String CHECK_MACCHINETTE = """
                 SELECT COUNT(*) FROM management_schema.macchinette\s
                 WHERE id_istituto = ?
                \s""";

        public static final String DELETE_ISTITUTO = """
                 DELETE FROM management_schema.istituti\s
                 WHERE id_istituto = ?
                
                \s""";
    }

    public static class Macchinetta {
        public static final String GET_ALL_MACCHINETTE = """
                SELECT * FROM management_schema.macchinette
                ORDER BY id_macchinetta ASC
                """;

        public static final String GET_MACCHINETTE_BY_ISTITUTO = """
                SELECT * FROM management_schema.macchinette
                WHERE id_istituto = ?
                ORDER BY id_macchinetta ASC
                """;

        public static final String GET_MACCHINETTA_BY_ID = """
                SELECT * FROM management_schema.macchinette
                WHERE id_macchinetta = ? AND id_istituto = ?
                """;

        public static final String ADD_MACCHINETTA = """
                INSERT INTO management_schema.macchinette
                (id_macchinetta, id_istituto, piano)
                VALUES (?, ?, ?)
                """;

        public static final String DELETE_MACCHINETTA = """
                DELETE FROM management_schema.macchinette
                WHERE id_macchinetta = ? AND id_istituto = ?
                """;

        public static final String UPDATE_MACCHINA_ONLINE_STATUS = """
                  UPDATE management_schema.macchinette\s
                 SET is_online = ?
                 WHERE id_macchinetta = ? AND id_istituto = ?
                \s""";
    }

    public static class Ricavo {
        public static final String GET_ALL_RICAVI = """
                SELECT * FROM management_schema.ricavi
                ORDER BY data_ricavo DESC
                """;

        public static final String GET_RICAVI_BY_MACCHINETTA = """
                SELECT * FROM management_schema.ricavi
                WHERE id_macchinetta = ? AND id_istituto = ?
                ORDER BY data_ricavo DESC
                """;

        public static final String GET_TOTALE_RICAVI_BY_ISTITUTO = """
                 SELECT COALESCE(SUM(somma_ricavo), 0) as totale
                 FROM management_schema.ricavi\s
                 WHERE id_istituto = ?
                \s""";

        public static final String GET_RICAVI_BY_ISTITUTO = """
                 SELECT *\s
                 FROM management_schema.ricavi
                 WHERE id_istituto = ?
                 ORDER BY data_ricavo DESC
                \s""";

        public static final String ADD_RICAVO = """
                INSERT INTO management_schema.ricavi
                (id_macchinetta,id_istituto, somma_ricavo, raccolto_da)
                VALUES (?, ?, ?, ?)
                """;

        public static final String GET_TOTALE_RICAVI = """
                SELECT COALESCE(SUM(somma_ricavo), 0) as totale
                FROM management_schema.ricavi
                """;

        public static final String GET_TOTALE_RICAVI_BY_MACCHINETTA = """
                SELECT COALESCE(SUM(somma_ricavo), 0) as totale
                FROM management_schema.ricavi
                WHERE id_macchinetta = ? AND id_istituto = ?
                """;


    }

    public static class Transazione {
        public static final String INSERT_TRANSACTION =
                "INSERT INTO management_schema.transazioni (id_macchinetta, id_istituto, codice_bevanda, livello_zucchero, timestamp, transaction_id) VALUES (?, ?, ?, ?, ?, ?)";
        public static final String GET_BY_MACHINE =
                "SELECT * FROM management_schema.transazioni WHERE id_macchinetta = ? AND id_istituto = ? ORDER BY timestamp DESC";
        public static final String GET_BY_INSTITUTE =
                "SELECT * FROM management_schema.transazioni WHERE id_istituto = ? ORDER BY timestamp DESC";
        public static final String GET_BY_ID =
                "SELECT * FROM management_schema.transazioni WHERE transaction_id = ?";
        public static final String GET_ALL =
                "SELECT * FROM management_schema.transazioni ORDER BY timestamp DESC";
    }

    public static class Fault {
        public static final String INSERT_FAULT = """
                 INSERT INTO management_schema.guasti\s
                 (id_macchinetta, id_istituto, descrizione, data_segnalazione, tipo_guasto, id_fault)
                 VALUES (?, ?, ?, ?, CAST(? as management_schema.tipo_guasto), ?)
                \s""";
        public static final String UPDATE_MACHINE_FAULT_STATUS = """
                  UPDATE management_schema.macchinette\s
                  SET guasto = true\s
                  WHERE id_macchinetta = ? AND id_istituto = ?
                \s""";
        public static final String GET_FAULTS_BY_MACHINE = """
                SELECT * FROM management_schema.guasti
                WHERE id_macchinetta = ?
                AND id_istituto = ?
                ORDER BY data_segnalazione DESC
                """;

        public static final String MARK_FAULTS_AS_RESOLVED =
                "UPDATE management_schema.guasti " +
                        "SET risolto = true " +
                        "WHERE id_fault::uuid = ?::uuid";

        public static final String UPDATE_MACHINE_NO_FAULT = """
                 UPDATE management_schema.macchinette\s
                 SET guasto = false\s
                 WHERE id_macchinetta = ?\s
                 AND id_istituto = ?
                \s""";


        public static final String GET_MACHINE_FOR_FAULT = """
                SELECT id_macchinetta, id_istituto FROM management_schema.guasti
                WHERE id_fault = ?""";
    }
}