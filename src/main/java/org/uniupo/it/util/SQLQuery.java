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
            WHERE id_macchinetta = ?
            """;

        public static final String ADD_MACCHINETTA = """
            INSERT INTO management_schema.macchinette
            (id_macchinetta, id_istituto, piano)
            VALUES (?, ?, ?)
            """;

        public static final String DELETE_MACCHINETTA = """
            DELETE FROM management_schema.macchinette
            WHERE id_macchinetta = ?
            """;
    }

    public static class Ricavo {
        public static final String GET_ALL_RICAVI = """
            SELECT * FROM management_schema.ricavi
            ORDER BY data_ricavo DESC
            """;

        public static final String GET_RICAVI_BY_MACCHINETTA = """
            SELECT * FROM management_schema.ricavi
            WHERE id_macchinetta = ?
            ORDER BY data_ricavo DESC
            """;

        public static final String GET_RICAVI_BY_ISTITUTO = """
            SELECT r.* 
            FROM management_schema.ricavi r
            JOIN management_schema.macchinette m ON r.id_macchinetta = m.id_macchinetta
            WHERE m.id_istituto = ?
            ORDER BY r.data_ricavo DESC
            """;

        public static final String ADD_RICAVO = """
            INSERT INTO management_schema.ricavi
            (id_macchinetta, somma_ricavo, raccolto_da)
            VALUES (?, ?, ?)
            """;

        public static final String GET_TOTALE_RICAVI = """
            SELECT COALESCE(SUM(somma_ricavo), 0) as totale
            FROM management_schema.ricavi
            """;

        public static final String GET_TOTALE_RICAVI_BY_MACCHINETTA = """
            SELECT COALESCE(SUM(somma_ricavo), 0) as totale
            FROM management_schema.ricavi
            WHERE id_macchinetta = ?
            """;

        public static final String GET_TOTALE_RICAVI_BY_ISTITUTO = """
            SELECT COALESCE(SUM(r.somma_ricavo), 0) as totale
            FROM management_schema.ricavi r
            JOIN management_schema.macchinette m ON r.id_macchinetta = m.id_macchinetta
            WHERE m.id_istituto = ?
            """;
    }
}