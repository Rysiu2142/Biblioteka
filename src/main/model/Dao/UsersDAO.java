package main.model.Dao;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import main.model.entity.AuthEntity;
import main.model.entity.UsersEntity;

import java.util.List;

public class UsersDAO extends DAO<UsersEntity, Integer> {
    private ObservableList<UsersEntity> users =  FXCollections.observableArrayList();
    private ObservableList<UsersEntity> employees =  FXCollections.observableArrayList();
    private ObservableList<UsersEntity> readers =  FXCollections.observableArrayList();



    @Override
    public UsersEntity findById(Integer id) {
        Long idLong = Long.parseLong(id.toString());
        openCurrentSession();
        UsersEntity entity = (UsersEntity) getCurrentSession().get(UsersEntity.class, idLong);
        closeCurrentSession();
        return entity;
    }

    @Override
    public ObservableList<UsersEntity> findAll() {
        openCurrentSession();
        ObservableList<UsersEntity> listEntities =  FXCollections.observableArrayList(getCurrentSession().createSQLQuery("select * from users LEFT JOIN auth on users.authId = auth.id WHERE auth.role ='czytelnik'").addEntity(UsersEntity.class).list());
        closeCurrentSession();
        this.users = listEntities;

        return listEntities;
    }
    public ObservableList<UsersEntity> findAllEmployees() {
        openCurrentSession();
        ObservableList<UsersEntity> listEntities =   FXCollections.observableArrayList(getCurrentSession().createSQLQuery("select * from users LEFT JOIN auth on users.authId = auth.id WHERE auth.role !='czytelnik' AND auth.role != 'admin'").addEntity(UsersEntity.class).list());
        closeCurrentSession();
        this.employees = listEntities;
        return listEntities;
    }

    public ObservableList<UsersEntity> findAllReaders() {
        openCurrentSession();
        ObservableList<UsersEntity> listEntities =   FXCollections.observableArrayList(getCurrentSession().createSQLQuery("select * from users LEFT JOIN auth on users.authId = auth.id WHERE auth.role ='czytelnik'").addEntity(UsersEntity.class).list());
        closeCurrentSession();
        this.readers = listEntities;
        return listEntities;
    }

    public List<UsersEntity> findByEmailAndPassword(String email, String password){
        openCurrentSession();
        List<UsersEntity> loggedUser = (List<UsersEntity>) getCurrentSession().createSQLQuery("select * from users LEFT JOIN auth ON users.authId = auth.id  where auth.email = :email ").addEntity(UsersEntity.class).setParameter("email",email).list();
        closeCurrentSession();
        System.out.println(loggedUser);
        return loggedUser;
    }

    public ObservableList<UsersEntity> getUsers() {
        return users;
    }

    public ObservableList<UsersEntity> getEmployees() {
        return employees;
    }

    public ObservableList<UsersEntity> getReaders() {
        return readers;
    }

    public void setUsers(ObservableList<UsersEntity> users) {
        this.users = users;
    }

    public void setEmployees(ObservableList<UsersEntity> employees) {
        this.employees = employees;
    }
}
