package org.example;
import static spark.Spark.*;
import com.google.gson.Gson;

import java.util.Map;

public class Main {
    public static void main(String[] args) {

        dao dao = new dao();
        // Port del server
        port(3001);

        // Gestione statici (cartella client)
        staticFiles.location("/client");

        // Crea un'istanza di Gson per gestire la serializzazione/deserializzazione JSON
        Gson gson = new Gson();

        // Middlewares
        before((req, res) -> {
            System.out.println("Richiesta ricevuta: " + req.requestMethod() + " " + req.url());
        });



        // Endpoint per ottenere tutte le scuole in città
        get("/api/scuole/citta", (req, res) -> {
            System.out.println("jjjjjjjj");
            try {
                return dao.getAllCitiesWithSchools();
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse(e.getMessage()));
            }
        });

        // Endpoint per ottenere le scuole in una città specifica
        get("/api/scuole/:city", (req, res) -> {
            String city = req.params(":city");
            try {
                return dao.getSchoolNamesAndIdsByCity(city);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse(e.getMessage()));
            }
        });

        // Endpoint per ottenere il piano massimo di una scuola
        get("/api/scuole/maxfloor/:schoolId", (req, res) -> {
            String schoolId = req.params(":schoolId");
            try {
                return dao.getMaxFloorBySchoolId(schoolId);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse(e.getMessage()));
            }
        });

        // Endpoint per ottenere le macchinette in base a ID scuola e piano
        get("/api/macchinette/info/:schoolId/:floor", (req, res) -> {
            String schoolId = req.params(":schoolId");
            String floor = req.params(":floor");
            try {
                return dao.getMachineIdsBySchoolIdAndFloor(schoolId, floor);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse(e.getMessage()));
            }
        });

        // Endpoint per ottenere informazioni cialde tramite MQTT
        get("/api/macchinette/cialde/:machineId", (req, res) -> {
            String machineId = req.params(":machineId");
            try {
                return dao.requestMachineInfoCialde(machineId);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse(e.getMessage()));
            }
        });

        // Endpoint per ottenere informazioni guasti tramite MQTT
        get("/api/macchinette/guasti/:machineId", (req, res) -> {
            String machineId = req.params(":machineId");
            try {
                return dao.requestMachineInfoGuasti(machineId);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse(e.getMessage()));
            }
        });

        // Endpoint per ottenere informazioni cassa tramite MQTT
        get("/api/macchinette/cassa/:machineId", (req, res) -> {
            String machineId = req.params(":machineId");
            try {
                return dao.requestMachineInfoCassa(machineId);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse(e.getMessage()));
            }
        });

        // Endpoint per ottenere i dettagli della macchinetta
        get("/api/macchinetta/dettagli/:machineId", (req, res) -> {
            String machineId = req.params(":machineId");
            try {
                return dao.getMachineDetailsById(machineId);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Errore nel recupero dei dettagli della macchinetta", e.getMessage()));
            }
        });



        post("/api/new/school", (req, res) -> {
            System.out.println("Ricevuta richiesta POST per aggiungere una scuola");
            try {
                // Converte il corpo della richiesta JSON in una mappa
                Map<String, Object> requestData = new Gson().fromJson(req.body(), Map.class);

                // Recupera i singoli campi dalla mappa
                String citta = (String) requestData.get("citta");
                String nome = (String) requestData.get("nome");
                String indirizzo = (String) requestData.get("indirizzo");

                // Chiama il metodo DAO per aggiungere i dati
                boolean success = dao.postSchoolData(citta, nome, indirizzo);

                if (success) {
                    res.status(200);
                    return new Gson().toJson(Map.of("message", "Macchinetta aggiunta con successo"));
                } else {
                    res.status(400);
                    return new Gson().toJson(new ErrorResponse("Errore durante l'aggiunta della macchinetta"));
                }
            } catch (Exception e) {
                res.status(500);
                return new Gson().toJson(new ErrorResponse("Errore interno del server: " + e.getMessage()));
            }
        });



        post("/api/new/machine", (req, res) -> {
            System.out.println("Ricevuta richiesta POST per aggiungere una macchinetta");
            try {
                // Converte il corpo della richiesta JSON in una mappa
                Map<String, Object> requestData = new Gson().fromJson(req.body(), Map.class);

                // Recupera i singoli campi dalla mappa e li converte in int
                int piano = Integer.parseInt(requestData.get("piano").toString());
                int school_id = Integer.parseInt(requestData.get("scuola").toString());

                System.out.println("Piano ricevuto: " + piano);
                System.out.println("School ID ricevuto: " + school_id);

                // Chiama il metodo DAO per aggiungere i dati
                boolean success = dao.postMachineData(piano,school_id);

                if (success) {
                    res.status(200);
                    return new Gson().toJson(Map.of("message", "Macchinetta aggiunta con successo"));
                } else {
                    res.status(400);
                    return new Gson().toJson(new ErrorResponse("Errore durante l'aggiunta della macchinetta"));
                }
            } catch (Exception e) {
                res.status(500);
                return new Gson().toJson(new ErrorResponse("Errore interno del server: " + e.getMessage()));
            }
        });

    }

    // Classe di risposta per gli errori
    private static class ErrorResponse {
        private String error;
        private String details;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public ErrorResponse(String error, String details) {
            this.error = error;
            this.details = details;
        }

        public String getError() {
            return error;
        }

        public String getDetails() {
            return details;
        }
    }
}
