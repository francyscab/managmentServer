package org.uniupo.it.mqtt;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.uniupo.it.consumables.Consumable;
import org.uniupo.it.consumables.ConsumablesStatus;
import org.uniupo.it.websocket.ConsumablesWebSocketHandler;

import java.lang.reflect.Type;
import java.util.List;

import static org.uniupo.it.util.Topics.CONSUMABLES_REQUEST_TOPIC;
import static org.uniupo.it.util.Topics.CONSUMABLES_RESPONSE_TOPIC;

public class ConsumablesMQTTManager {
    private static final Gson gson = new Gson();


    private final MQTTConnection mqttConnection;

    public ConsumablesMQTTManager() {
        this.mqttConnection = MQTTConnection.getInstance();
        setupSubscriptions();
    }

    private void setupSubscriptions() {
        try {
            mqttConnection.subscribe(CONSUMABLES_RESPONSE_TOPIC, (topic, message) -> {
                String[] topicParts = topic.split("/");
                String instituteId = topicParts[1];
                String machineId = topicParts[2];

                Type consumableListType = new TypeToken<List<Consumable>>() {
                }.getType();
                List<Consumable> consumables = gson.fromJson(
                        new String(message.getPayload()),
                        consumableListType
                );
                System.out.println("Consumables received: " + consumables);

                ConsumablesStatus status = new ConsumablesStatus(machineId, instituteId, consumables);
                ConsumablesWebSocketHandler.broadcastUpdate(status);
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        mqttConnection.unsubscribe(CONSUMABLES_RESPONSE_TOPIC);

    }

    public void requestConsumablesStatus(String machineId, String instituteId) {
        System.out.println("Requesting consumables status for machine " + machineId);
        try {
            String topic = String.format(CONSUMABLES_REQUEST_TOPIC, instituteId, machineId);
            System.out.println("Topic: " + topic);
            mqttConnection.publish(topic, "REQUEST");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}