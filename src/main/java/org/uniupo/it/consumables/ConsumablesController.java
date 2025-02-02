package org.uniupo.it.consumables;

import org.uniupo.it.websocket.ConsumablesWebSocketHandler;

import static spark.Spark.webSocket;

public class ConsumablesController {
    public static void initializeWebSocket() {
        webSocket("/ws/consumables", ConsumablesWebSocketHandler.class);
    }
}