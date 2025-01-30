package org.uniupo.it.util;

public class Path {

    public final static String basePath = "/api/management";

    public static class Web {

        //PATH PER GLI ISTITUTI
        public static final String IstitutiBasePath = basePath + "/istituti";
        public static final String GET_SCHOOL_BY_ID = "/:id";
        public static final String GET_SCHOOL_NAMES_AND_IDS_BY_CITY = "/istituti/:city";
        public static final String GET_MAX_FLOOR_BY_SCHOOL_ID = "/istituti/maxfloor/:schoolId";
        public static final String GET_MACHINE_IDS_BY_SCHOOL_ID_AND_FLOOR = "/macchinette/info/:schoolId/:floor";

        //PATH PER LE MACCHINETTE
        public static final String MacchinetteBasePath = basePath + "/macchinette";
        public static final String GET_MACCHINETTA_BY_ID = "/dettaglio/:idIstituto/:id";
        public static final String GET_MACCHINETTE_BY_ISTITUTO = "/:id/macchinette";
        public static final String ADD_MACCHINETTA_TO_ISTITUTO = "/:id/macchinette";

        // PATH PER I RICAVI
        public static final String RicaviBasePath = basePath + "/ricavi";
        public static final String GET_RICAVI_BY_MACCHINETTA = "/macchinetta/:idIstituto/:id";
        public static final String GET_RICAVI_BY_ISTITUTO = "/istituto/:id";
        public static final String GET_TOTALE_RICAVI = "/totale";
        public static final String GET_TOTALE_RICAVI_BY_MACCHINETTA = "/totale/macchinetta/:idIstituto/:id";
        public static final String GET_TOTALE_RICAVI_BY_ISTITUTO = "/totale/istituto/:id";
        public static final String ADD_RICAVO = "/add";
        public static final String SVUOTA_RICAVI = "/svuota/:idIstituto/:idMacchinetta";

        // PATH PER LE MANUTENZIONI
        public static final String ManutenzioniBasePath = basePath + "/manutenzioni";
        public static final String RICHIEDI_MANUTENZIONE = "/richiesta/:idIstituto/:idMacchinetta";


        // Updated order in Path.java
        public static final String TransazioniBasePath = basePath + "/transazioni";
        public static final String GET_TRANSAZIONI_BY_MACCHINETTA = "/istituto/:idIstituto/macchinetta/:idMacchinetta";
        public static final String GET_TRANSAZIONI_BY_ISTITUTO = "/istituto/:id";
        public static final String GET_TRANSAZIONE_BY_ID = "/transazione/:id";  // Changed to be more specific

    }
}
