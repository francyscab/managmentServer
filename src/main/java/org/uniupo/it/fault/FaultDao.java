package org.uniupo.it.fault;

import java.util.List;
import java.util.UUID;

public interface FaultDao {
    void saveFaults(List<FaultMessage> faults);

    List<Fault> getFaultsByMachine(String machineId, int idIstituto);

    void markFaultsAsResolved(List<UUID> faultIds);
}
