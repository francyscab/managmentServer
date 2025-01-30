package org.uniupo.it.manutenzione;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.uniupo.it.mqtt.MQTTConnection;
import spark.Route;

public class ManutenzioneController {
    public static Route richiediManutenzione = (req, res) -> {

        String idMacchinetta = req.params(":idMacchinetta");
        String idIstituto = req.params(":idIstituto");

        String topic = "manutenzione/" + idIstituto + "/" + idMacchinetta;

        try {
            MQTTConnection.getInstance().publish(topic, "Richiesta Manutenzione");
            System.out.println(topic);
            return "Richiesta di manutenzione inviata";
        }catch (MqttException e){
            res.status(500);
            System.out.println(e.getMessage());
            return "Errore interno del server";
        }
    };
}