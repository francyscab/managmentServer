package org.uniupo.it.util;

public class Path {

    public final static String basePath = "/api/management";

    public static class Web {
        public static final String IstitutiBasePath = basePath + "/istituti";
        public static final String GET_SCHOOL_BY_ID = "/:id";
        public static final String GET_SCHOOL_NAMES_AND_IDS_BY_CITY = "/istituti/:city";
        public static final String GET_MAX_FLOOR_BY_SCHOOL_ID = "/istituti/maxfloor/:schoolId";
        public static final String GET_MACHINE_IDS_BY_SCHOOL_ID_AND_FLOOR = "/macchinette/info/:schoolId/:floor";
    }
}
