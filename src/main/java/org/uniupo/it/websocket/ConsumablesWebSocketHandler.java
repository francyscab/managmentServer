package org.uniupo.it.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.uniupo.it.consumables.ConsumablesStatus;
import org.uniupo.it.mqtt.ConsumablesMQTTManager;
import org.uniupo.it.util.SuccessResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class ConsumablesWebSocketHandler {
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();
    private static final ConsumablesMQTTManager mqttManager = new ConsumablesMQTTManager();

    public static void broadcastUpdate(ConsumablesStatus status) {
        String sessionKey = getSessionKey(status.getMachineId(), status.getInstituteId());
        Session session = sessions.get(sessionKey);
        System.out.println("Broadcasting update to " + sessionKey);
        System.out.println((session == null) ? "Session is null" : "Session is not null");
        if (session != null && session.isOpen()) {

            session.getRemote().sendString(gson.toJson(status.getConsumables()), new WriteCallback() {
                @Override
                public void writeFailed(Throwable throwable) {
                    System.out.println("Error sending message to client" + throwable.getMessage());
                }

                @Override
                public void writeSuccess() {
                    System.out.println("Message sent to client");

                }
            });

        }
    }

    private static String getSessionKey(String machineId, String instituteId) {
        return machineId + "_" + instituteId;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        try {
            String machineId = session.getUpgradeRequest().getParameterMap().get("machineId").getFirst();
            String instituteId = session.getUpgradeRequest().getParameterMap().get("instituteId").getFirst();
            System.out.println("Connected to machine " + machineId + " in institute " + instituteId);


            String sessionKey = getSessionKey(machineId, instituteId);
            System.out.println("Session key: " + sessionKey);

            sessions.put(sessionKey, session);

            session.getRemote().sendString(gson.toJson(new SuccessResponse("Connected successfully")), new WriteCallback() {
                @Override
                public void writeFailed(Throwable throwable) {
                    System.out.println("Error sending message to client" + throwable.getMessage());
                }

                @Override
                public void writeSuccess() {

                }
            });

            mqttManager.requestConsumablesStatus(machineId, instituteId);


        } catch (Exception e) {

            session.close(1011, "Error in connection parameters");
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        String machineId = session.getUpgradeRequest().getParameterMap().get("machineId").getFirst();
        String instituteId = session.getUpgradeRequest().getParameterMap().get("instituteId").getFirst();
        sessions.remove(getSessionKey(machineId, instituteId));
    }
}