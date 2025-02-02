package org.uniupo.it.ricavo;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.uniupo.it.mqtt.MQTTConnection;
import org.uniupo.it.util.ErrorResponse;
import org.uniupo.it.util.Topics;
import spark.Route;

import java.util.List;
import java.util.Map;

import static org.uniupo.it.Application.gson;

public class RicavoController {

    private static final DaoRicavo daoRicavo = new DaoRicavoImpl();

    public static Route getAllRicavi = (req, res) -> {
        res.type("application/json");
        try {
            List<Ricavo> ricavi = daoRicavo.getAllRicavi();
            return gson.toJson(ricavi);
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Errore interno del server"));
        }
    };

    public static Route getRicaviByMacchinetta = (req, res) -> {
        res.type("application/json");
        System.out.println("GET /ricavi/:id/:idIstituto");
        String idMacchinetta = req.params(":id");
        int idIstituto = Integer.parseInt(req.params(":idIstituto"));
        List<Ricavo> ricavi = daoRicavo.getRicaviByMacchinetta(idMacchinetta, idIstituto);
        return gson.toJson(ricavi);
    };

    public static Route getRicaviByIstituto = (req, res) -> {
        res.type("application/json");
        try {
            int idIstituto = Integer.parseInt(req.params(":id"));
            List<Ricavo> ricavi = daoRicavo.getRicaviByIstituto(idIstituto);
            if (ricavi.isEmpty()) {
                res.status(404);
                return gson.toJson(new ErrorResponse("Nessun ricavo trovato per questo istituto"));
            }
            return gson.toJson(ricavi);
        } catch (NumberFormatException e) {
            res.status(400);
            return gson.toJson(new ErrorResponse("ID istituto non valido"));
        }
    };


    public static Route getTotaleRicavi = (req, res) -> {
        res.type("application/json");
        double totale = daoRicavo.getTotaleRicavi();
        return gson.toJson(totale);
    };

    public static Route getTotaleRicaviByMacchinetta = (req, res) -> {
        res.type("application/json");
        String idMacchinetta = req.params(":id");
        int idIstituto = Integer.parseInt(req.params(":idIstituto"));
        System.out.println("GET /ricavi/" + idMacchinetta + "/" + idIstituto);
        double totale = daoRicavo.getTotaleRicaviByMacchinetta(idMacchinetta, idIstituto);
        return gson.toJson(totale);
    };

    public static Route getTotaleRicaviByIstituto = (req, res) -> {
        res.type("application/json");
        try {
            int idIstituto = Integer.parseInt(req.params(":id"));
            double totale = daoRicavo.getTotaleRicaviByIstituto(idIstituto);
            return gson.toJson(totale);
        } catch (NumberFormatException e) {
            res.status(400);
            return gson.toJson(new ErrorResponse("ID istituto non valido"));
        }
    };

    public static Route svuotaRicavi = (req, res) -> {
        res.type("application/json");


        String idIstituto = req.params("idIstituto");
        String idMacchinetta = req.params("idMacchinetta");

        String username = gson.fromJson(req.body(), Map.class).get("username").toString();

        try{
            System.out.println("Sto inviando la richiesta di manutenzione " + String.format(Topics.MANAGEMENT_REVENUE_TOPIC, idIstituto, idMacchinetta));
            MQTTConnection.getInstance().publish(String.format(Topics.MANAGEMENT_REVENUE_TOPIC, idIstituto, idMacchinetta), username);
            System.out.println("Richiesta di manutenzione inviata");
            return "Richiesta di manutenzione inviata";
        } catch (MqttException e) {
            res.status(500);
            System.err.println("Errore nell'invio della richiesta di manutenzione" + e.getMessage());
            return gson.toJson(new ErrorResponse("Errore interno del server"));
        }
    };
}