package org.uniupo.it.istituto;

import java.util.List;

public interface DaoIstituto {
    List<Istituto> getAllIstituti() throws RuntimeException;
    void addIstituto(Istituto istituto);
    Istituto getIstitutoById(int id) throws IllegalStateException;
    void deleteIstituto(int id) throws IllegalStateException;
}
