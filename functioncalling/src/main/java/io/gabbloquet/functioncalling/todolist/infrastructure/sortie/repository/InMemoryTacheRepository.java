package io.gabbloquet.functioncalling.todolist.infrastructure.sortie.repository;

import io.gabbloquet.functioncalling.todolist.domaine.modele.Tache;
import io.gabbloquet.functioncalling.todolist.domaine.TacheRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
public class InMemoryTacheRepository implements TacheRepository {

    private final List<Tache> taches = new ArrayList<>();

    @Override
    public void save(Tache tache) {
        taches.add(tache);
    }

    @Override
    public List<Tache> findAll() {
        return new ArrayList<>(taches);
    }

    @Override
    public boolean deleteByTitle(String title) {
        Iterator<Tache> it = taches.iterator();
        while (it.hasNext()) {
            Tache t = it.next();
            if (t.getTitle().equals(title)) {
                it.remove();
                return true;
            }
        }
        return false;
    }
}
