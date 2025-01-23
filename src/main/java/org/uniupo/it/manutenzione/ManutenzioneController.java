package org.uniupo.it.manutenzione;

import org.uniupo.it.mqtt.MQTTConnection;
import spark.Route;

public class ManutenzioneController {
    public static Route richiediManutenzione = (req, res) -> {
        MQTTConnection mqttConnection = MQTTConnection.getInstance();
        String idMacchinetta = req.params(":idMacchinetta");
        String idIstituto = req.params(":idIstituto");

        String topic = "manutenzione/" + idIstituto + "/" + idMacchinetta;

        mqttConnection.publish(topic, "Richiesta Manutenzione");

        return null;
    };
}
