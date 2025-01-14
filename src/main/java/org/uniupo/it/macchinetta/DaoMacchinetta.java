package org.uniupo.it.macchinetta;

import java.util.List;

public interface DaoMacchinetta {
    List<Macchinetta> getAllMacchinette();
    List<Macchinetta> getMacchinetteByIstituto(int idIstituto);
    Macchinetta getMacchinettaById(String id);
    void addMacchinetta(Macchinetta macchinetta);
    void deleteMacchinetta(String id);
}
