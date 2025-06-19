package io.gabbloquet.functioncalling.domaine;

import io.gabbloquet.functioncalling.domaine.modele.Tache;
import java.util.List;

public interface TacheRepository {

    void save(Tache tache);

    List<Tache> findAll();

    boolean deleteByTitle(String title);
}
