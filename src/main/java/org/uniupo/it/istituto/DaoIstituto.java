package org.uniupo.it.istituto;

import java.util.List;

public interface DaoIstituto {
    List<Istituto> getAllIstituti();
    void addIstituto(Istituto istituto);
    Istituto getIstitutoById(int id);
}
