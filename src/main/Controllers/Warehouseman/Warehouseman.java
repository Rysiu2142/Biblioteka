package main.Controllers.Warehouseman;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Kontroler glowny panelu magazynier
 */
public class Warehouseman implements Initializable{
    @FXML
    private SugestionsTab sugestionsTabController;
    @FXML
    private OrdersTab ordersTabController;
    @FXML
    private BooksTab booksTabController;
    @FXML
    private TabPane tabPane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            refreshWarehousemanTables();
        });
    }

    /**
     * Metoda odswiezajaca dane w tabelach
     */
    public void refreshWarehousemanTables(){

        sugestionsTabController.refreshTable();
        ordersTabController.refreshTable();
        booksTabController.refreshTable();
    }

}
