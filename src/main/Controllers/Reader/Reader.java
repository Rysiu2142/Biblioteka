package main.Controllers.Reader;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import main.Controllers.Reader.BooksTab;
import main.Controllers.Reader.OrdersTab;
import main.model.Dao.AuthorDAO;
import main.model.Dao.BooksDAO;
import main.model.entity.AuthorEntity;
import main.model.entity.BooksEntity;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Kontroler glowny panelu czytelnik
 */
public class Reader implements Initializable {
    @FXML
    private OrdersTab ordersPanelController;
    @FXML
    private BooksTab booksPanelController;

    @FXML
    private Tab booksTab;
    @FXML
    private Tab ordersTab;
    @FXML
    private TabPane tabPane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        adminTab.setDisable(true);
//        BooksDAO booksDAO = new BooksDAO();
//        AuthorDAO authorDao = new AuthorDAO();
//        List<BooksEntity> books = booksDAO.findAll();
//        List<AuthorEntity> authors = authorDao.findAll();
//
//        System.out.println(books.get(0).toString());
//        System.out.println(authors.get(0).toString());
        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            refreshReaderTables();
        });
    }

    /**
     * Odswiezanie tabeli w czytelniku
     */
    public void refreshReaderTables(){

        ordersPanelController.refreshTable();
        booksPanelController.refreshTable();
    }


}
