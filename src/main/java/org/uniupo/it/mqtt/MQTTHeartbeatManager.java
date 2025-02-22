package org.uniupo.it.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.uniupo.it.macchinetta.DaoMacchinetta;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class MQTTHeartbeatManager {
    private final DaoMacchinetta daoMacchinetta;
    private final ConcurrentHashMap<String, Instant> lastHeartbeats;
    private Thread monitoringThread;
    private volatile boolean running = true;


    private static final String HEARTBEAT_TOPIC = "macchinette/heartbeat/";
    private static final long HEARTBEAT_INTERVAL = 30000;
    private static final long TIMEOUT_THRESHOLD = 90000;

    public MQTTHeartbeatManager(DaoMacchinetta daoMacchinetta) {
        this.daoMacchinetta = daoMacchinetta;
        this.lastHeartbeats = new ConcurrentHashMap<>();
        initialize();
    }

    private void initialize() {
        setupMqttSubscription();
        startHeartbeatMonitoring();
    }

    private void setupMqttSubscription() {
        try {
            MQTTConnection.getInstance().subscribe(HEARTBEAT_TOPIC + "+/+/response",
                    (topic, _) -> {
                        System.out.println("Received heartbeat response from " + topic);
                        String[] parts = topic.split("/");
                        if (parts.length >= 4) {
                            String compositeId = parts[2] + "-" + parts[3];
                            handleHeartbeatResponse(compositeId);
                        }
                    });
        } catch (MqttException e) {
            throw new RuntimeException("Errore nella sottoscrizione MQTT", e);
        }
    }

    private void startHeartbeatMonitoring() {
        monitoringThread = new Thread(() -> {
            while (running) {
                try {
                    daoMacchinetta.getAllMacchinette().forEach(macchinetta -> {
                        System.out.println("Sending heartbeat for " + macchinetta.getId_macchinetta());
                        try {
                            sendHeartbeat(macchinetta.getId_macchinetta(), macchinetta.getId_istituto());
                        } catch (MqttException e) {
                            System.err.println("Errore heartbeat per " + macchinetta.getId_macchinetta() +
                                    ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                    checkTimeouts();
                    Thread.sleep(HEARTBEAT_INTERVAL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "HeartbeatMonitor");
        monitoringThread.setDaemon(true);
        monitoringThread.start();
    }

    private void sendHeartbeat(String machineId, int instituteId) throws MqttException {
        String topic = HEARTBEAT_TOPIC + instituteId + "/" +machineId  + "/request";
        System.out.println("Sending heartbeat to " + topic);
        MQTTConnection.getInstance().publish(topic, "ping");
    }

    private void handleHeartbeatResponse(String compositeId) {
        lastHeartbeats.put(compositeId, Instant.now());
        String[] parts = compositeId.split("-");
        if (parts.length == 2) {
            try {
                daoMacchinetta.updateMachineOnlineStatus(parts[1],
                        Integer.parseInt(parts[0]), true);
            } catch (Exception e) {
                System.err.println("Errore aggiornamento stato per " + compositeId +
                        ": " + e.getMessage());
            }
        }
    }

    private void checkTimeouts() {
        Instant now = Instant.now();
        lastHeartbeats.forEach((compositeId, lastHeartbeat) -> {
            if (lastHeartbeat.toEpochMilli() + TIMEOUT_THRESHOLD < now.toEpochMilli()) {
                String[] parts = compositeId.split("-");
                if (parts.length == 2) {
                    try {
                        daoMacchinetta.updateMachineOnlineStatus(parts[1],
                                Integer.parseInt(parts[0]), false);
                        lastHeartbeats.remove(compositeId);
                    } catch (Exception e) {
                        System.err.println("Errore timeout per " + compositeId +
                                ": " + e.getMessage());
                    }
                }
            }
        });
    }


    public void shutdown() {
        running = false;
        if (monitoringThread != null) {
            monitoringThread.interrupt();
            try {
                monitoringThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}