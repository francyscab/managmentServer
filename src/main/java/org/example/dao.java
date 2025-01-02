package org.example;

import com.google.gson.JsonObject;
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
        Gson gson = new Gson();
        List<JsonObject> cittaList = new ArrayList<>();

        try (Statement stmt = dbConnection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Creiamo un JsonObject per ogni città
                JsonObject cityObj = new JsonObject();
                cityObj.addProperty("citta", rs.getString("citta"));
                cittaList.add(cityObj);
            }

            // Convertiamo la lista in una stringa JSON
            String result = gson.toJson(cittaList);
            System.out.println(result); // Debug: stampa il JSON
            return result;
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
                    // Creiamo una mappa per rappresentare ogni scuola
                    Map<String, Object> school = new HashMap<>();
                    school.put("id", rs.getInt("id")); // ID della scuola
                    school.put("nome", rs.getString("nome")); // Nome della scuola
                    schools.add(school);  // Aggiungi la scuola alla lista
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "{\"error\": \"Errore nel recupero delle scuole\"}";
        }

        // Serializziamo la lista di scuole in formato JSON
        Gson gson = new Gson();
        String resultJson = gson.toJson(schools); // Converte la lista in JSON
        System.out.println(resultJson);  // Facoltativo: stampa per debug
        return resultJson;  // Restituisce il risultato JSON
    }

    // Funzione per recuperare il piano massimo di una scuola
    public String getMaxFloorBySchoolId(String schoolId) {
        String sql = "SELECT MAX(piano) AS maxFloor FROM macchinette WHERE id_scuola = ?";

        try (PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
            stmt.setString(1, schoolId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Recupera il valore del piano massimo
                    Integer maxFloor = rs.getInt("maxFloor");
                    if (rs.wasNull()) {
                        maxFloor = null; // Gestisce i risultati NULL
                    }

                    // Usa Gson per serializzare il valore in JSON
                    Gson gson = new Gson();
                    return gson.toJson(maxFloor);
                } else {
                    // Nessun risultato trovato, restituisce null in JSON
                    return "null";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Restituisce un messaggio di errore in formato JSON
            return "{\"error\": \"Errore nel recupero del piano massimo\"}";
        }
    }

    public String getNextMachineId() {
        String sql = "SELECT id FROM macchinette ORDER BY id ASC";

        try (PreparedStatement stmt = dbConnection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Mantieni il prossimo ID atteso
            int expectedId = 1;

            // Scorri gli ID ordinati
            while (rs.next()) {
                int currentId = rs.getInt("id");

                // Se c'è una lacuna nella sequenza, restituisci l'ID mancante
                if (currentId != expectedId) {
                    Gson gson = new Gson();
                    return gson.toJson(expectedId);
                }

                // Aggiorna l'ID atteso
                expectedId++;
            }

            // Se non ci sono lacune, restituisci il prossimo ID disponibile
            Gson gson = new Gson();
            System.out.println("Prossimo ID disponibile: " + gson.toJson(expectedId)); // Stampa il risultato
            return gson.toJson(expectedId);

        } catch (SQLException e) {
            e.printStackTrace();
            // Restituisce un messaggio di errore in formato JSON
            return "{\"error\": \"Errore nel calcolo del prossimo ID\"}";
        }
    }

    public String getNextSchoolId() {
        String sql = "SELECT id FROM scuole ORDER BY id ASC";

        try (PreparedStatement stmt = dbConnection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Mantieni il prossimo ID atteso
            int expectedId = 1;

            // Scorri gli ID ordinati
            while (rs.next()) {
                int currentId = rs.getInt("id");

                // Se c'è una lacuna nella sequenza, restituisci l'ID mancante
                if (currentId != expectedId) {
                    Gson gson = new Gson();
                    return gson.toJson(expectedId);
                }

                // Aggiorna l'ID atteso
                expectedId++;
            }

            // Se non ci sono lacune, restituisci il prossimo ID disponibile
            Gson gson = new Gson();
            return gson.toJson(expectedId);

        } catch (SQLException e) {
            e.printStackTrace();
            // Restituisce un messaggio di errore in formato JSON
            return "{\"error\": \"Errore nel calcolo del prossimo ID\"}";
        }
    }



    public String getMachineIdsBySchoolIdAndFloor(String schoolId, String floor) {
        String sql = "SELECT id FROM macchinette WHERE id_scuola = ? AND piano = ?";
        List<Integer> machineIds = new ArrayList<>(); // Lista per memorizzare solo gli ID delle macchinette

        try (PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
            stmt.setString(1, schoolId);
            stmt.setString(2, floor);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    machineIds.add(rs.getInt("id")); // Aggiungi solo l'ID alla lista
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Restituisce un messaggio di errore in formato JSON
            return "{\"error\": \"Errore nel recupero degli ID delle macchinette\"}";
        }

        // Usa Gson per convertire la lista di ID in JSON
        Gson gson = new Gson();
        return gson.toJson(machineIds); // Restituisce un array JSON
    }

//funzione per aggiunta di una macchinetta
public boolean postMachineData(int piano, int school_id) {
    // Ottieni il prossimo ID disponibile
    String nextIdJson = getNextMachineId();
    Gson gson = new Gson();

    try {
        // Estrai il valore numerico dall'output JSON
        Integer nextId = gson.fromJson(nextIdJson, Integer.class);

        if (nextId == null) {
            return false; // Impossibile calcolare il prossimo ID
        }

        // Query per l'inserimento della nuova macchinetta
        String sql = "INSERT INTO macchinette (id, id_scuola, piano) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
            // Imposta i parametri della query
            stmt.setInt(1, nextId);
            stmt.setInt(2, school_id);
            stmt.setInt(3, piano);

            // Esegui l'inserimento
            int rowsInserted = stmt.executeUpdate();
            System.out.println("Righe inserite: " + rowsInserted);
            return rowsInserted > 0; // Restituisce true se l'inserimento è riuscito
        }
    } catch (SQLException e) {
        System.err.println("Errore durante l'esecuzione della query SQL:");
        e.printStackTrace();
        return false; // Restituisce false in caso di errore
    } catch (Exception e) {
        // Log dettagliato per qualsiasi altra eccezione
        System.err.println("Errore generale:");
        e.printStackTrace();
        return false;
    }
}

