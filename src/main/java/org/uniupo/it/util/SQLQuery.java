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
    }
}