package main.model.Dao;

import java.io.Serializable;
import java.util.List;


/**
 * Klasa abstrakcyjna pozwalajaca na dodawanie, edytowanie, usuwanie i wyszukiwanie danych w bazie.
 * @param <T>
 * @param <Id>
 */
public abstract class DAO<T, Id extends Serializable> extends HibernateUtil{

    /**
     * Metoda zapisujaca nowy obiekt w bazie
     * @param entity Zmienna generyczna przyjmujaca obiekt dowolnego typu.
     */
    public void save(T entity) {
        openCurrentSessionwithTransaction();
        getCurrentSession().save(entity);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Metoda aktualizujaca obiekt w bazie
     * @param entity Zmienna generyczna przyjmujaca obiekt dowolnego typu.
     */
    public void update(T entity) {
        openCurrentSessionwithTransaction();
        getCurrentSession().update(entity);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Metoda abstrakcyjna wyszukujaca obiekt w bazie po id.
     * @param id ID szukanego obiektu
     * @return Zwraca szukany obiekt
     */
    public abstract T findById(Id id);

    /**
     * Metoda abstrakcyjna wyciagajaca wszystkie obiekty z bazy;
     * @return zwraca liste szukanych obiektów.
     */
    public abstract List<T> findAll();

    /**
     * Metoda usuwajaca obiekt z bazy.
     * @param entity Zmienna generyczna przyjmujaca obiekt dowolnego typu.
     */
    public void delete(T entity) {
        openCurrentSessionwithTransaction();
        getCurrentSession().delete(entity);
        closeCurrentSessionwithTransaction();
    }

    public void deleteAll() {
        List<T> entityList = findAll();
        for (T entity : entityList) {
            delete(entity);
        }
    }
}
