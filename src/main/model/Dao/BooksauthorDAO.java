package main.model.Dao;

import java.util.List;

import main.model.entity.BooksEntity;
import main.model.entity.BooksauthorEntity;

public class BooksauthorDAO extends DAO<BooksauthorEntity, Integer> {
    @Override
    public BooksauthorEntity findById(Integer id) {
        openCurrentSession();
        BooksauthorEntity entity = (BooksauthorEntity) getCurrentSession().get(BooksauthorEntity.class, id);
        closeCurrentSession();
        return entity;
    }

    @Override
    public List<BooksauthorEntity> findAll() {
        openCurrentSession();
        List<BooksauthorEntity> listEntities = (List<BooksauthorEntity>) getCurrentSession().createSQLQuery("select * from booksauthor").addEntity(BooksauthorEntity.class).list();
        closeCurrentSession();
        return listEntities;
    }
}