package main.model.Dao;

import java.util.List;
import java.util.ArrayList;
import main.model.entity.BooksEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
public class BooksDAO extends DAO<BooksEntity, Integer> {
    private ObservableList<BooksEntity> books = FXCollections.observableArrayList();
    private ObservableList<BooksEntity> allAvailableBooks = FXCollections.observableArrayList();


    @Override
    public BooksEntity findById(Integer id) {
        openCurrentSession();
        BooksEntity entity = (BooksEntity) getCurrentSession().get(BooksEntity.class, id);
        closeCurrentSession();
        return entity;
    }

    @Override
    public ObservableList<BooksEntity> findAll() {
        openCurrentSession();
        ObservableList<BooksEntity> listEntities = FXCollections.observableArrayList(getCurrentSession().createSQLQuery("select * from books").addEntity(BooksEntity.class).list());
        closeCurrentSession();
        this.books = listEntities;
        return listEntities;
    }

    public ObservableList<BooksEntity> findAllAvailable() {
        openCurrentSession();
        ObservableList<BooksEntity> listEntities = FXCollections.observableArrayList(getCurrentSession().createSQLQuery("select * from books WHERE status = 'w magazynie'").addEntity(BooksEntity.class).list());
        closeCurrentSession();
        this.allAvailableBooks = listEntities;
        return listEntities;
    }

    public ObservableList<BooksEntity> getBooks() {
        return books;
    }

    public ObservableList<BooksEntity> getAllAvailableBooks() {
        return allAvailableBooks;
    }

    public void setBooks(ObservableList<BooksEntity> books) {
        this.books = books;
    }
}
