package main.model.Dao;
import main.model.entity.AuthEntity;


import java.util.List;

public class AuthDAO extends DAO<AuthEntity, Integer>  {
    @Override
    public AuthEntity findById(Integer id) {
        openCurrentSession();
        AuthEntity entity = (AuthEntity) getCurrentSession().get(AuthEntity.class, id);
        closeCurrentSession();
        return entity;
    }

    @Override
    public List<AuthEntity> findAll() {
        openCurrentSession();
        List<AuthEntity> listEntities = (List<AuthEntity>) getCurrentSession().createSQLQuery("select * from users").addEntity(AuthEntity.class).list();
        closeCurrentSession();
        return listEntities;
    }
    public List<AuthEntity> findByEmailAndPassword(String email, String password){
        openCurrentSession();
        List<AuthEntity> loggedUser = (List<AuthEntity>) getCurrentSession().createSQLQuery("select * from auth where auth.email = :email AND auth.password = :password").addEntity(AuthEntity.class).setParameter("email",email).setParameter("password",password).list();
        closeCurrentSession();
        System.out.println(loggedUser);
        return loggedUser;
    }
    public List<AuthEntity> findByEmail(String email){
        openCurrentSession();
        List<AuthEntity> user = (List<AuthEntity>) getCurrentSession().createSQLQuery("select * from auth where auth.email = :email").addEntity(AuthEntity.class).setParameter("email",email).list();
        closeCurrentSession();
        return user;
    }

}
