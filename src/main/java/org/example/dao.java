package org.example;

import org.eclipse.paho.client.mqttv3.*;
import com.google.gson.Gson;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class dao {

    private static Connection dbConnection;
    private static MqttClient mqttClient;

    // Crea la connessione al database MySQL
    static {
        try {
            dbConnection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/amministrativodb",
                    "root",
                    "Calzino99!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Crea la connessione MQTT
    private static MqttClient getMqttClient() throws MqttException {
        if (mqttClient == null) {
            mqttClient = new MqttClient("tcp://broker.emqx.io:1883", MqttClient.generateClientId());
            mqttClient.connect();
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Aggiungi logica per gestire i messaggi in arrivo
                }

                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connessione persa: " + cause.getMessage());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("Consegna completata per il messaggio: " + token.getMessageId());
                }
            });
        }
        return mqttClient;
    }

    // Funzione per recuperare tutte le città
    public String getAllCitiesWithSchools() {
        String sql = "SELECT DISTINCT citta FROM Scuole ORDER BY citta ASC";
        List<String> citta= new ArrayList<>();
        try (Statement stmt = dbConnection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()){ citta.add(rs.getString("citta")); }
            Gson gson = new Gson();
            // Costruisci il risultato JSON
            String r=gson.toJson(citta);
            System.out.println(r);
            return r ;
        } catch (SQLException e) {
            e.printStackTrace();
            return "{\"error\": \"Errore nel recupero delle città\"}";
        }
    }

    public String getSchoolNamesAndIdsByCity(String city) {
        String sql = "SELECT id, nome FROM Scuole WHERE citta = ?";
        List<Map<String, Object>> schools = new ArrayList<>();  // Lista per memorizzare le scuole

        try (PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
            stmt.setString(1, city);  // Imposta il parametro della città
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> school = new HashMap<>();
                    school.put("id", rs.getInt("id"));
                    school.put("nome", rs.getString("nome"));
                    schools.add(school);  // Aggiungi la scuola alla lista
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "{\"error\": \"Errore nel recupero delle scuole\"}";
        }

        // Usa Gson per convertire la lista di scuole in formato JSON
        Gson gson = new Gson();
        String resultJson = gson.toJson(schools);  // Converti la lista in JSON
        System.out.println(resultJson);  // Facoltativo: stampa il risultato per il debug
        return resultJson;  // Restituisci il risultato JSON
    }

    // Funzione per recuperare il piano massimo di una scuola
    public String getMaxFloorBySchoolId(String schoolId) {
        String sql = "SELECT MAX(piano) AS maxFloor FROM macchinette WHERE id_scuola = ?";
        List<Map<String, Object>> result = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
            stmt.setString(1, schoolId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("maxFloor", rs.getInt("maxFloor"));
                    result.add(map);
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("maxFloor", null);
                    result.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "{\"error\": \"Errore nel recupero del piano massimo\"}";
        }

        Gson gson = new Gson();
        return gson.toJson(result);  // Converti la lista in JSON
    }


    public String getMachineIdsBySchoolIdAndFloor(String schoolId, String floor) {
        String sql = "SELECT id FROM macchinette WHERE id_scuola = ? AND piano = ?";
        List<Map<String, Object>> result = new ArrayList<>();
        try (PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
            stmt.setString(1, schoolId);
            stmt.setString(2, floor);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> machine = new HashMap<>();
                    machine.put("id", rs.getInt("id"));
                    result.add(machine);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "{\"error\": \"Errore nel recupero degli ID delle macchinette\"}";
        }

        Gson gson = new Gson();
        return gson.toJson(result);  // Converti la lista in JSON
    }

    // Funzione per richiedere informazioni cialde tramite MQTT
    public String requestMachineInfoCialde(String machineId) {
        try {
            MqttClient client = getMqttClient();
            String message = "{\"id\": \"" + machineId + "\", \"richiesta\": \"cialde\"}";
            client.publish("/info", new MqttMessage(message.getBytes()));
            client.subscribe("/info/risposta/cialde/" + machineId);

            // Attendi la risposta
            Thread.sleep(1000);  // Timeout in attesa di una risposta
            return "{\"response\": \"Informazioni cialde ricevute\"}";
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
            return "{\"error\": \"Errore nella richiesta di informazioni per le cialde\"}";
        }
    }

    // Funzione per richiedere informazioni guasti tramite MQTT
    public String requestMachineInfoGuasti(String machineId) {
        try {
            MqttClient client = getMqttClient();
            String message = "{\"id\": \"" + machineId + "\", \"richiesta\": \"guasti\"}";
            client.publish("/info", new MqttMessage(message.getBytes()));
            client.subscribe("/info/risposta/guasti/" + machineId);

            // Attendi la risposta
            Thread.sleep(1000);  // Timeout
            return "{\"response\": \"Informazioni guasti ricevute\"}";
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
            return "{\"error\": \"Errore nella richiesta di informazioni per i guasti\"}";
        }
    }

    // Funzione per richiedere informazioni cassa tramite MQTT
    public String requestMachineInfoCassa(String machineId) {
        try {
            MqttClient client = getMqttClient();
            String message = "{\"id\": \"" + machineId + "\", \"richiesta\": \"cassa\"}";
            client.publish("/info", new MqttMessage(message.getBytes()));
            client.subscribe("/info/risposta/cassa/" + machineId);

            // Attendi la risposta
            Thread.sleep(1000);  // Timeout
            return "{\"response\": \"Informazioni cassa ricevute\"}";
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
            return "{\"error\": \"Errore nella richiesta di informazioni per la cassa\"}";
        }
    }

    public String getMachineDetailsById(String machineId) {
        String sql = "SELECT " +
                "macchinette.id AS machineId, " +
                "macchinette.piano AS floor, " +
                "scuole.nome AS schoolName, " +
                "scuole.citta AS city " +
                "FROM macchinette " +
                "JOIN scuole ON macchinette.id_scuola = scuole.id " +
                "WHERE macchinette.id = ?";

        try (PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
            stmt.setString(1, machineId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Recupera i dettagli della macchinetta
                    int id = rs.getInt("machineId");
                    int floor = rs.getInt("floor");
                    String schoolName = rs.getString("schoolName");
                    String city = rs.getString("city");

                    // Crea una stringa JSON per i dettagli della macchinetta
                    return String.format("{\"machineId\": %d, \"floor\": %d, \"schoolName\": \"%s\", \"city\": \"%s\"}",
                            id, floor, schoolName, city);
                } else {
                    // Se non si trova la macchinetta, restituisce un messaggio di errore
                    return "{\"error\": \"Macchinetta con ID " + machineId + " non trovata.\"}";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "{\"error\": \"Errore nel recupero dei dettagli della macchinetta\"}";
        }
    }
}
