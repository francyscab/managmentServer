package org.uniupo.it.fault;

import org.uniupo.it.util.ErrorResponse;
import spark.Route;

import java.util.List;

import static org.uniupo.it.Application.gson;

public class FaultController {

    private static final FaultDao faultDao = new FaultDaoImpl();

    public static Route getFaultsByMachine = (req, res) -> {
        res.type("application/json");
        String machineId = req.params(":id");
        String idIstituto = req.params(":idIstituto");

        try {
            List<Fault> faults = faultDao.getFaultsByMachine(machineId, Integer.parseInt(idIstituto));
            return gson.toJson(faults);
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Errore interno del server"));
        }
    };

}