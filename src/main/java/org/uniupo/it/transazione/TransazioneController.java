package org.uniupo.it.transazione;

import org.uniupo.it.util.ErrorResponse;
import spark.Route;

import java.util.List;

import static org.uniupo.it.Application.gson;

public class TransazioneController {
    private static final DaoTransazione daoTransazione = new DaoTransazioneImpl();

    public static Route getAllTransazioni = (req, res) -> {
        res.type("application/json");
        List<TransactionMessage> transazioni = daoTransazione.getAllTransazioni();
        System.out.println(transazioni);
        return gson.toJson(transazioni);
    };

    public static Route getTransazioniByMacchinetta = (req, res) -> {
        res.type("application/json");
        String idMacchinetta = req.params(":idMacchinetta");
        String idIstituto = req.params(":idIstituto");
        return gson.toJson(daoTransazione.getTransazioniByMacchinetta(idMacchinetta, idIstituto));
    };

    public static Route getTransazioniByIstituto = (req, res) -> {
        res.type("application/json");
        String idIstituto = req.params(":id");
        return gson.toJson(daoTransazione.getTransazioniByIstituto(idIstituto));
    };

    public static Route getTransazioneById = (req, res) -> {
        res.type("application/json");
        try {
            int id = Integer.parseInt(req.params(":id"));
            var transazione = daoTransazione.getTransazioneById(id);
            return gson.toJson(transazione != null ? transazione : new TransactionMessage(null, null, null, 0, id, null));
        } catch (NumberFormatException e) {
            res.status(400);
            return gson.toJson(new ErrorResponse("ID transazione non valido"));
        }
    };
}