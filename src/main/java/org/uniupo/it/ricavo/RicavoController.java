package org.uniupo.it.ricavo;

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
        List<Ricavo> ricavi = daoRicavo.getAllRicavi();
        if (ricavi.isEmpty()) {
            res.status(404);
            return gson.toJson(new ErrorResponse("Nessun ricavo trovato"));
        }
        return gson.toJson(ricavi);
    };

    public static Route getRicaviByMacchinetta = (req, res) -> {
        res.type("application/json");
        String idMacchinetta = req.params(":id");
        List<Ricavo> ricavi = daoRicavo.getRicaviByMacchinetta(idMacchinetta);
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

    public static Route addRicavo = (req, res) -> {
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
    };

    public static Route getTotaleRicavi = (req, res) -> {
        res.type("application/json");
        double totale = daoRicavo.getTotaleRicavi();
        return gson.toJson(totale);
    };

    public static Route getTotaleRicaviByMacchinetta = (req, res) -> {
        res.type("application/json");
        String idMacchinetta = req.params(":id");
        double totale = daoRicavo.getTotaleRicaviByMacchinetta(idMacchinetta);
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

}