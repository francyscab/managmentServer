package org.uniupo.it.istituto;

import com.google.gson.Gson;
import org.uniupo.it.Application;
import org.uniupo.it.util.ErrorResponse;
import org.uniupo.it.util.SuccessResponse;
import spark.Route;

import java.net.URLDecoder;
import java.util.List;

import static org.uniupo.it.Application.daoIstituto;
import static org.uniupo.it.Application.gson;

public class IstitutoController {

    public static Route getIstituti = (req, res) -> {
        res.type("application/json");
        List<Istituto> istituti = daoIstituto.getAllIstituti();

        return gson.toJson(istituti);
    };

    public static Route addIstituto = (req, res) -> {
        res.type("application/json");
        Istituto istituto = gson.fromJson(req.body(), Istituto.class);
        try {
            daoIstituto.addIstituto(istituto);
        }catch (RuntimeException e){
            res.status(500);
            System.out.println(e.getMessage());
            return "Errore nell'aggiunta dell'istituto";
        }
        return "Istituto aggiunto";
    };

    public static Route getIstitutoById = (req, res) -> {
        res.type("application/json");
        int id = Integer.parseInt(req.params(":id"));
        try {
            Istituto istituto = daoIstituto.getIstitutoById(id);
            return gson.toJson(istituto);
        } catch (IllegalStateException e) {
            res.status(404);
            return gson.toJson(new ErrorResponse(e.getMessage()));
        }
    };

    public static Route deleteIstituto = (req, res) -> {
        res.type("application/json");
        int id = Integer.parseInt(req.params(":id"));

        try {
            daoIstituto.deleteIstituto(id);
            res.status(200);
            return gson.toJson(new SuccessResponse("Istituto eliminato con successo"));
        } catch (IllegalStateException e) {
            res.status(400);
            return gson.toJson(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Errore interno del server"));
        }
    };
}
