package org.uniupo.it.manutenzione;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.uniupo.it.mqtt.MQTTConnection;
import org.uniupo.it.util.Topics;
import spark.Route;

public class ManutenzioneController {
    public static Route richiediManutenzione = (req, res) -> {
        System.out.println("Richiesta di manutenzione ricevuta");

        String idMacchinetta = req.params(":idMacchinetta");
        String idIstituto = req.params(":idIstituto");

        try {
            MQTTConnection.getInstance().publish(String.format(Topics.TECHNICIAN_ASSISTANCE_TOPIC, idIstituto, idMacchinetta), "Richiesta di manutenzione");
            System.out.println("Richiesta di manutenzione inviata");
            return "Richiesta di manutenzione inviata";
        } catch (MqttException e) {
            res.status(500);
            System.out.println(e.getMessage());
            return "Errore interno del server";
        }
    };
}