package main.Controllers.Reader;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import main.model.Dao.OrdersDAO;
import main.model.Dao.ServiceDAO;
import main.model.entity.OrdersEntity;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Kontroler panelu zamowienia czytelnika
 */
public class OrdersTab extends ServiceDAO implements Initializable {
    private static final String FXML_MAIN_PANEL = "../FXML/Main.fxml";
    private static final String TEMPORARY_LOGIN = "user";
    private static final String TEMPORARY_PASSWORD = "1234";



    @FXML
    private AnchorPane ordersPanel;
    @FXML
    private TableView tableView;
    @FXML
    private TableColumn<OrdersEntity, String> titleColumn;
    @FXML
    private TableColumn<OrdersEntity, String> authorColumn;
    @FXML
    private TableColumn<OrdersEntity, String> borrowColumn;
    @FXML
    private TableColumn<OrdersEntity, String> returnColumn;
    @FXML
    private TableColumn<OrdersEntity, String> statusColumn;

    /**
     * Odswiezanie tabeli
     */
    public void refreshTable(){
        tableView.setItems(ordersDAO.getUserOrders());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        adminTab.setDisable(true);
        initTableView();
        tableView.setItems(ordersDAO.getUserOrders());
    }

    /**
     * Odswiezanie tabeli
     */
    private void initTableView() {
        this.titleColumn = (TableColumn) tableView.getColumns().get(0);
        this.titleColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getBooksByBookId().getBookstitleByTitleId().getTitle()));
        this.authorColumn = (TableColumn) tableView.getColumns().get(1);
        this.authorColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getBooksByBookId().getBookstitleByTitleId().getAuthors().toString()));
        this.borrowColumn = (TableColumn) tableView.getColumns().get(2);
        this.borrowColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getDataFrom().toString()));
        this.returnColumn = (TableColumn) tableView.getColumns().get(3);
        this.returnColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getDataTo().toString()));
        this.statusColumn = (TableColumn) tableView.getColumns().get(4);
        this.statusColumn.setCellValueFactory(new PropertyValueFactory<OrdersEntity, String>("status"));
    }


}
