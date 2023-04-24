package main.model.Dao;


import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import main.model.entity.SugestionsEntity;

public class SugestionsDAO extends DAO<SugestionsEntity, Integer> {

    private ObservableList<SugestionsEntity> sugestions =  FXCollections.observableArrayList();



    @Override
    public SugestionsEntity findById(Integer id) {
        openCurrentSession();
        SugestionsEntity entity = (SugestionsEntity) getCurrentSession().get(SugestionsEntity.class, id);
        closeCurrentSession();
        return entity;
    }

    @Override
    public ObservableList<SugestionsEntity> findAll() {
        openCurrentSession();
        ObservableList<SugestionsEntity> listEntities = FXCollections.observableArrayList(getCurrentSession().createSQLQuery("select * from sugestions").addEntity(SugestionsEntity.class).list());
        closeCurrentSession();
        this.sugestions =listEntities;
        return listEntities;
    }

    public ObservableList<SugestionsEntity> getSugestions() {
        return sugestions;
    }
}