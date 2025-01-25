package org.uniupo.it.ricavo;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.uniupo.it.mqtt.MQTTConnection;
import org.uniupo.it.util.ErrorResponse;
import org.uniupo.it.util.SuccessResponse;
import spark.Route;

import java.math.BigDecimal;
import java.util.List;

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
        if (ricavi.isEmpty()) {
            res.status(404);
            return gson.toJson(new ErrorResponse("Nessun ricavo trovato per questa macchinetta"));
        }
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

    /**public static Route addRicavo = (req, res) -> {
        res.type("application/json");
        try {
            String idMacchinetta = req.queryParams("id_macchinetta");
            BigDecimal sommaRicavo = new BigDecimal(req.queryParams("somma_ricavo"));
            String raccoltoDa = req.queryParams("raccolto_da");

            if (idMacchinetta == null || req.queryParams("somma_ricavo") == null || raccoltoDa == null) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Parametri mancanti"));
            }

            Ricavo ricavo = new Ricavo(idMacchinetta, sommaRicavo, raccoltoDa);
            daoRicavo.addRicavo(ricavo);

            res.status(201);
            return gson.toJson(new SuccessResponse("Ricavo aggiunto con successo"));
        } catch (NumberFormatException e) {
            res.status(400);
            return gson.toJson(new ErrorResponse("Formato somma non valido"));
        } catch (Exception e) {
            res.status(500);
            System.out.println(e.getMessage());
            return gson.toJson(new ErrorResponse("Errore interno del server"));
        }
    };*/

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
        MQTTConnection mqttConnection = new MQTTConnection();

        String idIstituto = req.params("idIstituto");
        String idMacchinetta = req.params("idMacchinetta");

        String topic = "/macchinetta/ricavo/" + idIstituto + "/" + idMacchinetta;

        try{
            mqttConnection.publish(topic, "Preleva ricavo");
            return gson.toJson(new SuccessResponse("Richiesta inviata"));
        } catch (MqttException e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Errore interno del server"));
        }
    };
}