package main.model.Dao;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.model.entity.BooksEntity;
import main.model.entity.OrdersEntity;
import main.model.entity.UsersEntity;

public class OrdersDAO extends DAO<OrdersEntity, Integer> {
    private ObservableList<OrdersEntity> orders =  FXCollections.observableArrayList();
    private  ObservableList<OrdersEntity> userOrders =  FXCollections.observableArrayList();
    private  ObservableList<OrdersEntity> warehousemanTodayOrders =  FXCollections.observableArrayList();
    private  ObservableList<OrdersEntity> librarianTodayOrders =  FXCollections.observableArrayList();
    @Override
    public OrdersEntity findById(Integer id) {
        openCurrentSession();
        OrdersEntity entity = (OrdersEntity) getCurrentSession().get(OrdersEntity.class, id);
        closeCurrentSession();
        return entity;
    }

    //
    public List<OrdersEntity> findAllUsersOrders(Integer id){
        openCurrentSession();
        List<OrdersEntity> listEntities = (List<OrdersEntity>) getCurrentSession().createSQLQuery("SELECT * FROM orders WHERE userId=:id").addEntity(OrdersEntity.class).setParameter("id",id).list();
        closeCurrentSession();
        return listEntities;
    }

    //
    public void removeUsersOrders(Integer id) {
        System.out.println(id);
        List<OrdersEntity> deleted = findAllUsersOrders(id);
        deleted.forEach(ordersEntity -> {
            delete(ordersEntity);
        });

    }


    @Override
    public ObservableList<OrdersEntity> findAll() {
        openCurrentSession();
        ObservableList<OrdersEntity> listEntities =  FXCollections.observableArrayList(getCurrentSession().createSQLQuery("select * from orders").addEntity(OrdersEntity.class).list());
        closeCurrentSession();
        this.orders = listEntities;
        return listEntities;
    }
    public List<OrdersEntity> findAllBooksOrders(Integer id){
        openCurrentSession();
        List<OrdersEntity> listEntities = (List<OrdersEntity>) getCurrentSession().createSQLQuery("SELECT * FROM orders WHERE bookId=:id").addEntity(OrdersEntity.class).setParameter("id",id).list();
        closeCurrentSession();
        return listEntities;
    }
    public void removeBooksOrders(Integer id) {
        System.out.println(id);
        List<OrdersEntity> deleted = findAllBooksOrders(id);
        deleted.forEach(ordersEntity -> {
            delete(ordersEntity);
        });

    }
    public List<OrdersEntity> getLastBookOrder(Integer id){
        openCurrentSession();
        List<OrdersEntity> lastOrder = (List<OrdersEntity>) getCurrentSession().createSQLQuery("SELECT * FROM orders WHERE dataTO IN (SELECT max(dataTO) FROM orders) AND bookId = :id AND (status = 'zarezerwowana' OR status = 'wypozyczona') limit 1").setParameter("id",id).addEntity(OrdersEntity.class).list();
        closeCurrentSession();
        return lastOrder;
    }
    public ObservableList<OrdersEntity> findAllWarehousemanTodayOrders() {
        openCurrentSession();
        ObservableList<OrdersEntity> listEntities =  FXCollections.observableArrayList(getCurrentSession().createSQLQuery("SELECT * FROM orders where (status ='zarezerwowana' and dataFrom >= CURDATE()) OR status = 'zwrocona'").addEntity(OrdersEntity.class).list());
        closeCurrentSession();
        this.warehousemanTodayOrders = listEntities;
        return listEntities;
    }
    public ObservableList<OrdersEntity> findAllLibrarianTodayOrders() {
        openCurrentSession();
        ObservableList<OrdersEntity> listEntities =  FXCollections.observableArrayList(getCurrentSession().createSQLQuery("select * from orders where (status = 'do odebrania' AND CURDATE() BETWEEN dataFrom AND dataTO ) OR status = 'wypozyczona' ").addEntity(OrdersEntity.class).list());
        closeCurrentSession();
        this.librarianTodayOrders = listEntities;
        return listEntities;
    }
    public ObservableList<OrdersEntity> findAllUserOrders(Integer id) {
        Long lId = Long.parseLong(id.toString());
        openCurrentSession();
        ObservableList<OrdersEntity> listEntities =  FXCollections.observableArrayList(getCurrentSession().createSQLQuery("select * from orders WHERE userId=:id").setParameter("id",lId).addEntity(OrdersEntity.class).list());
        closeCurrentSession();
        this.userOrders = listEntities;
        return listEntities;
    }
    public ObservableList<OrdersEntity> updateAllNotGettingOrders() {

        openCurrentSession();
        ObservableList<OrdersEntity> listEntities =  FXCollections.observableArrayList(getCurrentSession().createSQLQuery("select * from orders WHERE dataTo<CURDATE() AND (status = 'do odebrania' || status = 'zarezerwowana')").addEntity(OrdersEntity.class).list());
        closeCurrentSession();
        System.out.println(listEntities);
        return listEntities;
    }

    public ObservableList<OrdersEntity> findAllBetweenDates(String dataFrom,String dataTO) {
        openCurrentSession();
        ObservableList<OrdersEntity> listEntities =  FXCollections.observableArrayList(getCurrentSession().createSQLQuery("select * from orders WHERE dataFrom >=:from AND dataTO <= :to").addEntity(OrdersEntity.class).setParameter("from",dataFrom).setParameter("to",dataTO).list());
        closeCurrentSession();
        return listEntities;
    }
    public ObservableList<OrdersEntity> getWarehousemanTodayOrders() {
        return warehousemanTodayOrders;
    }

    public ObservableList<OrdersEntity> getLibrarianTodayOrders() {
        return librarianTodayOrders;
    }

    public ObservableList<OrdersEntity> getOrders() {
        return orders;
    }

    public void setOrders(ObservableList<OrdersEntity> orders) {
        this.orders = orders;
    }

    public ObservableList<OrdersEntity> getUserOrders() {
        return userOrders;
    }
}