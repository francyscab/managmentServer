
package org.uniupo.it.macchinetta;

import org.uniupo.it.mqtt.MQTTConnection;
import org.uniupo.it.util.ErrorResponse;
import org.uniupo.it.util.SuccessResponse;
import org.uniupo.it.util.Topics;
import spark.Route;

import java.util.List;

import static org.uniupo.it.Application.gson;

public class MacchinettaController {

    private static final DaoMacchinetta daoMacchinetta = new DaoMacchinetteImpl();

    public static Route getAllMacchinette = (req, res) -> {
        res.type("application/json");
        List<Macchinetta> macchinette = daoMacchinetta.getAllMacchinette();
        if (macchinette.isEmpty()) {
            res.status(404);
            return gson.toJson(new ErrorResponse("Nessuna macchinetta trovata"));
        }
        return gson.toJson(macchinette);
    };

    public static Route getMacchinetteByIstituto = (req, res) -> {
        res.type("application/json");
        int idIstituto = Integer.parseInt(req.params(":id"));
        List<Macchinetta> macchinette = daoMacchinetta.getMacchinetteByIstituto(idIstituto);
        return gson.toJson(macchinette);
    };

    public static Route getMacchinettaById = (req, res) -> {
        res.type("application/json");
        String id = req.params(":id");
        int idIstituto = Integer.parseInt(req.params(":idIstituto"));

        Macchinetta macchinetta = daoMacchinetta.getMacchinettaById(id, idIstituto);

        if (macchinetta == null) {
            res.status(404);
            return gson.toJson(new ErrorResponse("Macchinetta non trovata"));
        }
        return gson.toJson(macchinetta);
    };

    public static Route addMacchinetta = (req, res) -> {
        res.type("application/json");
        try {
            int idIstituto = Integer.parseInt(req.params(":id"));
            String idMacchinetta = req.queryParams("id_macchinetta");
            String piano = req.queryParams("piano");

            if (idMacchinetta == null || piano == null) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Parametri mancanti"));
            }

            MQTTConnection.getInstance().publish(String.format(Topics.NEW_MACHINE_TOPIC, idIstituto, idMacchinetta), "newMachine");

            Macchinetta macchinetta = new Macchinetta(idMacchinetta, idIstituto, piano);
            daoMacchinetta.addMacchinetta(macchinetta);

            res.status(201);
            return gson.toJson(new SuccessResponse("Macchinetta aggiunta con successo"));
        } catch (NumberFormatException e) {
            res.status(400);
            return gson.toJson(new ErrorResponse("ID istituto non valido"));
        } catch (Exception e) {
            res.status(500);
            System.out.println(e.getMessage());
            return gson.toJson(new ErrorResponse("Errore interno del server"));
        }
    };

    public static Route deleteMacchinetta = (req, res) -> {
        res.type("application/json");
        String id = req.params(":id");
        int idIstituto = Integer.parseInt(req.params(":idIstituto"));
        try {
            daoMacchinetta.deleteMacchinetta(id, idIstituto);
            MQTTConnection.getInstance().publish(String.format(Topics.KILL_SERVICE_TOPIC, idIstituto, id), "kill");
            return gson.toJson(new SuccessResponse("Macchinetta eliminata con successo"));
        } catch (IllegalStateException e) {
            res.status(404);
            return gson.toJson(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Errore interno del server"));
        }
    };
}