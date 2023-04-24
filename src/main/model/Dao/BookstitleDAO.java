package main.model.Dao;

import java.util.List;

import javafx.collections.FXCollections;
import main.model.entity.BooksEntity;
import main.model.entity.BookstitleEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.model.entity.OrdersEntity;

public class BookstitleDAO extends DAO<BookstitleEntity, Integer> {
    private ObservableList<Object[]> allSugestionsAboutBooks =  FXCollections.observableArrayList();
    @Override
    public BookstitleEntity findById(Integer id) {
        Long longId = Long.parseLong(id.toString());
        openCurrentSession();
        BookstitleEntity entity = (BookstitleEntity) getCurrentSession().get(BookstitleEntity.class, longId);
        closeCurrentSession();
        return entity;
    }

    @Override
    public ObservableList<BookstitleEntity> findAll() {
        openCurrentSession();
        ObservableList<BookstitleEntity> listEntities =  FXCollections.observableArrayList(getCurrentSession().createSQLQuery("select * from bookstitle").addEntity(BookstitleEntity.class).list());
        closeCurrentSession();
        return listEntities;
    }
    public ObservableList<Object[]> getSugestionsAboutBooks() {
        openCurrentSession();
        ObservableList<Object[]> listEntities = FXCollections.observableArrayList(getCurrentSession().createSQLQuery("SELECT bt.*, bt1.books FROM (SELECT  bt.id,bt.title,count(*) as orders FROM bookstitle bt INNER JOIN books b ON bt.id = b.titleId INNER JOIN orders o ON o.bookId = b.id WHERE o.dataFrom > (CURDATE() - INTERVAL 1 MONTH) GROUP BY bt.title) as bt INNER JOIN (SELECT  bt.title,count(*) as books FROM bookstitle bt INNER JOIN books b ON bt.id = b.titleId GROUP BY bt.title) as bt1 ON  bt.title = bt1.title").list());
        closeCurrentSession();
        this.allSugestionsAboutBooks = listEntities;

        return listEntities;
    }

    public ObservableList<Object[]> getAllSugestionsAboutBooks() {
        return allSugestionsAboutBooks;
    }
}
