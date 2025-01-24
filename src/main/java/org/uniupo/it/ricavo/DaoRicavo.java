package org.uniupo.it.ricavo;

import java.util.List;

public interface DaoRicavo {
    List<Ricavo> getAllRicavi();
    List<Ricavo> getRicaviByMacchinetta(String id_macchinetta,int id_istituto);
    List<Ricavo> getRicaviByIstituto(int id_istituto);
    void addRicavo(Ricavo ricavo);
    double getTotaleRicavi();
    double getTotaleRicaviByMacchinetta(String id_macchinetta,int id_istituto);
    double getTotaleRicaviByIstituto(int id_istituto);
}