//elimina macchinetta dal db
public boolean deleteMachineById(int machineId) {
    // SQL per eliminare i ricavi associati alla macchinetta
    String deleteRevenuesSql = "DELETE FROM ricavi WHERE id_macchinetta = ?";
    // SQL per eliminare la macchinetta
    String deleteMachineSql = "DELETE FROM macchinette WHERE id = ?";

    try {
        // Disabilita l'autocommit per eseguire la transazione
        dbConnection.setAutoCommit(false);

        // Elimina i ricavi associati
        try (PreparedStatement stmtRevenues = dbConnection.prepareStatement(deleteRevenuesSql)) {
            stmtRevenues.setInt(1, machineId);
            stmtRevenues.executeUpdate();
        }

        // Elimina la macchinetta
        try (PreparedStatement stmtMachine = dbConnection.prepareStatement(deleteMachineSql)) {
            stmtMachine.setInt(1, machineId);
            int rowsDeleted = stmtMachine.executeUpdate();

            // Conferma la transazione se l'eliminazione è riuscita
            dbConnection.commit();
            return rowsDeleted > 0; // True se la macchinetta è stata eliminata
        }
    } catch (SQLException e) {
        e.printStackTrace();
        try {
            // Rollback della transazione in caso di errore
            dbConnection.rollback();
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        return false;
    } finally {
        try {
            // Ripristina l'autocommit
            dbConnection.setAutoCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}


    public boolean postSchoolData(String citta, String nome, String indirizzo ) {
        // Ottieni il prossimo ID disponibile
        String nextIdJson = getNextSchoolId();
        Gson gson = new Gson();

        try {
            // Estrai il valore numerico dall'output JSON
            Integer nextId = gson.fromJson(nextIdJson, Integer.class);

            if (nextId == null) {
                return false; // Impossibile calcolare il prossimo ID
            }

            // Query per l'inserimento della nuova macchinetta
            String sql = "INSERT INTO scuole (id, indirizzo, nome, citta) VALUES (?, ?, ?, ?)";

            try (PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
                // Imposta i parametri della query
                stmt.setInt(1, nextId);
                stmt.setString(2, indirizzo);
                stmt.setString(3, nome);
                stmt.setString(4, citta);

                // Esegui l'inserimento
                int rowsInserted = stmt.executeUpdate();

                return rowsInserted > 0; // Restituisce true se l'inserimento è riuscito
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Restituisce false in caso di errore
        }
    }




    // Funzione per richiedere informazioni cialde tramite MQTT
    public String requestMachineInfoCialde(String machineId) {
        String responseTopic = "/info/risposta/cialde/" + machineId;
        String requestTopic = "/info";
        final String[] receivedMessage = {null};

        try {
            // Ottieni il client MQTT esistente
            MqttClient client = getMqttClient();

            // Sottoscriviti al topic di risposta
            client.subscribe(responseTopic);

            // Pubblica il messaggio di richiesta
            String requestMessage = String.format("{\"id\": \"%s\", \"richiesta\": \"cialde\"}", machineId);
            client.publish(requestTopic, new MqttMessage(requestMessage.getBytes()));
            System.out.println("Messaggio pubblicato: " + requestMessage);

            // Attendi la risposta o timeout
            synchronized (receivedMessage) {
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        System.err.println("Connessione persa: " + cause.getMessage());
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        if (topic.equals(responseTopic)) {
                            synchronized (receivedMessage) {
                                receivedMessage[0] = new String(message.getPayload());
                                receivedMessage.notify(); // Notifica che il messaggio è arrivato
                            }
                        }
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        System.out.println("Messaggio pubblicato con successo!");
                    }
                });

                receivedMessage.wait(5000); // Timeout di 1 secondo
            }

            // Controlla se è stato ricevuto un messaggio
            if (receivedMessage[0] != null) {
                return receivedMessage[0]; // Restituisci il messaggio ricevuto
            } else {
                return "{\"error\": \"Timeout - Nessuna risposta ricevuta\"}";
            }
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
            return "{\"error\": \"Errore nella richiesta di informazioni per le cialde\"}";
        }
    }


    // Funzione per richiedere informazioni guasti tramite MQTT
    public String requestMachineInfoGuasti(String machineId) {
        String responseTopic = "/info/risposta/guasti/" + machineId;
        String requestTopic = "/info";
        final String[] receivedMessage = {null};

        try {
            // Ottieni il client MQTT esistente
            MqttClient client = getMqttClient();

            // Sottoscriviti al topic di risposta
            client.subscribe(responseTopic);

            // Pubblica il messaggio di richiesta
            String requestMessage = String.format("{\"id\": \"%s\", \"richiesta\": \"guasti\"}", machineId);
            client.publish(requestTopic, new MqttMessage(requestMessage.getBytes()));
            System.out.println("Messaggio pubblicato: " + requestMessage);

            // Attendi la risposta o timeout
            synchronized (receivedMessage) {
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        System.err.println("Connessione persa: " + cause.getMessage());
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        if (topic.equals(responseTopic)) {
                            synchronized (receivedMessage) {
                                receivedMessage[0] = new String(message.getPayload());
                                receivedMessage.notify(); // Notifica che il messaggio è arrivato
                            }
                        }
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        System.out.println("Messaggio pubblicato con successo!");
                    }
                });

                receivedMessage.wait(5000); // Timeout di 5 secondi
            }

            // Controlla se è stato ricevuto un messaggio
            if (receivedMessage[0] != null) {
                return receivedMessage[0]; // Restituisci il messaggio ricevuto
            } else {
                return "{\"error\": \"Timeout - Nessuna risposta ricevuta\"}";
            }
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
            return "{\"error\": \"Errore nella richiesta di informazioni per i guasti\"}";
        }
    }


    public String requestMachineInfoCassa(String machineId) {
        String responseTopic = "/info/risposta/cassa/" + machineId;
        String requestTopic = "/info";
        final String[] receivedMessage = {null};

        try {
            // Ottieni il client MQTT esistente
            MqttClient client = getMqttClient();

            // Sottoscriviti al topic di risposta
            client.subscribe(responseTopic);

            // Pubblica il messaggio di richiesta
            String requestMessage = String.format("{\"id\": \"%s\", \"richiesta\": \"cassa\"}", machineId);
            client.publish(requestTopic, new MqttMessage(requestMessage.getBytes()));
            System.out.println("Messaggio pubblicato: " + requestMessage);

            // Attendi la risposta o timeout
            synchronized (receivedMessage) {
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        System.err.println("Connessione persa: " + cause.getMessage());
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        if (topic.equals(responseTopic)) {
                            synchronized (receivedMessage) {
                                receivedMessage[0] = new String(message.getPayload());
                                receivedMessage.notify(); // Notifica che il messaggio è arrivato
                            }
                        }
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        System.out.println("Messaggio pubblicato con successo!");
                    }
                });

                receivedMessage.wait(5000); // Timeout di 5 secondi
            }

            // Controlla se è stato ricevuto un messaggio
            if (receivedMessage[0] != null) {
                return receivedMessage[0]; // Restituisci il messaggio ricevuto
            } else {
                return "{\"error\": \"Timeout - Nessuna risposta ricevuta\"}";
            }
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
            return "{\"error\": \"Errore nella richiesta di informazioni perla cassa\"}";
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
                    Map<String, Object> details = new HashMap<>();
                    details.put("machineId", rs.getInt("machineId"));
                    details.put("floor", rs.getInt("floor"));
                    details.put("schoolName", rs.getString("schoolName"));
                    details.put("city", rs.getString("city"));

                    // Usa Gson per convertire i dettagli in JSON
                    Gson gson = new Gson();
                    System.out.println(details);
                    return gson.toJson(details);
                } else {
                    // Se non si trova la macchinetta, restituisce un messaggio di errore
                    return String.format("{\"error\": \"Macchinetta con ID %s non trovata.\"}", machineId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Restituisce un messaggio di errore in formato JSON
            return "{\"error\": \"Errore nel recupero dei dettagli della macchinetta\"}";
        }
    }
}
