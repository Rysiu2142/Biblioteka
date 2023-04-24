package main.model.Dao;

import java.util.List;
import main.model.entity.AuthorEntity;
import main.model.entity.BooksEntity;

public class AuthorDAO extends DAO<AuthorEntity, Integer> {
    @Override
    public AuthorEntity findById(Integer id) {
        openCurrentSession();
        AuthorEntity author = (AuthorEntity) getCurrentSession().get(AuthorEntity.class, id);
        closeCurrentSession();
        return author;
    }

    @Override
    public List<AuthorEntity> findAll() {
        openCurrentSession();
        List<AuthorEntity> authorEntities = (List<AuthorEntity>) getCurrentSession().createSQLQuery("select * from author").addEntity(AuthorEntity.class).list();
        closeCurrentSession();
        return authorEntities;
    }
}
