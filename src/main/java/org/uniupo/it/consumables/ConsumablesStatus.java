package org.uniupo.it.consumables;

import java.util.List;

public class ConsumablesStatus {
    private String machineId;
    private String instituteId;
    private List<Consumable> consumables;

    public ConsumablesStatus(String machineId, String instituteId, List<Consumable> consumables) {
        this.machineId = machineId;
        this.instituteId = instituteId;
        this.consumables = consumables;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public void setInstituteId(String instituteId) {
        this.instituteId = instituteId;
    }

    public void setConsumables(List<Consumable> consumables) {
        this.consumables = consumables;
    }

    public String getMachineId() {
        return machineId;
    }

    public String getInstituteId() {
        return instituteId;
    }

    public List<Consumable> getConsumables() {
        return consumables;
    }
}